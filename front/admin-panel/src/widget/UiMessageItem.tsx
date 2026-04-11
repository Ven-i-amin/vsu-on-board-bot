import '../pages/AdminPage.css'
import fileIcon from '../assets/fi-rs-document.svg'
import pencilIcon from '../assets/fi-rr-pencil.svg'
import type { UiMessage } from '../entities/models'

type UiMessageItemProps = {
  message: UiMessage
  langCode?: string
  onEdit: (messageName: string) => void
}

function UiMessageItem({ message, langCode = 'ru', onEdit }: UiMessageItemProps) {
  const title = message.text[langCode] ?? message.text.ru ?? message.text.en ?? message.name

  return (
    <div
      className="browser__item browser__item_muted"
      role="button"
      tabIndex={0}
      onClick={() => onEdit(message.name)}
      onKeyDown={(event) => {
        if (event.key === 'Enter' || event.key === ' ') {
          event.preventDefault()
          onEdit(message.name)
        }
      }}
    >
      <div className="browser__item-main">
        <img className="browser__item-icon" src={fileIcon} alt="" aria-hidden="true" />
        <span className="browser__item-label">{title}</span>
      </div>

      <div className="browser__item-actions">
        <button
          className="browser__item-action"
          type="button"
          aria-label="Редактировать технический вопрос"
          onClick={(event) => {
            event.stopPropagation()
            onEdit(message.name)
          }}
        >
          <img src={pencilIcon} alt="" aria-hidden="true" />
        </button>
      </div>
    </div>
  )
}

export default UiMessageItem
