# wbm-testing-service-api

Spring Boot JMS proxy with Karate tests and Gatling performance runner.

## Execute

Start the regular unit test, by itself without triggering Gatling performance test.

    mvn clean test -Dtest.env=int -Dtest.type=reg


## Perf Test

Not sure why, but the test-compile step is required in this command.

    mvn test-compile gatling:test -Dtest.env=int -Dtest.type=reg


## Troubleshooting

If the test reports that the service is already running, force kill it with `lsof -i TCP:8080`.
