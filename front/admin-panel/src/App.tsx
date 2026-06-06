import { useEffect, useMemo, useRef, useState } from 'react'
import './App.css'
import moonIcon from './assets/fi-rr-moon.svg'
import sunIcon from './assets/fi-rr-sun.svg'
import userIcon from './assets/fi-rr-user.svg'
import {
  clearAuthToken,
  createGroup,
  createQuestion,
  deleteGroup,
  deleteQuestion,
  fetchGroupQuestions,
  fetchUiMessages,
  getAuthToken,
  isUnauthorizedError,
  loginAdmin,
  mapGroupDto,
  mapQuestion,
  mapUiMessage,
  normalizeDisplayText,
  registerAdmin,
  setAuthToken,
  updateGroupTitle,
  updateQuestion,
  updateQuestionFiles,
  uploadFile,
  updateUiMessage as updateUiMessageRequest,
  type QuestionFileDto,
} from './api/adminApi'
import { useAppRouter, buildRoutePath } from './hooks/useAppRouter'
import { useGroupStore } from './hooks/useGroupStore'
import {
  useModalState,
  getModalTitle,
  buildTitlePayload,
  buildTextPayload,
  areLocalizedDraftsEqual,
  createLocalizedDraft,
} from './hooks/useModalState'
import AdminPage, { type BreadcrumbItem } from './pages/AdminPage'
import DashboardPage from './pages/DashboardPage'
import LoginPage from './pages/LoginPage'
import QuestionEditorPage from './pages/QuestionEditorPage'
import RegistrationPage from './pages/RegistrationPage'
import StatisticsPage from './pages/StatisticsPage'
import TechnicalQuestionsPage from './pages/TechnicalQuestionsPage'
import OverlayModal from './widget/OverlayModal'
import RichTextEditor from './widget/RichTextEditor'
import { FileAttachments } from './widget/FileAttachments'
import {
  ConfirmBody,
  LanguageDropdown,
  ModalActions,
  ReadOnlyField,
  TextField,
  TextAreaField,
} from './widget/ModalForm'
import { UiMessage } from './entities/models'

type Theme = 'light' | 'dark'

