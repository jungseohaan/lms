Feature: JWT Publish API Test

  Background:
    # Base URL for the API
    * url 'http://localhost:15000'

  Scenario: Generate JWT Token
    Given path '/common/jwt'
    And param timestamp = 1625142600000
    And param id = 'testUser'
    When method get
    Then status 200
    And match response contains { jwtToken: '#string', hmac: '#string' }

  # Helper to extract JWT and HMAC
  Scenario: Extract JWT and HMAC
    Given path '/common/jwt'
    And param timestamp = 1625142600000
    And param id = 'testUser'
    When method get
    Then status 200
    * def jwtToken = response.jwtToken
    * def hmac = response.hmac
