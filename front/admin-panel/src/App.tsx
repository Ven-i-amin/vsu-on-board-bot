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
import {
  clearAuthToken,
  createGroup,
  createQuestion,
  deleteGroup,
  deleteQuestion,
  fetchAdminData,
  fetchGroup,
  fetchInnerGroups,
  fetchStartGroup,
  getAuthToken,
  isUnauthorizedError,
  loginAdmin,
  mapGroupDto,
  mapQuestion,
  mapUiMessage,
  registerAdmin,
  setAuthToken,
  updateGroupTitle,
  updateQuestion,
  updateUiMessage as updateUiMessageRequest,
  type GroupNode,
} from './api/adminApi'
import AdminPage, { type BreadcrumbItem } from './pages/AdminPage'
import DashboardPage from './pages/DashboardPage'
import LoginPage from './pages/LoginPage'
import QuestionEditorPage from './pages/QuestionEditorPage'
import RegistrationPage from './pages/RegistrationPage'
import StatisticsPage from './pages/StatisticsPage'
import TechnicalQuestionsPage from './pages/TechnicalQuestionsPage'
import OverlayModal from './widget/OverlayModal'
import RichTextEditor from './widget/RichTextEditor'

type Theme = 'light' | 'dark'
type LanguageCode = AvailableLanguageCode
type LocalizedDraft = Record<LanguageCode, string>
type AppRoute =
  | { type: 'login' }
  | { type: 'dashboard' }
  | { type: 'statistics' }
  | { type: 'registration' }
  | { type: 'technical-questions' }
  | { type: 'groups'; path: string[] }
  | { type: 'question'; path: string[]; questionName: string }
type ModalState =
  | { type: 'edit-group'; groupName: string }
  | { type: 'create-group'; groupName: string }
  | { type: 'create-question'; groupName: string }
  | { type: 'delete-group'; groupName: string }
  | { type: 'delete-question'; questionId: string }
  | { type: 'edit-ui-message'; messageName: string }
  | null

type CachedGroupMap = Record<string, GroupNode>
type QuestionLookup = {
  question: Question
  groupName: string
} | null

const THEME_STORAGE_KEY = 'admin-panel-theme'

const slugify = (value: string) =>
  value
    .trim()
    .toLowerCase()
    .replace(/[^\p{L}\p{N}]+/gu, '-')
    .replace(/^-+|-+$/g, '')

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
      next[code] = normalizeRichTextForRequest(value)
    }
  }

  return next
}

const areLocalizedDraftsEqual = (
  left: LocalizedDraft,
  right: LocalizedDraft,
  normalizer: (value: string) => string = (value) => value,
) =>
  AVAILABLE_LANGUAGE_CODES.every((code) => normalizer(left[code]) === normalizer(right[code]))

function getInitialTheme(): Theme {
  if (typeof window === 'undefined') {
    return 'light'
  }

  const savedTheme = window.localStorage.getItem(THEME_STORAGE_KEY)
  if (savedTheme === 'light' || savedTheme === 'dark') {
    return savedTheme
  }

  return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
}

function parseRoute(pathname: string): AppRoute {
  const normalizedPathname =
    pathname.length > 1 && pathname.endsWith('/') ? pathname.slice(0, -1) : pathname
  const segments = normalizedPathname.split('/').filter(Boolean).map(decodeURIComponent)

  if (!segments.length) {
    return { type: 'dashboard' }
  }

  if (segments[0] === 'login') {
    return { type: 'login' }
  }

  if (segments[0] === 'statistics') {
    return { type: 'statistics' }
  }

  if (segments[0] === 'registration') {
    return { type: 'registration' }
  }

  if (segments[0] === 'technical-questions') {
    return { type: 'technical-questions' }
  }

  if (segments[0] === 'groups') {
    return { type: 'groups', path: segments.slice(1) }
  }

  return { type: 'dashboard' }
}

function buildRoutePath(route: AppRoute) {
  switch (route.type) {
    case 'login':
      return '/login'
    case 'dashboard':
      return '/'
    case 'statistics':
      return '/statistics'
    case 'registration':
      return '/registration'
    case 'technical-questions':
      return '/technical-questions'
    case 'groups':
      return route.path.length
        ? `/groups/${route.path.map(encodeURIComponent).join('/')}`
        : '/groups'
    case 'question':
      return `/groups/${[...route.path, route.questionName].map(encodeURIComponent).join('/')}`
  }
}

