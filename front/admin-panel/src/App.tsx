import { useEffect, useMemo, useRef, useState } from 'react'
import './App.css'
import moonIcon from './assets/fi-rr-moon.svg'
import sunIcon from './assets/fi-rr-sun.svg'
import userIcon from './assets/fi-rr-user.svg'
import globeIcon from './assets/fi-rr-globe.svg'
import {
  AVAILABLE_LANGUAGES,
  AVAILABLE_LANGUAGE_CODES,
  Group as GroupModel,
  Question,
  UiMessage,
  type AvailableLanguageCode,
  type LocalizedText,
} from './entities/models'
import AdminPage from './pages/AdminPage'
import OverlayModal from './widget/OverlayModal'
import RichTextEditor from './widget/RichTextEditor'

type Theme = 'light' | 'dark'
type LanguageCode = AvailableLanguageCode
type LocalizedDraft = Record<LanguageCode, string>
type ModalState =
  | { type: 'edit-group'; groupName: string }
  | { type: 'select-add-child'; groupName: string }
  | { type: 'create-group'; groupName: string }
  | { type: 'create-question'; groupName: string }
  | { type: 'delete-group'; groupName: string }
  | { type: 'edit-question'; questionId: string }
  | { type: 'delete-question'; questionId: string }
  | { type: 'edit-ui-message'; messageName: string }
  | null

const initialGroups = [
  new GroupModel({
    name: 'admin',
    title: { ru: 'Администрирование', en: 'Administration' },
    parentName: '',
    innerGroups: [
      new GroupModel({
        name: 'users',
        title: { ru: 'Пользователи', en: 'Users' },
        parentName: 'admin',
        questions: [
          new Question({
            questionId: 'active-users',
            name: 'active-users',
            parent: 'users',
            title: { ru: 'Активные пользователи', en: 'Active users' },
            text: { ru: '<p>Список активных пользователей.</p>' },
          }),
        ],
      }),
      new GroupModel({
        name: 'content',
        title: { ru: 'Контент', en: 'Content' },
        parentName: 'admin',
      }),
    ],
    questions: [
      new Question({
        questionId: 'settings',
        name: 'settings',
        parent: 'admin',
        title: { ru: 'Настройки панели', en: 'Panel settings' },
        text: { ru: '<p>Общие настройки панели.</p>' },
      }),
    ],
  }),
  new GroupModel({
    name: 'analytics',
    title: { ru: 'Аналитика', en: 'Analytics' },
    parentName: '',
    questions: [
      new Question({
        questionId: 'weekly-report',
        name: 'weekly-report',
        parent: 'analytics',
        title: { ru: 'Недельный отчёт', en: 'Weekly report' },
        text: { ru: '<p>Еженедельный отчёт по проекту.</p>' },
      }),
    ],
  }),
]

const initialUiMessages = [
  new UiMessage({
    name: 'unknown-command',
    description: {
      ru: 'Показывается пользователю, когда бот не распознал введённую команду.',
      en: 'Shown to the user when the bot cannot recognize the entered command.',
    },
    text: { ru: 'Неизвестная команда', en: 'Unknown command' },
  }),
  new UiMessage({
    name: 'internal-error',
    description: {
      ru: 'Показывается пользователю, если во время обработки произошла внутренняя ошибка.',
      en: 'Shown to the user if an internal error occurs during processing.',
    },
    text: { ru: 'Внутренняя ошибка', en: 'Internal error' },
  }),
]

const slugify = (value: string) =>
  value
    .trim()
    .toLowerCase()
    .replace(/[^\p{L}\p{N}]+/gu, '-')
    .replace(/^-+|-+$/g, '')

const cloneGroups = (groups: GroupModel[]) => groups.map((group) => new GroupModel(group))

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

const buildTitlePayload = (draft: LocalizedDraft): LocalizedText => {
  const next: LocalizedText = {}

  for (const code of AVAILABLE_LANGUAGE_CODES) {
    const value = draft[code].trim()
    if (value) {
      next[code] = value
    }
  }

  return next
}

const buildTextPayload = (draft: LocalizedDraft): LocalizedText => {
  const next: LocalizedText = {}

  for (const code of AVAILABLE_LANGUAGE_CODES) {
    const value = draft[code]
    if (!isRichTextEmpty(value)) {
      next[code] = value
    }
  }

  return next
}

function updateGroupByName(
  groups: GroupModel[],
  groupName: string,
  updater: (group: GroupModel) => GroupModel,
): GroupModel[] {
  return groups.map((group) => {
    if (group.name === groupName) {
      return updater(new GroupModel(group))
    }

    return new GroupModel({
      ...group,
      innerGroups: updateGroupByName(group.innerGroups, groupName, updater),
    })
  })
}

