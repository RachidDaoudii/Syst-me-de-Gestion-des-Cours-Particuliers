import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const profileLink = { to: '/profile', icon: 'bi-person-circle', label: 'Mon profil' }

const adminLinks = [
  { to: '/dashboard', icon: 'bi-speedometer2', label: 'Tableau de bord' },
  { to: '/users', icon: 'bi-people', label: 'Utilisateurs' },
  { to: '/cours', icon: 'bi-book', label: 'Cours' },
  { to: '/reservations', icon: 'bi-calendar-check', label: 'Réservations' },
  { to: '/notifications', icon: 'bi-bell', label: 'Notifications' },
  profileLink
]

const profLinks = [
  { to: '/dashboard', icon: 'bi-speedometer2', label: 'Tableau de bord' },
  { to: '/cours', icon: 'bi-book', label: 'Mes cours' },
  { to: '/plannings', icon: 'bi-calendar-week', label: 'Planning' },
  { to: '/reservations', icon: 'bi-calendar-check', label: 'Réservations' },
  { to: '/seances', icon: 'bi-journal-text', label: 'Séances' },
  { to: '/evaluations', icon: 'bi-star', label: 'Évaluations' },
  { to: '/notifications', icon: 'bi-bell', label: 'Notifications' },
  profileLink
]

const eleveLinks = [
  { to: '/dashboard', icon: 'bi-speedometer2', label: 'Tableau de bord' },
  { to: '/cours', icon: 'bi-search', label: 'Catalogue' },
  { to: '/plannings', icon: 'bi-calendar-week', label: 'Créneaux' },
  { to: '/reservations', icon: 'bi-calendar-check', label: 'Mes réservations' },
  { to: '/seances', icon: 'bi-journal-text', label: 'Séances' },
  { to: '/evaluations', icon: 'bi-star', label: 'Évaluations' },
  { to: '/notifications', icon: 'bi-bell', label: 'Notifications' },
  profileLink
]

export default function Layout({ children }) {
  const { user, logout, isAdmin, isProfesseur } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()
  const [sidebarOpen, setSidebarOpen] = useState(false)

  const links = isAdmin() ? adminLinks : isProfesseur() ? profLinks : eleveLinks

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div className="d-flex">
      <nav className={`sidebar ${sidebarOpen ? 'show' : ''}`}>
        <div className="p-4 text-white">
          <h5 className="fw-bold mb-0">
            <i className="bi bi-mortarboard-fill me-2"></i>
            Cours Part.
          </h5>
          <Link to="/profile" className="text-white-50 text-decoration-none small">
            {user?.prenom} {user?.nom}
          </Link>
        </div>
        <ul className="nav flex-column">
          {links.map((link) => (
            <li key={link.to} className="nav-item">
              <Link
                to={link.to}
                className={`nav-link ${location.pathname === link.to ? 'active' : ''}`}
                onClick={() => setSidebarOpen(false)}
              >
                <i className={`bi ${link.icon} me-2`}></i>
                {link.label}
              </Link>
            </li>
          ))}
        </ul>
        <div className="position-absolute bottom-0 w-100 p-3">
          <button className="btn btn-outline-light btn-sm w-100" onClick={handleLogout}>
            <i className="bi bi-box-arrow-right me-1"></i> Déconnexion
          </button>
        </div>
      </nav>

      <div className="main-content flex-grow-1">
        <div className="d-md-none mb-3">
          <button className="btn btn-primary" onClick={() => setSidebarOpen(!sidebarOpen)}>
            <i className="bi bi-list"></i> Menu
          </button>
        </div>
        {children}
      </div>
    </div>
  )
}
