import './AdminPage.css'
import arrowLeftIcon from '../assets/fi-rr-angle-left.svg'
import { UiMessage } from '../entities/models'
import UiMessageItem from '../widget/UiMessageItem'

type TechnicalQuestionsPageProps = {
  uiMessages: UiMessage[]
  langCode?: string
  onBack: () => void
  onEditUiMessage: (messageName: string) => void
}

function TechnicalQuestionsPage({
  uiMessages,
  langCode = 'ru',
  onBack,
  onEditUiMessage,
}: TechnicalQuestionsPageProps) {
  return (
    <main className="page-content">
      <div className="page-content__columns">
        <div className="browser">
          <div className="browser__toolbar">
            <div className="browser__current browser__current_full">
              <button
                className="browser__back"
                type="button"
                onClick={onBack}
                aria-label="Вернуться в главное меню"
              >
                <img src={arrowLeftIcon} alt="" aria-hidden="true" />
              </button>
              <span className="browser__current-title">Технические вопросы</span>
            </div>
          </div>

          <div className="browser__path">
            <div className="browser__crumb-wrap">
              <button className="browser__crumb" type="button" onClick={onBack}>
                Главное меню
              </button>
            </div>
            <div className="browser__crumb-wrap">
              <span className="browser__crumb-separator">/</span>
              <span className="browser__crumb browser__crumb_static">Технические вопросы</span>
            </div>
          </div>

          <div className="browser__list">
            {uiMessages.map((message) => (
              <UiMessageItem
                key={message.name}
                message={message}
                langCode={langCode}
                onEdit={onEditUiMessage}
              />
            ))}

            {!uiMessages.length && (
              <div className="browser__empty">Технических вопросов пока нет</div>
            )}
          </div>
        </div>
      </div>
    </main>
  )
}

export default TechnicalQuestionsPage