function removeGroupByName(groups: GroupModel[], groupName: string): GroupModel[] {
  return groups
    .filter((group) => group.name !== groupName)
    .map(
      (group) =>
        new GroupModel({
          ...group,
          innerGroups: removeGroupByName(group.innerGroups, groupName),
        }),
    )
}

function updateQuestionById(
  groups: GroupModel[],
  questionId: string,
  updater: (question: Question) => Question,
): GroupModel[] {
  return groups.map(
    (group) =>
      new GroupModel({
        ...group,
        innerGroups: updateQuestionById(group.innerGroups, questionId, updater),
        questions: group.questions.map((question) =>
          question.questionId === questionId ? updater(new Question(question)) : new Question(question),
        ),
      }),
  )
}

function removeQuestionById(groups: GroupModel[], questionId: string): GroupModel[] {
  return groups.map(
    (group) =>
      new GroupModel({
        ...group,
        innerGroups: removeQuestionById(group.innerGroups, questionId),
        questions: group.questions
          .filter((question) => question.questionId !== questionId)
          .map((question) => new Question(question)),
      }),
  )
}

function findGroup(groups: GroupModel[], groupName: string): GroupModel | null {
  for (const group of groups) {
    if (group.name === groupName) {
      return group
    }

    const nested = findGroup(group.innerGroups, groupName)
    if (nested) {
      return nested
    }
  }

  return null
}

function findQuestion(groups: GroupModel[], questionId: string): Question | null {
  for (const group of groups) {
    const found = group.questions.find((question) => question.questionId === questionId)
    if (found) {
      return found
    }

    const nested = findQuestion(group.innerGroups, questionId)
    if (nested) {
      return nested
    }
  }

  return null
}

