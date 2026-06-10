import { useEffect, useState } from 'react'
import api from '../api/axios'
import Layout from '../components/Layout'
import DataTable from '../components/DataTable'
import Pagination from '../components/Pagination'
import { useAuth } from '../context/AuthContext'

const emptyForm = { titre: '', description: '', matiere: '', niveau: '', prix: '', professeurId: '', statut: 'ACTIF' }

export default function Cours() {
  const { isAdmin, isProfesseur, user } = useAuth()
  const [cours, setCours] = useState([])
  const [professeurs, setProfesseurs] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [filters, setFilters] = useState({ matiere: '', niveau: '', search: '' })
  const [showModal, setShowModal] = useState(false)
  const [form, setForm] = useState(emptyForm)
  const [editId, setEditId] = useState(null)

  const fetchCours = async () => {
    const params = { page, size: 10, ...filters }
    if (isProfesseur() && user?.professeurId) params.professeurId = user.professeurId

    const { data } = await api.get('/api/cours', { params })
    setCours(data.data.content)
    setTotalPages(data.data.totalPages)
  }

  const fetchProfesseurs = async () => {
    try {
      const { data } = await api.get('/api/professeurs', { params: { size: 100 } })
      setProfesseurs(data.data.content.filter(p => p.professeurId))
    } catch {
      setProfesseurs([])
    }
  }

  useEffect(() => {
    if (!user) return
    fetchCours()
    if (isAdmin() || isProfesseur()) fetchProfesseurs()
  }, [page, filters, user?.professeurId])

  const openModal = (coursItem = null) => {
    if (coursItem) {
      setForm({
        titre: coursItem.titre,
        description: coursItem.description || '',
        matiere: coursItem.matiere,
        niveau: coursItem.niveau,
        prix: coursItem.prix,
        professeurId: coursItem.professeurId,
        statut: coursItem.statut
      })
      setEditId(coursItem.id)
    } else {
      const initial = { ...emptyForm }
      if (isProfesseur() && user?.professeurId) {
        initial.professeurId = String(user.professeurId)
      }
      setForm(initial)
      setEditId(null)
    }
    setShowModal(true)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    const professeurId = isProfesseur() && user?.professeurId
      ? user.professeurId
      : parseInt(form.professeurId)

    const payload = { ...form, prix: parseFloat(form.prix), professeurId }
    if (editId) await api.put(`/api/cours/${editId}`, payload)
    else await api.post('/api/cours', payload)
    setShowModal(false)
    setForm(emptyForm)
    setEditId(null)
    fetchCours()
  }

  const handleDelete = async (id) => {
    if (confirm('Supprimer ce cours ?')) {
      await api.delete(`/api/cours/${id}`)
      fetchCours()
    }
  }

  const canEdit = isAdmin() || isProfesseur()

  return (
    <Layout>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2><i className="bi bi-book me-2"></i>Cours</h2>
        {canEdit && (
          <button className="btn btn-primary" onClick={() => openModal()}>
            <i className="bi bi-plus-lg me-1"></i> Ajouter
          </button>
        )}
      </div>

      <div className="row g-2 mb-3">
        <div className="col-md-3">
          <input className="form-control" placeholder="Rechercher..." value={filters.search}
            onChange={(e) => setFilters({...filters, search: e.target.value})} />
        </div>
        <div className="col-md-3">
          <input className="form-control" placeholder="Matière" value={filters.matiere}
            onChange={(e) => setFilters({...filters, matiere: e.target.value})} />
        </div>
        <div className="col-md-3">
          <input className="form-control" placeholder="Niveau" value={filters.niveau}
            onChange={(e) => setFilters({...filters, niveau: e.target.value})} />
        </div>
      </div>

      <DataTable
        columns={[
          { key: 'titre', label: 'Titre' },
          { key: 'matiere', label: 'Matière' },
          { key: 'niveau', label: 'Niveau' },
          { key: 'prix', label: 'Prix', render: (c) => `${c.prix} €` },
          { key: 'professeurNom', label: 'Professeur' },
          { key: 'statut', label: 'Statut', render: (c) => <span className="badge bg-info">{c.statut}</span> }
        ]}
        data={cours}
        actions={canEdit ? (c) => (
          <div className="btn-group btn-group-sm">
            <button className="btn btn-outline-primary" onClick={() => openModal(c)}>
              <i className="bi bi-pencil"></i>
            </button>
            <button className="btn btn-outline-danger" onClick={() => handleDelete(c.id)}>
              <i className="bi bi-trash"></i>
            </button>
          </div>
        ) : undefined}
      />
      <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />

      {showModal && (
        <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog">
            <div className="modal-content">
              <form onSubmit={handleSubmit}>
                <div className="modal-header">
                  <h5 className="modal-title">{editId ? 'Modifier' : 'Ajouter'} un cours</h5>
                  <button type="button" className="btn-close" onClick={() => setShowModal(false)}></button>
                </div>
                <div className="modal-body">
                  {isProfesseur() && !user?.professeurId && (
                    <div className="alert alert-warning">
                      Profil professeur introuvable. Déconnectez-vous et reconnectez-vous.
                    </div>
                  )}
                  <div className="mb-3">
                    <label className="form-label">Titre</label>
                    <input className="form-control" value={form.titre} onChange={(e) => setForm({...form, titre: e.target.value})} required />
                  </div>
                  <div className="mb-3">
                    <label className="form-label">Description</label>
                    <textarea className="form-control" value={form.description} onChange={(e) => setForm({...form, description: e.target.value})} />
                  </div>
                  <div className="row g-3">
                    <div className="col-md-6">
                      <label className="form-label">Matière</label>
                      <input className="form-control" value={form.matiere} onChange={(e) => setForm({...form, matiere: e.target.value})} required />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Niveau</label>
                      <input className="form-control" value={form.niveau} onChange={(e) => setForm({...form, niveau: e.target.value})} required />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Prix (€)</label>
                      <input type="number" step="0.01" className="form-control" value={form.prix} onChange={(e) => setForm({...form, prix: e.target.value})} required />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Professeur</label>
                      {isProfesseur() && user?.professeurId ? (
                        <input className="form-control" value={`${user.prenom} ${user.nom}`} readOnly />
                      ) : (
                        <select className="form-select" value={form.professeurId} onChange={(e) => setForm({...form, professeurId: e.target.value})} required>
                          <option value="">Sélectionner</option>
                          {professeurs.map(p => (
                            <option key={p.professeurId} value={p.professeurId}>{p.prenom} {p.nom}</option>
                          ))}
                        </select>
                      )}
                    </div>
                  </div>
                </div>
                <div className="modal-footer">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Annuler</button>
                  <button type="submit" className="btn btn-primary" disabled={isProfesseur() && !user?.professeurId}>
                    Enregistrer
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
