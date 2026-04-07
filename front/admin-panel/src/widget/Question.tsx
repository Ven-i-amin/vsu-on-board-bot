import './Group.css'
import interrogation from '../assets/fi-rr-interrogation.svg'
import pencil from '../assets/fi-rr-pencil.svg'
import trash from '../assets/fi-rs-trash.svg'
import type { Question as QuestionModel } from '../entities/models'

type QuestionProps = {
  question: QuestionModel
  langCode?: string
  level?: number
  onEdit: (questionId: string) => void
  onDelete: (questionId: string) => void
}

function Question({ question, langCode = 'ru', onEdit, onDelete }: QuestionProps) {
  const title = question.title[langCode] ?? question.title.en ?? question.name

  return (
    <div className="question">
      <div className="question__header">
        <div className="question__marker" aria-hidden="true">
          <img src={interrogation} alt="" />
        </div>
        <h3 className="question__title" lang={langCode}>
          {title}
        </h3>

        <div className="question__actions">
          <button className="question__action" type="button" aria-label="Edit question" onClick={() => onEdit(question.questionId)}>
            <img src={pencil} alt="" aria-hidden="true" />
          </button>
          <button className="question__action" type="button" aria-label="Delete question" onClick={() => onDelete(question.questionId)}>
            <img src={trash} alt="" aria-hidden="true" />
          </button>
        </div>
      </div>
    </div>
  )
}

export default Question
