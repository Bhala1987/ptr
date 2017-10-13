@FCPH-425
Feature: Request to Reprice the basket Digital and AD

  Scenario Outline: Successful - No basket price changes identified.
    Given I am using the channel <channel>
    And I searched a flight for 1 adult
    And I added it to the basket with Standard fare as outbound journey
    When I trigger the recalculatePrices service
    Then I should get the success response with "SVC_100187_1000"
    And I should get the same basket if there are no price changes
    Examples:
      | channel           |
      | PublicApiMobile   |
      | ADCustomerService |

  Scenario Outline: Error - Invalid basket code in the request body for recalculatePrices() service
    Given I am using the channel <channel>
    When I trigger the recalculatePrices service with invalid basket code
    Then I will receive an error code as "SVC_100187_2005"
    Examples:
      | channel   |
      | Digital   |
      | ADAirport |

  Scenario Outline: Success - Flight price change
    Given I am using the channel <channel>
    And I searched a flight for 1 adult
    And I added it to the basket with Standard fare as outbound journey
    But the base price should be changed
    When I trigger the recalculatePrices service
    Then I should get the success response with "SVC_100187_1001"
    And I should get the affected data with Basket price change identified
    And I should get the updated basket for a Flight price change
    Examples:
      | channel           |
      | Digital           |
      | ADCustomerService |

  @manual
  Scenario: 2 - generate a request to AL to check the prices of the flight in the basket
    Given I have added a flight to the basket
    When I initiate the recalculatePrices service
    Then I will check the flight price change in AL

  @manual
  Scenario: 3 - generate a reqeuest to seating servcie to check price of the seat in the basket
    Given I have added a flight to the basket
    When I initiate the recalculatePrices service
    Then I will check the seat price change in seating service