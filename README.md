# Nirvana

**Nirvana** is an AI-powered mental wellness platform that helps users understand and manage their emotional health through conversation, self-reflection, and intelligent insight. It acts as a digital emotional companion — combining a conversational AI chatbot, daily mood/journal tracking, AI-driven sentiment analysis, and long-term pattern tracking into a single secure platform.

> Built as a full-stack learning and portfolio project to demonstrate production-style backend architecture: authentication, role-based access, resilient third-party AI integration, and data-driven emotional insight.

---

## Features

- **AI Chatbot Companion** — Conversational support powered by Gemini, with persistent session history so users can pick up past conversations.
- **Mood & Journal Logging** — Daily mood and thought entries, automatically scored for sentiment.
- **AI-Driven Emotional Analysis** — Journal and mood entries are analyzed to surface emotional trends, not just raw logs.
- **Long-Term Pattern Tracking** — Wellness reports and mood prediction/forecasting built from historical entries.
- **Clinic & Help Finder** — Locates nearby mental health clinics using the Overpass API.
- **Gamification** — Streaks and badges to encourage consistent check-ins.
- **Doctor–Patient Model** — Role-based access (Doctor/Patient) with an explicit consent layer before any patient data is shared with a doctor.
- **Resilient AI Layer** — Spring Retry ensures that AI provider calls are retried on transient failures, with exponential backoff.

---

## Tech Stack

**Backend**
- Java, Spring Boot
- Spring Security + JWT (role-based access control)
- Spring Data JPA / Hibernate
- Spring Retry (resilience for AI provider calls)
- PostgreSQL
- BCrypt (password hashing)
- Lombok

**AI Integrations**
- Gemini

**Frontend**
- HTML/CSS/JS (legacy pages), migrating to React + Vite
- Axios (API client)

**Infrastructure**
- Docker (multi-stage build)
- Render (backend hosting)
- Supabase (managed PostgreSQL)
- Vercel (frontend hosting)

---

## Architecture

```
┌─────────────┐      JWT Auth       ┌──────────────────┐      ┌─────────────┐
│   Frontend   │ ──────────────────▶ │   Spring Boot     │ ───▶ │ PostgreSQL  │
│ (React/Vite) │ ◀────────────────── │   REST API        │ ◀─── │ (Supabase)  │
└─────────────┘                     └────────┬──────────┘      └─────────────┘
                                              │
                                              ▼
                                  ┌────────────────────────┐
                                  │  AI Provider Chain      │
                                  │  (Spring Retry-backed)  │
                                  └────────────────────────┘
```

Key design decisions:
- **Role comes from the database, not the client** — the login payload cannot be used to escalate privileges.
- **Consent precedes data sharing** — a patient must explicitly consent before a doctor can view mood/journal history or crisis-flag summaries.
- **Global CORS on the security filter chain** — ensures error responses (4xx/5xx) carry CORS headers too, not just successful ones.

---

## Live Demo

- **App:** [nirvana-uxd7.onrender.com](https://nirvana-uxd7.onrender.com/)
- **API Docs (Swagger UI):** [nirvana-uxd7.onrender.com/swagger-ui.html](https://nirvana-uxd7.onrender.com/swagger-ui.html)
- **Frontend Login:** [nirvana-frontend-ubd6.onrender.com/login](https://nirvana-frontend-ubd6.onrender.com/login)

---

## Getting Started

### Prerequisites
- Java 21
- Maven (or use the included `./mvnw` wrapper)
- PostgreSQL (local) — or a Supabase connection string
- API keys for at least one AI provider (Gemini recommended)

### Setup

1. Clone the repo
   ```bash
   git clone https://github.com/apurvaKhade25/Nirvana.git
   cd Nirvana
   ```

2. Configure local secrets — create `src/main/resources/application-local.properties` (gitignored):
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/nirvana_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password

   jwt.secret=your_jwt_secret

   gemini.api.key=your_gemini_key
   ```

3. Run with the local profile active:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
   ```

4. The API will be available at `http://localhost:8080`.

### Docker

```bash
docker build -t nirvana .
docker run -p 8080:8080 --env-file .env nirvana
```

---

## API Documentation

OpenAPI spec available via springdoc-openapi.

- Local: `http://localhost:8080/swagger-ui.html`
- Live: [nirvana-uxd7.onrender.com/swagger-ui.html](https://nirvana-uxd7.onrender.com/swagger-ui.html)

---

## Deployment

| Layer | Provider |
|---|---|
| Backend | Render (Docker) |
| Database | Supabase (PostgreSQL) |
| Frontend | Render |

---

## Roadmap

- [ ] Per-session scoping for chat history
- [ ] Relevance-gated context injection for chatbot prompts (recent entries only)
- [ ] Expanded test coverage

---

## Contributing

This is currently a personal/portfolio project. Feature branches are merged into `main` as stable checkpoints. Issues and suggestions are welcome via GitHub Issues.

---

## License

This project is licensed under the MIT License.