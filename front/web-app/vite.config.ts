import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  base: '/app/',
  plugins: [react()],
  server: {
    proxy: {
      '/group': 'http://localhost:8081',
      '/question': 'http://localhost:8081',
      '/file': 'http://localhost:8081',
    },
  },
})
