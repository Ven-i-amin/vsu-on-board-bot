export type LocalizedText = Record<string, string>

type QuestionInit = Partial<Question>

export class Question {
    name: string
    parent: string
    title: LocalizedText
    text: LocalizedText

    constructor(init: QuestionInit = {}) {
        this.name = init.name ?? ''
        this.parent = init.parent ?? ''
        this.title = init.title ?? {}
        this.text = init.text ?? {}
    }
}
