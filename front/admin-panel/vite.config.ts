import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

const gatewayTarget = process.env.VITE_API_BASE_URL || 'http://localhost'

// https://vite.dev/config/
export default defineConfig({
  base: '/admin/',
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: gatewayTarget,
        changeOrigin: true,
        secure: false,
      },
    },
  },
})
