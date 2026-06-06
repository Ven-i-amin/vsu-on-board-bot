import './QuestionText.css'

type Props = { html: string }

// Converts bot-format text (HTML + \n) to safe web HTML.
// Allowed tags: b, i, s, u, code, pre, span.editor-hidden
function processHtml(raw: string): string {
  // Split by <pre> blocks to avoid converting \n inside code
  const parts = raw.split(/(<pre\b[^>]*>[\s\S]*?<\/pre>)/gi)

  return parts
    .map((part, i) => {
      if (i % 2 === 1) {
        // pre block — strip tags other than the pre itself and code inside
        return part.replace(/<pre\b([^>]*)>([\s\S]*?)<\/pre>/gi, (_m, attrs, content) => {
          const langMatch = /language="([^"]+)"/.exec(attrs)
          const lang = langMatch ? langMatch[1] : ''
          const label = lang ? `<span class="qt-lang">${escapeHtml(lang)}</span>` : ''
          return `<pre class="qt-pre">${label}<code>${escapeHtml(content)}</code></pre>`
        })
      }
      // Normal text — convert \n to <br> and sanitize tags
      return sanitize(part.replace(/\n/g, '<br>'))
    })
    .join('')
}

function escapeHtml(text: string): string {
  return text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
}

// Keep only allowed tags, strip the rest
const ALLOWED = /^(b|i|s|u|code|br|span)$/i

function sanitize(html: string): string {
  return html.replace(/<\/?([a-z][a-z0-9]*)\b([^>]*)>/gi, (match, tag, attrs) => {
    if (!ALLOWED.test(tag)) return ''
    // For span, only allow class="editor-hidden"
    if (tag.toLowerCase() === 'span') {
      if (/class="editor-hidden"/.test(attrs)) {
        return `<span class="qt-spoiler">`
      }
      return '<span>'
    }
    return match
  })
}

export function QuestionText({ html }: Props) {
  return (
    <div
      className="qt-body"
      dangerouslySetInnerHTML={{ __html: processHtml(html) }}
    />
  )
}
