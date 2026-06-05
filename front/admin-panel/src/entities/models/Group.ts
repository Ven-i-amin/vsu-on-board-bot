import { Question, type LocalizedText } from './Question'

type GroupInit = Partial<Group>

export class Group {
  name: string
  title: LocalizedText
  parentName: string
  innerGroups: Group[]
  questions: Question[]

  constructor(init: GroupInit = {}) {
    this.name = init.name ?? ''
    this.title = init.title ?? {}
    this.parentName = init.parentName ?? ''
    this.innerGroups = (init.innerGroups ?? []).map((group) =>
      group instanceof Group ? group : new Group(group),
    )
    this.questions = (init.questions ?? []).map((question) =>
      question instanceof Question ? question : new Question(question),
    )
  }
}
