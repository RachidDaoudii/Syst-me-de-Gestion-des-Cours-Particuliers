import { useEffect, useState } from 'react'
import api from '../api/axios'
import Layout from '../components/Layout'
import DataTable from '../components/DataTable'
import Pagination from '../components/Pagination'
import { useAuth } from '../context/AuthContext'

const emptyForm = { eleveId: '', professeurId: '', note: 5, commentaire: '' }

export default function Evaluations() {
  const { isEleve, isProfesseur, user } = useAuth()
  const [evaluations, setEvaluations] = useState([])
  const [professeurs, setProfesseurs] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [showModal, setShowModal] = useState(false)
  const [form, setForm] = useState(emptyForm)

  const fetchEvaluations = async () => {
    try {
      let url = '/api/evaluations/professeur/1'
      if (isEleve() && user?.eleveId) {
        url = `/api/evaluations/eleve/${user.eleveId}`
      } else if (isProfesseur() && user?.professeurId) {
        url = `/api/evaluations/professeur/${user.professeurId}`
      }
      const { data } = await api.get(url, { params: { page, size: 10 } })
      setEvaluations(data.data.content)
      setTotalPages(data.data.totalPages)
    } catch {
      setEvaluations([])
      setTotalPages(0)
    }
  }

  const loadProfesseurs = async () => {
    try {
      const { data } = await api.get('/api/professeurs', { params: { size: 100 } })
      setProfesseurs(data.data.content.filter(p => p.professeurId))
    } catch {
      if (user?.eleveId) {
        try {
          const res = await api.get('/api/reservations', { params: { eleveId: user.eleveId, size: 100 } })
          const fromReservations = [...new Map(
            res.data.data.content.map(r => [r.professeurId, {
              professeurId: r.professeurId,
              prenom: r.professeurNom?.split(' ')[0] || '',
              nom: r.professeurNom?.split(' ').slice(1).join(' ') || r.professeurNom || ''
            }])
          ).values()]
          setProfesseurs(fromReservations)
        } catch {
          setProfesseurs([])
        }
      } else {
        setProfesseurs([])
      }
    }
  }

  useEffect(() => {
    if (!user) return
    fetchEvaluations()
    if (isEleve()) loadProfesseurs()
  }, [page, user?.eleveId, user?.professeurId])

  const openModal = () => {
    const initial = { ...emptyForm }
    if (user?.eleveId) initial.eleveId = String(user.eleveId)
    setForm(initial)
    loadProfesseurs()
    setShowModal(true)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    await api.post('/api/evaluations', {
      eleveId: user.eleveId,
      professeurId: parseInt(form.professeurId),
      note: parseInt(form.note),
      commentaire: form.commentaire
    })
    setShowModal(false)
    setForm(emptyForm)
    fetchEvaluations()
  }

  const handleDelete = async (id) => {
    if (confirm('Supprimer cette évaluation ?')) {
      await api.delete(`/api/evaluations/${id}`)
      fetchEvaluations()
    }
  }

  return (
    <Layout>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2><i className="bi bi-star me-2"></i>Évaluations</h2>
        {isEleve() && (
          <button className="btn btn-primary" onClick={openModal}>
            <i className="bi bi-plus-lg me-1"></i> Évaluer
          </button>
        )}
      </div>

      <DataTable
        columns={[
          { key: 'professeurNom', label: 'Professeur' },
          { key: 'eleveNom', label: 'Élève' },
          { key: 'note', label: 'Note', render: (e) => (
            <span>{'★'.repeat(e.note)}{'☆'.repeat(5 - e.note)} ({e.note}/5)</span>
          )},
          { key: 'commentaire', label: 'Commentaire' },
          { key: 'date', label: 'Date', render: (e) => new Date(e.date).toLocaleDateString('fr-FR') }
        ]}
        data={evaluations}
        actions={isEleve() ? (e) => (
          <button className="btn btn-sm btn-outline-danger" onClick={() => handleDelete(e.id)}>
            <i className="bi bi-trash"></i>
          </button>
        ) : undefined}
        emptyMessage="Aucune évaluation"
      />
      <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />

      {showModal && (
        <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog">
            <div className="modal-content">
              <form onSubmit={handleSubmit}>
                <div className="modal-header">
                  <h5 className="modal-title">Nouvelle évaluation</h5>
                  <button type="button" className="btn-close" onClick={() => setShowModal(false)}></button>
                </div>
                <div className="modal-body">
                  {!user?.eleveId && (
                    <div className="alert alert-warning">
                      Profil élève introuvable. Déconnectez-vous et reconnectez-vous.
                    </div>
                  )}
                  <div className="mb-3">
                    <label className="form-label">Élève</label>
                    <input className="form-control" value={`${user?.prenom || ''} ${user?.nom || ''}`} readOnly />
                  </div>
                  <div className="mb-3">
                    <label className="form-label">Professeur</label>
                    <select
                      className="form-select"
                      value={form.professeurId}
                      onChange={(e) => setForm({ ...form, professeurId: e.target.value })}
                      required
                    >
                      <option value="">Sélectionner</option>
                      {professeurs.map(p => (
                        <option key={p.professeurId} value={p.professeurId}>
                          {p.prenom} {p.nom}
                        </option>
                      ))}
                    </select>
                    {professeurs.length === 0 && (
                      <small className="text-muted">Aucun professeur trouvé. Effectuez d'abord une réservation.</small>
                    )}
                  </div>
                  <div className="mb-3">
                    <label className="form-label">Note (1-5)</label>
                    <input type="range" min="1" max="5" className="form-range" value={form.note}
                      onChange={(e) => setForm({ ...form, note: e.target.value })} />
                    <div className="text-center fw-bold">{form.note}/5</div>
                  </div>
                  <div className="mb-3">
                    <label className="form-label">Commentaire</label>
                    <textarea className="form-control" value={form.commentaire} onChange={(e) => setForm({ ...form, commentaire: e.target.value })} />
                  </div>
                </div>
                <div className="modal-footer">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Annuler</button>
                  <button type="submit" className="btn btn-primary" disabled={!user?.eleveId || !form.professeurId}>
                    Envoyer
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </Layout>
  )
}
