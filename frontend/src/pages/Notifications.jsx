import { useEffect, useState } from 'react'
import api from '../api/axios'
import Layout from '../components/Layout'
import { useAuth } from '../context/AuthContext'

export default function Notifications() {
  const { user } = useAuth()
  const [notifications, setNotifications] = useState([])

  const fetchNotifications = async () => {
    const { data } = await api.get(`/api/notifications/utilisateur/${user.userId}`, { params: { size: 50 } })
    setNotifications(data.data.content)
  }

  useEffect(() => { fetchNotifications() }, [])

  const markAsRead = async (id) => {
    await api.patch(`/api/notifications/${id}/lire`)
    fetchNotifications()
  }

  return (
    <Layout>
      <h2 className="mb-4"><i className="bi bi-bell me-2"></i>Notifications</h2>

      {notifications.length === 0 ? (
        <div className="alert alert-info">Aucune notification</div>
      ) : (
        <div className="list-group">
          {notifications.map((n) => (
            <div key={n.id} className={`list-group-item list-group-item-action d-flex justify-content-between align-items-start ${!n.lu ? 'list-group-item-primary' : ''}`}>
              <div>
                <p className="mb-1">{n.message}</p>
                <small className="text-muted">{new Date(n.date).toLocaleString('fr-FR')}</small>
              </div>
              {!n.lu && (
                <button className="btn btn-sm btn-outline-primary" onClick={() => markAsRead(n.id)}>
                  Marquer lu
                </button>
              )}
            </div>
          ))}
        </div>
      )}
    </Layout>
  )
}
