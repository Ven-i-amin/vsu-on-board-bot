import { useState } from 'react'
import './StatsPanel.css'

type StatItem = {
  label: string
  value: number
}

const languageStats: StatItem[] = [
  { label: 'Русский', value: 1284 },
  { label: 'English', value: 938 },
  { label: 'Deutsch', value: 412 },
  { label: 'Español', value: 286 },
  { label: 'Français', value: 214 },
  { label: 'Italiano', value: 167 },
  { label: '中文', value: 123 },
  { label: 'Português', value: 94 },
]

const popularQuestions: StatItem[] = [
  { label: 'Активные пользователи', value: 864 },
  { label: 'Настройки панели', value: 731 },
  { label: 'Недельный отчёт', value: 664 },
  { label: 'Ошибки авторизации', value: 521 },
  { label: 'Резервное копирование', value: 438 },
  { label: 'Роли и доступы', value: 372 },
  { label: 'Массовые рассылки', value: 305 },
  { label: 'Внутренние уведомления', value: 244 },
]

function StatsList({ title, items }: { title: string; items: StatItem[] }) {
  const [showAll, setShowAll] = useState(false)
  const visibleItems = showAll ? items : items.slice(0, 5)

  return (
    <article className="stats-card">
      <div className="stats-card__header">
        <h3 className="stats-card__title">{title}</h3>
        {items.length > 5 && (
          <button
            className="stats-card__toggle"
            type="button"
            onClick={() => setShowAll((current) => !current)}
          >
            {showAll ? 'Скрыть' : 'Показать все'}
          </button>
        )}
      </div>

      <ol className="stats-card__list">
        {visibleItems.map((item, index) => (
          <li className="stats-card__item" key={item.label}>
            <span className="stats-card__rank">{index + 1}</span>
            <span className="stats-card__label">{item.label}</span>
            <span className="stats-card__value">{item.value}</span>
          </li>
        ))}
      </ol>
    </article>
  )
}

function StatsPanel() {
  return (
    <div className="stats-panel">
      <StatsList title="Топ языков пользователей" items={languageStats} />
      <StatsList title="Самые популярные вопросы" items={popularQuestions} />
    </div>
  )
}

export default StatsPanel
