## Purpose

Quick, repository-specific guidance for AI coding agents working on this React + Vite frontend.

## Big picture

- Frontend: React (via Vite) single-page app using `react-router-dom` for routing and Tailwind for styling.
- Backend: expected at `http://localhost:8080/api` (see `src/api/axios.js`). The frontend talks to the backend via REST endpoints (e.g. `/auth/login`).
- Auth: token-based with localStorage + Authorization header; `withCredentials: true` is enabled on the axios client.

## Key files to inspect first

- `package.json` — dev scripts: `npm run dev`, `npm run build`, `npm run preview`, `npm run lint`.
- `src/api/axios.js` — central axios instance; adds `Authorization: Bearer <token>` from `localStorage` and sets `baseURL` and `withCredentials`.
- `src/api/auth.js` — thin wrappers for auth endpoints (login used in `src/pages/Login.jsx`).
- `src/context/AuthContext.jsx` — holds `user`, `token`, `loading` and exposes `login()` / `logout()`; persists to `localStorage`.
- `src/hooks/useAuth.js` — convenience hook to access `AuthContext`.
- `src/components/ProtectedRoute.jsx` — intended guard for private routes (watch for `useAuth` usage here).
- `src/components/Layout.jsx` and `src/components/Navbar.jsx` — layout composition; `Layout` uses `Outlet` to render routes.

## Patterns and conventions (examples)

- API calls: import the shared client `import axios from "../api/axios"` and call endpoints relative to `/api` (e.g. `axios.post('/auth/login', {...})`).
- Auth flow: after a successful login the backend should return user data + token; `AuthContext.login(userData, jwtToken)` stores both and sets `localStorage` keys `token` and `user`.
- Routing: routes are declared in `App.jsx`. Public routes: `/login`, `/register`. App layout is mounted via a parent `<Route element={<Layout/>}>` that holds the main pages.

## Integration & environment notes

- Backend base URL is hard-coded in `src/api/axios.js` as `http://localhost:8080/api`. If the backend runs elsewhere, update that file or use an env-based override.
- `withCredentials: true` indicates the backend may use cookies in addition to tokens — be mindful of CORS settings on the backend when testing.

## Common gotchas (repo-observed)

- `ProtectedRoute.jsx` currently references `useAuth` instead of calling it (`const {token, loading} = useAuth` should be `useAuth()`). Expect easy fixes like this when adding guards.
- `AuthContext` stores `user` as JSON in `localStorage` (`user` key) and `token` key as plain string — keep both in sync when updating login/logout flows.

## Developer workflows / commands

- Start dev server (PowerShell):
  ```pwsh
  npm install
  npm run dev
  ```
- Build for production:
  ```pwsh
  npm run build
  ```
- Lint the codebase:
  ```pwsh
  npm run lint
  ```

## When making changes

- Prefer editing the shared axios client (`src/api/axios.js`) for cross-cutting HTTP concerns (headers, baseURL, interceptors).
- Update `AuthContext.jsx` if changing authentication shape; keep the `localStorage` keys `token` and `user` consistent.
- Update routes in `App.jsx` when adding pages; wrap new protected pages with `ProtectedRoute`.

## Quick debugging tips

- If API calls fail locally, check CORS and the backend listening port (`8080`) and confirm the `baseURL` in `src/api/axios.js`.
- Use browser devtools to inspect `localStorage` keys `token` and `user` and network request headers to confirm `Authorization` is present.

If anything here is unclear or you want additional examples (tests, CI hints, or environment overrides), tell me which areas to expand.
