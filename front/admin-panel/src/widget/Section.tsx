import { useState, type ReactNode } from 'react'
import './Section.css'
import arrowUp from '../assets/fi-rr-angle-up.svg'

type SectionProps = {
  title: string
  children: ReactNode
  defaultOpen?: boolean
  variant?: 'default' | 'muted'
}

function Section({
  title,
  children,
  defaultOpen = true,
  variant = 'default',
}: SectionProps) {
  const [isOpen, setIsOpen] = useState(defaultOpen)

  return (
    <section className={`section section_${variant}`}>
      <div className="section__header">
        <button
          className={`section__toggle ${isOpen ? 'section__toggle_open' : ''}`}
          type="button"
          onClick={() => setIsOpen((current) => !current)}
          aria-label={isOpen ? `Свернуть раздел ${title}` : `Развернуть раздел ${title}`}
          aria-expanded={isOpen}
        >
          <img src={arrowUp} alt="" aria-hidden="true" />
        </button>

        <h1 className="section__title">{title}</h1>
      </div>

      <div className={`section__content ${isOpen ? 'section__content_open' : ''}`}>
        <div className="section__content-inner">{children}</div>
      </div>
    </section>
  )
}

export default Section
