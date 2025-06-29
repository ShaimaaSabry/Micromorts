# Micromorts

A micromort is a unit of risk representing a one-in-a-million chance of death.

This application exposes APIs to calculate the risk of death in micromorts associated with various day-to-day activities.

Swagger: http://localhost:8080/swagger-ui/index.html

## Technology Stack

- Kotlin
- Spring Boot
- Gradle
- Qdrant
- Docker Compose

## System Design

The platform stores a sample list of day-to-day activities and their associated risk of death in micromorts in a vector database (Qdrant). Given a string representing an action, the platform tries to match the string to the closest item from the list of activities stored in Qdrant.

OpenAI is used to generate a vector from a string representing an activity or action.

The platform exposes a REST API that accepts a list of actions taken by a person on a day and calculates the risk of death from these actions in micromorts.

## Architecture

The platform is built using clean architecture and domain-driven design principles. Thus, the project consists of the following layers:
* presentation layer - contains the REST API controllers
* application layer - contains the use cases and application services
* domain layer - contains the domain entities and business logic
* infrastructure layer - contains the implementation of the repositories and external services

## Run Locally

* Using Qdrant:

  1. Get an OpenAI API key and set it as an env var `OPENAI_API_KEY`: https://platform.openai.com/signup
     ```bash
     export OPENAI_API_KEY=your_openai_api_key
     ```

  2. Start docker compose:
     ```bash
     docker-compose -f local/docker-compose.yaml up
     ```
   
  3. Run the Spring Boot application:
     ```bash
     ./gradlew bootRun
     ```

  4. If you are running the app for the first time, create and seed the Qdrant `activities` collection:
     ```bash
     ./local/setup_qdrant.sh
     ```
* Using a simple in-memory activities repository:

  1. Remove the `@Primary` annotation from the `ActivityQdrantRepository` and add it to the `ActivityInMemoryRepository`.

  2. Run the Spring Boot application:
     ```bash
     ./gradlew bootRun
     ```
  
## Tests & Linting

* Linting: 
    ```
   ./gradlew ktlintFormat
    ```
  
* Unit tests:
    ```
   ./gradlew test
    ```
  
* Code coverage: 
    ```
   ./gradlew jacocoTestReport
    ```
  
* Integration tests: 
    ```
   ./gradlew integrationTest
    ```

## Authors & Copyright

Shaimaa Sabry
