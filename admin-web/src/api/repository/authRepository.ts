import { apiClient, setAccessToken } from '../client'
import type {
    AdminLoginRequest,
    AdminRegisterRequest,
    AuthTokenResponse,
} from '../types'

async function authorize(
    path: string,
    payload: AdminLoginRequest | AdminRegisterRequest,
): Promise<AuthTokenResponse> {
    const response = await apiClient.post<AuthTokenResponse>(path, payload)
    setAccessToken(response.token)
    return response
}

export const authRepository = {
    login: (payload: AdminLoginRequest) =>
        authorize('/api/auth/login', payload),
    register: (payload: AdminRegisterRequest) =>
        authorize('/api/auth/register', payload),
}
