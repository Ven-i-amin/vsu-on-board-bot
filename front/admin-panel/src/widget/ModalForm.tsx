import { useEffect, useRef, useState } from 'react'
import { AVAILABLE_LANGUAGES } from '../entities/models'
import type { AvailableLanguageCode } from '../entities/models'
import globeIcon from '../assets/fi-rr-globe.svg'
import RichTextEditor from './RichTextEditor'

type TextFieldProps = {
  label: string
  value: string
  onChange: (value: string) => void
  required?: boolean
}

export function TextField({ label, value, onChange, required = false }: TextFieldProps) {
  return (
    <label className="modal-form__field">
      <span className="modal-form__label">
        {label}
        {required ? ' *' : ''}
      </span>
      <input
        className="modal-form__input"
        value={value}
        onChange={(event) => onChange(event.target.value)}
      />
    </label>
  )
}

export function TextAreaField({ label, value, onChange, required = false }: TextFieldProps) {
  return (
    <label className="modal-form__field">
      <span className="modal-form__label">
        {label}
        {required ? ' *' : ''}
      </span>
      <textarea
        className="modal-form__textarea"
        value={value}
        onChange={(event) => onChange(event.target.value)}
      />
    </label>
  )
}

export function ReadOnlyField({ label, value }: { label: string; value: string }) {
  return (
    <div className="modal-form__field">
      <span className="modal-form__label">{label}</span>
      <div className="modal-form__readonly">{value}</div>
    </div>
  )
}

export function LanguageDropdown({
  value,
  onChange,
}: {
  value: AvailableLanguageCode
  onChange: (value: AvailableLanguageCode) => void
}) {
  const [isOpen, setIsOpen] = useState(false)
  const containerRef = useRef<HTMLDivElement | null>(null)
  const current = AVAILABLE_LANGUAGES.find((item) => item.code === value) ?? AVAILABLE_LANGUAGES[0]

  useEffect(() => {
    if (!isOpen) return

    const handlePointerDown = (event: MouseEvent) => {
      if (!containerRef.current?.contains(event.target as Node)) {
        setIsOpen(false)
      }
    }

    document.addEventListener('mousedown', handlePointerDown)
    return () => document.removeEventListener('mousedown', handlePointerDown)
  }, [isOpen])

  return (
    <div className="modal-form__language-picker" ref={containerRef}>
      <button
        className={`modal-form__language-button ${isOpen ? 'modal-form__language-button_open' : ''}`}
        type="button"
        onClick={() => setIsOpen((s) => !s)}
        aria-haspopup="listbox"
        aria-expanded={isOpen}
      >
        <span className="modal-form__language-value">
          <img src={globeIcon} alt="" aria-hidden="true" />
          <span>{current.name}</span>
        </span>
      </button>

      {isOpen && (
        <div className="modal-form__language-menu" role="listbox">
          {AVAILABLE_LANGUAGES.map((language) => (
            <button
              key={language.code}
              className="modal-form__language-option"
              type="button"
              role="option"
              aria-selected={language.code === value}
              onClick={() => {
                onChange(language.code as AvailableLanguageCode)
                setIsOpen(false)
              }}
            >
              <span>{language.name}</span>
            </button>
          ))}
        </div>
      )}
    </div>
  )
}

export function ModalActions({
  onConfirm,
  disabled = false,
}: {
  onConfirm: () => void
  disabled?: boolean
}) {
  return (
    <div className="modal-form__actions">
      <button className="modal-form__button" type="button" onClick={onConfirm} disabled={disabled}>
        Подтвердить
      </button>
    </div>
  )
}

export function ConfirmBody({ text, onConfirm }: { text: string; onConfirm: () => void }) {
  return (
    <div className="modal-form">
      <p className="modal-form__text">{text}</p>
      <ModalActions onConfirm={onConfirm} />
    </div>
  )
}

type RichTextFormFieldProps = {
  label: string
  value: string
  onChange: (value: string) => void
}

export function RichTextFormField({ label, value, onChange }: RichTextFormFieldProps) {
  return (
    <div className="modal-form__field">
      <span className="modal-form__label">{label}</span>
      <RichTextEditor value={value} onChange={onChange} />
    </div>
  )
}
