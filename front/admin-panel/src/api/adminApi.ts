import { Question, UiMessage, type LocalizedText } from '../entities/models'

type GroupResponseDto = {
  name: string
  title: LocalizedText
  parentName: string
  innerGroups?: GroupResponseDto[]
  childrenNames?: string[]
  questions?: QuestionResponseDto[]
}

type QuestionResponseDto = {
  questionId: string
  name: string
  parent: string
  title: LocalizedText
  text: LocalizedText
}

type UiMessageResponseDto = {
  name: string
  description: LocalizedText
  text: LocalizedText
}

type GroupDto = {
  groupId?: string
  name: string
  title: LocalizedText
  parentName: string
}

export type GroupNode = {
  name: string
  title: LocalizedText
  parentName: string
  childrenNames: string[]
  questions: Question[]
  isLoaded: boolean
}

type LanguageCountResponse = {
  languageCode: string
  name: LocalizedText
  count: number
}

type TopQuestionResponse = {
  name: string
  parent: string
  title: LocalizedText
  text: LocalizedText
  using: number
}

type AuthTokenResponse = {
  token: string
}

export class ApiError extends Error {
  status: number

  constructor(status: number, message: string) {
    super(message)
    this.name = 'ApiError'
    this.status = status
  }
}

const configuredApiBaseUrl = import.meta.env.VITE_API_BASE_URL?.replace(/\/+$/, '')
const API_BASE = configuredApiBaseUrl ? `${configuredApiBaseUrl}/api` : '/api'
export const AUTH_TOKEN_STORAGE_KEY = 'admin-panel-auth-token'

const DEFAULT_UI_MESSAGE_DESCRIPTIONS: Record<string, LocalizedText> = {
  'unknown-command': {
    ru: 'Показывается пользователю, когда бот не распознал введённую команду.',
    en: 'Shown to the user when the bot cannot recognize the entered command.',
  },
  'internal-error': {
    ru: 'Показывается пользователю, если во время обработки произошла внутренняя ошибка.',
    en: 'Shown to the user if an internal error occurs during processing.',
  },
}

function decodeMojibake(value: string): string {
  if (!/[ÐÑÃ]/.test(value)) {
    return value
  }

  try {
    return new TextDecoder('utf-8').decode(Uint8Array.from(value, (char) => char.charCodeAt(0) & 0xff))
  } catch {
    return value
  }
}

export function normalizeDisplayText(value: string): string {
  const trimmed = value.trim()
  if (!trimmed) {
    return ''
  }

  const decoded = decodeMojibake(trimmed).replace(/^Error:\s*/i, '').trim()

  if (decoded.startsWith('<!DOCTYPE html') || decoded.startsWith('<html')) {
    return ''
  }

  try {
    const parsed = JSON.parse(decoded) as { message?: string; error?: string }
    const nestedMessage: string =
      typeof parsed.message === 'string' ? normalizeDisplayText(parsed.message) : ''
    if (nestedMessage) {
      return nestedMessage
    }

    const nestedError: string =
      typeof parsed.error === 'string' ? normalizeDisplayText(parsed.error) : ''
    if (nestedError) {
      return nestedError
    }
  } catch {
    // keep plain text as-is
  }

  return decoded
}

function getDefaultErrorMessage(status: number): string {
  switch (status) {
    case 400:
      return 'Некорректный запрос'
    case 401:
      return 'Сессия истекла. Войдите заново'
    case 403:
      return 'Доступ запрещен'
    case 404:
      return 'Запрошенные данные не найдены'
    case 409:
      return 'Такой объект уже существует'
    case 500:
      return 'Внутренняя ошибка сервера'
    default:
      return 'Не удалось выполнить запрос'
  }
}

function buildRequestErrorMessage(status: number, responseText: string): string {
  const normalizedText = normalizeDisplayText(responseText)
  if (normalizedText) {
    return normalizedText
  }

  return `${getDefaultErrorMessage(status)} (${status})`
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const headers = new Headers(init?.headers)
  const token = getAuthToken()
  const isAuthRequest = path.startsWith('/auth/')

  if (init?.body && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }

  if (!isAuthRequest && token && !headers.has('Authorization')) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  const response = await fetch(`${API_BASE}${path}`, {
    headers,
    ...init,
  })

  if (!response.ok) {
    const text = await response.text()
    throw new ApiError(response.status, buildRequestErrorMessage(response.status, text))
  }

  if (response.status === 204) {
    return undefined as T
  }

  const contentLength = response.headers.get('content-length')
  if (contentLength === '0') {
    return undefined as T
  }

  const text = await response.text()
  if (!text.trim()) {
    return undefined as T
  }

  return JSON.parse(text) as T
}

export function getAuthToken() {
  if (typeof window === 'undefined') {
    return ''
  }

  return window.localStorage.getItem(AUTH_TOKEN_STORAGE_KEY) ?? ''
}

