import { useEffect, useState } from 'react'
import api from '../api/axios'
import Layout from '../components/Layout'
import DataTable from '../components/DataTable'
import Pagination from '../components/Pagination'
import { useAuth } from '../context/AuthContext'

export default function Seances() {
  const { user, isProfesseur } = useAuth()
  const [seances, setSeances] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  const fetchSeances = async () => {
    const endpoint = isProfesseur()
      ? `/api/seances/professeur/${user.professeurId || 1}`
      : `/api/seances/eleve/${user.eleveId || 1}`
    try {
      const { data } = await api.get(endpoint, { params: { page, size: 10 } })
      setSeances(data.data.content)
      setTotalPages(data.data.totalPages)
    } catch {
      const { data } = await api.get('/api/reservations', { params: { page: 0, size: 1 } })
      setSeances([])
    }
  }

  useEffect(() => { fetchSeances() }, [page])

  const marquerRealisee = async (id) => {
    const compteRendu = prompt('Compte-rendu de la séance (optionnel):')
    await api.patch(`/api/seances/${id}/realiser`, { compteRendu })
    fetchSeances()
  }

  return (
    <Layout>
      <h2 className="mb-4"><i className="bi bi-journal-text me-2"></i>Séances</h2>

      <DataTable
        columns={[
          { key: 'date', label: 'Date' },
          { key: 'heureDebut', label: 'Début' },
          { key: 'heureFin', label: 'Fin' },
          { key: 'statut', label: 'Statut', render: (s) => (
            <span className={`badge bg-${s.statut === 'REALISEE' ? 'success' : s.statut === 'ANNULEE' ? 'danger' : 'primary'}`}>
              {s.statut}
            </span>
          )},
          { key: 'compteRendu', label: 'Compte-rendu', render: (s) => s.compteRendu || '-' }
        ]}
        data={seances}
        actions={isProfesseur() ? (s) => s.statut === 'PLANIFIEE' ? (
          <button className="btn btn-sm btn-outline-success" onClick={() => marquerRealisee(s.id)}>
            <i className="bi bi-check-circle"></i> Réalisée
          </button>
        ) : null : undefined}
        emptyMessage="Aucune séance enregistrée"
      />
      <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
    </Layout>
  )
}
