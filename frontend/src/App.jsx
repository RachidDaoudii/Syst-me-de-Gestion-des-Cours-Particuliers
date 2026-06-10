import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './context/AuthContext'
import PrivateRoute from './components/PrivateRoute'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import Users from './pages/Users'
import Cours from './pages/Cours'
import Plannings from './pages/Plannings'
import Reservations from './pages/Reservations'
import Seances from './pages/Seances'
import Evaluations from './pages/Evaluations'
import Notifications from './pages/Notifications'
import Profile from './pages/Profile'

export default function App() {
  const { user } = useAuth()

  return (
    <Routes>
      <Route path="/login" element={user ? <Navigate to="/dashboard" /> : <Login />} />
      <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
      <Route path="/users" element={<PrivateRoute roles={['ADMINISTRATEUR']}><Users /></PrivateRoute>} />
      <Route path="/cours" element={<PrivateRoute><Cours /></PrivateRoute>} />
      <Route path="/plannings" element={<PrivateRoute><Plannings /></PrivateRoute>} />
      <Route path="/reservations" element={<PrivateRoute><Reservations /></PrivateRoute>} />
      <Route path="/seances" element={<PrivateRoute><Seances /></PrivateRoute>} />
      <Route path="/evaluations" element={<PrivateRoute><Evaluations /></PrivateRoute>} />
      <Route path="/notifications" element={<PrivateRoute><Notifications /></PrivateRoute>} />
      <Route path="/profile" element={<PrivateRoute><Profile /></PrivateRoute>} />
      <Route path="*" element={<Navigate to={user ? '/dashboard' : '/login'} />} />
    </Routes>
  )
}
