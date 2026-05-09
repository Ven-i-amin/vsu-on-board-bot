import { apiClient } from '../client'
import type {
    QuestionCreateRequest,
    QuestionDto,
    QuestionUpdateRequest,
} from '../types'

export const questionRepository = {
    create(payload: QuestionCreateRequest): Promise<void> {
        return apiClient.post<void>('/api/question', payload)
    },

    update(
        questionId: string,
        payload: QuestionUpdateRequest,
    ): Promise<QuestionDto> {
        return apiClient.patch<QuestionDto>(
            `/api/question/${encodeURIComponent(questionId)}`,
            payload,
        )
    },

    delete(questionId: string): Promise<void> {
        return apiClient.delete<void>(
            `/api/question/${encodeURIComponent(questionId)}`,
        )
    },
}
