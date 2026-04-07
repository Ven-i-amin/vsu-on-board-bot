import { Mark, mergeAttributes } from '@tiptap/core'

declare module '@tiptap/core' {
  interface Commands<ReturnType> {
    hiddenText: {
      toggleHiddenText: () => ReturnType
      unsetHiddenText: () => ReturnType
    }
  }
}

export const HiddenTextMark = Mark.create({
  name: 'hiddenText',

  parseHTML() {
    return [{ tag: 'span.editor-hidden' }]
  },

  renderHTML({ HTMLAttributes }) {
    return ['span', mergeAttributes(HTMLAttributes, { class: 'editor-hidden' }), 0]
  },

  addCommands() {
    return {
      toggleHiddenText:
        () =>
        ({ commands }: { commands: { toggleMark: (name: string) => boolean } }) =>
          commands.toggleMark(this.name),
      unsetHiddenText:
        () =>
        ({ commands }: { commands: { unsetMark: (name: string) => boolean } }) =>
          commands.unsetMark(this.name),
    }
  },
})
