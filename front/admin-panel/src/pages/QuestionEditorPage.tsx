import { useEffect, useMemo, useRef, useState } from 'react'
import './AdminPage.css'
import globeIcon from '../assets/fi-rr-globe.svg'
import arrowLeftIcon from '../assets/fi-rr-angle-left.svg'
import RichTextEditor from '../widget/RichTextEditor'
import {
  AVAILABLE_LANGUAGES,
  type AvailableLanguageCode,
  type LocalizedText,
  Question,
} from '../entities/models'

type LocalizedDraft = Record<AvailableLanguageCode, string>

type QuestionEditorPageProps = {
  question: Question | null
  langCode?: AvailableLanguageCode
  isSubmitting?: boolean
  errorMessage?: string
  onBack: () => void
  onSave: (questionId: string, title: LocalizedText, text: LocalizedText) => Promise<void>
  onDelete: (questionId: string) => Promise<void>
}

const createEmptyLocalizedDraft = (): LocalizedDraft => ({
  ru: '',
  en: '',
})

const createLocalizedDraft = (value?: LocalizedText): LocalizedDraft => ({
  ru: value?.ru ?? '',
  en: value?.en ?? '',
})

const isRichTextEmpty = (value: string) =>
  value.replace(/<[^>]+>/g, '').replace(/&nbsp;/g, ' ').trim() === ''

const normalizeRichTextForRequest = (value: string) =>
  value
    .replace(/\r\n/g, '\n')
    .replace(/\r/g, '\n')
    .replace(/&nbsp;/g, ' ')
    .replace(/<br\s*\/?>/gi, '\n')
    .replace(/<strong\b[^>]*>/gi, '<b>')
    .replace(/<\/strong\s*>/gi, '</b>')
    .replace(/<em\b[^>]*>/gi, '<i>')
    .replace(/<\/em\s*>/gi, '</i>')
    .replace(/<strike\b[^>]*>/gi, '<s>')
    .replace(/<\/strike\s*>/gi, '</s>')
    .replace(/<del\b[^>]*>/gi, '<s>')
    .replace(/<\/del\s*>/gi, '</s>')
    .replace(/<p\b[^>]*>/gi, '')
    .replace(/<\/p\s*>/gi, '\n\n')
    .replace(/<div\b[^>]*>/gi, '')
    .replace(/<\/div\s*>/gi, '\n')
    .replace(/<pre\b([^>]*)class=(['"])language-([^'"]+)\2([^>]*)>/gi, '<pre language="$3">')
    .replace(/<(?!\/?(?:b|i|code|s|u|pre)\b)[^>]+>/gi, '')
    .replace(/\n{3,}/g, '\n\n')
    .trim()

const buildTitlePayload = (draft: LocalizedDraft): LocalizedText => {
  const next: LocalizedText = {}

  for (const code of ['ru', 'en'] as const) {
    const value = draft[code].trim()
    if (value) {
      next[code] = value
    }
  }

  return next
}

const buildTextPayload = (draft: LocalizedDraft): LocalizedText => {
  const next: LocalizedText = {}

  for (const code of ['ru', 'en'] as const) {
    const value = draft[code]
    if (!isRichTextEmpty(value)) {
      next[code] = normalizeRichTextForRequest(value)
    }
  }

  return next
}

const areLocalizedDraftsEqual = (
  left: LocalizedDraft,
  right: LocalizedDraft,
  normalizer: (value: string) => string = (value) => value,
) => (['ru', 'en'] as const).every((code) => normalizer(left[code]) === normalizer(right[code]))

function QuestionEditorPage({
  question,
  langCode = 'ru',
  isSubmitting = false,
  errorMessage = '',
  onBack,
  onSave,
  onDelete,
}: QuestionEditorPageProps) {
  const [activeLanguage, setActiveLanguage] = useState<AvailableLanguageCode>(langCode)
  const [titleValues, setTitleValues] = useState<LocalizedDraft>(() => createEmptyLocalizedDraft())
  const [textValues, setTextValues] = useState<LocalizedDraft>(() => createEmptyLocalizedDraft())

  useEffect(() => {
    if (!question) {
      return
    }

    setTitleValues(createLocalizedDraft(question.title))
    setTextValues(createLocalizedDraft(question.text))
    setActiveLanguage(langCode)
  }, [langCode, question])

  const originalTitleValues = useMemo(() => createLocalizedDraft(question?.title), [question])
  const originalTextValues = useMemo(() => createLocalizedDraft(question?.text), [question])
  const isUnchanged =
    areLocalizedDraftsEqual(titleValues, originalTitleValues, (value) => value.trim())
    && areLocalizedDraftsEqual(textValues, originalTextValues, (value) => normalizeRichTextForRequest(value))
  const isSaveDisabled =
    !question || isSubmitting || !titleValues.ru.trim() || isRichTextEmpty(textValues[activeLanguage]) || isUnchanged

  const titleValue = titleValues[activeLanguage]
  const textValue = textValues[activeLanguage] || '<p></p>'

  if (!question) {
    return (
      <main className="page-content">
        <div className="page-content__columns">
          <div className="question-editor">
            <div className="question-editor__toolbar">
              <button className="browser__back" type="button" onClick={onBack}>
                <img src={arrowLeftIcon} alt="" aria-hidden="true" />
              </button>
            </div>
            <div className="browser__empty">Вопрос не найден</div>
          </div>
        </div>
      </main>
    )
  }

  return (
    <main className="page-content">
      <div className="page-content__columns">
        <div className="question-editor">
          <div className="question-editor__toolbar">
            <button className="browser__back" type="button" onClick={onBack} aria-label="Назад">
              <img src={arrowLeftIcon} alt="" aria-hidden="true" />
            </button>
            <span className="question-editor__title">
              {question.title[activeLanguage] ?? question.title.ru ?? question.name}
            </span>
          </div>

          {errorMessage && <div className="app-status app-status_error">{errorMessage}</div>}

          <div className="question-editor__form modal-form">
            <LanguageDropdown value={activeLanguage} onChange={setActiveLanguage} />
            <TextField
              label="Название вопроса"
              value={titleValue}
              onChange={(value) =>
                setTitleValues((current) => ({
                  ...current,
                  [activeLanguage]: value,
                }))
              }
              required={activeLanguage === 'ru'}
            />
            <div className="modal-form__field">
              <span className="modal-form__label">Текст вопроса</span>
              <RichTextEditor
                value={textValue}
                onChange={(value) =>
                  setTextValues((current) => ({
                    ...current,
                    [activeLanguage]: value,
                  }))
                }
              />
            </div>

            <div className="question-editor__footer">
              <button
                className="question-editor__delete"
                type="button"
                onClick={() => void onDelete(question.questionId)}
                disabled={isSubmitting}
              >
                Удалить
              </button>

              <div className="question-editor__actions modal-form__actions">
                <button
                  className="modal-form__button"
                  type="button"
                  disabled={isSaveDisabled}
                  onClick={() =>
                    void onSave(
                      question.questionId,
                      buildTitlePayload(titleValues),
                      buildTextPayload(textValues),
                    )
                  }
                >
                  Сохранить
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  )
}

function TextField({
  label,
  value,
  onChange,
  required = false,
}: {
  label: string
  value: string
  onChange: (value: string) => void
  required?: boolean
}) {
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

function LanguageDropdown({
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
    if (!isOpen) {
      return
    }

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
        onClick={() => setIsOpen((currentState) => !currentState)}
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

export default QuestionEditorPage
