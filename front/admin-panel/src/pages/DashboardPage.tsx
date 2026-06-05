import './AdminPage.css'
import folderIcon from '../assets/fi-rr-folder.svg'

type DashboardPageProps = {
  onOpenTechnicalQuestions: () => void
  onOpenGroups: () => void
}

function DashboardPage({
  onOpenTechnicalQuestions,
  onOpenGroups,
}: DashboardPageProps) {
  return (
    <main className="page-content">
      <div className="page-content__columns">
        <div className="browser">
          <div className="browser__toolbar">
            <div className="browser__current browser__current_full">
              <span className="browser__current-title">Перевод</span>
            </div>
          </div>

          <div className="browser__list">
            <div
              className="browser__item"
              role="button"
              tabIndex={0}
              onClick={onOpenTechnicalQuestions}
              onKeyDown={(event) => {
                if (event.key === 'Enter' || event.key === ' ') {
                  event.preventDefault()
                  onOpenTechnicalQuestions()
                }
              }}
            >
              <div className="browser__item-main">
                <img className="browser__item-icon" src={folderIcon} alt="" aria-hidden="true" />
                <span className="browser__item-label">Технические вопросы</span>
              </div>
            </div>

            <div
              className="browser__item"
              role="button"
              tabIndex={0}
              onClick={onOpenGroups}
              onKeyDown={(event) => {
                if (event.key === 'Enter' || event.key === ' ') {
                  event.preventDefault()
                  onOpenGroups()
                }
              }}
            >
              <div className="browser__item-main">
                <img className="browser__item-icon" src={folderIcon} alt="" aria-hidden="true" />
                <span className="browser__item-label">Главное меню</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  )
}

export default DashboardPage
