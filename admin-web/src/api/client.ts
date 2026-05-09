const DEFAULT_API_URL = 'http://localhost:8080'
const ACCESS_TOKEN_KEY = 'accessToken'

type HttpMethod = 'GET' | 'POST' | 'PATCH' | 'DELETE'

type RequestOptions = {
    body?: unknown
    headers?: HeadersInit
}

function buildHeaders(headers?: HeadersInit, hasBody?: boolean): Headers {
    const result = new Headers(headers)
    const token = localStorage.getItem(ACCESS_TOKEN_KEY)

    if (hasBody && !result.has('Content-Type')) {
        result.set('Content-Type', 'application/json')
    }

    if (token && !result.has('Authorization')) {
        result.set('Authorization', `Bearer ${token}`)
    }

    return result
}

function getApiUrl(): string {
    return import.meta.env.VITE_API_URL ?? DEFAULT_API_URL
}

async function request<T>(
    path: string,
    method: HttpMethod,
    options: RequestOptions = {},
): Promise<T> {
    const hasBody = options.body !== undefined
    const response = await fetch(`${getApiUrl()}${path}`, {
        method,
        headers: buildHeaders(options.headers, hasBody),
        body: hasBody ? JSON.stringify(options.body) : undefined,
    })

    if (!response.ok) {
        const message = await response.text()
        throw new Error(message || `HTTP ${response.status}`)
    }

    if (response.status === 204) {
        return undefined as T
    }

    return (await response.json()) as T
}

export function setAccessToken(token: string): void {
    localStorage.setItem(ACCESS_TOKEN_KEY, token)
}

export function clearAccessToken(): void {
    localStorage.removeItem(ACCESS_TOKEN_KEY)
}

export function getAccessToken(): string | null {
    return localStorage.getItem(ACCESS_TOKEN_KEY)
}

export const apiClient = {
    get: <T>(path: string, headers?: HeadersInit) =>
        request<T>(path, 'GET', { headers }),
    post: <T>(path: string, body?: unknown, headers?: HeadersInit) =>
        request<T>(path, 'POST', { body, headers }),
    patch: <T>(path: string, body?: unknown, headers?: HeadersInit) =>
        request<T>(path, 'PATCH', { body, headers }),
    delete: <T>(path: string, headers?: HeadersInit) =>
        request<T>(path, 'DELETE', { headers }),
}
