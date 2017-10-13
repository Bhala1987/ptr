@Sprint25 @Sprint26
Feature: Update Basket with new currency

#  The scenarios in these feature file have been duplicated instead of using examples because serenity is not picking up the tag at example level

  @FCPH-3444
  Scenario: Invalid basket id
    Given I am using one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile
    And I am using an invalid basketId
    When I send a request to update basket currency
    Then I will receive an error with code 'SVC_100013_1001'

  @FCPH-3444
  Scenario Outline: Invalid currency received
    Given I am using one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile
    And I have a basket with a valid flight with Standard faretype
    And I am using '<type>' currency
    When I send a request to update basket currency
    Then I will receive an error with code '<error>'
    Examples: AD channels
      | type    | error           |
      | invalid | SVC_100011_2001 |
      | same    | SVC_100011_2002 |
      | blank   | SVC_100011_2001 |

  @FCPH-3444
  @BR:BR_00662
  Scenario: Convert basket to the requested currency
    Given I am using one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile
    And I have a basket with a valid flight with Standard faretype
    And I added an hold bag to first passenger
    And I added a sports equipment to first passenger
    And I am using 'valid' currency
    When I send a request to update basket currency
    Then I will receive a confirmation for the update
    And the fare product price is converted from the old currency to the request currency at the current exchange rates and the products price are update with the new currency values
    And the original currency is stored

  @FCPH-8269
  @BR:BR_00662
  Scenario Outline: Return currency back to original currency or to another currency
    Given I am using one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile
    And I have a basket with a valid flight with Standard faretype
    And I added an hold bag to first passenger
    And I added a sports equipment to first passenger
    And I have converted the basket currency
    And I have received a confirmation
    And I am using '<currency>' currency
    When I send a request to update basket currency
    Then I will receive a confirmation for the update
    And the fare product price is converted from the old currency to the request currency at the current exchange rates and the products price are update with the new currency values
    And the original currency is stored
    Examples:
      | currency     |
      | original     |
      | not original |

  @Sprint27
  @FCPH-8996
  @BR:BR_00662
  Scenario Outline: Convert basket with a seat product to the requested currency
    Given I am using one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile
    And I have a basket with a valid flight with <fareType> faretype
    And I added an hold bag to first passenger
    And I added a sports equipment to first passenger
    And I added a seat <seatType> to the first passenger
    And I am using 'valid' currency
    When I send a request to update basket currency
    Then I will receive a confirmation for the update
    And the fare product price is converted from the old currency to the request currency at the current exchange rates and the margin are applied on top of the conversion, and the products price are update with the new currency values
    And the original currency is stored
    Examples: Seat not part of the bundle
      | fareType | seatType |
      | Standard | STANDARD |
    Examples: Seat part of the bundle
      | fareType | seatType      |
      | Flexi    | STANDARD      |
      | Flexi    | UPFRONT       |
      | Flexi    | EXTRA_LEGROOM |

  @Sprint27
  @FCPH-9683
  @BR:BR_00662
  Scenario Outline: Return currency back to original currency or to another currency for basket with seat product
    Given I am using one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile
    And I have a basket with a valid flight with Standard faretype
    And I added an hold bag to first passenger
    And I added a sports equipment to first passenger
    And I added a seat STANDARD to the first passenger
    And I have converted the basket currency
    And I have received a confirmation
    And I am using '<currency>' currency
    When I send a request to update basket currency
    Then I will receive a confirmation for the update
    And the fare product price is converted from the old currency to the request currency at the current exchange rates and the products price are update with the new currency values
    And the original currency is stored
    Examples:
      | currency     |
      | original     |
      | not original |

  @FCPH-9178
  Scenario: Add flight after converting basket currency
    Given I am using one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile
    And I have a basket with a valid flight with Standard faretype
    And I am using 'valid' currency
    And I sent a request to update basket currency
    And I received a confirmation for the update
    And the fare product price is converted from the old currency to the request currency at the current exchange rates and the products price are update with the new currency values
    And the original currency is stored
    And I searched a flight for 1 adult
    And the price in the find flight response are updated with currency of the basket
    When I add it to the basket
    Then the flight price in the basket is in the request currency at the current exchange rates
    And the original currency is stored