const THEME_STORAGE_KEY = 'admin-panel-theme'

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
    .replace(/<span\b([^>]*)class=(['"])([^'"]*\beditor-hidden\b[^'"]*)\2([^>]*)>/gi, '<span class="editor-hidden">')
    .replace(/<pre\b([^>]*)class=(['"])language-([^'"]+)\2([^>]*)>/gi, '<pre language="$3">')
    .replace(/<(?!\/?(?:b|i|code|s|u|pre|span)\b)[^>]+>/gi, '')
    .replace(/\n{3,}/g, '\n\n')
    .trim()

function getInitialTheme(): Theme {
  if (typeof window === 'undefined') return 'light'
  const saved = window.localStorage.getItem(THEME_STORAGE_KEY)
  if (saved === 'light' || saved === 'dark') return saved
  return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
}

function localizeTitle(value: Record<string, string> | undefined, fallback: string, langCode = 'ru') {
  return value?.[langCode] ?? value?.ru ?? value?.en ?? fallback
}

function getErrorMessage(error: unknown) {
  if (error instanceof Error) {
    const normalized = normalizeDisplayText(error.message)
    if (normalized) return normalized
  }
  return 'Не удалось выполнить запрос'
}

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(() => Boolean(getAuthToken()))
  const [theme, setTheme] = useState<Theme>(() => getInitialTheme())
  const [uiMessages, setUiMessages] = useState<UiMessage[]>([])
  const [isLoading, setIsLoading] = useState(() => Boolean(getAuthToken()))
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')
  const lastGroupNameRef = useRef<string | null>(null)
  const [modalFiles, setModalFiles] = useState<QuestionFileDto[]>([])
  const [isModalFileUploading, setIsModalFileUploading] = useState(false)
  const [modalFileError, setModalFileError] = useState('')

  const groupStore = useGroupStore()
  const { route, navigate } = useAppRouter(isAuthenticated)

  const modalState = useModalState(
    (name) => groupStore.getGroupsByName()[name] ?? null,
    (name) => uiMessages.find((m) => m.name === name) ?? null,
  )

  const currentGroupName =
    route.type === 'groups' ? route.groupName : route.type === 'question' ? route.groupName : null
  const effectiveGroupName = currentGroupName ?? groupStore.rootGroupName

  const currentGroup = useMemo(
    () => groupStore.toGroupModel(effectiveGroupName),
    [groupStore.groupsByName, effectiveGroupName],
  )

  const currentQuestion = useMemo(() => {
    if (route.type === 'question') {
      return groupStore.findQuestionByName(route.groupName, route.questionName)?.question ?? null
    }
    return null
  }, [groupStore.groupsByName, route])

  const breadcrumbs = useMemo((): BreadcrumbItem[] => {
    if (route.type !== 'groups' && route.type !== 'question') return []
    const group = effectiveGroupName ? groupStore.getGroupsByName()[effectiveGroupName] : null
    if (!group) return [{ label: 'Главное меню', groupName: null }]

    const items: BreadcrumbItem[] = [{ label: 'Главное меню', groupName: null }]
    for (const ancestorName of group.parents) {
      const ancestor = groupStore.getGroupsByName()[ancestorName]
      items.push({
        label: localizeTitle(ancestor?.title, ancestorName),
        groupName: ancestorName,
      })
    }
    if (group.parents.length > 0) {
      items.push({ label: localizeTitle(group.title, group.name), groupName: group.name })
    }
    return items
  }, [groupStore.groupsByName, effectiveGroupName, route.type])

  useEffect(() => {
    document.documentElement.dataset.theme = theme
    window.localStorage.setItem(THEME_STORAGE_KEY, theme)
  }, [theme])

  useEffect(() => {
    if (!isAuthenticated) {
      groupStore.resetStore()
      setUiMessages([])
      setIsLoading(false)
      return
    }
    void loadInitialData()
  }, [isAuthenticated])

  useEffect(() => {
    if (route.type === 'groups' && route.groupName) {
      lastGroupNameRef.current = route.groupName
    }
  }, [route])

  useEffect(() => {
    if (
      (route.type === 'groups' || route.type === 'question') &&
      groupStore.rootGroupName
    ) {
      const groupName = route.type === 'groups' ? route.groupName : route.groupName
      if (groupName) {
        void loadGroup(groupName)
      }
    }
  }, [groupStore.rootGroupName, route])

  async function loadGroup(groupName: string) {
    try {
      await groupStore.ensureGroupLoaded(groupName)
      const node = groupStore.getGroupsByName()[groupName]
      if (node) {
        await groupStore.ensureAncestorsLoaded(node.parents)
      }
    } catch (error) {
      handleApiError(error)
    }
  }

  async function loadInitialData() {
    setErrorMessage('')
    setIsLoading(true)
    try {
      const [uiMsgs] = await Promise.all([
        fetchUiMessages(),
        groupStore.ensureRootLoaded(),
      ])
      setUiMessages(uiMsgs)
    } catch (error) {
      handleApiError(error)
    } finally {
      setIsLoading(false)
    }
  }

  function logout() {
    clearAuthToken()
    setIsAuthenticated(false)
    setErrorMessage('')
    setIsSubmitting(false)
    groupStore.resetStore()
    setUiMessages([])
    const loginRoute = { type: 'login' } as const
    window.history.replaceState(null, '', buildRoutePath(loginRoute))
    navigate(loginRoute)
  }

  function handleApiError(error: unknown) {
    if (isUnauthorizedError(error)) {
      logout()
      return true
    }
    setErrorMessage(getErrorMessage(error))
    return false
  }

  async function handleLogin(login: string, password: string) {
    setErrorMessage('')
    setIsSubmitting(true)
    clearAuthToken()
    try {
      const response = await loginAdmin(login, password)
      setAuthToken(response.token)
      const dashboardRoute = { type: 'dashboard' } as const
      window.history.replaceState(null, '', buildRoutePath(dashboardRoute))
      navigate(dashboardRoute)
      setIsAuthenticated(true)
    } catch (error) {
      setErrorMessage(getErrorMessage(error))
    } finally {
      setIsSubmitting(false)
    }
  }

  async function handleRegisterAdmin(login: string, password: string) {
    setErrorMessage('')
    setIsSubmitting(true)
    try {
      await registerAdmin(login, password)
    } catch (error) {
      handleApiError(error)
    } finally {
      setIsSubmitting(false)
    }
  }

  async function handleSaveQuestion(
    _questionId: string,
    title: Record<string, string>,
    text: Record<string, string>,
    fileHashes: string[],
  ) {
    if (route.type !== 'question') return
    const lookup = groupStore.findQuestionByName(route.groupName, route.questionName)
    if (!lookup) {
      setErrorMessage('Вопрос не найден')
      return
    }

    setErrorMessage('')
    setIsSubmitting(true)
    try {
      const effectiveId = lookup.question.questionId
      const [updated] = await Promise.all([
        updateQuestion(effectiveId, title, text).then(mapQuestion),
        updateQuestionFiles(effectiveId, fileHashes),
      ])
      groupStore.updateQuestionInCache(effectiveId, updated)
      if (route.questionName !== updated.name) {
        navigate({ type: 'question', groupName: route.groupName, questionName: updated.name }, { replace: true })
      }
    } catch (error) {
      handleApiError(error)
    } finally {
      setIsSubmitting(false)
    }
  }

  async function handleDeleteQuestionFromPage(_questionId: string) {
    if (route.type !== 'question') return
    const lookup = groupStore.findQuestionByName(route.groupName, route.questionName)
    if (!lookup) {
      navigate({ type: 'groups', groupName: lastGroupNameRef.current }, { replace: true })
      return
    }

    setErrorMessage('')
    setIsSubmitting(true)
    try {
      await deleteQuestion(lookup.question.questionId)
      groupStore.removeQuestionFromCache(lookup.question.questionId)
      navigate({ type: 'groups', groupName: lastGroupNameRef.current }, { replace: true })
    } catch (error) {
      handleApiError(error)
    } finally {
      setIsSubmitting(false)
    }
  }

  async function handleConfirm() {
    const { modal, titleValues, textValues, textValue, closeModal } = modalState
    if (!modal) return

    const titles = buildTitlePayload(titleValues)
    const texts = buildTextPayload(textValues, normalizeRichTextForRequest, isRichTextEmpty)
    const requiredRussianTitle = titleValues.ru.trim()
    setErrorMessage('')
    setIsSubmitting(true)

    try {
      switch (modal.type) {
        case 'edit-group': {
          if (!requiredRussianTitle) return
          const updatedGroup = mapGroupDto(await updateGroupTitle(modal.groupName, titles))
          groupStore.renameGroupInCache(modal.groupName, updatedGroup)
          if (groupStore.getRootGroupName() === modal.groupName) {
            groupStore.resetStore()
            await groupStore.ensureRootLoaded()
          }
          if (route.type === 'groups' && route.groupName === modal.groupName) {
            navigate({ type: 'groups', groupName: updatedGroup.name }, { replace: true })
          }
          closeModal()
          break
        }
        case 'create-group': {
          if (!requiredRussianTitle) return
          const createdGroup = mapGroupDto(await createGroup(modal.groupName, titles))
          groupStore.addGroupToCache(modal.groupName, createdGroup)
          closeModal()
          break
        }
        case 'create-question': {
          if (!requiredRussianTitle) return
          const createdDto = await createQuestion(modal.groupName, titles, texts)
          const fileHashes = modalFiles.map((f) => f.fileHash)
          if (fileHashes.length > 0 && createdDto.questionId) {
            await updateQuestionFiles(createdDto.questionId, fileHashes)
          }
          closeModal()
          const freshQuestions = await fetchGroupQuestions(modal.groupName)
          groupStore.replaceGroupQuestions(modal.groupName, freshQuestions)
          break
        }
        case 'delete-group': {
          await deleteGroup(modal.groupName)
          const deletedGroup = groupStore.getGroupsByName()[modal.groupName]
          groupStore.removeGroupFromCache(modal.groupName)
          if (route.type === 'groups' && route.groupName === modal.groupName) {
            navigate(
              { type: 'groups', groupName: deletedGroup?.parentName ?? null },
              { replace: true },
            )
          }
          closeModal()
          break
        }
        case 'delete-question': {
          const lookup = groupStore.findQuestionById(modal.questionId)
          if (!lookup) return
          await deleteQuestion(lookup.question.questionId)
          groupStore.removeQuestionFromCache(modal.questionId)
          closeModal()
          break
        }
        case 'edit-ui-message': {
          if (isRichTextEmpty(textValue)) return
          const updatedMsg = mapUiMessage(await updateUiMessageRequest(modal.messageName, texts))
          setUiMessages((current) =>
            current.map((m) => (m.name === modal.messageName ? updatedMsg : new UiMessage(m))),
          )
          closeModal()
          break
        }
      }
    } catch (error) {
      handleApiError(error)
    } finally {
      setIsSubmitting(false)
    }
  }

  const {
    modal,
    languageCode,
    setLanguageCode,
    titleValue,
    textValue,
    currentUiMessage,
    openModal,
    closeModal: closeModalBase,
    setTitleValue,
    setTextValue,
    titleValues,
    textValues,
  } = modalState

  function closeModal() {
    if (!isSubmitting) {
      closeModalBase()
      setModalFiles([])
      setModalFileError('')
    }
  }

  async function handleModalFileSelect(file: File) {
    setModalFileError('')
    setIsModalFileUploading(true)
    try {
      const uploaded = await uploadFile(file)
      setModalFiles((current) => [...current, uploaded])
    } catch {
      setModalFileError('Не удалось загрузить файл')
    } finally {
      setIsModalFileUploading(false)
    }
  }

  function handleModalFileRemove(fileHash: string) {
    setModalFiles((current) => current.filter((f) => f.fileHash !== fileHash))
  }

  const themeIcon = theme === 'light' ? sunIcon : moonIcon
  const themeLabel = theme === 'light' ? 'Переключить на тёмную тему' : 'Переключить на светлую тему'
  const isTranslationRoute =
    route.type === 'dashboard' ||
    route.type === 'technical-questions' ||
    route.type === 'groups' ||
    route.type === 'question'

  const currentUiMessageDescription =
    currentUiMessage?.description[languageCode] ??
    currentUiMessage?.description.ru ??
    currentUiMessage?.description.en ??
    ''

  const isEditGroupUnchanged =
    modal?.type === 'edit-group' &&
    areLocalizedDraftsEqual(
      titleValues,
      createLocalizedDraft(groupStore.getGroupsByName()[modal.groupName]?.title),
      (v) => v.trim(),
    )

  const isEditUiMessageUnchanged =
    modal?.type === 'edit-ui-message' &&
    areLocalizedDraftsEqual(
      textValues,
      createLocalizedDraft(currentUiMessage?.text),
      normalizeRichTextForRequest,
    )

  const isConfirmDisabled =
    isSubmitting ||
    (modal?.type === 'edit-group' && isEditGroupUnchanged) ||
    (modal?.type === 'edit-ui-message' && isEditUiMessageUnchanged) ||
    ((modal?.type === 'create-group' || modal?.type === 'create-question') && !titleValues.ru.trim()) ||
    (modal?.type === 'edit-group' && !titleValues.ru.trim()) ||
    (modal?.type === 'edit-ui-message' && isRichTextEmpty(textValue))

  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="topbar__title">Админская панель</div>

        {route.type !== 'login' && (
          <div className="topbar__nav">
            <button
              className={`topbar__nav-link ${isTranslationRoute ? 'topbar__nav-link_active' : ''}`}
              type="button"
              onClick={() => navigate({ type: 'dashboard' })}
            >
              Перевод
            </button>
            <button
              className={`topbar__nav-link ${route.type === 'statistics' ? 'topbar__nav-link_active' : ''}`}
              type="button"
              onClick={() => navigate({ type: 'statistics' })}
            >
              Статистика
            </button>
            <button
              className={`topbar__nav-link ${route.type === 'registration' ? 'topbar__nav-link_active' : ''}`}
              type="button"
              onClick={() => navigate({ type: 'registration' })}
            >
              Регистрация
            </button>
          </div>
        )}

        <div className="topbar__actions">
          <button
            className="topbar__button"
            type="button"
            onClick={() => setTheme((t) => (t === 'light' ? 'dark' : 'light'))}
            aria-label={themeLabel}
            title={themeLabel}
          >
            <img src={themeIcon} alt="" aria-hidden="true" />
          </button>

          {route.type !== 'login' && (
            <button
              className="topbar__profile"
              type="button"
              aria-label="Выход из профиля"
              onClick={logout}
            >
              <img src={userIcon} alt="" aria-hidden="true" />
              <span>Выход из профиля</span>
            </button>
          )}
        </div>
      </header>

      {route.type !== 'question' && route.type !== 'login' && errorMessage && (
        <div className="app-status app-status_error">{errorMessage}</div>
      )}

      {route.type === 'login' && (
        <LoginPage
          errorMessage={errorMessage}
          isSubmitting={isSubmitting}
          onSubmit={handleLogin}
        />
      )}

      {!isLoading && route.type === 'dashboard' && (
        <DashboardPage
          onOpenTechnicalQuestions={() => navigate({ type: 'technical-questions' })}
          onOpenGroups={() => navigate({ type: 'groups', groupName: null })}
        />
      )}

      {!isLoading && route.type === 'statistics' && <StatisticsPage langCode="ru" />}

      {!isLoading && route.type === 'registration' && (
        <RegistrationPage
          errorMessage={errorMessage}
          isSubmitting={isSubmitting}
          onSubmit={handleRegisterAdmin}
        />
      )}

      {!isLoading && route.type === 'technical-questions' && (
        <TechnicalQuestionsPage
          uiMessages={uiMessages}
          langCode="ru"
          onBack={() => navigate({ type: 'dashboard' })}
          onEditUiMessage={(messageName) => openModal({ type: 'edit-ui-message', messageName })}
        />
      )}

      {!isLoading && route.type === 'groups' && (
        <AdminPage
          currentGroup={currentGroup}
          langCode="ru"
          breadcrumbs={breadcrumbs}
          onGoHome={() => navigate({ type: 'dashboard' })}
          onNavigateToGroup={(groupName) => navigate({ type: 'groups', groupName })}
          onOpenGroup={(groupName) => navigate({ type: 'groups', groupName })}
          onOpenQuestion={(questionName, groupName) =>
            navigate({ type: 'question', groupName, questionName })
          }
          onGoBack={() => {
            const group = effectiveGroupName ? groupStore.getGroupsByName()[effectiveGroupName] : null
            const parentName = group?.parentName ?? null
            if (!parentName) {
              navigate({ type: 'dashboard' })
            } else {
              navigate({ type: 'groups', groupName: parentName })
            }
          }}
          onEditGroup={(groupName) => openModal({ type: 'edit-group', groupName })}
          onCreateGroup={(groupName) => openModal({ type: 'create-group', groupName })}
          onCreateQuestion={(groupName) => openModal({ type: 'create-question', groupName })}
          onDeleteGroup={(groupName) => openModal({ type: 'delete-group', groupName })}
          onDeleteQuestion={(questionId) => openModal({ type: 'delete-question', questionId })}
        />
      )}

      {!isLoading && route.type === 'question' && (
        <QuestionEditorPage
          question={currentQuestion}
          langCode="ru"
          isSubmitting={isSubmitting}
          errorMessage={errorMessage}
          onBack={() =>
            navigate({ type: 'groups', groupName: lastGroupNameRef.current ?? route.groupName })
          }
          onSave={handleSaveQuestion}
          onDelete={handleDeleteQuestionFromPage}
        />
      )}

      {modal && (
        <OverlayModal title={getModalTitle(modal)} onClose={closeModal}>
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
              <ModalActions onConfirm={handleConfirm} disabled={isConfirmDisabled} />
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
              <ModalActions onConfirm={handleConfirm} disabled={isConfirmDisabled} />
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
              <div className="modal-form__field">
                <span className="modal-form__label">Файлы</span>
                <FileAttachments
                  files={modalFiles}
                  isUploading={isModalFileUploading}
                  isDisabled={isSubmitting}
                  uploadError={modalFileError}
                  onFileSelect={handleModalFileSelect}
                  onRemove={handleModalFileRemove}
                />
              </div>
              <ModalActions onConfirm={handleConfirm} disabled={isConfirmDisabled || isModalFileUploading} />
            </div>
          )}
        </OverlayModal>
      )}

      {(isLoading || isSubmitting) && (
        <div className="app-loading-overlay" role="status" aria-live="polite" aria-busy="true">
          <div className="app-loading-indicator">
            <span className="app-loading-indicator__spinner" aria-hidden="true" />
            <span className="app-loading-indicator__label">
              {isLoading ? 'Загрузка данных...' : 'Сохранение изменений...'}
            </span>
          </div>
        </div>
      )}
    </div>
  )
}

export default App