function normalizeRoute(route: AppRoute, isAuthenticated: boolean): AppRoute {
  if (!isAuthenticated) {
    return { type: 'login' }
  }

  if (route.type === 'login') {
    return { type: 'dashboard' }
  }

  return route
}

function localizeTitle(value: LocalizedText | undefined, fallback: string, languageCode = 'ru') {
  return value?.[languageCode] ?? value?.ru ?? value?.en ?? fallback
}

function toGroupModel(node: GroupNode | null, groupsByName: CachedGroupMap): GroupModel | null {
  if (!node) {
    return null
  }

  return new GroupModel({
    name: node.name,
    title: node.title,
    parentName: node.parentName,
    innerGroups: node.childrenNames
      .map((childName) => groupsByName[childName])
      .filter((child): child is GroupNode => Boolean(child))
      .map(
        (child) =>
          new GroupModel({
            name: child.name,
            title: child.title,
            parentName: child.parentName,
            innerGroups: [],
            questions: [],
          }),
      ),
    questions: node.questions.map((question) => new Question(question)),
  })
}

function findQuestion(
  groupsByName: CachedGroupMap,
  questionName: string,
  groupName?: string | null,
): QuestionLookup {
  if (groupName) {
    const group = groupsByName[groupName]
    const question = group?.questions.find((item) => item.name === questionName)
    if (question) {
      return {
        question: new Question(question),
        groupName: group.name,
      }
    }
  }

  for (const group of Object.values(groupsByName)) {
    const question = group.questions.find((item) => item.name === questionName)
    if (question) {
      return {
        question: new Question(question),
        groupName: group.name,
      }
    }
  }

  return null
}

function findQuestionById(groupsByName: CachedGroupMap, questionId: string): QuestionLookup {
  for (const group of Object.values(groupsByName)) {
    const question = group.questions.find((item) => item.questionId === questionId)
    if (question) {
      return {
        question: new Question(question),
        groupName: group.name,
      }
    }
  }

  return null
}

async function resolveQuestionForMutation(
  questionId: string,
  route: AppRoute,
  rootGroupName: string | null,
  ensureGroupLoaded: (groupName: string, options?: { force?: boolean }) => Promise<GroupNode>,
  getGroupsByName: () => CachedGroupMap,
): Promise<QuestionLookup> {
  if (questionId.trim()) {
    return findQuestionById(getGroupsByName(), questionId)
  }

  if (route.type !== 'question') {
    return null
  }

  const groupName = route.path.length > 0 ? route.path[route.path.length - 1] : rootGroupName
  if (!groupName) {
    return null
  }

  await ensureGroupLoaded(groupName, { force: true })
  return findQuestion(getGroupsByName(), route.questionName, groupName)
}

function renameGroupInCache(
  groupsByName: CachedGroupMap,
  previousName: string,
  nextGroup: GroupNode,
): CachedGroupMap {
  const previousGroup = groupsByName[previousName]
  if (!previousGroup) {
    return groupsByName
  }

  return Object.fromEntries(
    Object.entries(groupsByName).map(([name, group]) => {
      if (name === previousName) {
        return [
          nextGroup.name,
          {
            ...previousGroup,
            ...nextGroup,
            childrenNames: previousGroup.childrenNames,
            questions: previousGroup.questions.map(
              (question) =>
                new Question({
                  ...question,
                  parent: nextGroup.name,
                }),
            ),
            isLoaded: previousGroup.isLoaded,
          } satisfies GroupNode,
        ]
      }

      return [
        name,
        {
          ...group,
          parentName: group.parentName === previousName ? nextGroup.name : group.parentName,
          childrenNames: group.childrenNames.map((childName) =>
            childName === previousName ? nextGroup.name : childName,
          ),
          questions: group.questions.map((question) =>
            question.parent === previousName
              ? new Question({
                  ...question,
                  parent: nextGroup.name,
                })
              : new Question(question),
          ),
        } satisfies GroupNode,
      ]
    }),
  )
}

