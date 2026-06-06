import { useEffect, useState } from 'react'
import { EditorContent, useEditor, useEditorState } from '@tiptap/react'
import StarterKit from '@tiptap/starter-kit'
import Link from '@tiptap/extension-link'
import Underline from '@tiptap/extension-underline'
import type { Node as ProseMirrorNode } from '@tiptap/pm/model'
import './RichTextEditor.css'
import OverlayModal from './OverlayModal'
import { HiddenTextMark } from './HiddenTextMark'

type RichTextEditorProps = {
  value: string
  onChange: (value: string) => void
}

type LinkPromptState = {
  open: boolean
  href: string
}

type ToolbarAction = 'bold' | 'italic' | 'underline' | 'strike' | 'hidden' | 'link'

const toolbarItems: Array<{ action: ToolbarAction; label: string }> = [
  { action: 'bold', label: 'B' },
  { action: 'italic', label: 'I' },
  { action: 'underline', label: 'U' },
  { action: 'strike', label: 'S' },
  { action: 'hidden', label: 'Скрытый' },
  { action: 'link', label: 'Ссылка' },
]

const markNameByAction: Record<ToolbarAction, string> = {
  bold: 'bold',
  italic: 'italic',
  underline: 'underline',
  strike: 'strike',
  hidden: 'hiddenText',
  link: 'link',
}

const hasMark = (node: ProseMirrorNode | null, markName: string) =>
  Boolean(node?.marks.some((mark) => mark.type.name === markName))

const normalizeEditorContent = (value: string) => {
  const trimmed = value.trim()
  if (!trimmed) {
    return '<p></p>'
  }

  // Preserve already-structured block HTML, but still rebuild plain/inline markup text
  // from newline-based storage so line breaks survive reopening the editor.
  if (/<(?:p|div|br|ul|ol|li|blockquote|h[1-6]|pre)\b/i.test(trimmed)) {
    return trimmed
  }

  return trimmed
    .split(/\n{2,}/)
    .map((paragraph) => `<p>${paragraph.replace(/\n/g, '<br>')}</p>`)
    .join('')
}

function RichTextEditor({ value, onChange }: RichTextEditorProps) {
  const [linkPrompt, setLinkPrompt] = useState<LinkPromptState>({ open: false, href: '' })

  const editor = useEditor({
    extensions: [
      StarterKit.configure({
        code: false,
        codeBlock: false,
      }),
      Underline,
      Link.configure({
        openOnClick: false,
        autolink: false,
        linkOnPaste: true,
      }),
      HiddenTextMark,
    ],
    content: value,
    immediatelyRender: false,
    onUpdate: ({ editor: currentEditor }) => {
      onChange(currentEditor.getHTML())
    },
  })

  useEffect(() => {
    if (!editor) {
      return
    }

    const normalizedValue = normalizeEditorContent(value)

    if (editor.getHTML() !== normalizedValue) {
      editor.commands.setContent(normalizedValue, { emitUpdate: false })
    }
  }, [editor, value])

  const activeMarks = useEditorState({
    editor,
    selector: ({ editor: currentEditor }) => ({
      bold: currentEditor?.isActive('bold') ?? false,
      italic: currentEditor?.isActive('italic') ?? false,
      underline: currentEditor?.isActive('underline') ?? false,
      strike: currentEditor?.isActive('strike') ?? false,
      hidden: currentEditor?.isActive('hiddenText') ?? false,
      link: currentEditor?.isActive('link') ?? false,
    }),
  })

  const isCursorInsideMark = (action: ToolbarAction) => {
    if (!editor || !editor.state.selection.empty) {
      return false
    }

    const markName = markNameByAction[action]
    const { $from } = editor.state.selection

    return hasMark($from.nodeBefore, markName) && hasMark($from.nodeAfter, markName)
  }

  const unsetActiveMarkAtCursor = (action: ToolbarAction) => {
    if (!editor || !isCursorInsideMark(action)) {
      return false
    }

    switch (action) {
      case 'bold':
        return editor.chain().focus().extendMarkRange('bold').unsetBold().run()
      case 'italic':
        return editor.chain().focus().extendMarkRange('italic').unsetItalic().run()
      case 'underline':
        return editor.chain().focus().extendMarkRange('underline').unsetUnderline().run()
      case 'strike':
        return editor.chain().focus().extendMarkRange('strike').unsetStrike().run()
      case 'hidden':
        return editor.chain().focus().extendMarkRange('hiddenText').unsetHiddenText().run()
      case 'link':
        return editor.chain().focus().extendMarkRange('link').unsetLink().run()
    }
  }

  const handleToolbarAction = (action: ToolbarAction) => {
    if (!editor) {
      return
    }

    if (unsetActiveMarkAtCursor(action)) {
      return
    }

    switch (action) {
      case 'bold':
        editor.chain().focus().toggleBold().run()
        return
      case 'italic':
        editor.chain().focus().toggleItalic().run()
        return
      case 'underline':
        editor.chain().focus().toggleUnderline().run()
        return
      case 'strike':
        editor.chain().focus().toggleStrike().run()
        return
      case 'hidden':
        editor.chain().focus().toggleHiddenText().run()
        return
      case 'link':
        setLinkPrompt({ open: true, href: '' })
    }
  }

  const handleCreateLink = () => {
    if (!editor || !linkPrompt.href.trim()) {
      setLinkPrompt({ open: false, href: '' })
      return
    }

    editor.chain().focus().extendMarkRange('link').setLink({ href: linkPrompt.href.trim() }).run()
    setLinkPrompt({ open: false, href: '' })
  }

  return (
    <>
      <div className="editor">
        <div className="editor__toolbar">
          {toolbarItems.map((item) => (
            <button
              key={item.action}
              className={`editor__tool ${activeMarks?.[item.action] ? 'editor__tool_active' : ''}`}
              type="button"
              onMouseDown={(event) => {
                event.preventDefault()
                handleToolbarAction(item.action)
              }}
            >
              {item.label}
            </button>
          ))}
        </div>

        <div className="editor__surface">
          <EditorContent editor={editor} />
        </div>
      </div>

      {linkPrompt.open && (
        <OverlayModal title="Добавить ссылку" onClose={() => setLinkPrompt({ open: false, href: '' })}>
          <div className="modal-form">
            <label className="modal-form__field">
              <span className="modal-form__label">Ссылка</span>
              <input
                className="modal-form__input"
                type="url"
                placeholder="https://example.com"
                value={linkPrompt.href}
                onChange={(event) =>
                  setLinkPrompt((current) => ({ ...current, href: event.target.value }))
                }
              />
            </label>

            <div className="modal-form__actions">
              <button
                className="modal-form__button modal-form__button_secondary"
                type="button"
                onClick={() => setLinkPrompt({ open: false, href: '' })}
              >
                Отмена
              </button>
              <button className="modal-form__button" type="button" onClick={handleCreateLink}>
                Подтвердить
              </button>
            </div>
          </div>
        </OverlayModal>
      )}
    </>
  )
}

export default RichTextEditor
