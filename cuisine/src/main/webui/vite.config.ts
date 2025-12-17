import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
    // depending on your application, base can also be "/"
    base: '',
    plugins: [react()],
    server: {
        // this ensures that the browser opens upon server rangeStart
        open: false,
        // this sets a default port to 3000, you can change this
        port: 3000,
    },
    define: {
        // By default, Vite doesn't include shims for NodeJS/
        // necessary for segment analytics lib to work
        global: {},
    },
    build: {
        // Should fix when building: (!) Some chunks are larger than 500 kB after minification.
        chunkSizeWarningLimit: 1600,
        outDir: '../resources/META-INF/resources',
        emptyOutDir: true, // empty the folder before each build
    },
})
