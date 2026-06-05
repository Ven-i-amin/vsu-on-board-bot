import { useEffect, useState, type RefObject } from 'react'

type UseActionsWrapParams = {
  containerRef: RefObject<HTMLElement | null>
  leadRef: RefObject<HTMLElement | null>
  titleRef: RefObject<HTMLElement | null>
  actionsRef: RefObject<HTMLElement | null>
  nestingPenalty?: number
}

export function useActionsWrap({
  containerRef,
  leadRef,
  titleRef,
  actionsRef,
  nestingPenalty = 0,
}: UseActionsWrapParams) {
  const [isWrapped, setIsWrapped] = useState(false)

  useEffect(() => {
    const container = containerRef.current
    const lead = leadRef.current
    const title = titleRef.current
    const actions = actionsRef.current

    if (!container || !lead || !title || !actions) {
      return
    }

    const measureTextWidth = () => {
      const computed = window.getComputedStyle(title)
      const canvas = document.createElement('canvas')
      const context = canvas.getContext('2d')

      if (!context) {
        return Math.ceil(title.scrollWidth)
      }

      context.font = [
        computed.fontStyle,
        computed.fontVariant,
        computed.fontWeight,
        computed.fontSize,
        computed.fontFamily,
      ].join(' ')

      const text = title.textContent ?? ''
      const letterSpacing = Number.parseFloat(computed.letterSpacing || '0') || 0
      const measured = context.measureText(text).width
      const spacing = Math.max(0, text.length - 1) * letterSpacing

      return Math.ceil(measured + spacing)
    }

    const measure = () => {
      const containerWidth = container.clientWidth
      const leadWidth = lead.offsetWidth
      const styles = window.getComputedStyle(container)
      const gap = Number.parseFloat(styles.gap || styles.columnGap || '0') || 0
      const paddingLeft = Number.parseFloat(styles.paddingLeft || '0') || 0
      const paddingRight = Number.parseFloat(styles.paddingRight || '0') || 0
      const titleWidth = measureTextWidth()
      const actionsStyles = window.getComputedStyle(actions)
      const actionsGap =
        Number.parseFloat(actionsStyles.gap || actionsStyles.columnGap || '0') || 0
      const actionItems = Array.from(actions.children) as HTMLElement[]
      const actionsWidth =
        actionItems.reduce((sum, item) => sum + item.offsetWidth, 0)
        + Math.max(0, actionItems.length - 1) * actionsGap

      const requiredWidth =
        paddingLeft + paddingRight + leadWidth + titleWidth + actionsWidth + gap * 2

      setIsWrapped(requiredWidth > containerWidth - nestingPenalty)
    }

    measure()

    const observer = new ResizeObserver(measure)
    observer.observe(container)
    observer.observe(lead)
    observer.observe(title)
    observer.observe(actions)
    window.addEventListener('resize', measure)

    return () => {
      observer.disconnect()
      window.removeEventListener('resize', measure)
    }
  }, [actionsRef, containerRef, leadRef, nestingPenalty, titleRef])

  return isWrapped
}
