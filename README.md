# test-intuit-planetary

This project tests the below endpoint using various test scenarios described under Test Scenario section:

**HTTP REQUEST**

GET [https://api.nasa.gov/planetary/sounds]

**QUERY PARAMETERS**

| Parameter | Type   | Default  | Description                         |
|-----------|--------|----------|-------------------------------------|
| q         | string | None     | Search text to filter results       |
| limit     | int    | 10       | number of tracks to return          |
| api_key   | string | DEMO_KEY | api.nasa.gov key for expanded usage |

**EXAMPLE QUERY**

[https://api.nasa.gov/planetary/sounds?q=apollo&api_key=DEMO_KEY]


## Prerequisites

**Command Line:**

- JDK 1.8
- Maven 3.3.9

**Eclipse (tested using Mars):**

- TestNg Plugin
- JDK 1.8
- Java Compiler 8

## Usage

**Maven:**

Execute below command where projects .pom file is located

```
mvn clean test -Denv=PROD
```

**Eclipse:**

Right-click on testng.xml (located under src/test/resources) and select Run As -> TestNG Suite


## Test Scenarios

NOTE: Using DataProviders which reuses test methods/code for various test scenarios

1. Tests happy path using all valid query strings
2. Tests response JSON schema to ensure structure/contract is valid (i.e. fields, data types)
3. Tests using invalid *api_key* query string in additon to testing query string data type with non-string values (i.e. int, double, double byte, null, etc)
4. Tests using unsupported query strings
5. Tests using invalid _q_ query string in additon to testing query string data type with non-string values (i.e. int, double, double byte, null, etc)
6. Tests _q_ filter using _id_ field values as a search filter and ensure response only contains object with specified id/s
7. Tests using invalid _limit_ query string in additon to testing query string data type with non-int values (i.e. negative int, string, double, double byte, null, etc)
8. Tests the _limit_ query string to ensure response size is as expected

## Comments

**Issues Found**

1. _limit_ query string returns 500 when non-int data type values are uses when it should be returning a 400
2. _q_ query string accepts non-string data types which should result to a 400 but a 200 is returned
3. _q_ filter is currently not working as when using a search filter it returns everything vs. adhering to the filter condition
4. There is no boundary for limit so when using a large number a 500 is returned vs. a meaningful response
5. HTML responses are returning when it should be in the form of Json
6. Endpoint has a test limitation as when you make a few requests 429 response codes are returned stating you hit your limitation


