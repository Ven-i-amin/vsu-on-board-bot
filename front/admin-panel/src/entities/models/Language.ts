export class Language {
  code: string
  name: string

  constructor(code = '', name = '') {
    this.code = code
    this.name = name
  }
}

export const AVAILABLE_LANGUAGE_CODES = ['ru', 'en'] as const

export type AvailableLanguageCode = (typeof AVAILABLE_LANGUAGE_CODES)[number]

export const AVAILABLE_LANGUAGES = [
  new Language('ru', 'Русский'),
  new Language('en', 'English'),
]
