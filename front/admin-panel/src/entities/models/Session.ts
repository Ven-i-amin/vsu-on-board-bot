import { Group } from './Group'
import { UiMessage } from './UiMessage'

type SessionInit = Partial<Session>

export class Session {
  chatId?: number
  update?: unknown
  botState: string
  messageState: string
  globalState: string
  start?: Group
  groupWindow: Group[]
  uiMessages: UiMessage[]
  lastMessageId?: number
  langCode: string

  constructor(init: SessionInit = {}) {
    this.chatId = init.chatId
    this.update = init.update
    this.botState = init.botState ?? ''
    this.messageState = init.messageState ?? ''
    this.globalState = init.globalState ?? ''
    this.start = init.start
      ? init.start instanceof Group
        ? init.start
        : new Group(init.start)
      : undefined
    this.groupWindow = (init.groupWindow ?? []).map((group) =>
      group instanceof Group ? group : new Group(group),
    )
    this.uiMessages = (init.uiMessages ?? []).map((message) =>
      message instanceof UiMessage ? message : new UiMessage(message),
    )
    this.lastMessageId = init.lastMessageId
    this.langCode = init.langCode ?? ''
  }
}
