import '../index.css';
import GroupPanel from '../widget/GroupPanel'
import {Group} from "../api/entities/Group.ts";
import {Question} from "../api/entities/Question.ts";

function Explorer() {
    const group: Group = new Group({
        name: 'privet',
        title: { ru: 'Привет' },
        innerGroups: [
            new Group({
                name: 'privet2',
                title: { ru: 'Привет2' }
            })
        ],
        questions: [
            new Question({
                name: "question",
                title: { ru: 'Вопрос' },
                text: { ru: 'Вопрос пользователю' }
            })
        ]
    })

    const language: string = 'ru'

    const path: string[] = ['a', 'b', 'c']

    return (
        <div>
            <GroupPanel group={group} language={language} path={path} className="mx-10"/>
        </div>
    );
}

export default Explorer;
