import { useState } from 'react'
import '../App.css'

function LoginPage() {
  const [login, setLogin] = useState('')
  const [password, setPassword] = useState('')

  return (
    <main className="auth-page">
      <section className="auth-card">
        <div className="auth-card__header">
          <h1 className="auth-card__title">Логин</h1>
          <p className="auth-card__subtitle">Войдите в административную панель</p>
        </div>

        <div className="auth-card__form">
          <label className="modal-form__field">
            <span className="modal-form__label">Логин</span>
            <input
              className="modal-form__input"
              value={login}
              onChange={(event) => setLogin(event.target.value)}
            />
          </label>

          <label className="modal-form__field">
            <span className="modal-form__label">Пароль</span>
            <input
              className="modal-form__input"
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
            />
          </label>

          <div className="auth-card__actions">
            <button
              className="modal-form__button"
              type="button"
              disabled={!login.trim() || !password.trim()}
            >
              Войти
            </button>
          </div>
        </div>
      </section>
    </main>
  )
}

export default LoginPage
