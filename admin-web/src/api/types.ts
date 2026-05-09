import type { LocalizedText } from './entities/Question'

export type GroupResponse = {
    name: string
    title: LocalizedText
    parentName: string
    innerGroups: GroupResponse[]
    questions: QuestionResponse[]
}

export type GroupDto = {
    groupId: string
    name: string
    title: LocalizedText
    parentName: string
}

export type QuestionResponse = {
    questionId: string
    name: string
    parent: string
    title: LocalizedText
    text: LocalizedText
}

export type QuestionDto = QuestionResponse

export type LanguageResponse = {
    code: string
    name: LocalizedText
}

export type UiMessageDto = {
    id: string
    name: string
    description: LocalizedText
    text: LocalizedText
}

export type LanguageCountResponse = {
    languageCode: string
    name: LocalizedText
    count: number
}

export type TopQuestionResponse = {
    name: string
    parent: string
    title: LocalizedText
    text: LocalizedText
    using: number
}

export type AuthTokenResponse = {
    token: string
}

export type GroupCreateRequest = {
    title: LocalizedText
    parentName: string
}

export type GroupTitleUpdateRequest = {
    title: LocalizedText
}

export type QuestionCreateRequest = {
    groupName: string
    title: LocalizedText
    text: LocalizedText
}

export type QuestionUpdateRequest = {
    title: LocalizedText
    text: LocalizedText
}

export type UiMessageUpdateRequest = {
    text: LocalizedText
}

export type AdminLoginRequest = {
    email: string
    password: string
}

export type AdminRegisterRequest = {
    email: string
    password: string
}
