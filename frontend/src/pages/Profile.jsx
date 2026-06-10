import { useEffect, useState } from 'react'
import api from '../api/axios'
import Layout from '../components/Layout'
import { useAuth } from '../context/AuthContext'

const emptyForm = {
  nom: '', prenom: '', email: '', telephone: '', motDePasse: '',
  matieres: '', description: '', tarifHoraire: '', photoProfil: '',
  niveauScolaire: '', adresse: ''
}

function mapProfileToForm(p) {
  return {
    nom: p.nom || '',
    prenom: p.prenom || '',
    email: p.email || '',
    telephone: p.telephone || '',
    motDePasse: '',
    matieres: p.matieres?.join(', ') || '',
    description: p.description || '',
    tarifHoraire: p.tarifHoraire ?? '',
    photoProfil: p.photoProfil || '',
    niveauScolaire: p.niveauScolaire || '',
    adresse: p.adresse || ''
  }
}

export default function Profile() {
  const { user, updateUser } = useAuth()
  const [form, setForm] = useState(emptyForm)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  const loadProfile = async () => {
    setLoading(true)
    setError('')
    try {
      const { data } = await api.get('/api/profile/me')
      setForm(mapProfileToForm(data.data))
    } catch (err) {
      const status = err.response?.status
      if (status === 401 || status === 403) {
        setError('Session expirée. Reconnectez-vous.')
      } else if (status === 404) {
        setError('Service profil indisponible. Redémarrez le backend.')
      } else {
        setForm({
          ...emptyForm,
          nom: user?.nom || '',
          prenom: user?.prenom || '',
          email: user?.email || ''
        })
        setError(err.response?.data?.message || 'Chargement partiel — vous pouvez modifier et enregistrer.')
      }
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { loadProfile() }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSaving(true)
    setMessage('')
    setError('')
    try {
      const payload = {
        nom: form.nom,
        prenom: form.prenom,
        email: form.email,
        telephone: form.telephone,
        description: form.description,
        tarifHoraire: form.tarifHoraire !== '' ? parseFloat(form.tarifHoraire) : null,
        photoProfil: form.photoProfil || null,
        niveauScolaire: form.niveauScolaire,
        adresse: form.adresse,
        matieres: form.matieres ? form.matieres.split(',').map(m => m.trim()).filter(Boolean) : []
      }
      if (form.motDePasse) payload.motDePasse = form.motDePasse

      const { data } = await api.put('/api/profile/me', payload)
      const updated = data.data
      updateUser({
        nom: updated.nom,
        prenom: updated.prenom,
        email: updated.email,
        professeurId: updated.professeurId,
        eleveId: updated.eleveId
      })
      setForm(mapProfileToForm(updated))
      setMessage('Profil mis à jour avec succès')
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la mise à jour')
    } finally {
      setSaving(false)
    }
  }

  if (loading) {
    return (
      <Layout>
        <div className="text-center py-5">
          <div className="spinner-border text-primary" />
        </div>
      </Layout>
    )
  }

  return (
    <Layout>
      <h2 className="mb-4"><i className="bi bi-person-circle me-2"></i>Mon profil</h2>

      {message && <div className="alert alert-success">{message}</div>}
      {error && <div className="alert alert-warning">{error}</div>}

      <div className="card border-0 shadow-sm" style={{ maxWidth: 720 }}>
        <div className="card-body p-4">
          <form onSubmit={handleSubmit}>
            <h5 className="mb-3 text-muted">Informations personnelles</h5>
            <div className="row g-3 mb-4">
              <div className="col-md-6">
                <label className="form-label">Nom</label>
                <input className="form-control" value={form.nom}
                  onChange={(e) => setForm({ ...form, nom: e.target.value })} required />
              </div>
              <div className="col-md-6">
                <label className="form-label">Prénom</label>
                <input className="form-control" value={form.prenom}
                  onChange={(e) => setForm({ ...form, prenom: e.target.value })} required />
              </div>
              <div className="col-md-6">
                <label className="form-label">Email</label>
                <input type="email" className="form-control" value={form.email}
                  onChange={(e) => setForm({ ...form, email: e.target.value })} required />
              </div>
              <div className="col-md-6">
                <label className="form-label">Téléphone</label>
                <input className="form-control" value={form.telephone}
                  onChange={(e) => setForm({ ...form, telephone: e.target.value })} />
              </div>
              <div className="col-md-6">
                <label className="form-label">Rôle</label>
                <input className="form-control bg-light" value={user?.role || ''} readOnly />
              </div>
              <div className="col-md-6">
                <label className="form-label">Nouveau mot de passe</label>
                <input type="password" className="form-control" value={form.motDePasse}
                  onChange={(e) => setForm({ ...form, motDePasse: e.target.value })}
                  placeholder="Laisser vide pour ne pas changer" />
              </div>
            </div>

            {user?.role === 'PROFESSEUR' && (
              <>
                <h5 className="mb-3 text-muted">Profil professeur</h5>
                <div className="row g-3 mb-4">
                  <div className="col-12">
                    <label className="form-label">Matières (séparées par virgule)</label>
                    <input className="form-control" value={form.matieres}
                      onChange={(e) => setForm({ ...form, matieres: e.target.value })}
                      placeholder="Mathématiques, Physique" />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label">Tarif horaire (€)</label>
                    <input type="number" step="0.01" className="form-control" value={form.tarifHoraire}
                      onChange={(e) => setForm({ ...form, tarifHoraire: e.target.value })} />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label">URL photo de profil</label>
                    <input className="form-control" value={form.photoProfil}
                      onChange={(e) => setForm({ ...form, photoProfil: e.target.value })} />
                  </div>
                  <div className="col-12">
                    <label className="form-label">Description</label>
                    <textarea className="form-control" rows={3} value={form.description}
                      onChange={(e) => setForm({ ...form, description: e.target.value })} />
                  </div>
                </div>
              </>
            )}

            {user?.role === 'ELEVE' && (
              <>
                <h5 className="mb-3 text-muted">Profil élève</h5>
                <div className="row g-3 mb-4">
                  <div className="col-md-6">
                    <label className="form-label">Niveau scolaire</label>
                    <input className="form-control" value={form.niveauScolaire}
                      onChange={(e) => setForm({ ...form, niveauScolaire: e.target.value })}
                      placeholder="Terminale, Licence..." />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label">Adresse</label>
                    <input className="form-control" value={form.adresse}
                      onChange={(e) => setForm({ ...form, adresse: e.target.value })} />
                  </div>
                </div>
              </>
            )}

            <div className="d-flex gap-2">
              <button type="submit" className="btn btn-primary" disabled={saving}>
                {saving ? 'Enregistrement...' : 'Enregistrer les modifications'}
              </button>
              <button type="button" className="btn btn-outline-secondary" onClick={loadProfile}>
                Actualiser
              </button>
            </div>
          </form>
        </div>
      </div>
    </Layout>
  )
}
