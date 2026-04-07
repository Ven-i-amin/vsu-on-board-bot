type UserInit = Partial<User>

export class User {
  chatId?: number
  langCode: string

  constructor(init: UserInit = {}) {
    this.chatId = init.chatId
    this.langCode = init.langCode ?? ''
  }
}
