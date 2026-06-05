import { useState } from 'react'
import './AdminPage.css'
import folderIcon from '../assets/fi-rr-folder.svg'
import fileIcon from '../assets/fi-rs-document.svg'
import pencilIcon from '../assets/fi-rr-pencil.svg'
import trashIcon from '../assets/fi-rs-trash.svg'
import plusIcon from '../assets/fi-rr-plus.svg'
import arrowLeftIcon from '../assets/fi-rr-angle-left.svg'
import arrowUpIcon from '../assets/fi-rr-angle-up.svg'
import { Group as GroupModel } from '../entities/models'

export type BreadcrumbItem = {
  label: string
  path: string[]
}

type AdminPageProps = {
  currentGroup: GroupModel | null
  langCode?: string
  currentFolderPath: string[]
  breadcrumbs: BreadcrumbItem[]
  onGoHome: () => void
  onNavigateToPath: (path: string[]) => void
  onOpenGroup: (groupName: string) => void
  onOpenQuestion: (questionName: string, groupPath: string[]) => void
  onGoBack: () => void
  onEditGroup: (groupName: string) => void
  onCreateGroup: (groupName: string) => void
  onCreateQuestion: (groupName: string) => void
  onDeleteGroup: (groupName: string) => void
  onDeleteQuestion: (questionId: string) => void
}

const ROOT_LABEL = 'Главное меню'

function localizeTitle(
  value: Record<string, string> | undefined,
  langCode: string,
  fallback: string,
) {
  return value?.[langCode] ?? value?.ru ?? value?.en ?? fallback
}

