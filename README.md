# Paradise Island API

Contract first design using [swagger-codegen-maven-plugin](https://github.com/swagger-api/swagger-codegen/blob/master/modules/swagger-codegen-maven-plugin/README.md) 
to generate Controllers and DTOs.


## Quick start

Verify you have Maven and Java 8 installed.

```
# Build the jar
mvn clean install

# start the mysql container
docker-compose -f mysql.yml up -d

# Run the jar
java -jar target/paradise-island-api-1.0-SNAPSHOT.jar
```
Then hit [localhost:8080/v1/swagger-ui.html](http://localhost:8080/v1/swagger-ui.html) for UI interface.


To stop the mysql docker container run :
```
docker-compose -f mysql.yml down
```

You can connect the database on jdbc:mysql://localhost:3306/paradise_island using root user with no password.

## Tests

You can find tests under the src/test folder.
Functional tests have also been included in that folder for simplicity purpose, it wouldn't be the best in a real 
project since they would run with unit tests and take a long time to complete. Best option will be to run them into 
a separate folder as mentioned here https://dzone.com/articles/advanced-functional-testing-in-spring-boot-by-usin.

Functional tests show how the API handle concurrent reservations for the same dates.

To run all tests (Unit + Functional) do:
```
mvn clean test
```

Test are using an in-memory H2 database.


## Notes
- Code is feel bit verbose, this is intended since some companies enforce no abbreviations convention.
- For simplicity API is using UTC timezone.
- Unit tests have not been pushed too far in this exercise except for controller validation, real project would require more for 
service layer and Util classes.
