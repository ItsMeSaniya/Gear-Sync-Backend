// tailwind.config.ts
import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./index.html",               // include your index.html
    "./src/**/*.{ts,tsx,js,jsx}" // include all React components/pages
  ],
  theme: {
    extend: {},
  },
  plugins: [],
};

export default config;