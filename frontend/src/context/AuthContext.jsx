import { createContext, useContext, useState, useEffect } from 'react'
import api from '../api/axios'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const init = async () => {
      const stored = localStorage.getItem('user')
      if (!stored) {
        setLoading(false)
        return
      }
      let userData = JSON.parse(stored)
      if (userData.role === 'PROFESSEUR' && !userData.professeurId) {
        try {
          const { data } = await api.get('/api/professeurs', { params: { size: 100 } })
          const prof = data.data.content.find(p => p.email === userData.email)
          if (prof?.professeurId) {
            userData = { ...userData, professeurId: prof.professeurId }
            localStorage.setItem('user', JSON.stringify(userData))
          }
        } catch { /* ignore */ }
      }
      if (userData.role === 'ELEVE' && !userData.eleveId) {
        try {
          const { data } = await api.get('/api/eleves', { params: { size: 100 } })
          const eleve = data.data.content.find(e => e.email === userData.email)
          if (eleve?.eleveId) {
            userData = { ...userData, eleveId: eleve.eleveId }
            localStorage.setItem('user', JSON.stringify(userData))
          }
        } catch { /* ignore */ }
      }
      setUser(userData)
      setLoading(false)
    }
    init()
  }, [])

  const login = async (email, motDePasse) => {
    const { data } = await api.post('/api/auth/login', { email, motDePasse })
    const auth = data.data
    localStorage.setItem('accessToken', auth.accessToken)
    localStorage.setItem('refreshToken', auth.refreshToken)
    const userData = {
      userId: auth.userId,
      email: auth.email,
      nom: auth.nom,
      prenom: auth.prenom,
      role: auth.role,
      professeurId: auth.professeurId,
      eleveId: auth.eleveId
    }
    localStorage.setItem('user', JSON.stringify(userData))
    setUser(userData)
    return userData
  }

  const logout = () => {
    localStorage.clear()
    setUser(null)
  }

  const updateUser = (updates) => {
    setUser((prev) => {
      const updated = { ...prev, ...updates }
      localStorage.setItem('user', JSON.stringify(updated))
      return updated
    })
  }

  const isAdmin = () => user?.role === 'ADMINISTRATEUR'
  const isProfesseur = () => user?.role === 'PROFESSEUR'
  const isEleve = () => user?.role === 'ELEVE'

  return (
    <AuthContext.Provider value={{ user, login, logout, updateUser, loading, isAdmin, isProfesseur, isEleve }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
