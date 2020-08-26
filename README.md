# wbm-testing-service-api

Spring Boot JMS proxy with Karate tests and Gatling performance runner.

&#x1F34E; TODO:  This project isn't working at the moment. I want to refactor it to use Docker ActiveMQ.  But, this project will give you a basic idea of how to structure such a project so Karate tests can be run either individually or with Gatling.

## Execute

Start the regular unit test, by itself without triggering Gatling performance test.

    mvn clean test -Dtest.env=int -Dtest.type=reg


## Perf Test

Not sure why, but the test-compile step is required in this command.

    mvn test-compile gatling:test -Dtest.env=int -Dtest.type=reg


## Troubleshooting

If the test reports that the service is already running, force kill it with `lsof -i TCP:8080`.
