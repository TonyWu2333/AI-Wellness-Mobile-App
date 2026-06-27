# AI-Enabled Wellness Mobile App

Team project for Mobile Application Development CA.

## Project Structure

```
├── android/            # Kotlin Android Application
├── backend/            # Java Spring Boot Backend
├── database/           # MySQL Schema & Init Scripts
├── agent/              # Python Agentic AI Component
├── docs/               # Documentation
├── PROJECT_PLAN.md     # Development Plan
└── STAUTS.md          # Current Status
```

## Quick Start

### Backend
```bash
cd backend
mvn spring-boot:run
```
Before starting the backend, fill in `deepseek.api-key` in `backend/src/main/resources/application.yml`.

### Android
Open `android/` in Android Studio, sync Gradle, and run on emulator.

### Database
Execute `database/schema.sql` against MySQL, or use the default H2 in-memory database.

### Python Agent
```bash
cd agent
pip install -r requirements.txt
python main.py
```

## Tech Stack
- **Android:** Kotlin, Retrofit, ViewModel, LiveData
- **Backend:** Spring Boot 3.2, Spring Security, JPA
- **Database:** MySQL 8.0 / H2 (dev)
- **Auth:** JWT (jjwt)
- **AI:** Integrated chatbot + Python agentic AI
