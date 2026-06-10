import { useEffect, useState } from 'react'
import api from '../api/axios'
import Layout from '../components/Layout'
import DataTable from '../components/DataTable'
import Pagination from '../components/Pagination'
import { useAuth } from '../context/AuthContext'

const emptyForm = { professeurId: '', date: '', heureDebut: '', heureFin: '' }

export default function Plannings() {
  const { isProfesseur, user } = useAuth()
  const [plannings, setPlannings] = useState([])
  const [professeurs, setProfesseurs] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [professeurFilter, setProfesseurFilter] = useState('')
  const [showModal, setShowModal] = useState(false)
  const [form, setForm] = useState(emptyForm)

  const fetchPlannings = async () => {
    const params = { page, size: 10 }
    if (professeurFilter) params.professeurId = professeurFilter

    const url = isProfesseur() && user?.professeurId
      ? `/api/plannings/professeur/${user.professeurId}`
      : '/api/plannings/disponibles'

    const { data } = await api.get(url, { params })
    setPlannings(data.data.content)
    setTotalPages(data.data.totalPages)
  }

  useEffect(() => {
    fetchPlannings()
    api.get('/api/professeurs', { params: { size: 100 } })
      .then(r => setProfesseurs(r.data.data.content))
      .catch(() => setProfesseurs([]))
  }, [page, professeurFilter, user?.professeurId])

  const openModal = () => {
    const initial = { ...emptyForm }
    if (isProfesseur() && user?.professeurId) {
      initial.professeurId = String(user.professeurId)
    }
    setForm(initial)
    setShowModal(true)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    await api.post('/api/plannings', { ...form, professeurId: parseInt(form.professeurId) })
    setShowModal(false)
    setForm(emptyForm)
    fetchPlannings()
  }

  const handleDelete = async (id) => {
    if (confirm('Supprimer ce créneau ?')) {
      await api.delete(`/api/plannings/${id}`)
      fetchPlannings()
    }
  }

  return (
    <Layout>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2><i className="bi bi-calendar-week me-2"></i>Plannings</h2>
        {isProfesseur() && (
          <button className="btn btn-primary" onClick={openModal}>
            <i className="bi bi-plus-lg me-1"></i> Ajouter un créneau
          </button>
        )}
      </div>

      {!isProfesseur() && (
        <div className="mb-3">
          <select className="form-select" style={{ maxWidth: 300 }} value={professeurFilter}
            onChange={(e) => setProfesseurFilter(e.target.value)}>
            <option value="">Tous les professeurs</option>
            {professeurs.map(p => (
              <option key={p.professeurId} value={p.professeurId}>{p.prenom} {p.nom}</option>
            ))}
          </select>
        </div>
      )}

      <DataTable
        columns={[
          { key: 'professeurNom', label: 'Professeur' },
          { key: 'date', label: 'Date' },
          { key: 'heureDebut', label: 'Début' },
          { key: 'heureFin', label: 'Fin' },
          { key: 'disponible', label: 'Disponible', render: (p) => (
            <span className={`badge bg-${p.disponible ? 'success' : 'secondary'}`}>
              {p.disponible ? 'Oui' : 'Non'}
            </span>
          )}
        ]}
        data={plannings}
        actions={isProfesseur() ? (p) => (
          <button className="btn btn-sm btn-outline-danger" onClick={() => handleDelete(p.id)}>
            <i className="bi bi-trash"></i>
          </button>
        ) : undefined}
      />
      <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />

      {showModal && (
        <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog">
            <div className="modal-content">
              <form onSubmit={handleSubmit}>
                <div className="modal-header">
                  <h5 className="modal-title">Nouveau créneau</h5>
                  <button type="button" className="btn-close" onClick={() => setShowModal(false)}></button>
                </div>
                <div className="modal-body">
                  {isProfesseur() && user?.professeurId ? (
                    <div className="mb-3">
                      <label className="form-label">Professeur</label>
                      <input className="form-control" value={`${user.prenom} ${user.nom}`} readOnly />
                    </div>
                  ) : (
                    <div className="mb-3">
                      <label className="form-label">Professeur</label>
                      <select className="form-select" value={form.professeurId} onChange={(e) => setForm({...form, professeurId: e.target.value})} required>
                        <option value="">Sélectionner</option>
                        {professeurs.filter(p => p.professeurId).map(p => (
                          <option key={p.professeurId} value={p.professeurId}>{p.prenom} {p.nom}</option>
                        ))}
                      </select>
                    </div>
                  )}
                  <div className="mb-3">
                    <label className="form-label">Date</label>
                    <input type="date" className="form-control" value={form.date} onChange={(e) => setForm({...form, date: e.target.value})} required />
                  </div>
                  <div className="row g-3">
                    <div className="col-6">
                      <label className="form-label">Heure début</label>
                      <input type="time" className="form-control" value={form.heureDebut} onChange={(e) => setForm({...form, heureDebut: e.target.value})} required />
                    </div>
                    <div className="col-6">
                      <label className="form-label">Heure fin</label>
                      <input type="time" className="form-control" value={form.heureFin} onChange={(e) => setForm({...form, heureFin: e.target.value})} required />
                    </div>
                  </div>
                </div>
                <div className="modal-footer">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Annuler</button>
                  <button type="submit" className="btn btn-primary">Créer</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </Layout>
  )
}
