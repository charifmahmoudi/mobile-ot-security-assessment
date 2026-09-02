import { defineConfig } from 'astro/config';
import tailwindcss from '@tailwindcss/vite';

export default defineConfig({
  site: 'https://charifmahmoudi.github.io',
  base: '/mobile-ot-security-assessment',
  trailingSlash: 'always',
  vite: {
    plugins: [tailwindcss()],
  },
});
