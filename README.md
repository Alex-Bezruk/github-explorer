# Quarkus GitHub Explorer

Quarkus application to explore GitHub repositories based on a user's name.

## Overview

This Quarkus application provides a simple API endpoint to retrieve GitHub repositories based on a user's name.

## Getting Started

### Prerequisites

Make sure you have the following installed:

- Java Development Kit (JDK) 17 or later
- Gradle

### Installation

Clone the repository and build the project using Gradle:

```bash
git clone https://github.com/Alex-Bezruk/quarkus-github-explorer.git
cd quarkus-github-explorer
./gradlew build
```

### Running the Application

./gradlew quarkusDev

### API Documentation

The API documentation is generated automatically by Quarkus. Access it at http://localhost:8080/q/swagger-ui/.

### Configuration
No specific configuration is required for this project.

### Usage
To retrieve GitHub repositories, make a GET request to http://localhost:8080/repositories?userName=<github_username>. Replace <github_username> with the desired GitHub username.

