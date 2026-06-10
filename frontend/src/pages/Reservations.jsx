import { useEffect, useState } from 'react'
import api from '../api/axios'
import Layout from '../components/Layout'
import DataTable from '../components/DataTable'
import Pagination from '../components/Pagination'
import { useAuth } from '../context/AuthContext'

const emptyForm = { eleveId: '', coursId: '', planningId: '' }

export default function Reservations() {
  const { isEleve, isProfesseur, user } = useAuth()
  const [reservations, setReservations] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [showModal, setShowModal] = useState(false)
  const [cours, setCours] = useState([])
  const [plannings, setPlannings] = useState([])
  const [form, setForm] = useState(emptyForm)

  const fetchReservations = async () => {
    const params = { page, size: 10 }
    if (user?.eleveId) params.eleveId = user.eleveId
    if (user?.professeurId) params.professeurId = user.professeurId

    const { data } = await api.get('/api/reservations', { params })
    setReservations(data.data.content)
    setTotalPages(data.data.totalPages)
  }

  const loadFormData = async () => {
    try {
      const [coursRes, planningsRes] = await Promise.all([
        api.get('/api/cours', { params: { size: 100, statut: 'ACTIF' } }),
        api.get('/api/plannings/disponibles', { params: { size: 100 } })
      ])
      setCours(coursRes.data.data.content)
      setPlannings(planningsRes.data.data.content)
    } catch {
      setCours([])
      setPlannings([])
    }
  }

  useEffect(() => {
    if (!user) return
    fetchReservations()
    if (isEleve()) loadFormData()
  }, [page, user?.eleveId, user?.professeurId])

  const openModal = () => {
    const initial = { ...emptyForm }
    if (user?.eleveId) initial.eleveId = String(user.eleveId)
    setForm(initial)
    loadFormData()
    setShowModal(true)
  }

  const handleCoursChange = (coursId) => {
    setForm({ ...form, coursId, planningId: '' })
    const selected = cours.find(c => String(c.id) === coursId)
    if (selected?.professeurId) {
      api.get('/api/plannings/disponibles', { params: { professeurId: selected.professeurId, size: 100 } })
        .then(r => setPlannings(r.data.data.content))
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    await api.post('/api/reservations', {
      eleveId: user.eleveId,
      coursId: parseInt(form.coursId),
      planningId: parseInt(form.planningId)
    })
    setShowModal(false)
    setForm(emptyForm)
    fetchReservations()
  }

  const confirmer = async (id) => {
    await api.patch(`/api/reservations/${id}/confirmer`)
    fetchReservations()
  }

  const annuler = async (id) => {
    if (confirm('Annuler cette réservation ?')) {
      await api.patch(`/api/reservations/${id}/annuler`)
      fetchReservations()
    }
  }

  const filteredPlannings = form.coursId
    ? plannings.filter(p => {
        const selected = cours.find(c => String(c.id) === form.coursId)
        return selected ? p.professeurId === selected.professeurId : true
      })
    : plannings

  return (
    <Layout>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2><i className="bi bi-calendar-check me-2"></i>Réservations</h2>
        {isEleve() && (
          <button className="btn btn-primary" onClick={openModal}>
            <i className="bi bi-plus-lg me-1"></i> Réserver
          </button>
        )}
      </div>

      <DataTable
        columns={[
          { key: 'coursTitre', label: 'Cours' },
          { key: 'eleveNom', label: 'Élève' },
          { key: 'professeurNom', label: 'Professeur' },
          { key: 'dateReservation', label: 'Date', render: (r) => new Date(r.dateReservation).toLocaleDateString('fr-FR') },
          { key: 'statut', label: 'Statut', render: (r) => (
            <span className={`badge bg-${r.statut === 'CONFIRMEE' ? 'success' : r.statut === 'ANNULEE' ? 'danger' : r.statut === 'TERMINEE' ? 'info' : 'warning'}`}>
              {r.statut}
            </span>
          )}
        ]}
        data={reservations}
        actions={(r) => (
          <div className="btn-group btn-group-sm">
            {isProfesseur() && r.statut === 'EN_ATTENTE' && (
              <button className="btn btn-outline-success" onClick={() => confirmer(r.id)}>
                <i className="bi bi-check-lg"></i>
              </button>
            )}
            {r.statut !== 'ANNULEE' && r.statut !== 'TERMINEE' && (
              <button className="btn btn-outline-danger" onClick={() => annuler(r.id)}>
                <i className="bi bi-x-lg"></i>
              </button>
            )}
          </div>
        )}
      />
      <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />

      {showModal && (
        <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog">
            <div className="modal-content">
              <form onSubmit={handleSubmit}>
                <div className="modal-header">
                  <h5 className="modal-title">Nouvelle réservation</h5>
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
                    <label className="form-label">Cours</label>
                    <select className="form-select" value={form.coursId} onChange={(e) => handleCoursChange(e.target.value)} required>
                      <option value="">Sélectionner</option>
                      {cours.map(c => (
                        <option key={c.id} value={c.id}>{c.titre} - {c.matiere} ({c.professeurNom})</option>
                      ))}
                    </select>
                  </div>
                  <div className="mb-3">
                    <label className="form-label">Créneau</label>
                    <select
                      className="form-select"
                      value={form.planningId}
                      onChange={(e) => setForm({ ...form, planningId: e.target.value })}
                      required
                      disabled={!form.coursId}
                    >
                      <option value="">{form.coursId ? 'Sélectionner' : "Choisissez d'abord un cours"}</option>
                      {filteredPlannings.map(p => (
                        <option key={p.id} value={p.id}>{p.date} {p.heureDebut}-{p.heureFin}</option>
                      ))}
                    </select>
                  </div>
                </div>
                <div className="modal-footer">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Annuler</button>
                  <button type="submit" className="btn btn-primary" disabled={!user?.eleveId}>Réserver</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </Layout>
  )
}
