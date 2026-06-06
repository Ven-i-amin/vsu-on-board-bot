import { useEffect, useState } from 'react'
import { getAuthToken } from '../api/adminApi'

export type AppRoute =
  | { type: 'login' }
  | { type: 'dashboard' }
  | { type: 'statistics' }
  | { type: 'registration' }
  | { type: 'technical-questions' }
  | { type: 'groups'; groupName: string | null }
  | { type: 'question'; groupName: string; questionName: string }

const ADMIN_BASE_PATH = '/admin'

function parseRoute(pathname: string): AppRoute {
  const normalized =
    pathname.length > 1 && pathname.endsWith('/') ? pathname.slice(0, -1) : pathname
  const withoutBase = normalized.startsWith(ADMIN_BASE_PATH)
    ? normalized.slice(ADMIN_BASE_PATH.length) || '/'
    : normalized
  const segments = withoutBase.split('/').filter(Boolean).map(decodeURIComponent)

  if (!segments.length) return { type: 'dashboard' }

  switch (segments[0]) {
    case 'login':
      return { type: 'login' }
    case 'statistics':
      return { type: 'statistics' }
    case 'registration':
      return { type: 'registration' }
    case 'technical-questions':
      return { type: 'technical-questions' }
    case 'groups': {
      const groupName = segments[1] ?? null
      if (groupName && segments[2] === 'question' && segments[3]) {
        return { type: 'question', groupName, questionName: segments[3] }
      }
      return { type: 'groups', groupName }
    }
    default:
      return { type: 'dashboard' }
  }
}

export function buildRoutePath(route: AppRoute): string {
  switch (route.type) {
    case 'login':
      return `${ADMIN_BASE_PATH}/login`
    case 'dashboard':
      return ADMIN_BASE_PATH
    case 'statistics':
      return `${ADMIN_BASE_PATH}/statistics`
    case 'registration':
      return `${ADMIN_BASE_PATH}/registration`
    case 'technical-questions':
      return `${ADMIN_BASE_PATH}/technical-questions`
    case 'groups':
      return route.groupName
        ? `${ADMIN_BASE_PATH}/groups/${encodeURIComponent(route.groupName)}`
        : `${ADMIN_BASE_PATH}/groups`
    case 'question':
      return `${ADMIN_BASE_PATH}/groups/${encodeURIComponent(route.groupName)}/question/${encodeURIComponent(route.questionName)}`
  }
}

function normalizeRoute(route: AppRoute, isAuthenticated: boolean): AppRoute {
  if (!isAuthenticated) return { type: 'login' }
  if (route.type === 'login') return { type: 'dashboard' }
  return route
}

export function useAppRouter(isAuthenticated: boolean) {
  const [route, setRoute] = useState<AppRoute>(() =>
    normalizeRoute(parseRoute(window.location.pathname), Boolean(getAuthToken())),
  )

  useEffect(() => {
    const handlePopState = () =>
      setRoute(normalizeRoute(parseRoute(window.location.pathname), Boolean(getAuthToken())))

    window.addEventListener('popstate', handlePopState)
    return () => window.removeEventListener('popstate', handlePopState)
  }, [])

  useEffect(() => {
    const normalized = normalizeRoute(route, isAuthenticated)
    if (buildRoutePath(normalized) !== buildRoutePath(route)) {
      navigate(normalized, { replace: true })
    }
  }, [isAuthenticated, route])

  function navigate(nextRoute: AppRoute, options?: { replace?: boolean }) {
    const normalized = normalizeRoute(nextRoute, isAuthenticated)
    const nextPath = buildRoutePath(normalized)
    const currentPath = `${window.location.pathname}${window.location.search}${window.location.hash}`

    if (currentPath !== nextPath) {
      if (options?.replace) {
        window.history.replaceState(null, '', nextPath)
      } else {
        window.history.pushState(null, '', nextPath)
      }
    }

    setRoute(normalized)
  }

  return { route, navigate }
}
