import { useRef, type PointerEvent, type ReactNode } from 'react'
import './OverlayModal.css'
import closeIcon from '../assets/fi-rr-cross-small.svg'

type OverlayModalProps = {
  title: string
  children: ReactNode
  onClose: () => void
  compact?: boolean
}

function OverlayModal({ title, children, onClose, compact = false }: OverlayModalProps) {
  const shouldCloseOnPointerUpRef = useRef(false)

  const handleOverlayPointerDown = (event: PointerEvent<HTMLDivElement>) => {
    shouldCloseOnPointerUpRef.current = event.target === event.currentTarget
  }

  const handleOverlayPointerUp = (event: PointerEvent<HTMLDivElement>) => {
    const shouldClose =
      shouldCloseOnPointerUpRef.current && event.target === event.currentTarget

    shouldCloseOnPointerUpRef.current = false

    if (shouldClose) {
      onClose()
    }
  }

  return (
    <div
      className="modal-overlay"
      role="presentation"
      onPointerDown={handleOverlayPointerDown}
      onPointerUp={handleOverlayPointerUp}
    >
      <div
        className={`modal-card ${compact ? 'modal-card_compact' : ''}`}
        role="dialog"
        aria-modal="true"
        aria-label={title}
      >
        <div className={`modal-card__header ${compact ? 'modal-card__header_compact' : ''}`}>
          <h2 className="modal-card__title">{title}</h2>
          {!compact && (
            <button className="modal-card__close" type="button" onClick={onClose} aria-label="Закрыть">
              <img src={closeIcon} alt="" aria-hidden="true" />
            </button>
          )}
        </div>

        <div className="modal-card__body">{children}</div>
      </div>
    </div>
  )
}

export default OverlayModal
