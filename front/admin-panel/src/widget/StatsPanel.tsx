import { useEffect, useMemo, useState } from 'react'
import './StatsPanel.css'
import { fetchTopLanguages, fetchTopQuestions } from '../api/adminApi'
import type { LocalizedText } from '../entities/models'

type StatItem = {
  label: string
  value: number
}

type LanguageStatResponse = {
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

const localize = (value: LocalizedText | undefined, langCode: string, fallback: string) =>
  value?.[langCode] ?? value?.ru ?? value?.en ?? fallback

function StatsList({ title, items }: { title: string; items: StatItem[] }) {
  const visibleItems = items.slice(0, 5)

  return (
    <article className="stats-card">
      <div className="stats-card__header">
        <h3 className="stats-card__title">{title}</h3>
      </div>

      <ol className="stats-card__list">
        {visibleItems.map((item, index) => (
          <li className="stats-card__item" key={`${item.label}-${index}`}>
            <span className="stats-card__rank">{index + 1}</span>
            <span className="stats-card__label">{item.label}</span>
            <span className="stats-card__value">{item.value}</span>
          </li>
        ))}
      </ol>
    </article>
  )
}

function StatsPanel({ langCode = 'ru' }: { langCode?: string }) {
  const [languages, setLanguages] = useState<LanguageStatResponse[]>([])
  const [questions, setQuestions] = useState<TopQuestionResponse[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [errorMessage, setErrorMessage] = useState('')

  useEffect(() => {
    let cancelled = false

    async function loadStats() {
      setErrorMessage('')
      setIsLoading(true)

      try {
        const [topLanguages, topQuestions] = await Promise.all([
          fetchTopLanguages(),
          fetchTopQuestions(),
        ])

        if (cancelled) {
          return
        }

        setLanguages(topLanguages as LanguageStatResponse[])
        setQuestions(topQuestions as TopQuestionResponse[])
      } catch (error) {
        if (cancelled) {
          return
        }

        setErrorMessage(error instanceof Error ? error.message : 'Не удалось загрузить статистику')
      } finally {
        if (!cancelled) {
          setIsLoading(false)
        }
      }
    }

    void loadStats()

    return () => {
      cancelled = true
    }
  }, [])

  const languageStats = useMemo(
    () =>
      languages.map((item) => ({
        label: localize(item.name, langCode, item.languageCode),
        value: item.count,
      })),
    [langCode, languages],
  )

  const popularQuestions = useMemo(
    () =>
      questions.map((item) => ({
        label: localize(item.title, langCode, item.name),
        value: item.using,
      })),
    [langCode, questions],
  )

  if (isLoading) {
    return <div className="stats-panel__status">Загрузка статистики...</div>
  }

  if (errorMessage) {
    return <div className="stats-panel__status stats-panel__status_error">{errorMessage}</div>
  }

  return (
    <div className="stats-panel">
      <StatsList title="Топ языков пользователей" items={languageStats} />
      <StatsList title="Самые популярные вопросы" items={popularQuestions} />
    </div>
  )
}

export default StatsPanel
