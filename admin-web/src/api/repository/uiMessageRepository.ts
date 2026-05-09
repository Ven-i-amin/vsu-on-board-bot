import { apiClient } from '../client'
import type { UiMessageDto, UiMessageUpdateRequest } from '../types'

export const uiMessageRepository = {
    getAll(): Promise<UiMessageDto[]> {
        return apiClient.get<UiMessageDto[]>('/api/ui-message')
    },

    getByName(name: string): Promise<UiMessageDto> {
        return apiClient.get<UiMessageDto>(
            `/api/ui-message/${encodeURIComponent(name)}`,
        )
    },

    update(name: string, payload: UiMessageUpdateRequest): Promise<UiMessageDto> {
        return apiClient.patch<UiMessageDto>(
            `/api/ui-message/${encodeURIComponent(name)}`,
            payload,
        )
    },
}
