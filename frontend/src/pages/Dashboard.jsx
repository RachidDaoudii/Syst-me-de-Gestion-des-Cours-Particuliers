import { useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import api from '../api/axios'
import Layout from '../components/Layout'
import DataTable from '../components/DataTable'

function StatCard({ icon, label, value, color }) {
  return (
    <div className="col-md-3 col-sm-6 mb-3">
      <div className={`card stat-card h-100 border-start border-4 border-${color}`}>
        <div className="card-body d-flex align-items-center">
          <div className={`rounded-circle bg-${color} bg-opacity-10 p-3 me-3`}>
            <i className={`bi ${icon} text-${color} fs-4`}></i>
          </div>
          <div>
            <h3 className="mb-0 fw-bold">{value ?? 0}</h3>
            <small className="text-muted">{label}</small>
          </div>
        </div>
      </div>
    </div>
  )
}

export default function Dashboard() {
  const { user, isAdmin, isProfesseur } = useAuth()
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        let url = '/api/dashboard/eleve/' + user.userId
        if (isAdmin()) url = '/api/dashboard/admin'
        else if (isProfesseur()) url = '/api/dashboard/professeur/' + user.userId

        const { data: res } = await api.get(url)
        setData(res.data)
      } catch (err) {
        console.error(err)
      } finally {
        setLoading(false)
      }
    }
    fetchDashboard()
  }, [user])

  if (loading) {
    return (
      <Layout>
        <div className="text-center py-5">
          <div className="spinner-border text-primary"></div>
        </div>
      </Layout>
    )
  }

  return (
    <Layout>
      <h2 className="mb-4">
        <i className="bi bi-speedometer2 me-2"></i>
        Tableau de bord
      </h2>

      {isAdmin() && (
        <div className="row">
          <StatCard icon="bi-person-workspace" label="Professeurs" value={data?.nombreProfesseurs} color="primary" />
          <StatCard icon="bi-people" label="Élèves" value={data?.nombreEleves} color="success" />
          <StatCard icon="bi-book" label="Cours" value={data?.nombreCours} color="info" />
          <StatCard icon="bi-calendar-check" label="Réservations" value={data?.nombreReservations} color="warning" />
        </div>
      )}

      {isProfesseur() && (
        <div className="row">
          <StatCard icon="bi-calendar-check" label="Réservations" value={data?.nombreReservations} color="primary" />
          <StatCard icon="bi-currency-euro" label="Revenus estimés" value={data?.revenusEstimes + ' €'} color="success" />
          <StatCard icon="bi-star" label="Note moyenne" value={data?.noteMoyenne + '/5'} color="warning" />
        </div>
      )}

      {!isAdmin() && !isProfesseur() && (
        <div className="row">
          <StatCard icon="bi-calendar-check" label="Mes réservations" value={data?.nombreReservations} color="primary" />
        </div>
      )}

      {data?.reservationsRecentes?.length > 0 && (
        <div className="mt-4">
          <h5>Réservations récentes</h5>
          <DataTable
            columns={[
              { key: 'coursTitre', label: 'Cours' },
              { key: 'eleveNom', label: 'Élève' },
              { key: 'professeurNom', label: 'Professeur' },
              { key: 'statut', label: 'Statut', render: (r) => (
                <span className={`badge bg-${r.statut === 'CONFIRMEE' ? 'success' : r.statut === 'ANNULEE' ? 'danger' : 'warning'}`}>
                  {r.statut}
                </span>
              )}
            ]}
            data={data.reservationsRecentes}
          />
        </div>
      )}

      {data?.evaluationsRecentes?.length > 0 && (
        <div className="mt-4">
          <h5>Évaluations récentes</h5>
          <DataTable
            columns={[
              { key: 'professeurNom', label: 'Professeur' },
              { key: 'eleveNom', label: 'Élève' },
              { key: 'note', label: 'Note', render: (r) => `${r.note}/5` },
              { key: 'commentaire', label: 'Commentaire' }
            ]}
            data={data.evaluationsRecentes}
          />
        </div>
      )}
    </Layout>
  )
}