function removeGroupFromCache(groupsByName: CachedGroupMap, groupName: string): CachedGroupMap {
  if (!groupsByName[groupName]) {
    return groupsByName
  }

  const namesToDelete = new Set<string>()
  const queue = [groupName]

  while (queue.length > 0) {
    const current = queue.shift()
    if (!current || namesToDelete.has(current)) {
      continue
    }

    namesToDelete.add(current)

    for (const group of Object.values(groupsByName)) {
      if (group.parentName === current) {
        queue.push(group.name)
      }
    }
  }

  return Object.fromEntries(
    Object.entries(groupsByName)
      .filter(([name]) => !namesToDelete.has(name))
      .map(([name, group]) => [
        name,
        {
          ...group,
          childrenNames: group.childrenNames.filter((childName) => !namesToDelete.has(childName)),
          questions: group.questions.map((question) => new Question(question)),
        } satisfies GroupNode,
      ]),
  )
}

function removeQuestionFromCache(groupsByName: CachedGroupMap, questionId: string): CachedGroupMap {
  return Object.fromEntries(
    Object.entries(groupsByName).map(([name, group]) => [
      name,
      {
        ...group,
        questions: group.questions
          .filter((question) => question.questionId !== questionId)
          .map((question) => new Question(question)),
      } satisfies GroupNode,
    ]),
  )
}

