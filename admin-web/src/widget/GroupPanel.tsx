import '../index.css'
import type { Group } from '../api/entities/Group.ts'
import folderImg from '../assets/fi-rr-folder.svg'
import documentImg from '../assets/fi-rs-document.svg'
import addNewImg from '../assets/fi-rr-plus.svg'
import ButtonPanel from '../shared/ButtonPanel.tsx'
import type { Question } from '../api/entities/Question.ts'
import InlineBlock from '../shared/InlineBlock.tsx'

type GroupPanelProps = {
    group: Group
    language: string
    path: string[]
    className?: string
}

function GroupPanel({ group, language, path, className = '' }: GroupPanelProps) {
    return (
        <div className={`border-2 border-black ${className}`}>
            <div className="border-b-2 border-gray-500">
                <div className="items-end justify-start">
                    <h2 className="mb-0 w-fit text-2xl">{group.title[language]}</h2>
                    <CreateLink path={path} className="w-fit items-end" />
                </div>

                <InlineBlock buttonChildren={<img src={addNewImg} alt="add" />} className="w-20">
                    <button>
                        Добавить группу
                    </button>

                    <button>
                        Добавить файл
                    </button>
                </InlineBlock>
            </div>

            <div>
                {group.innerGroups.map((gr) => (
                    <GroupButton
                        key={gr.name}
                        group={gr}
                        language={language}
                        changeable={true}
                        deletable={true}
                    />
                ))}
            </div>

            <div>
                {group.questions.map((question) => (
                    <QuestionButton
                        key={question.name}
                        question={question}
                        language={language}
                        changeable={true}
                        deletable={true}
                    />
                ))}
            </div>
        </div>
    )
}

function CreateLink({ path, className = '' }: { path: string[]; className?: string }) {
    return (
        <div className={`flex ${className}`}>
            {path.map((p) => (
                <div key={p} className="flex items-end">
                    <button className="">
                        <p>{p}</p>
                    </button>
                    <p>/</p>
                </div>
            ))}
        </div>
    )
}

type GroupButtonProps = {
    group: Group
    language: string
    changeable: boolean
    deletable: boolean
    className?: string
}

function GroupButton({
    group,
    language,
    changeable,
    deletable,
    className = '',
}: GroupButtonProps) {
    return (
        <ButtonPanel
            name={group.name}
            title={group.title}
            language={language}
            fileTypeImg={folderImg}
            changeable={changeable}
            deletable={deletable}
            className={className}
        />
    )
}

type QuestionButtonProps = {
    question: Question
    language: string
    changeable: boolean
    deletable: boolean
    className?: string
}

function QuestionButton({
    question,
    language,
    changeable,
    deletable,
    className = '',
}: QuestionButtonProps) {
    return (
        <ButtonPanel
            name={question.name}
            title={question.title}
            language={language}
            fileTypeImg={documentImg}
            changeable={changeable}
            deletable={deletable}
            className={className}
        />
    )
}

export default GroupPanel
