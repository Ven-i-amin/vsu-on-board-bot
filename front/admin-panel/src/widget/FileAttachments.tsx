import { useRef } from 'react'
import fileIcon from '../assets/fi-rr-file.svg'
import { API_BASE, type QuestionFileDto } from '../api/adminApi'
import '../pages/AdminPage.css'

type FileAttachmentsProps = {
  files: QuestionFileDto[]
  isUploading?: boolean
  isDisabled?: boolean
  uploadError?: string
  onFileSelect: (file: File) => void
  onRemove: (fileHash: string) => void
}

export function FileAttachments({
  files,
  isUploading = false,
  isDisabled = false,
  uploadError = '',
  onFileSelect,
  onRemove,
}: FileAttachmentsProps) {
  const inputRef = useRef<HTMLInputElement>(null)

  function handleChange(event: React.ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0]
    if (file) {
      onFileSelect(file)
      event.target.value = ''
    }
  }

  return (
    <div className="question-file-list">
      {files.length === 0 && !isUploading && (
        <span className="question-file-list__empty">Файлы не прикреплены</span>
      )}

      {files.map((file) => (
        <div key={file.fileHash} className="question-file-list__item">
          <a
            className="question-file-list__name"
            href={`${API_BASE}/file/${file.fileHash}/content`}
            download={file.fileName}
            target="_blank"
            rel="noreferrer"
          >
            {file.fileName || file.fileHash.slice(0, 16) + '…'}
          </a>
          <button
            className="question-file-list__remove"
            type="button"
            onClick={() => onRemove(file.fileHash)}
            aria-label={`Открепить файл ${file.fileName}`}
            disabled={isDisabled || isUploading}
          >
            ×
          </button>
        </div>
      ))}

      {isUploading && (
        <div className="question-file-list__item question-file-list__item_uploading">
          <span className="question-file-list__uploading-spinner" aria-hidden="true" />
          <span className="question-file-list__uploading-text">Загрузка файла...</span>
        </div>
      )}

      {uploadError && (
        <span className="question-file-list__upload-error">{uploadError}</span>
      )}

      <label
        className={`question-file-list__upload-label${isUploading || isDisabled ? ' question-file-list__upload-label_disabled' : ''}`}
      >
        <input
          ref={inputRef}
          type="file"
          className="question-file-list__file-input"
          onChange={handleChange}
          disabled={isUploading || isDisabled}
          aria-label="Прикрепить файл"
        />
        <img
          src={fileIcon}
          alt=""
          aria-hidden="true"
          style={{ width: '0.95rem', height: '0.95rem', filter: 'var(--app-icon-filter)' }}
        />
        Прикрепить файл
      </label>
    </div>
  )
}
