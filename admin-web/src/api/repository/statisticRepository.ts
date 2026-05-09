import { apiClient } from '../client'
import type { LanguageCountResponse, TopQuestionResponse } from '../types'

export const statisticRepository = {
    getTopLanguages(): Promise<LanguageCountResponse[]> {
        return apiClient.get<LanguageCountResponse[]>(
            '/api/statistic/topLanguages',
        )
    },

    getTopQuestions(): Promise<TopQuestionResponse[]> {
        return apiClient.get<TopQuestionResponse[]>(
            '/api/statistic/topQuestions',
        )
    },
}
