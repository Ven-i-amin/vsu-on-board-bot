import './Group.css'
import circleIcon from '../assets/fi-rr-circle-small.svg'
import pencil from '../assets/fi-rr-pencil.svg'
import type { UiMessage } from '../entities/models'

type UiMessageItemProps = {
  message: UiMessage
  langCode?: string
  onEdit: (messageName: string) => void
}

function UiMessageItem({ message, langCode = 'ru', onEdit }: UiMessageItemProps) {
  const title = message.text[langCode] ?? message.text.en ?? message.name

  return (
    <div className="ui-message">
      <div className="ui-message__header">
        <div className="ui-message__marker" aria-hidden="true">
          <img src={circleIcon} alt="" />
        </div>

        <h3 className="ui-message__title" lang={langCode}>
          {title}
        </h3>

        <div className="ui-message__actions">
          <button className="ui-message__action" type="button" aria-label="Edit ui message" onClick={() => onEdit(message.name)}>
            <img src={pencil} alt="" aria-hidden="true" />
          </button>
        </div>
      </div>
    </div>
  )
}

export default UiMessageItem
