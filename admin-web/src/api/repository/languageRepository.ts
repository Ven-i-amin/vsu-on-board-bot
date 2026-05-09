import { apiClient } from '../client'
import type { LanguageResponse } from '../types'

export const languageRepository = {
    getAll(): Promise<LanguageResponse[]> {
        return apiClient.get<LanguageResponse[]>('/api/language')
    },
}
