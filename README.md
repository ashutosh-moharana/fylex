# Fylex

Temporary room-based file sharing backend built with Spring Boot.

## Features
- Create 6-character room (24h expiry)
- Upload files to room
- Download files
- Delete files
- Auto-delete expired rooms (scheduler)

## Tech Stack
- Java
- Spring Boot
- Spring Data JPA
- MySQL
- Maven

## API Endpoints

### Rooms
POST   /api/rooms
GET    /api/rooms/{code}

### Files
POST   /api/rooms/{code}/files
GET    /api/rooms/{code}/files/{fileId}
DELETE /api/rooms/{code}/files/{fileId}

## How to Run
1. Configure DB in application.properties
2. Run the application
3. Test using Postman