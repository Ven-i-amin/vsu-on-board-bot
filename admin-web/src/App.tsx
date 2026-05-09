import Explorer from './pages/Explorer'

function App() {
  return (
    <div className="min-h-screen">
      <header className="border-b border-black/10 px-6 py-4 text-left">
        <p className="text-sm font-medium uppercase tracking-[0.2em] text-black/60">
          Admin Web
        </p>
      </header>

      <Explorer />
    </div>
  )
}

export default App
