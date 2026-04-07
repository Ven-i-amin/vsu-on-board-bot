import './AdminPage.css'
import { Group as GroupModel, UiMessage } from '../entities/models'
import Group from '../widget/Group'
import Section from '../widget/Section'
import StatsPanel from '../widget/StatsPanel'
import UiMessageItem from '../widget/UiMessageItem'

type AdminPageProps = {
  groups: GroupModel[]
  uiMessages: UiMessage[]
  langCode?: string
  onEditGroup: (groupName: string) => void
  onAddChild: (groupName: string) => void
  onDeleteGroup: (groupName: string) => void
  onEditQuestion: (questionId: string) => void
  onDeleteQuestion: (questionId: string) => void
  onEditUiMessage: (messageName: string) => void
}

function AdminPage({
  groups,
  uiMessages,
  langCode = 'ru',
  onEditGroup,
  onAddChild,
  onDeleteGroup,
  onEditQuestion,
  onDeleteQuestion,
  onEditUiMessage,
}: AdminPageProps) {
  return (
    <main className="page-content">
      <div className="page-content__columns">
        <Section title="Статистика">
          <StatsPanel />
        </Section>

        <Section title="Технические вопросы" variant="muted">
          <div className="page-content__list">
            {uiMessages.map((message) => (
              <UiMessageItem
                key={message.name}
                message={message}
                langCode={langCode}
                onEdit={onEditUiMessage}
              />
            ))}
          </div>
        </Section>

        <Section title="Группы и вопросы">
          <div className="page-content__list">
            {groups.map((group) => (
              <Group
                key={group.name}
                group={group}
                langCode={langCode}
                onEditGroup={onEditGroup}
                onAddChild={onAddChild}
                onDeleteGroup={onDeleteGroup}
                onEditQuestion={onEditQuestion}
                onDeleteQuestion={onDeleteQuestion}
              />
            ))}
          </div>
        </Section>
      </div>
    </main>
  )
}

export default AdminPage
