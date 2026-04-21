# Fylex

Temporary room-based file sharing backend built with Spring Boot.

---

## Overview

Fylex allows users to share files using simple room codes without authentication.
Rooms are temporary and automatically expire after 24 hours.

---

## Features

- Create rooms with a unique 6-character code
- Automatic room expiry after 24 hours
- Upload files to a room
- List all files in a room
- Download files
- Delete files
- Scheduled cleanup of expired rooms and files

---

## Tech Stack

- Java
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven

---

## API Endpoints

### Room APIs

```http
POST   /api/rooms
GET    /api/rooms/{code}
```

### File APIs

```http
POST   /api/rooms/{code}/files
GET    /api/rooms/{code}/files
GET    /api/rooms/{code}/files/{fileId}
DELETE /api/rooms/{code}/files/{fileId}
```

---

## How to Run

1. Clone the repository
2. Configure database in `application.properties`
3. Run the application
4. Test APIs using Swagger or Postman

---

## Notes

- Rooms expire automatically after 24 hours
- Files are deleted along with expired rooms
- Access is controlled using room codes (no authentication)
