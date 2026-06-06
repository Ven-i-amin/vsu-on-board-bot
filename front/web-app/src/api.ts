export type Group = {
  name: string
  title: Record<string, string>
  parents: string[]
}

export type Question = {
  questionId: string
  name: string
  parent: string
  title: Record<string, string>
  text: Record<string, string>
}

export type QuestionFile = {
  fileHash: string
  fileName: string | null
}

export type Language = {
  code: string
  name: Record<string, string>
}

const LANG_KEY = 'lang'

export function getLang(): string {
  return localStorage.getItem(LANG_KEY) ?? 'ru'
}

export function setLang(code: string): void {
  localStorage.setItem(LANG_KEY, code)
}

export function hasLang(): boolean {
  return localStorage.getItem(LANG_KEY) !== null
}

export function localize(map: Record<string, string> | undefined, fallback = ''): string {
  if (!map) return fallback
  const lang = getLang()
  return map[lang] ?? map['en'] ?? map['ru'] ?? Object.values(map)[0] ?? fallback
}

async function get<T>(path: string): Promise<T> {
  const res = await fetch(path)
  if (!res.ok) throw new Error(`${res.status}`)
  return res.json() as Promise<T>
}

export const api = {
  rootGroup: () => get<Group>('/group/root'),
  groupChildren: (name: string) => get<Group[]>(`/group/${encodeURIComponent(name)}/children`),
  groupQuestions: (name: string) => get<Question[]>(`/question/group/${encodeURIComponent(name)}`),
  questionFiles: (id: string) => get<QuestionFile[]>(`/question/${encodeURIComponent(id)}/files`),
  languages: () => get<Language[]>('/languages'),
}
