import './AdminPage.css'
import Section from '../widget/Section'
import StatsPanel from '../widget/StatsPanel'

type StatisticsPageProps = {
  langCode?: string
}

function StatisticsPage({ langCode = 'ru' }: StatisticsPageProps) {
  return (
    <main className="page-content">
      <div className="page-content__columns">
        <Section title="Статистика" collapsible={false}>
          <StatsPanel langCode={langCode} />
        </Section>
      </div>
    </main>
  )
}

export default StatisticsPage
