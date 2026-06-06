import { useEffect, useState } from 'react'
import { AVAILABLE_LANGUAGE_CODES } from '../entities/models'
import type { AvailableLanguageCode, LocalizedText } from '../entities/models'
import type { UiMessage } from '../entities/models'
import type { GroupNode } from '../api/adminApi'

export type ModalState =
  | { type: 'edit-group'; groupName: string }
  | { type: 'create-group'; groupName: string }
  | { type: 'create-question'; groupName: string }
  | { type: 'delete-group'; groupName: string }
  | { type: 'delete-question'; questionId: string }
  | { type: 'edit-ui-message'; messageName: string }
  | null

export type LocalizedDraft = Record<AvailableLanguageCode, string>

export function createEmptyLocalizedDraft(): LocalizedDraft {
  return Object.fromEntries(AVAILABLE_LANGUAGE_CODES.map((code) => [code, ''])) as LocalizedDraft
}

export function createLocalizedDraft(value?: LocalizedText): LocalizedDraft {
  return Object.fromEntries(
    AVAILABLE_LANGUAGE_CODES.map((code) => [code, value?.[code] ?? '']),
  ) as LocalizedDraft
}

export function buildTitlePayload(draft: LocalizedDraft): LocalizedText {
  const next: LocalizedText = {}
  for (const code of AVAILABLE_LANGUAGE_CODES) {
    const value = draft[code].trim()
    if (value) next[code] = value
  }
  return next
}

export function buildTextPayload(
  draft: LocalizedDraft,
  normalizer: (v: string) => string,
  isEmpty: (v: string) => boolean,
): LocalizedText {
  const next: LocalizedText = {}
  for (const code of AVAILABLE_LANGUAGE_CODES) {
    const value = draft[code]
    if (!isEmpty(value)) next[code] = normalizer(value)
  }
  return next
}

export function areLocalizedDraftsEqual(
  left: LocalizedDraft,
  right: LocalizedDraft,
  normalizer: (value: string) => string = (v) => v,
) {
  return AVAILABLE_LANGUAGE_CODES.every((code) => normalizer(left[code]) === normalizer(right[code]))
}

export function getModalTitle(modal: Exclude<ModalState, null>): string {
  switch (modal.type) {
    case 'edit-group':
      return 'Изменить группу'
    case 'create-group':
      return 'Создать группу'
    case 'create-question':
      return 'Создать вопрос'
    case 'delete-group':
      return 'Удаление группы'
    case 'delete-question':
      return 'Удаление вопроса'
    case 'edit-ui-message':
      return 'Изменить технический вопрос'
  }
}

export function useModalState(
  getGroupNode: (name: string) => GroupNode | null,
  getUiMessage: (name: string) => UiMessage | null,
) {
  const [modal, setModal] = useState<ModalState>(null)
  const [languageCode, setLanguageCode] = useState<AvailableLanguageCode>('ru')
  const [titleValues, setTitleValues] = useState<LocalizedDraft>(() => createEmptyLocalizedDraft())
  const [textValues, setTextValues] = useState<LocalizedDraft>(() => createEmptyLocalizedDraft())

  const currentModalGroup = modal && 'groupName' in modal ? getGroupNode(modal.groupName) : null
  const currentUiMessage = modal && 'messageName' in modal ? getUiMessage(modal.messageName) : null

  useEffect(() => {
    if (!modal) return

    setLanguageCode('ru')

    switch (modal.type) {
      case 'edit-group':
        setTitleValues(createLocalizedDraft(currentModalGroup?.title))
        setTextValues(createEmptyLocalizedDraft())
        break
      case 'edit-ui-message':
        setTitleValues(createEmptyLocalizedDraft())
        setTextValues(createLocalizedDraft(currentUiMessage?.text))
        break
      case 'create-group':
        setTitleValues(createEmptyLocalizedDraft())
        setTextValues(createEmptyLocalizedDraft())
        break
      case 'create-question':
        setTitleValues(createEmptyLocalizedDraft())
        setTextValues(
          Object.fromEntries(
            AVAILABLE_LANGUAGE_CODES.map((code) => [code, '<p></p>']),
          ) as LocalizedDraft,
        )
        break
      default:
        setTitleValues(createEmptyLocalizedDraft())
        setTextValues(createEmptyLocalizedDraft())
    }
  }, [modal?.type, currentModalGroup?.name, currentUiMessage?.name])

  function openModal(nextModal: Exclude<ModalState, null>) {
    setModal(nextModal)
  }

  function closeModal() {
    setModal(null)
  }

  function setTitleValue(value: string) {
    setTitleValues((current) => ({ ...current, [languageCode]: value }))
  }

  function setTextValue(value: string) {
    setTextValues((current) => ({ ...current, [languageCode]: value }))
  }

  return {
    modal,
    languageCode,
    setLanguageCode,
    titleValues,
    textValues,
    titleValue: titleValues[languageCode],
    textValue: textValues[languageCode] || '<p></p>',
    currentModalGroup,
    currentUiMessage,
    openModal,
    closeModal,
    setTitleValue,
    setTextValue,
  }
}