function updateQuestionInCache(
  groupsByName: CachedGroupMap,
  questionId: string,
  nextQuestion: Question,
): CachedGroupMap {
  return Object.fromEntries(
    Object.entries(groupsByName).map(([name, group]) => [
      name,
      {
        ...group,
        questions: group.questions.map((question) =>
          question.questionId === questionId ? new Question(nextQuestion) : new Question(question),
        ),
      } satisfies GroupNode,
    ]),
  )
}

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(() => Boolean(getAuthToken()))
  const [theme, setTheme] = useState<Theme>(() => getInitialTheme())
  const [groupsByName, setGroupsByName] = useState<CachedGroupMap>({})
  const [rootGroupName, setRootGroupName] = useState<string | null>(null)
  const [uiMessages, setUiMessages] = useState<UiMessage[]>([])
  const [route, setRoute] = useState<AppRoute>(() =>
    normalizeRoute(parseRoute(window.location.pathname), Boolean(getAuthToken())),
  )
  const [modal, setModal] = useState<ModalState>(null)
  const [languageCode, setLanguageCode] = useState<LanguageCode>('ru')
  const [titleValues, setTitleValues] = useState<LocalizedDraft>(() => createEmptyLocalizedDraft())
  const [textValues, setTextValues] = useState<LocalizedDraft>(() => createEmptyLocalizedDraft())
  const [isLoading, setIsLoading] = useState(() => Boolean(getAuthToken()))
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')
  const lastGroupsPathRef = useRef<string[]>([])
  const groupsByNameRef = useRef<CachedGroupMap>({})
  const rootGroupNameRef = useRef<string | null>(null)

  useEffect(() => {
    groupsByNameRef.current = groupsByName
  }, [groupsByName])

  useEffect(() => {
    rootGroupNameRef.current = rootGroupName
  }, [rootGroupName])

  const resetAdminState = () => {
    setGroupsByName({})
    setRootGroupName(null)
    setUiMessages([])
    setModal(null)
    setLanguageCode('ru')
    setTitleValues(createEmptyLocalizedDraft())
    setTextValues(createEmptyLocalizedDraft())
    setIsLoading(false)
  }

  const logout = () => {
    clearAuthToken()
    setIsAuthenticated(false)
    setErrorMessage('')
    setIsSubmitting(false)
    resetAdminState()
    const loginRoute = { type: 'login' } satisfies AppRoute
    window.history.replaceState(null, '', buildRoutePath(loginRoute))
    setRoute(loginRoute)
  }

  const handleApiError = (error: unknown) => {
    if (isUnauthorizedError(error)) {
      logout()
      return true
    }

    setErrorMessage(getErrorMessage(error))
    return false
  }

  const navigate = (nextRoute: AppRoute, options?: { replace?: boolean }) => {
    const normalizedRoute = normalizeRoute(nextRoute, isAuthenticated)
    const nextPath = buildRoutePath(normalizedRoute)
    const currentPath = `${window.location.pathname}${window.location.search}${window.location.hash}`

    if (currentPath !== nextPath) {
      if (options?.replace) {
        window.history.replaceState(null, '', nextPath)
      } else {
        window.history.pushState(null, '', nextPath)
      }
    }

    setRoute(normalizedRoute)
  }

  const mergeGroups = (groups: GroupNode[]) => {
    setGroupsByName((current) => {
      const next = { ...current }

      for (const group of groups) {
        const existing = next[group.name]
        next[group.name] = {
          ...existing,
          ...group,
          title: group.title ?? existing?.title ?? {},
          parentName: group.parentName ?? existing?.parentName ?? '',
          childrenNames: group.childrenNames ?? existing?.childrenNames ?? [],
          questions: group.questions ?? existing?.questions ?? [],
          isLoaded: group.isLoaded ?? existing?.isLoaded ?? false,
        }
      }

      return next
    })
  }

  const ensureGroupLoaded = async (groupName: string, options?: { force?: boolean }) => {
    const force = options?.force ?? false
    const current = groupsByNameRef.current[groupName]

    if (current?.isLoaded && !force) {
      return current
    }

    const group = await fetchGroup(groupName)
    mergeGroups([group])

    if (group.childrenNames.length > 0) {
      mergeGroups(await fetchInnerGroups(group.name))
    }

    return group
  }

  const ensureStartGroupLoaded = async () => {
    const startGroup = await fetchStartGroup()
    setRootGroupName(startGroup.name)
    mergeGroups([startGroup])

    if (startGroup.childrenNames.length > 0) {
      mergeGroups(await fetchInnerGroups(startGroup.name))
    }

    return startGroup
  }

  const ensurePathLoaded = async (path: string[]) => {
    const rootName = rootGroupNameRef.current
    if (!rootName) {
      return []
    }

    let parentName = rootName
    const normalizedPath: string[] = []

    for (const groupName of path) {
      const parentGroup = await ensureGroupLoaded(parentName)
      if (!parentGroup.childrenNames.includes(groupName)) {
        break
      }

      await ensureGroupLoaded(groupName)
      normalizedPath.push(groupName)
      parentName = groupName
    }

    return normalizedPath
  }

  useEffect(() => {
    document.documentElement.dataset.theme = theme
    window.localStorage.setItem(THEME_STORAGE_KEY, theme)
  }, [theme])

  useEffect(() => {
    const handlePopState = () =>
      setRoute(normalizeRoute(parseRoute(window.location.pathname), Boolean(getAuthToken())))

    window.addEventListener('popstate', handlePopState)
    return () => window.removeEventListener('popstate', handlePopState)
  }, [])

  useEffect(() => {
    const normalizedRoute = normalizeRoute(route, isAuthenticated)
    if (buildRoutePath(normalizedRoute) !== buildRoutePath(route)) {
      navigate(normalizedRoute, { replace: true })
    }
  }, [isAuthenticated, route])

  useEffect(() => {
    if (!isAuthenticated) {
      resetAdminState()
      return
    }

    void loadData()
  }, [isAuthenticated])

  useEffect(() => {
    if (route.type === 'groups') {
      lastGroupsPathRef.current = route.path
    }
    if (route.type === 'question') {
      lastGroupsPathRef.current = route.path
    }
  }, [route])

  useEffect(() => {
    if ((route.type !== 'groups' && route.type !== 'question') || !rootGroupName) {
      return
    }

    let isCancelled = false

    void (async () => {
      try {
        const targetPath = route.path
        const normalizedPath = await ensurePathLoaded(targetPath)
        if (isCancelled) {
          return
        }

        if (route.type === 'groups' && normalizedPath.length === targetPath.length - 1) {
          const questionName = targetPath[targetPath.length - 1]
          const parentGroupName =
            normalizedPath.length > 0 ? normalizedPath[normalizedPath.length - 1] : rootGroupNameRef.current

          if (parentGroupName) {
            await ensureGroupLoaded(parentGroupName)
            const questionLookup = findQuestion(groupsByNameRef.current, questionName, parentGroupName)

            if (questionLookup) {
              navigate({ type: 'question', path: normalizedPath, questionName }, { replace: true })
              return
            }
          }
        }

        const hasPathMismatch =
          normalizedPath.length !== targetPath.length
          || normalizedPath.some((groupName, index) => groupName !== targetPath[index])

        if (hasPathMismatch) {
          navigate(
            route.type === 'groups'
              ? { type: 'groups', path: normalizedPath }
              : { type: 'question', path: normalizedPath, questionName: route.questionName },
            { replace: true },
          )
        }
      } catch (error) {
        if (!isCancelled) {
          handleApiError(error)
        }
      }
    })()

    return () => {
      isCancelled = true
    }
  }, [rootGroupName, route])

  const rootGroup = useMemo(
    () => (rootGroupName ? toGroupModel(groupsByName[rootGroupName] ?? null, groupsByName) : null),
    [groupsByName, rootGroupName],
  )
  const currentGroupName =
    route.type === 'groups' && route.path.length > 0
      ? route.path[route.path.length - 1]
      : rootGroupName
  const currentGroup = useMemo(
    () => (currentGroupName ? toGroupModel(groupsByName[currentGroupName] ?? null, groupsByName) : null),
    [currentGroupName, groupsByName],
  )
  const currentModalGroup = useMemo(() => {
    if (!modal || !('groupName' in modal)) {
      return null
    }

    return toGroupModel(groupsByName[modal.groupName] ?? null, groupsByName)
  }, [groupsByName, modal])
  const currentQuestionLookup = useMemo(() => {
    if (route.type === 'question') {
      const routeGroupName = route.path.length > 0 ? route.path[route.path.length - 1] : rootGroupName
      return findQuestion(groupsByName, route.questionName, routeGroupName)
    }

    if (modal && 'questionId' in modal) {
      return findQuestionById(groupsByName, modal.questionId)
    }

    return null
  }, [groupsByName, modal, route])
  const currentQuestion = currentQuestionLookup?.question ?? null
  const currentUiMessage = useMemo(
    () =>
      modal && 'messageName' in modal
        ? uiMessages.find((item) => item.name === modal.messageName) ?? null
        : null,
    [modal, uiMessages],
  )
  const groupsPath = route.type === 'groups' ? route.path : []
  const breadcrumbs = useMemo(() => {
    const items: BreadcrumbItem[] = [{ label: 'Главное меню', path: [] }]

    for (let index = 0; index < groupsPath.length; index += 1) {
      const groupName = groupsPath[index]
      const group = groupsByName[groupName]
      items.push({
        label: localizeTitle(group?.title, groupName, 'ru'),
        path: groupsPath.slice(0, index + 1),
      })
    }

    return items
  }, [groupsByName, groupsPath])

  useEffect(() => {
    if (!modal) {
      return
    }

    setLanguageCode('ru')

    switch (modal.type) {
      case 'edit-group':
        setTitleValues(createLocalizedDraft(currentModalGroup?.title))
        setTextValues(createEmptyLocalizedDraft())
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
  }, [currentModalGroup, currentUiMessage, modal])

  async function loadData(options?: { silent?: boolean }) {
    const silent = options?.silent ?? false

    if (!isAuthenticated) {
      setIsLoading(false)
      return
    }

    setErrorMessage('')
    if (!silent) {
      setIsLoading(true)
    }

    try {
      const [data] = await Promise.all([fetchAdminData(), ensureStartGroupLoaded()])
      setUiMessages(data.uiMessages)
    } catch (error) {
      handleApiError(error)
    } finally {
      if (!silent) {
        setIsLoading(false)
      }
    }
  }

  const closeModal = () => {
    if (isSubmitting) {
      return
    }

    setModal(null)
  }

  const titleValue = titleValues[languageCode]
  const textValue = textValues[languageCode] || '<p></p>'
  const currentUiMessageDescription =
    currentUiMessage?.description[languageCode]
    ?? currentUiMessage?.description.ru
    ?? currentUiMessage?.description.en
    ?? ''
  const isEditGroupUnchanged =
    modal?.type === 'edit-group'
      && areLocalizedDraftsEqual(titleValues, createLocalizedDraft(currentModalGroup?.title), (value) =>
        value.trim(),
      )
  const isEditUiMessageUnchanged =
    modal?.type === 'edit-ui-message'
      && areLocalizedDraftsEqual(textValues, createLocalizedDraft(currentUiMessage?.text), (value) =>
        normalizeRichTextForRequest(value),
      )
  const isConfirmDisabled =
    isSubmitting
    || (modal?.type === 'edit-group' && isEditGroupUnchanged)
    || (modal?.type === 'edit-ui-message' && isEditUiMessageUnchanged)
    || ((modal?.type === 'create-group' || modal?.type === 'create-question') && !titleValues.ru.trim())
    || (modal?.type === 'edit-group' && !titleValues.ru.trim())
    || (modal?.type === 'edit-ui-message' && isRichTextEmpty(textValue))

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

  const handleConfirm = async () => {
    if (!modal) {
      return
    }

    const titles = buildTitlePayload(titleValues)
    const texts = buildTextPayload(textValues)
    const requiredRussianTitle = titleValues.ru.trim()
    setErrorMessage('')
    setIsSubmitting(true)

    try {
      switch (modal.type) {
        case 'edit-group':
          if (!requiredRussianTitle) return
          {
            const updatedGroup = mapGroupDto(await updateGroupTitle(modal.groupName, titles))
            setGroupsByName((current) => renameGroupInCache(current, modal.groupName, updatedGroup))
            if (rootGroupNameRef.current === modal.groupName) {
              setRootGroupName(updatedGroup.name)
            }
            if (route.type === 'groups') {
              navigate(
                {
                  type: 'groups',
                  path: route.path.map((groupName) =>
                    groupName === modal.groupName ? updatedGroup.name : groupName,
                  ),
                },
                { replace: true },
              )
            }
            setModal(null)
          }
          break
        case 'create-group':
          if (!requiredRussianTitle) return
          {
            const createdGroup = mapGroupDto(await createGroup(modal.groupName, titles))
            setGroupsByName((current) => {
              const parent = current[modal.groupName]
              if (!parent) {
                return current
              }

              return {
                ...current,
                [createdGroup.name]: createdGroup,
                [modal.groupName]: {
                  ...parent,
                  childrenNames: [...parent.childrenNames, createdGroup.name],
                },
              }
            })
            setModal(null)
          }
          break
        case 'create-question':
          if (!requiredRussianTitle) return
          {
            const optimisticId = slugify(requiredRussianTitle) || `question-${Date.now()}`
            setGroupsByName((current) => {
              const group = current[modal.groupName]
              if (!group) {
                return current
              }

              return {
                ...current,
                [modal.groupName]: {
                  ...group,
                  questions: [
                    ...group.questions,
                    new Question({
                      questionId: optimisticId,
                      name: optimisticId,
                      parent: group.name,
                      title: titles,
                      text: texts,
                    }),
                  ],
                },
              }
            })
            setModal(null)
            await createQuestion(modal.groupName, titles, texts)
            await ensureGroupLoaded(modal.groupName, { force: true })
          }
          break
        case 'delete-group':
          await deleteGroup(modal.groupName)
          setGroupsByName((current) => removeGroupFromCache(current, modal.groupName))
          if (route.type === 'groups') {
            const deletedIndex = route.path.indexOf(modal.groupName)
            if (deletedIndex !== -1) {
              navigate({ type: 'groups', path: route.path.slice(0, deletedIndex) }, { replace: true })
            }
          }
          setModal(null)
          break
        case 'delete-question':
          if (!currentQuestion) return
          await deleteQuestion(currentQuestion.questionId)
          setGroupsByName((current) => removeQuestionFromCache(current, modal.questionId))
          setModal(null)
          break
        case 'edit-ui-message':
          if (isRichTextEmpty(textValue)) return
          {
            const updatedMessage = mapUiMessage(await updateUiMessageRequest(modal.messageName, texts))
            setUiMessages((current) =>
              current.map((message) =>
                message.name === modal.messageName ? updatedMessage : new UiMessage(message),
              ),
            )
            setModal(null)
          }
          break
        default:
          return
      }
    } catch (error) {
      handleApiError(error)
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleLogin = async (login: string, password: string) => {
    setErrorMessage('')
    setIsSubmitting(true)
    clearAuthToken()

    try {
      const response = await loginAdmin(login, password)
      setAuthToken(response.token)
      setIsAuthenticated(true)
      navigate({ type: 'dashboard' }, { replace: true })
    } catch (error) {
      setErrorMessage(getErrorMessage(error))
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleRegisterAdmin = async (login: string, password: string) => {
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

  const handleSaveQuestion = async (
    questionId: string,
    title: LocalizedText,
    text: LocalizedText,
  ) => {
    const questionLookup = await resolveQuestionForMutation(
      questionId,
      route,
      rootGroupNameRef.current,
      ensureGroupLoaded,
      () => groupsByNameRef.current,
    )
    if (!questionLookup) {
      setErrorMessage('Question id is missing')
      return
    }

    setErrorMessage('')
    setIsSubmitting(true)

    try {
      const effectiveQuestionId = questionLookup.question.questionId
      const updatedQuestion = mapQuestion(await updateQuestion(effectiveQuestionId, title, text))
      setGroupsByName((current) => updateQuestionInCache(current, effectiveQuestionId, updatedQuestion))
      if (route.type === 'question' && currentQuestion?.questionId === effectiveQuestionId && route.questionName !== updatedQuestion.name) {
        navigate({ type: 'question', path: route.path, questionName: updatedQuestion.name }, { replace: true })
      }
    } catch (error) {
      handleApiError(error)
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleDeleteQuestionFromPage = async (questionId: string) => {
    const questionLookup = await resolveQuestionForMutation(
      questionId,
      route,
      rootGroupNameRef.current,
      ensureGroupLoaded,
      () => groupsByNameRef.current,
    )
    if (!questionLookup) {
      navigate({ type: 'groups', path: lastGroupsPathRef.current }, { replace: true })
      return
    }

    setErrorMessage('')
    setIsSubmitting(true)

    try {
      const effectiveQuestionId = questionLookup.question.questionId
      await deleteQuestion(effectiveQuestionId)
      setGroupsByName((current) => removeQuestionFromCache(current, effectiveQuestionId))
      navigate({ type: 'groups', path: lastGroupsPathRef.current }, { replace: true })
    } catch (error) {
      handleApiError(error)
    } finally {
      setIsSubmitting(false)
    }
  }

  const themeIcon = theme === 'light' ? sunIcon : moonIcon
  const themeLabel =
    theme === 'light' ? 'Переключить на тёмную тему' : 'Переключить на светлую тему'
  const isTranslationRoute =
    route.type === 'dashboard'
    || route.type === 'technical-questions'
    || route.type === 'groups'
    || route.type === 'question'

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
            onClick={() => setTheme((current) => (current === 'light' ? 'dark' : 'light'))}
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
      {isLoading && route.type !== 'login' && <div className="app-status">Loading data...</div>}

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
          onOpenGroups={() => navigate({ type: 'groups', path: [] })}
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
          onEditUiMessage={(messageName) => setModal({ type: 'edit-ui-message', messageName })}
        />
      )}

      {!isLoading && route.type === 'groups' && (
        <AdminPage
          currentGroup={currentGroup ?? rootGroup}
          langCode="ru"
          currentFolderPath={route.path}
          breadcrumbs={breadcrumbs}
          onGoHome={() => navigate({ type: 'dashboard' })}
          onNavigateToPath={(path) => navigate({ type: 'groups', path })}
          onOpenGroup={(groupName) => navigate({ type: 'groups', path: [...route.path, groupName] })}
          onOpenQuestion={(questionName, groupPath) => navigate({ type: 'question', path: groupPath, questionName })}
          onGoBack={() =>
            route.path.length === 0
              ? navigate({ type: 'dashboard' })
              : navigate({ type: 'groups', path: route.path.slice(0, -1) })
          }
          onEditGroup={(groupName) => setModal({ type: 'edit-group', groupName })}
          onCreateGroup={(groupName) => setModal({ type: 'create-group', groupName })}
          onCreateQuestion={(groupName) => setModal({ type: 'create-question', groupName })}
          onDeleteGroup={(groupName) => setModal({ type: 'delete-group', groupName })}
          onDeleteQuestion={(questionId) => setModal({ type: 'delete-question', questionId })}
        />
      )}

      {!isLoading && route.type === 'question' && (
        <QuestionEditorPage
          question={currentQuestion}
          langCode="ru"
          isSubmitting={isSubmitting}
          errorMessage={errorMessage}
          onBack={() => navigate({ type: 'groups', path: lastGroupsPathRef.current })}
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
              <ModalActions onConfirm={handleConfirm} disabled={isConfirmDisabled} />
            </div>
          )}
        </OverlayModal>
      )}
    </div>
  )
}

function getErrorMessage(error: unknown) {
  if (error instanceof Error && error.message.trim()) {
    return error.message
  }

  return 'Request failed'
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

function ModalActions({ onConfirm, disabled = false }: { onConfirm: () => void; disabled?: boolean }) {
  return (
    <div className="modal-form__actions">
      <button className="modal-form__button" type="button" onClick={onConfirm} disabled={disabled}>
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
    case 'create-group':
      return 'Создать группу'
    case 'create-question':
      return 'Создать вопрос'
    case 'delete-group':
      return 'Удаление группы'
    case 'delete-question':
      return 'Удаление вопроса'
    case 'edit-ui-message':
      return 'Изменить технический вопрос'
  }
}

export default App
