import { useMemo, useState } from 'react'
import '../App.css'

type RegistrationPageProps = {
  errorMessage?: string
  isSubmitting?: boolean
  onSubmit: (login: string, password: string) => Promise<void> | void
}

function RegistrationPage({
  errorMessage = '',
  isSubmitting = false,
  onSubmit,
}: RegistrationPageProps) {
  const [login, setLogin] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')

  const isPasswordMismatch = useMemo(
    () => confirmPassword.trim().length > 0 && password !== confirmPassword,
    [confirmPassword, password],
  )

  const handleSubmit = async () => {
    if (
      !login.trim()
      || !password.trim()
      || !confirmPassword.trim()
      || isPasswordMismatch
      || isSubmitting
    ) {
      return
    }

    await onSubmit(login.trim(), password)
    setLogin('')
    setPassword('')
    setConfirmPassword('')
  }

  return (
    <main className="auth-page">
      <section className="auth-card">
        <div className="auth-card__header">
          <h1 className="auth-card__title">Регистрация администратора</h1>
          <p className="auth-card__subtitle">Любой администратор может создать нового администратора</p>
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

          <label className="modal-form__field">
            <span className="modal-form__label">Повторите пароль</span>
            <input
              className="modal-form__input"
              type="password"
              value={confirmPassword}
              onChange={(event) => setConfirmPassword(event.target.value)}
            />
          </label>

          {isPasswordMismatch && <div className="auth-card__error">Пароли не совпадают</div>}
          {errorMessage && <div className="auth-card__error">{errorMessage}</div>}

          <div className="auth-card__actions">
            <button
              className="modal-form__button"
              type="button"
              disabled={!login.trim() || !password.trim() || !confirmPassword.trim() || isPasswordMismatch || isSubmitting}
              onClick={() => void handleSubmit()}
            >
              {isSubmitting ? 'Создание...' : 'Создать администратора'}
            </button>
          </div>
        </div>
      </section>
    </main>
  )
}

export default RegistrationPage
