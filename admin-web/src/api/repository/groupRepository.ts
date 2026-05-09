import { apiClient } from '../client'
import { Group } from '../entities/Group'
import type {
    GroupCreateRequest,
    GroupDto,
    GroupResponse,
    GroupTitleUpdateRequest,
} from '../types'

function toGroup(group: GroupResponse): Group {
    return new Group(group)
}

export const groupRepository = {
    async getByName(groupName: string, depth = 0): Promise<Group> {
        const group = await apiClient.get<GroupResponse>(
            `/api/group/${encodeURIComponent(groupName)}?depth=${depth}`,
        )
        return toGroup(group)
    },

    async getInner(groupName: string): Promise<Group[]> {
        const groups = await apiClient.get<GroupResponse[]>(
            `/api/group/${encodeURIComponent(groupName)}/inner`,
        )
        return groups.map(toGroup)
    },

    async getStart(): Promise<Group> {
        const group = await apiClient.get<GroupResponse>('/api/group/start')
        return toGroup(group)
    },

    create(payload: GroupCreateRequest): Promise<GroupDto> {
        return apiClient.post<GroupDto>('/api/group', payload)
    },

    updateTitle(
        groupName: string,
        payload: GroupTitleUpdateRequest,
    ): Promise<GroupDto> {
        return apiClient.patch<GroupDto>(
            `/api/group/${encodeURIComponent(groupName)}/title`,
            payload,
        )
    },

    delete(groupName: string): Promise<void> {
        return apiClient.delete<void>(`/api/group/${encodeURIComponent(groupName)}`)
    },
}
