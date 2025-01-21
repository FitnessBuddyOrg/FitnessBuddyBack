# FitnessBuddy Backend 🏋️‍♂️

Welcome to the FitnessBuddyBack project! This is a backend service for a fitness application built with Java and Spring Boot. It provides various functionalities such as user authentication, exercise sharing, and routine tracking.

## Features ✨

- **User Authentication**: Secure login and registration with JWT and OAuth2 (Google, GitHub).
- **Exercise Management**: Create, share, and manage exercises.
- **Routine Tracking**: Track completed routines and app usage.
- **Profile Management**: Update profile information and profile pictures.
- **MinIO Integration**: Store and retrieve profile pictures using MinIO.

## Technologies Used 🛠️

- **Java**
- **Spring Boot**
- **Spring Security**
- **Spring Data JPA**
- **MapStruct**
- **MinIO**
- **Maven**

## Getting Started 🚀

### Prerequisites

- Java 17 or higher
- Maven
- MinIO
- A database (e.g., PostgreSQL)

### Installation

1. Clone the repository

2. Configure the application:
   - Create a `.env` file in the root directory and add the necessary environment variables:
       ```env
       DB_URL=your_database_url
       DB_USERNAME=your_database_username
       DB_PASSWORD=your_database_password
       MINIO_ENDPOINT=your_minio_endpoint
       MINIO_ACCESS_KEY=your_minio_access_key
       MINIO_SECRET_KEY=your_minio_secret_key
       JWT_SECRET=your_jwt_secret
       JWT_EXPIRATION=your_jwt_expiration_time
       GOOGLE_CLIENT_ID=your_google_client_id
       GOOGLE_CLIENT_SECRET=your_google_client_secret
       GITHUB_CLIENT_ID=your_github_client_id
       GITHUB_CLIENT_SECRET=your_github_client_secret
       ```

3. Build the project:
    ```bash
    mvn clean install
    ```

4. Run the application:
    ```bash
    mvn spring-boot:run
    ```

## API Endpoints 📡

### Authentication

- **POST** `/auth/register`: Register a new user.
- **POST** `/auth/login`: Login with email and password.
- **POST** `/auth/login/oauth2/google`: Login with Google.
- **POST** `/auth/login/oauth2/github`: Login with GitHub.

### Exercises

- **POST** `/exercises/share`: Share an exercise.
- **GET** `/exercises/{shareToken}`: Get exercise by share token.
- **GET** `/exercises/templates`: Get all template exercises.

### Routines

- **POST** `/routines/complete`: Mark a routine as completed.
- **GET** `/routines/user/{userId}`: Get routines completed by a user.
- **GET** `/routines/all`: Get all completed routines.

### Users

- **GET** `/users/{email}`: Get user by email.
- **PATCH** `/users/update`: Update user information.
- **GET** `/users/profile-picture/{username}`: Get profile picture URL.
- **POST** `/users/profile-picture/{username}`: Update profile picture.

## Contributing 🤝

Contributions are welcome! Please fork the repository and submit a pull request.

## Contact 📧

For any inquiries, please contact [fitbud@ldelatullaye.fr](mailto:fitbud@ldelatullaye.fr).

---

Thank you for using FitnessBuddyBack! 💪
