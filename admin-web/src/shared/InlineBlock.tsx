import { useState, type ReactNode } from 'react'

type InlineBlockProps = {
    children: ReactNode
    className?: string
    buttonChildren?: ReactNode
}

function InlineBlock({
    children,
    className = '',
    buttonChildren,
}: InlineBlockProps) {
    const [open, setOpen] = useState(false)

    return (
        <div className={`relative inline-block ${className}`}>
            <button
                type="button"
                onClick={() => setOpen((prev) => !prev)}
                className="rounded border px-4 py-2"
            >
                {buttonChildren ?? 'Menu'}
            </button>

            {open && (
                <div className="absolute left-0 mt-2 rounded border bg-white shadow">
                    {children}
                </div>
            )}
        </div>
    )
}

export default InlineBlock
