/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        library: {
          primary: "#6b4f3a",
          secondary: "#a17449",
          bg: "#f7f4ef",
          accent: "#4a6d5c"
        }
      }
    },
  },
  plugins: [],
};