function App() {
  const [theme, setTheme] = useState<Theme>('light')
  const [groups, setGroups] = useState<GroupModel[]>(() => cloneGroups(initialGroups))
  const [uiMessages, setUiMessages] = useState<UiMessage[]>(() =>
    initialUiMessages.map((item) => new UiMessage(item)),
  )
  const [modal, setModal] = useState<ModalState>(null)
  const [languageCode, setLanguageCode] = useState<LanguageCode>('ru')
  const [titleValues, setTitleValues] = useState<LocalizedDraft>(() => createEmptyLocalizedDraft())
  const [textValues, setTextValues] = useState<LocalizedDraft>(() => createEmptyLocalizedDraft())

  useEffect(() => {
    document.documentElement.dataset.theme = theme
  }, [theme])

  const currentGroup = useMemo(
    () => (modal && 'groupName' in modal ? findGroup(groups, modal.groupName) : null),
    [groups, modal],
  )
  const currentQuestion = useMemo(
    () => (modal && 'questionId' in modal ? findQuestion(groups, modal.questionId) : null),
    [groups, modal],
  )
  const currentUiMessage = useMemo(
    () =>
      modal && 'messageName' in modal
        ? uiMessages.find((item) => item.name === modal.messageName) ?? null
        : null,
    [modal, uiMessages],
  )

  useEffect(() => {
    if (!modal) {
      return
    }

    setLanguageCode('ru')

    switch (modal.type) {
      case 'edit-group':
        setTitleValues(createLocalizedDraft(currentGroup?.title))
        setTextValues(createEmptyLocalizedDraft())
        break
      case 'edit-question':
        setTitleValues(createLocalizedDraft(currentQuestion?.title))
        setTextValues(createLocalizedDraft(currentQuestion?.text))
        break
      case 'edit-ui-message':
        setTitleValues(createEmptyLocalizedDraft())
        setTextValues(createLocalizedDraft(currentUiMessage?.text))
        break
      case 'create-group':
        setTitleValues(createEmptyLocalizedDraft())
        setTextValues(createEmptyLocalizedDraft())
        break
      case 'create-question':
        setTitleValues(createEmptyLocalizedDraft())
        setTextValues({
          ru: '<p></p>',
          en: '<p></p>',
        })
        break
      default:
        setTitleValues(createEmptyLocalizedDraft())
        setTextValues(createEmptyLocalizedDraft())
        break
    }
  }, [currentGroup, currentQuestion, currentUiMessage, modal])

  const closeModal = () => setModal(null)

  const updateUiMessage = (messageName: string, updater: (item: UiMessage) => UiMessage) => {
    setUiMessages((current) =>
      current.map((item) =>
        item.name === messageName ? updater(new UiMessage(item)) : new UiMessage(item),
      ),
    )
  }

  const titleValue = titleValues[languageCode]
  const textValue = textValues[languageCode] || '<p></p>'
  const currentUiMessageDescription =
    currentUiMessage?.description[languageCode] ??
    currentUiMessage?.description.ru ??
    currentUiMessage?.description.en ??
    ''

  const setTitleValue = (value: string) =>
    setTitleValues((current) => ({
      ...current,
      [languageCode]: value,
    }))

  const setTextValue = (value: string) =>
    setTextValues((current) => ({
      ...current,
      [languageCode]: value,
    }))

  const handleConfirm = () => {
    if (!modal) {
      return
    }

    const titles = buildTitlePayload(titleValues)
    const texts = buildTextPayload(textValues)
    const requiredRussianTitle = titleValues.ru.trim()

    switch (modal.type) {
      case 'edit-group':
        if (!requiredRussianTitle) return
        setGroups((current) =>
          updateGroupByName(current, modal.groupName, (group) =>
            new GroupModel({
              ...group,
              title: { ...group.title, ...titles },
            }),
          ),
        )
        closeModal()
        return
      case 'create-group':
        if (!requiredRussianTitle) return
        setGroups((current) =>
          updateGroupByName(current, modal.groupName, (group) =>
            new GroupModel({
              ...group,
              innerGroups: [
                ...group.innerGroups,
                new GroupModel({
                  name: slugify(requiredRussianTitle) || `group-${Date.now()}`,
                  parentName: group.name,
                  title: titles,
                }),
              ],
            }),
          ),
        )
        closeModal()
        return
      case 'create-question':
        if (!requiredRussianTitle) return
        setGroups((current) =>
          updateGroupByName(current, modal.groupName, (group) =>
            new GroupModel({
              ...group,
              questions: [
                ...group.questions,
                new Question({
                  questionId: slugify(requiredRussianTitle) || `question-${Date.now()}`,
                  name: slugify(requiredRussianTitle) || `question-${Date.now()}`,
                  parent: group.name,
                  title: titles,
                  text: texts,
                }),
              ],
            }),
          ),
        )
        closeModal()
        return
      case 'delete-group':
        setGroups((current) => removeGroupByName(current, modal.groupName))
        closeModal()
        return
      case 'edit-question':
        if (!requiredRussianTitle) return
        setGroups((current) =>
          updateQuestionById(current, modal.questionId, (question) =>
            new Question({
              ...question,
              title: { ...question.title, ...titles },
              text: { ...question.text, ...texts },
            }),
          ),
        )
        closeModal()
        return
      case 'delete-question':
        setGroups((current) => removeQuestionById(current, modal.questionId))
        closeModal()
        return
      case 'edit-ui-message':
        if (isRichTextEmpty(textValue)) return
        updateUiMessage(modal.messageName, (message) =>
          new UiMessage({
            ...message,
            text: { ...message.text, ...texts },
          }),
        )
        closeModal()
        return
      default:
        return
    }
  }

  const themeIcon = theme === 'light' ? sunIcon : moonIcon
  const themeLabel =
    theme === 'light' ? 'Переключить на тёмную тему' : 'Переключить на светлую тему'

  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="topbar__title">Админская панель</div>

        <div className="topbar__actions">
          <button
            className="topbar__button"
            type="button"
            onClick={() => setTheme((current) => (current === 'light' ? 'dark' : 'light'))}
            aria-label={themeLabel}
            title={themeLabel}
          >
            <img src={themeIcon} alt="" aria-hidden="true" />
          </button>

          <button className="topbar__profile" type="button" aria-label="Профиль">
            <img src={userIcon} alt="" aria-hidden="true" />
            <span>Профиль</span>
          </button>
        </div>
      </header>

      <AdminPage
        groups={groups}
        uiMessages={uiMessages}
        langCode="ru"
        onEditGroup={(groupName) => setModal({ type: 'edit-group', groupName })}
        onAddChild={(groupName) => setModal({ type: 'select-add-child', groupName })}
        onDeleteGroup={(groupName) => setModal({ type: 'delete-group', groupName })}
        onEditQuestion={(questionId) => setModal({ type: 'edit-question', questionId })}
        onDeleteQuestion={(questionId) => setModal({ type: 'delete-question', questionId })}
        onEditUiMessage={(messageName) => setModal({ type: 'edit-ui-message', messageName })}
      />

      {modal && (
        <OverlayModal
          title={getModalTitle(modal)}
          onClose={closeModal}
          compact={modal.type === 'select-add-child'}
        >
          {modal.type === 'select-add-child' && (
            <div className="modal-form modal-form_compact">
              <div className="modal-form__choice-grid modal-form__choice-grid_compact">
                <button
                  className="modal-form__choice"
                  type="button"
                  onClick={() => setModal({ type: 'create-group', groupName: modal.groupName })}
                >
                  Группу
                </button>
                <button
                  className="modal-form__choice"
                  type="button"
                  onClick={() => setModal({ type: 'create-question', groupName: modal.groupName })}
                >
                  Вопрос
                </button>
              </div>
            </div>
          )}

          {modal.type === 'delete-group' && (
            <ConfirmBody text="Точно ли вы хотите удалить эту группу?" onConfirm={handleConfirm} />
          )}

          {modal.type === 'delete-question' && (
            <ConfirmBody text="Точно ли вы хотите удалить этот вопрос?" onConfirm={handleConfirm} />
          )}

          {(modal.type === 'edit-group' || modal.type === 'create-group') && (
            <div className="modal-form">
              <LanguageDropdown value={languageCode} onChange={setLanguageCode} />
              <TextField
                label="Название группы"
                value={titleValue}
                onChange={setTitleValue}
                required={languageCode === 'ru'}
              />
              <ModalActions onConfirm={handleConfirm} />
            </div>
          )}

          {modal.type === 'edit-ui-message' && (
            <div className="modal-form">
              <LanguageDropdown value={languageCode} onChange={setLanguageCode} />
              <ReadOnlyField label="Описание" value={currentUiMessageDescription} />
              <TextAreaField
                label="Текст"
                value={textValues[languageCode]}
                onChange={setTextValue}
                required={languageCode === 'ru'}
              />
              <ModalActions onConfirm={handleConfirm} />
            </div>
          )}

          {modal.type === 'create-question' && (
            <div className="modal-form">
              <LanguageDropdown value={languageCode} onChange={setLanguageCode} />
              <TextField
                label="Название вопроса"
                value={titleValue}
                onChange={setTitleValue}
                required={languageCode === 'ru'}
              />
              <div className="modal-form__field">
                <span className="modal-form__label">Текст вопроса</span>
                <RichTextEditor value={textValue} onChange={setTextValue} />
              </div>
              <ModalActions onConfirm={handleConfirm} />
            </div>
          )}

          {modal.type === 'edit-question' && (
            <div className="modal-form">
              <LanguageDropdown value={languageCode} onChange={setLanguageCode} />
              <TextField
                label="Название вопроса"
                value={titleValue}
                onChange={setTitleValue}
                required={languageCode === 'ru'}
              />
              <div className="modal-form__field">
                <span className="modal-form__label">Текст вопроса</span>
                <RichTextEditor value={textValue} onChange={setTextValue} />
              </div>
              <ModalActions onConfirm={handleConfirm} />
            </div>
          )}
        </OverlayModal>
      )}
    </div>
  )
}

