# Accounts Service
Before the development of the superclient, the accounts service was written in Java supported by a client library. The accounts service contained the following functionality:

  - Each service provided a REST API to abstract complex business logic and service interactions from the user interface backend.
  - Behaviour-driven testing.

**Deprecated:** This repository is no longer in use, please review the `project-and-dissertation` project for currently in-use repositories.

## How to run
This repository is configured using Maven, ensure Maven is installed and then execute the following steps:

1. Run `mvn clean install` to build your application.
2. Start application with `java -jar target/transactionservice-1.0-SNAPSHOT.jar server config.yml`.

## How to test
This repository is configured using Maven, ensure Maven is installed and then run `mvn test`.
