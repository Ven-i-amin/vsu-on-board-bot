import { useState } from 'react'
import './Group.css'
import arrowUp from '../assets/fi-rr-angle-up.svg'
import pencil from '../assets/fi-rr-pencil.svg'
import plus from '../assets/fi-rr-plus.svg'
import trash from '../assets/fi-rs-trash.svg'
import type { Group as GroupModel } from '../entities/models'
import Question from './Question'

type GroupProps = {
  group: GroupModel
  langCode?: string
  level?: number
  onEditGroup: (groupName: string) => void
  onAddChild: (groupName: string) => void
  onDeleteGroup: (groupName: string) => void
  onEditQuestion: (questionId: string) => void
  onDeleteQuestion: (questionId: string) => void
}

function Group({
  group,
  langCode = 'ru',
  level = 0,
  onEditGroup,
  onAddChild,
  onDeleteGroup,
  onEditQuestion,
  onDeleteQuestion,
}: GroupProps) {
  const [isOpen, setIsOpen] = useState(false)
  const title = group.title[langCode] ?? group.title.en ?? group.name
  const hasChildren = group.innerGroups.length > 0 || group.questions.length > 0

  return (
    <div className="group">
      <div className="group__header">
        <button
          className={`group__toggle ${isOpen ? 'group__toggle_open' : ''}`}
          type="button"
          onClick={() => setIsOpen((current) => !current)}
          aria-label={isOpen ? 'Collapse group' : 'Expand group'}
          aria-expanded={isOpen}
        >
          <img src={arrowUp} alt="" aria-hidden="true" />
        </button>

        <h2 className="group__title" lang={langCode}>
          {title}
        </h2>

        <div className="group__actions">
          <button className="group__action" type="button" aria-label="Add child to group" onClick={() => onAddChild(group.name)}>
            <img src={plus} alt="" aria-hidden="true" />
          </button>
          <button className="group__action" type="button" aria-label="Edit group" onClick={() => onEditGroup(group.name)}>
            <img src={pencil} alt="" aria-hidden="true" />
          </button>
          <button className="group__action" type="button" aria-label="Delete group" onClick={() => onDeleteGroup(group.name)}>
            <img src={trash} alt="" aria-hidden="true" />
          </button>
        </div>
      </div>

      <div className={`group__content ${isOpen ? 'group__content_open' : ''}`}>
        <div className="group__content-inner">
          {group.innerGroups.map((innerGroup) => (
            <Group
              key={innerGroup.name}
              group={innerGroup}
              langCode={langCode}
              level={level + 1}
              onEditGroup={onEditGroup}
              onAddChild={onAddChild}
              onDeleteGroup={onDeleteGroup}
              onEditQuestion={onEditQuestion}
              onDeleteQuestion={onDeleteQuestion}
            />
          ))}

          {group.questions.map((question) => (
            <Question
              key={question.questionId || question.name}
              question={question}
              langCode={langCode}
              level={level + 1}
              onEdit={onEditQuestion}
              onDelete={onDeleteQuestion}
            />
          ))}

          {!hasChildren && <div className="group__empty">Здесь еще ничего нет</div>}
        </div>
      </div>
    </div>
  )
}

export default Group