export function setAuthToken(token: string) {
  if (typeof window === 'undefined') {
    return
  }

  window.localStorage.setItem(AUTH_TOKEN_STORAGE_KEY, token)
}

export function clearAuthToken() {
  if (typeof window === 'undefined') {
    return
  }

  window.localStorage.removeItem(AUTH_TOKEN_STORAGE_KEY)
}

export function isUnauthorizedError(error: unknown) {
  return (error instanceof ApiError && error.status === 401)
    || (error instanceof Error
      && (
        error.message.includes('401')
        || error.message.includes('JWT token is required')
        || error.message.includes('Invalid JWT token')
        || error.message.includes('Сессия истекла')
      ))
}

function mapQuestion(dto: QuestionResponseDto): Question {
  return new Question({
    questionId: dto.questionId,
    name: dto.name,
    parent: dto.parent,
    title: dto.title ?? {},
    text: dto.text ?? {},
  })
}

function mapGroup(dto: GroupResponseDto): GroupNode {
  const nestedChildren = dto.innerGroups?.map((group) => group.name) ?? []

  return {
    name: dto.name,
    title: dto.title ?? {},
    parentName: dto.parentName ?? '',
    childrenNames: dto.childrenNames ?? nestedChildren,
    questions: (dto.questions ?? []).map(mapQuestion),
    isLoaded: true,
  }
}

function mapUiMessage(dto: UiMessageResponseDto): UiMessage {
  return new UiMessage({
    name: dto.name,
    description: dto.description ?? DEFAULT_UI_MESSAGE_DESCRIPTIONS[dto.name] ?? {},
    text: dto.text ?? {},
  })
}

function dedupeUiMessages(messages: UiMessageResponseDto[]) {
  const uniqueByName = new Map<string, UiMessageResponseDto>()

  for (const message of messages) {
    uniqueByName.set(message.name, message)
  }

  return Array.from(uniqueByName.values())
}

export async function fetchAdminData() {
  return {
    groups: [],
    uiMessages: dedupeUiMessages(await request<UiMessageResponseDto[]>('/ui-message')).map(mapUiMessage),
  }
}

export function loginAdmin(email: string, password: string) {
  return request<AuthTokenResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  })
}

export function registerAdmin(email: string, password: string) {
  return request<AuthTokenResponse>('/auth/register', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  })
}

export function fetchStartGroup() {
  return request<GroupResponseDto>('/group/start').then(mapGroup)
}

export function fetchGroup(groupName: string) {
  return request<GroupResponseDto>(`/group/${encodeURIComponent(groupName)}`).then(mapGroup)
}

export function fetchInnerGroups(groupName: string) {
  return request<GroupDto[]>(`/group/${encodeURIComponent(groupName)}/inner`).then((groups) =>
    groups.map(mapGroupDto),
  )
}

export function createGroup(parentName: string, title: LocalizedText) {
  return request<GroupDto>('/group', {
    method: 'POST',
    body: JSON.stringify({ parentName, title }),
  })
}

export function updateGroupTitle(groupName: string, title: LocalizedText) {
  return request<GroupDto>(`/group/${encodeURIComponent(groupName)}/title`, {
    method: 'PATCH',
    body: JSON.stringify({ title }),
  })
}

export function deleteGroup(groupName: string) {
  return request<void>(`/group/${encodeURIComponent(groupName)}`, {
    method: 'DELETE',
  })
}

export function createQuestion(groupName: string, title: LocalizedText, text: LocalizedText) {
  return request<void>('/question', {
    method: 'POST',
    body: JSON.stringify({ groupName, title, text }),
  })
}

export function updateQuestion(questionId: string, title: LocalizedText, text: LocalizedText) {
  return request<QuestionResponseDto>(`/question/${encodeURIComponent(questionId)}`, {
    method: 'PATCH',
    body: JSON.stringify({ title, text }),
  })
}

export function deleteQuestion(questionId: string) {
  return request<void>(`/question/${encodeURIComponent(questionId)}`, {
    method: 'DELETE',
  })
}

export function updateUiMessage(messageName: string, text: LocalizedText) {
  return request<UiMessageResponseDto>(`/ui-message/${encodeURIComponent(messageName)}`, {
    method: 'PATCH',
    body: JSON.stringify({ text }),
  })
}

export function mapGroupDto(dto: GroupDto) {
  return {
    name: dto.name,
    title: dto.title ?? {},
    parentName: dto.parentName ?? '',
    childrenNames: [],
    questions: [],
    isLoaded: false,
  } satisfies GroupNode
}

export { mapQuestion, mapUiMessage, mapGroup }

export function fetchTopLanguages() {
  return request<LanguageCountResponse[]>('/statistic/topLanguages')
}

export function fetchTopQuestions() {
  return request<TopQuestionResponse[]>('/statistic/topQuestions')
}
