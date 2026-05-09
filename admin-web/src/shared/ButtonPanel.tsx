import '../index.css';
import changeImg from "../assets/fi-rr-pencil.svg"
import trashImg from "../assets/fi-rs-trash.svg"
import type {LocalizedText} from "../api/entities/Question.ts";

type ButtonPanelProps = {
    name: string,
    title: LocalizedText,
    language: string,
    fileTypeImg: string,
    changeable: boolean,
    deletable: boolean,
    className?: string
}

function ButtonPanel({ name: _name, title, language, fileTypeImg, changeable, deletable, className = '' }: ButtonPanelProps ) {
    return (
        <div className={`flex ${className}`}>
            <button className="flex" onClick={() => console.log("selected " + _name)}>
            <img src = {fileTypeImg} alt="type image" />
            <p> {title[language]} </p>

            </button>

            {
                changeable &&
                <button onClick={() => console.log("change " + _name)}>
                    <img src={changeImg} alt="change" />
                </button>
            }

            {
                deletable &&
                <button onClick={() => console.log("trash " + _name)}>
                    <img src={trashImg} alt="trash" />
                </button>
            }
        </div>
    )
}

export default ButtonPanel
