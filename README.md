[![Build Status](https://travis-ci.com/nameishari/loanRepaymentCalculator.svg?branch=master)](https://travis-ci.com/nameishari/loanRepaymentCalculator/) 

## Overview
Simple RESTful API for generating annuity loan repayment plan.

## Technologies/Libraries used:

<ul>
  <li>Spring Boot</li>
  <li>Java 8</li>
  <li>Junit and Rest Assured</li>
</ul>

## Endpoints
* `POST` /generate-plan - Generates plan
  - example request body
  ```json
    {
      "duration": 5,
      "loanAmount": 5000,
      "nominalRate": 3,
      "startDate": "2019-12-31T15:10:02.550Z"
    }
  ```
## Running
This service is using maven wrapper, it is not necessary to have maven in the execution environment.
<ul>
  <li><b>./mvnw clean verify</b> - to run tests</li>
  <li><b>./mvnw clean package</b> - creates executable jar</li>
  <li><b>java -jar ./target/loanRepaymentCalculator-1.0-SNAPSHOT.jar</b> - Runs the executable jar on default port(8080)</li>
  <li><b>java -Dserver.port=8090 -jar ./target/loanRepaymentCalculator-1.0-SNAPSHOT.jar</b> - If default port, 8080 already in use then it can be changed with server.port</li>
 </ul>

## Testing the application
<ul> 
    <li>I have deployed this service in <a href="www.heroku.com">heroku</a>. You can visit the link and test the endpoint using swagger. URL - <a href="https://loan-repayment-generator.herokuapp.com/swagger-ui.html">https://loan-repayment-generator.herokuapp.com/</a></li>
    <li>You can run the jar file in local and visit <a href="http://localhost:8080">localhost:8080</a>. Change the port as per local environment.</li>
</ul>
