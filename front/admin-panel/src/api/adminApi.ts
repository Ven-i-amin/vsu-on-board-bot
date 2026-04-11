import { Question, UiMessage, type LocalizedText } from '../entities/models'

type GroupResponseDto = {
  name: string
  title: LocalizedText
  parentName: string
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

const API_BASE = `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'}/api`

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

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const headers = new Headers(init?.headers)

  if (init?.body && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }

  const response = await fetch(`${API_BASE}${path}`, {
    headers,
    ...init,
  })

  if (!response.ok) {
    const text = await response.text()
    throw new Error(text || `Request failed with status ${response.status}`)
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
  return {
    name: dto.name,
    title: dto.title ?? {},
    parentName: dto.parentName ?? '',
    childrenNames: dto.childrenNames ?? [],
    questions: (dto.questions ?? []).map(mapQuestion),
    isLoaded: true,
  }
}

function mapUiMessage(dto: UiMessageResponseDto): UiMessage {
  return new UiMessage({
    name: dto.name,
    description: DEFAULT_UI_MESSAGE_DESCRIPTIONS[dto.name] ?? {},
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

export function updateQuestion(questionName: string, title: LocalizedText, text: LocalizedText) {
  return request<QuestionResponseDto>(`/question/${encodeURIComponent(questionName)}`, {
    method: 'PATCH',
    body: JSON.stringify({ title, text }),
  })
}

export function deleteQuestion(questionName: string) {
  return request<void>(`/question/${encodeURIComponent(questionName)}`, {
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
