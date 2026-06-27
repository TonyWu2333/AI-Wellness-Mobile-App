# Database Design

## ERD

```
┌─────────────────┐       ┌────────────────────────┐
│     users        │       │   wellness_records      │
├─────────────────┤       ├────────────────────────┤
│ id (PK)         │──┐    │ id (PK)                 │
│ username (UQ)   │  │    │ user_id (FK → users.id) │
│ email (UQ)      │  ├───>│ sleep_hours             │
│ password_hash    │  │    │ activity_name           │
│ created_at      │  │    │ activity_duration_mins  │
│ updated_at      │  │    │ record_date             │
└─────────────────┘  │    │ notes                   │
       │              │    │ created_at              │
       │              │    │ updated_at              │
       │              │    └────────────────────────┘
       │              │
       │              │    ┌────────────────────────┐
       │              │    │    chat_messages        │
       │              │    ├────────────────────────┤
       │              │    │ id (PK)                 │
       │              ├───>│ user_id (FK → users.id) │
       │              │    │ user_message            │
       │              │    │ bot_response            │
       │              │    │ created_at              │
       │              │    └────────────────────────┘
       │              │
       │              │    ┌────────────────────────┐
       │              │    │   recommendations       │
       │              │    ├────────────────────────┤
       │              └───>│ id (PK)                 │
       │                   │ user_id (FK → users.id) │
       │                   │ recommendation_text     │
       │                   │ analysis_summary        │
       │                   │ generated_at            │
       │                   │ is_read                 │
       │                   └────────────────────────┘
       v
```

## Table Details

### users
| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| username | VARCHAR(50) | UNIQUE, NOT NULL | Login identifier |
| email | VARCHAR(100) | UNIQUE, NOT NULL | |
| password_hash | VARCHAR(255) | NOT NULL | BCrypt hash |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | |
| updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | |

### wellness_records
| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| user_id | BIGINT | FK → users.id, NOT NULL | Owner |
| sleep_hours | DECIMAL(4,1) | | Hours slept |
| activity_name | VARCHAR(100) | | Exercise/activity |
| activity_duration_minutes | INT | | Duration in minutes |
| record_date | DATE | NOT NULL | Date of record |
| notes | TEXT | | Free text notes |
| created_at / updated_at | TIMESTAMP | | Audit columns |

### chat_messages
| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| user_id | BIGINT | FK → users.id, NOT NULL | |
| user_message | TEXT | NOT NULL | |
| bot_response | TEXT | NOT NULL | |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | |

### recommendations
| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| user_id | BIGINT | FK → users.id, NOT NULL | |
| recommendation_text | TEXT | NOT NULL | AI recommendation |
| analysis_summary | TEXT | | Trend analysis detail |
| generated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | |
| is_read | BOOLEAN | DEFAULT FALSE | Read status |

## Key Design Decisions

1. **Cascade deletes**: Deleting a user cascades to their records, messages, and recommendations.
2. **Indexes**: Added on `user_id` foreign keys and `record_date` for query performance.
3. **DECIMAL(4,1)**: Sleep hours support values like 7.5 with one decimal place.
4. **TEXT columns**: Notes, messages, and recommendations use TEXT for flexibility.
5. **is_read flag**: Allows Android to show unread recommendation badges.
