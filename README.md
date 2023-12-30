# eat-what

## Design Considerations & Assumptions

1. The application is designed as a REST API backend service that is meant to be consumed by an frontend application
2. User can create as many sessions as they want
3. There is no need to track which user submitted what restaurant (Anonymity)
4. One participant can join many sessions concurrently
5. One participant can submit as many restaurant choices as they want

## Prerequisites

Before you begin, ensure you have met the following requirements:

- Java JDK 11 or newer
- Maven (Ensure it's installed and properly set up in your system's PATH).

## Cloning the Repository

To clone the repository, run the following command:

```bash
git clone https://github.com/thomastgy/eat-what
```
## Running the Application

1. cd to the application base path
```bash
cd eat-what
```
2. run mvn clean package to generate a jar file
```bash
mvn clean package
```
3. run the jar file using
```bash
java -jar target/rest-service-0.0.1-SNAPSHOT.jar
```
4. Access the application at
``` bash
http://localhost:8080/api/sessions/*
```

## API Docs
You can access the API Documentation directly through the Swagger UI:
```bash
http://localhost:8080/swagger-ui/index.html
```