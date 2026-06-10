import { useEffect, useState } from 'react'
import api from '../api/axios'
import Layout from '../components/Layout'
import DataTable from '../components/DataTable'
import Pagination from '../components/Pagination'

const emptyForm = {
  nom: '', prenom: '', email: '', telephone: '', motDePasse: '', role: 'ELEVE',
  matieres: '', description: '', tarifHoraire: '', niveauScolaire: '', adresse: ''
}

export default function Users() {
  const [users, setUsers] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [search, setSearch] = useState('')
  const [showModal, setShowModal] = useState(false)
  const [form, setForm] = useState(emptyForm)
  const [editId, setEditId] = useState(null)

  const fetchUsers = async () => {
    const { data } = await api.get('/api/users', { params: { page, size: 10, search } })
    setUsers(data.data.content)
    setTotalPages(data.data.totalPages)
  }

  useEffect(() => { fetchUsers() }, [page, search])

  const handleSubmit = async (e) => {
    e.preventDefault()
    const payload = {
      ...form,
      matieres: form.matieres ? form.matieres.split(',').map(m => m.trim()) : [],
      tarifHoraire: form.tarifHoraire ? parseFloat(form.tarifHoraire) : null
    }
    if (editId) {
      await api.put(`/api/users/${editId}`, payload)
    } else {
      await api.post('/api/users', payload)
    }
    setShowModal(false)
    setForm(emptyForm)
    setEditId(null)
    fetchUsers()
  }

  const handleEdit = (user) => {
    setForm({
      nom: user.nom, prenom: user.prenom, email: user.email, telephone: user.telephone || '',
      motDePasse: '', role: user.role,
      matieres: user.matieres?.join(', ') || '', description: user.description || '',
      tarifHoraire: user.tarifHoraire || '', niveauScolaire: user.niveauScolaire || '', adresse: user.adresse || ''
    })
    setEditId(user.id)
    setShowModal(true)
  }

  const handleDelete = async (id) => {
    if (confirm('Supprimer cet utilisateur ?')) {
      await api.delete(`/api/users/${id}`)
      fetchUsers()
    }
  }

  const toggleActif = async (id) => {
    await api.patch(`/api/users/${id}/toggle-actif`)
    fetchUsers()
  }

  return (
    <Layout>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2><i className="bi bi-people me-2"></i>Utilisateurs</h2>
        <button className="btn btn-primary" onClick={() => { setForm(emptyForm); setEditId(null); setShowModal(true) }}>
          <i className="bi bi-plus-lg me-1"></i> Ajouter
        </button>
      </div>

      <div className="mb-3">
        <input className="form-control" placeholder="Rechercher..." value={search}
          onChange={(e) => { setSearch(e.target.value); setPage(0) }} style={{ maxWidth: 300 }} />
      </div>

      <DataTable
        columns={[
          { key: 'nom', label: 'Nom', render: (u) => `${u.prenom} ${u.nom}` },
          { key: 'email', label: 'Email' },
          { key: 'role', label: 'Rôle', render: (u) => <span className="badge bg-secondary">{u.role}</span> },
          { key: 'actif', label: 'Statut', render: (u) => (
            <span className={`badge bg-${u.actif ? 'success' : 'danger'}`}>{u.actif ? 'Actif' : 'Inactif'}</span>
          )}
        ]}
        data={users}
        actions={(u) => (
          <div className="btn-group btn-group-sm">
            <button className="btn btn-outline-primary" onClick={() => handleEdit(u)}><i className="bi bi-pencil"></i></button>
            <button className="btn btn-outline-warning" onClick={() => toggleActif(u.id)}><i className="bi bi-toggle-on"></i></button>
            <button className="btn btn-outline-danger" onClick={() => handleDelete(u.id)}><i className="bi bi-trash"></i></button>
          </div>
        )}
      />
      <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />

      {showModal && (
        <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog modal-lg">
            <div className="modal-content">
              <form onSubmit={handleSubmit}>
                <div className="modal-header">
                  <h5 className="modal-title">{editId ? 'Modifier' : 'Ajouter'} un utilisateur</h5>
                  <button type="button" className="btn-close" onClick={() => setShowModal(false)}></button>
                </div>
                <div className="modal-body">
                  <div className="row g-3">
                    <div className="col-md-6">
                      <label className="form-label">Nom</label>
                      <input className="form-control" value={form.nom} onChange={(e) => setForm({...form, nom: e.target.value})} required />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Prénom</label>
                      <input className="form-control" value={form.prenom} onChange={(e) => setForm({...form, prenom: e.target.value})} required />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Email</label>
                      <input type="email" className="form-control" value={form.email} onChange={(e) => setForm({...form, email: e.target.value})} required />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Téléphone</label>
                      <input className="form-control" value={form.telephone} onChange={(e) => setForm({...form, telephone: e.target.value})} />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Mot de passe {editId && '(laisser vide pour ne pas changer)'}</label>
                      <input type="password" className="form-control" value={form.motDePasse} onChange={(e) => setForm({...form, motDePasse: e.target.value})} required={!editId} />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Rôle</label>
                      <select className="form-select" value={form.role} onChange={(e) => setForm({...form, role: e.target.value})}>
                        <option value="ADMINISTRATEUR">Administrateur</option>
                        <option value="PROFESSEUR">Professeur</option>
                        <option value="ELEVE">Élève</option>
                      </select>
                    </div>
                    {form.role === 'PROFESSEUR' && (
                      <>
                        <div className="col-12">
                          <label className="form-label">Matières (séparées par virgule)</label>
                          <input className="form-control" value={form.matieres} onChange={(e) => setForm({...form, matieres: e.target.value})} />
                        </div>
                        <div className="col-md-6">
                          <label className="form-label">Tarif horaire (€)</label>
                          <input type="number" className="form-control" value={form.tarifHoraire} onChange={(e) => setForm({...form, tarifHoraire: e.target.value})} />
                        </div>
                        <div className="col-12">
                          <label className="form-label">Description</label>
                          <textarea className="form-control" value={form.description} onChange={(e) => setForm({...form, description: e.target.value})} />
                        </div>
                      </>
                    )}
                    {form.role === 'ELEVE' && (
                      <>
                        <div className="col-md-6">
                          <label className="form-label">Niveau scolaire</label>
                          <input className="form-control" value={form.niveauScolaire} onChange={(e) => setForm({...form, niveauScolaire: e.target.value})} />
                        </div>
                        <div className="col-md-6">
                          <label className="form-label">Adresse</label>
                          <input className="form-control" value={form.adresse} onChange={(e) => setForm({...form, adresse: e.target.value})} />
                        </div>
                      </>
                    )}
                  </div>
                </div>
                <div className="modal-footer">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Annuler</button>
                  <button type="submit" className="btn btn-primary">{editId ? 'Modifier' : 'Créer'}</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </Layout>
  )
}