function AdminPage({
  currentGroup,
  langCode = 'ru',
  currentFolderPath,
  breadcrumbs,
  onGoHome,
  onNavigateToPath,
  onOpenGroup,
  onOpenQuestion,
  onGoBack,
  onEditGroup,
  onCreateGroup,
  onCreateQuestion,
  onDeleteGroup,
  onDeleteQuestion,
}: AdminPageProps) {
  const [isHiddenPathOpen, setIsHiddenPathOpen] = useState(false)
  const hiddenBreadcrumbs = breadcrumbs.length > 3 ? breadcrumbs.slice(1, -1) : []
  const visibleBreadcrumbs =
    breadcrumbs.length > 3
      ? [breadcrumbs[0], breadcrumbs[breadcrumbs.length - 1]]
      : breadcrumbs
  const currentFolderTitle = currentGroup
    ? localizeTitle(currentGroup.title, langCode, currentGroup.name)
    : ROOT_LABEL

  return (
    <main className="page-content">
      <div className="page-content__columns">
        <div className="browser">
          <div className="browser__toolbar">
            <div className="browser__current browser__current_full">
              <button
                className="browser__back"
                type="button"
                onClick={onGoBack}
                aria-label={currentFolderPath.length === 0 ? 'Вернуться в меню перевода' : 'Вернуться на уровень выше'}
              >
                <img src={arrowLeftIcon} alt="" aria-hidden="true" />
              </button>

              <span className="browser__current-title">{currentFolderTitle}</span>

              {currentGroup && (
                <button
                  className="browser__header-action"
                  type="button"
                  aria-label="Редактировать текущую группу"
                  onClick={() => onEditGroup(currentGroup.name)}
                >
                  <img src={pencilIcon} alt="" aria-hidden="true" />
                </button>
              )}
            </div>
          </div>

          <div className="browser__path">
            {visibleBreadcrumbs.map((item, index) => (
              <div key={`${item.path.join('/')}-${index}`} className="browser__crumb-wrap">
                {index > 0 && <span className="browser__crumb-separator">/</span>}
                <button
                  className="browser__crumb"
                  type="button"
                  onClick={() => {
                    setIsHiddenPathOpen(false)
                    if (item.path.length === 0) {
                      onGoHome()
                    } else {
                      onNavigateToPath(item.path)
                    }
                  }}
                >
                  {item.label}
                </button>
              </div>
            ))}

            {hiddenBreadcrumbs.length > 0 && (
              <div className="browser__ellipsis-wrap">
                <span className="browser__crumb-separator">/</span>
                <button
                  className="browser__crumb browser__crumb_ellipsis"
                  type="button"
                  onClick={() => setIsHiddenPathOpen((current) => !current)}
                >
                  ...
                </button>

                {isHiddenPathOpen && (
                  <div className="browser__ellipsis-menu">
                    {hiddenBreadcrumbs.map((item) => (
                      <button
                        key={item.path.join('/')}
                        className="browser__ellipsis-item"
                        type="button"
                        onClick={() => {
                          setIsHiddenPathOpen(false)
                          onNavigateToPath(item.path)
                        }}
                      >
                        {item.label}
                      </button>
                    ))}
                  </div>
                )}
              </div>
            )}
          </div>

          <div className="browser__actions">
            <div className="browser__create">
              <button className="browser__create-button" type="button" aria-label="Создать">
                <img src={plusIcon} alt="" aria-hidden="true" />
                <img className="browser__create-arrow" src={arrowUpIcon} alt="" aria-hidden="true" />
              </button>

              {currentGroup && (
                <div className="browser__create-menu">
                  <button
                    className="browser__create-option"
                    type="button"
                    onClick={() => onCreateGroup(currentGroup.name)}
                  >
                    Создать группу
                  </button>
                  <button
                    className="browser__create-option"
                    type="button"
                    onClick={() => onCreateQuestion(currentGroup.name)}
                  >
                    Создать вопрос
                  </button>
                </div>
              )}
            </div>
          </div>

          <div className="browser__list">
            {currentGroup?.innerGroups.map((group) => (
              <div
                key={group.name}
                className="browser__item"
                role="button"
                tabIndex={0}
                onClick={() => onOpenGroup(group.name)}
                onKeyDown={(event) => {
                  if (event.key === 'Enter' || event.key === ' ') {
                    event.preventDefault()
                    onOpenGroup(group.name)
                  }
                }}
              >
                <div className="browser__item-main">
                  <img className="browser__item-icon" src={folderIcon} alt="" aria-hidden="true" />
                  <span className="browser__item-label">
                    {localizeTitle(group.title, langCode, group.name)}
                  </span>
                </div>

                <div className="browser__item-actions">
                  <button
                    className="browser__item-action"
                    type="button"
                    aria-label="Редактировать группу"
                    onClick={(event) => {
                      event.stopPropagation()
                      onEditGroup(group.name)
                    }}
                  >
                    <img src={pencilIcon} alt="" aria-hidden="true" />
                  </button>
                  <button
                    className="browser__item-action"
                    type="button"
                    aria-label="Удалить группу"
                    onClick={(event) => {
                      event.stopPropagation()
                      onDeleteGroup(group.name)
                    }}
                  >
                    <img src={trashIcon} alt="" aria-hidden="true" />
                  </button>
                </div>
              </div>
            ))}

            {currentGroup?.questions.map((question) => (
              <div
                key={question.questionId || question.name}
                className="browser__item"
                role="button"
                tabIndex={0}
                onClick={() => onOpenQuestion(question.name, currentFolderPath)}
                onKeyDown={(event) => {
                  if (event.key === 'Enter' || event.key === ' ') {
                    event.preventDefault()
                    onOpenQuestion(question.name, currentFolderPath)
                  }
                }}
              >
                <div className="browser__item-main">
                  <img className="browser__item-icon" src={fileIcon} alt="" aria-hidden="true" />
                  <span className="browser__item-label">
                    {localizeTitle(question.title, langCode, question.name)}
                  </span>
                </div>

                <div className="browser__item-actions">
                  <button
                    className="browser__item-action"
                    type="button"
                    aria-label="Редактировать вопрос"
                    onClick={(event) => {
                      event.stopPropagation()
                      onOpenQuestion(question.name, currentFolderPath)
                    }}
                  >
                    <img src={pencilIcon} alt="" aria-hidden="true" />
                  </button>
                  <button
                    className="browser__item-action"
                    type="button"
                    aria-label="Удалить вопрос"
                    onClick={(event) => {
                      event.stopPropagation()
                      onDeleteQuestion(question.questionId)
                    }}
                  >
                    <img src={trashIcon} alt="" aria-hidden="true" />
                  </button>
                </div>
              </div>
            ))}

            {!currentGroup?.innerGroups.length && !currentGroup?.questions.length && (
              <div className="browser__empty">В этой папке пока ничего нет</div>
            )}
          </div>
        </div>
      </div>
    </main>
  )
}

export default AdminPage
