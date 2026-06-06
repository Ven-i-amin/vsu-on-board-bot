import { useEffect, useRef, useState } from 'react'
import './App.css'
import { api, localize, hasLang, setLang, type Group, type Language, type Question, type QuestionFile } from './api'
import { QuestionText } from './QuestionText'

// ── Route types ───────────────────────────────────────────────────────────────

type Route =
  | { type: 'group'; groupName: string; breadcrumbs: Breadcrumb[] }
  | { type: 'question'; question: Question; breadcrumbs: Breadcrumb[] }

type Breadcrumb = { label: string; groupName: string }

// ── Helpers ───────────────────────────────────────────────────────────────────

function useAsync<T>(fn: () => Promise<T>, deps: unknown[]) {
  const [data, setData] = useState<T | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(false)

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError(false)
    fn()
      .then((v) => { if (!cancelled) { setData(v); setLoading(false) } })
      .catch(() => { if (!cancelled) { setError(true); setLoading(false) } })
    return () => { cancelled = true }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps)

  return { data, loading, error }
}

// ── Group page ────────────────────────────────────────────────────────────────

function GroupPage({
  groupName,
  breadcrumbs,
  onOpenGroup,
  onOpenQuestion,
}: {
  groupName: string
  breadcrumbs: Breadcrumb[]
  onOpenGroup: (group: Group, crumbs: Breadcrumb[]) => void
  onOpenQuestion: (question: Question, crumbs: Breadcrumb[]) => void
}) {
  const children = useAsync(() => api.groupChildren(groupName), [groupName])
  const questions = useAsync(() => api.groupQuestions(groupName), [groupName])
  const loading = children.loading || questions.loading
  const error = children.error || questions.error

  const hasContent =
    (children.data?.length ?? 0) > 0 || (questions.data?.length ?? 0) > 0

  return (
    <div className="page-body">
      {loading && <div className="state-message">Загрузка...</div>}
      {error && <div className="state-message state-message_error">Не удалось загрузить данные</div>}

      {!loading && !error && !hasContent && (
        <div className="state-message">Раздел пуст</div>
      )}

      {!loading && !error && hasContent && (
        <ul className="item-list">
          {children.data?.map((group) => (
            <li key={group.name}>
              <button
                className="item item_group"
                onClick={() => onOpenGroup(group, breadcrumbs)}
              >
                <span className="item__icon" aria-hidden="true">📁</span>
                <span className="item__label">{localize(group.title, group.name)}</span>
                <span className="item__arrow" aria-hidden="true">›</span>
              </button>
            </li>
          ))}

          {questions.data?.map((q) => (
            <li key={q.questionId}>
              <button
                className="item item_question"
                onClick={() => onOpenQuestion(q, breadcrumbs)}
              >
                <span className="item__icon" aria-hidden="true">💬</span>
                <span className="item__label">{localize(q.title, q.name)}</span>
                <span className="item__arrow" aria-hidden="true">›</span>
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

// ── Question page ─────────────────────────────────────────────────────────────

function QuestionPage({ question }: { question: Question }) {
  const text = localize(question.text)
  const files = useAsync(() => api.questionFiles(question.questionId), [question.questionId])

  return (
    <div className="page-body">
      {text ? (
        <div className="question-card">
          <QuestionText html={text} />
        </div>
      ) : (
        <div className="state-message">Ответ пока недоступен на выбранном языке</div>
      )}

      {!files.loading && (files.data?.length ?? 0) > 0 && (
        <div className="files-section">
          <div className="files-section__title">Прикреплённые файлы</div>
          <ul className="files-list">
            {files.data!.map((file) => (
              <FileItem key={file.fileHash} file={file} />
            ))}
          </ul>
        </div>
      )}
    </div>
  )
}

function FileItem({ file }: { file: QuestionFile }) {
  const name = file.fileName || file.fileHash.slice(0, 16) + '…'
  const ext = name.includes('.') ? name.split('.').pop()!.toLowerCase() : ''
  const icon = fileIcon(ext)

  return (
    <li>
      <a
        className="file-item"
        href={`/file/${encodeURIComponent(file.fileHash)}/content`}
        download={name}
        target="_blank"
        rel="noreferrer"
      >
        <span className="file-item__icon" aria-hidden="true">{icon}</span>
        <span className="file-item__name">{name}</span>
        <span className="file-item__download" aria-hidden="true">↓</span>
      </a>
    </li>
  )
}

function fileIcon(ext: string): string {
  if (['jpg', 'jpeg', 'png', 'gif', 'webp', 'svg'].includes(ext)) return '🖼️'
  if (['pdf'].includes(ext)) return '📄'
  if (['mp4', 'mov', 'avi', 'mkv'].includes(ext)) return '🎬'
  if (['mp3', 'wav', 'ogg', 'flac'].includes(ext)) return '🎵'
  if (['zip', 'rar', '7z', 'tar', 'gz'].includes(ext)) return '🗜️'
  if (['doc', 'docx'].includes(ext)) return '📝'
  if (['xls', 'xlsx'].includes(ext)) return '📊'
  if (['py', 'js', 'ts', 'java', 'kt', 'go', 'rs', 'cpp', 'c', 'cs'].includes(ext)) return '💻'
  return '📎'
}

// ── Language picker ───────────────────────────────────────────────────────────

function LanguagePicker({ onSelected }: { onSelected: () => void }) {
  const { data, loading, error } = useAsync(() => api.languages(), [])

  function select(lang: Language) {
    setLang(lang.code)
    onSelected()
  }

  return (
    <div className="lang-picker">
      <div className="lang-picker__card">
        <div className="lang-picker__title">Выберите язык</div>
        <div className="lang-picker__subtitle">Select language</div>

        {loading && <div className="lang-picker__spinner"><div className="splash__spinner" /></div>}
        {error && <div className="lang-picker__error">Не удалось загрузить список языков</div>}

        {!loading && !error && (
          <ul className="lang-picker__list">
            {data?.map((lang) => (
              <li key={lang.code}>
                <button className="lang-picker__btn" onClick={() => select(lang)}>
                  <span className="lang-picker__name">{lang.name[lang.code] ?? lang.code}</span>
                </button>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  )
}

// ── Root loader ───────────────────────────────────────────────────────────────

function RootLoader({ onLoaded }: { onLoaded: (root: Group) => void }) {
  const root = useAsync(() => api.rootGroup(), [])

  useEffect(() => {
    if (root.data) onLoaded(root.data)
  }, [root.data, onLoaded])

  if (root.loading) {
    return (
      <div className="splash">
        <div className="splash__spinner" />
        <div className="splash__text">Загрузка...</div>
      </div>
    )
  }

  if (root.error) {
    return (
      <div className="splash">
        <div className="splash__text splash__text_error">Сервис временно недоступен</div>
      </div>
    )
  }

  return null
}

// ── Main App ──────────────────────────────────────────────────────────────────

export default function App() {
  const [langReady, setLangReady] = useState(() => hasLang())
  const [route, setRoute] = useState<Route | null>(null)
  const [rootGroupName, setRootGroupName] = useState<string | null>(null)
  const rootTitleRef = useRef<string>('Главное меню')

  function handleRootLoaded(root: Group) {
    const label = localize(root.title, 'Главное меню')
    rootTitleRef.current = label
    setRootGroupName(root.name)
    setRoute({
      type: 'group',
      groupName: root.name,
      breadcrumbs: [],
    })
  }

  function openGroup(group: Group, parentBreadcrumbs: Breadcrumb[]) {
    const currentGroupName = route?.type === 'group' ? route.groupName : null
    const currentLabel = currentGroupName
      ? (parentBreadcrumbs.length === 0
          ? rootTitleRef.current
          : parentBreadcrumbs[parentBreadcrumbs.length - 1]?.label)
      : rootTitleRef.current

    const newCrumbs: Breadcrumb[] = [
      ...parentBreadcrumbs,
      { label: currentLabel ?? rootTitleRef.current, groupName: currentGroupName ?? rootGroupName! },
    ]

    setRoute({
      type: 'group',
      groupName: group.name,
      breadcrumbs: newCrumbs,
    })
  }

  function openQuestion(question: Question, parentBreadcrumbs: Breadcrumb[]) {
    const currentGroupName = route?.type === 'group' ? route.groupName : null
    const currentLabel = parentBreadcrumbs.length === 0
      ? rootTitleRef.current
      : parentBreadcrumbs[parentBreadcrumbs.length - 1]?.label

    const newCrumbs: Breadcrumb[] = [
      ...parentBreadcrumbs,
      { label: currentLabel ?? rootTitleRef.current, groupName: currentGroupName ?? rootGroupName! },
    ]

    setRoute({
      type: 'question',
      question,
      breadcrumbs: newCrumbs,
    })
  }

  function goBack() {
    if (!route || route.breadcrumbs.length === 0) return
    const crumbs = route.breadcrumbs
    const prev = crumbs[crumbs.length - 1]
    setRoute({
      type: 'group',
      groupName: prev.groupName,
      breadcrumbs: crumbs.slice(0, -1),
    })
  }

  function goToCrumb(index: number) {
    if (!route) return
    if (index < 0) {
      // root
      setRoute({
        type: 'group',
        groupName: rootGroupName!,
        breadcrumbs: [],
      })
      return
    }
    const target = route.breadcrumbs[index]
    setRoute({
      type: 'group',
      groupName: target.groupName,
      breadcrumbs: route.breadcrumbs.slice(0, index),
    })
  }

  const canGoBack = route !== null && route.breadcrumbs.length > 0

  const currentTitle = (() => {
    if (!route) return 'Справочник'
    if (route.type === 'group') {
      return route.breadcrumbs.length === 0
        ? rootTitleRef.current
        : route.groupName
    }
    return localize(route.question.title, route.question.name)
  })()

  if (!langReady) {
    return <LanguagePicker onSelected={() => setLangReady(true)} />
  }

  return (
    <div className="shell">
      <header className="topbar">
        <div className="topbar__inner">
          {canGoBack && (
            <button className="topbar__back" onClick={goBack} aria-label="Назад">
              ‹
            </button>
          )}
          <h1 className="topbar__title">
            {currentTitle}
          </h1>
        </div>

        {route && route.breadcrumbs.length > 0 && (
          <nav className="breadcrumb" aria-label="Навигация">
            <button className="breadcrumb__item" onClick={() => goToCrumb(-1)}>
              {rootTitleRef.current}
            </button>
            {route.breadcrumbs.map((crumb, i) => (
              <span key={crumb.groupName} className="breadcrumb__row">
                <span className="breadcrumb__sep" aria-hidden="true">›</span>
                <button
                  className="breadcrumb__item"
                  onClick={() => goToCrumb(i)}
                >
                  {crumb.label}
                </button>
              </span>
            ))}
          </nav>
        )}
      </header>

      <main className="content">
        {route === null && <RootLoader onLoaded={handleRootLoaded} />}

        {route?.type === 'group' && (
          <GroupPage
            key={route.groupName}
            groupName={route.groupName}
            breadcrumbs={route.breadcrumbs}
            onOpenGroup={openGroup}
            onOpenQuestion={openQuestion}
          />
        )}

        {route?.type === 'question' && (
          <QuestionPage key={route.question.questionId} question={route.question} />
        )}
      </main>
    </div>
  )
}

