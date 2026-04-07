import type { LocalizedText } from './Question'

type UiMessageInit = Partial<UiMessage>

export class UiMessage {
  name: string
  description: LocalizedText
  text: LocalizedText

  constructor(init: UiMessageInit = {}) {
    this.name = init.name ?? ''
    this.description = init.description ?? {}
    this.text = init.text ?? {}
  }
}