type TextFieldProps = {
  label: string
  value: string
  onChange: (value: string) => void
  required?: boolean
}

function TextField({ label, value, onChange, required = false }: TextFieldProps) {
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

function TextAreaField({ label, value, onChange, required = false }: TextFieldProps) {
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

function ReadOnlyField({ label, value }: { label: string; value: string }) {
  return (
    <div className="modal-form__field">
      <span className="modal-form__label">{label}</span>
      <div className="modal-form__readonly">{value}</div>
    </div>
  )
}

function LanguageDropdown({
  value,
  onChange,
}: {
  value: LanguageCode
  onChange: (value: LanguageCode) => void
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
                onChange(language.code as LanguageCode)
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

function ModalActions({ onConfirm }: { onConfirm: () => void }) {
  return (
    <div className="modal-form__actions">
      <button className="modal-form__button" type="button" onClick={onConfirm}>
        Подтвердить
      </button>
    </div>
  )
}

function ConfirmBody({ text, onConfirm }: { text: string; onConfirm: () => void }) {
  return (
    <div className="modal-form">
      <p className="modal-form__text">{text}</p>
      <ModalActions onConfirm={onConfirm} />
    </div>
  )
}

function getModalTitle(modal: Exclude<ModalState, null>) {
  switch (modal.type) {
    case 'edit-group':
      return 'Изменить группу'
    case 'select-add-child':
      return 'Что вы хотите добавить?'
    case 'create-group':
      return 'Создать группу'
    case 'create-question':
      return 'Создать вопрос'
    case 'delete-group':
      return 'Удаление группы'
    case 'edit-question':
      return 'Изменить вопрос'
    case 'delete-question':
      return 'Удаление вопроса'
    case 'edit-ui-message':
      return 'Изменить технический вопрос'
  }
}

export default App
