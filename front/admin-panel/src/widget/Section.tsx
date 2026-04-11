import { useState, type ReactNode } from 'react'
import './Section.css'
import arrowUp from '../assets/fi-rr-angle-up.svg'
import plus from '../assets/fi-rr-plus.svg'

type SectionProps = {
  title: string
  children: ReactNode
  defaultOpen?: boolean
  collapsible?: boolean
  variant?: 'default' | 'muted'
  onAdd?: () => void
  addLabel?: string
  addDisabled?: boolean
}

function Section({
  title,
  children,
  defaultOpen = true,
  collapsible = true,
  variant = 'default',
  onAdd,
  addLabel = 'Добавить',
  addDisabled = false,
}: SectionProps) {
  const [isOpen, setIsOpen] = useState(defaultOpen)
  const isSectionOpen = collapsible ? isOpen : true

  return (
    <section className={`section section_${variant}`}>
      <div className="section__header">
        {collapsible ? (
          <button
            className={`section__toggle ${isSectionOpen ? 'section__toggle_open' : ''}`}
            type="button"
            onClick={() => setIsOpen((current) => !current)}
            aria-label={isSectionOpen ? `Свернуть раздел ${title}` : `Развернуть раздел ${title}`}
            aria-expanded={isSectionOpen}
          >
            <img src={arrowUp} alt="" aria-hidden="true" />
          </button>
        ) : (
          <div className="section__toggle-placeholder" aria-hidden="true" />
        )}

        <h1 className="section__title">{title}</h1>

        {onAdd && (
          <button
            className="section__action"
            type="button"
            onClick={onAdd}
            aria-label={addLabel}
            title={addLabel}
            disabled={addDisabled}
          >
            <img src={plus} alt="" aria-hidden="true" />
          </button>
        )}
      </div>

      <div className={`section__content ${isSectionOpen ? 'section__content_open' : ''}`}>
        <div className="section__content-inner">{children}</div>
      </div>
    </section>
  )
}

export default Section
