import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Login() {
  const [email, setEmail] = useState('')
  const [motDePasse, setMotDePasse] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await login(email, motDePasse)
      navigate('/dashboard')
    } catch (err) {
      setError(err.response?.data?.message || 'Identifiants invalides')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-page">
      <div className="container">
        <div className="row justify-content-center">
          <div className="col-md-5">
            <div className="card login-card border-0">
              <div className="card-body p-5">
                <div className="text-center mb-4">
                  <i className="bi bi-mortarboard-fill text-primary" style={{ fontSize: '3rem' }}></i>
                  <h3 className="mt-2 fw-bold">Cours Particuliers</h3>
                  <p className="text-muted">Connectez-vous à votre espace</p>
                </div>
                {error && <div className="alert alert-danger">{error}</div>}
                <form onSubmit={handleSubmit}>
                  <div className="mb-3">
                    <label className="form-label">Email</label>
                    <input
                      type="email"
                      className="form-control form-control-lg"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      required
                      placeholder="admin@coursparticuliers.fr"
                    />
                  </div>
                  <div className="mb-4">
                    <label className="form-label">Mot de passe</label>
                    <input
                      type="password"
                      className="form-control form-control-lg"
                      value={motDePasse}
                      onChange={(e) => setMotDePasse(e.target.value)}
                      required
                    />
                  </div>
                  <button type="submit" className="btn btn-primary btn-lg w-100" disabled={loading}>
                    {loading ? 'Connexion...' : 'Se connecter'}
                  </button>
                </form>
                <div className="mt-4 text-center text-muted small">
                  <p className="mb-0">Compte admin par défaut :</p>
                  <code>admin@coursparticuliers.fr / admin123</code>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
