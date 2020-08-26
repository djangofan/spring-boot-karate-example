# Normally, .feature files should reside under src/test/resources/features

@api-test
Feature: Example JMS query

  @reg
  Scenario: Send a single JMS message
    Given url 'http://localhost:8080/send'
    When method get
    Then status 200

