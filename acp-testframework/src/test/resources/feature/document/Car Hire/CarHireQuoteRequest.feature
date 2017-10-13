@FCPH-3676
@Sprint30
@TeamC
Feature: Create a invalid request to generate a quote for car hire

  Background:
    Given I am using the channel Digital

  @Sprint30 @FCPH-3676 @TeamC
  Scenario: Generate error message when the mandatory fields are not provided
    When I add the flight to the basket with passenger 1 Adult
    And I request for a car hire with out mandatory fields
      | pickUpDate  |
      | pickUpTime  |
      | dropOffDate |
      | dropOffTime |
    Then I see an error message for car hire mandatory fields SVC_100800_1000

  @Sprint30 @FCPH-3676 @TeamC
  Scenario: No error message displayed when the non mandatory fields are missing
    When I add the flight to the basket with passenger 1 Adult
    And I request for a car hire with out non mandatory fields
      | carCategory            |
      | locale                 |
      | driverCountryResidence |
      | target                 |
      | ageOfDriver            |
      | pickUpAirport          |
      | dropOffAirport         |
      | driverCountryResidence |
    Then I see 200 response code for car hire missing non mandatory fields

  @Sprint30 @FCPH-3676 @TeamC
  Scenario Outline: Generate Error if the logged in customer is a staff customer BR_01520
    Given I am a staff member and logged in as user <username> and <password>
    When I add the flight to the basket as staff with passenger <passengers>
    And I request for a car hire search
    Then I see an error message for car hire add basket SVC_100800_1004

    Examples:
      | passengers | username            | password |
      | 1 Adult    | a.rossi@reply.co.uk | 1234     |

  @Sprint30 @FCPH-3676 @TeamC
  Scenario Outline: logged in customer is a staff customer with standard booking type
    When I am a staff member and logged in as user <username> and <password>
    And I add the flight to the basket with <passengers>  with booking type Standard
    And I request for a car hire search
    Then I see car hire search results
    And I see car hire products including both credit and debit card prices
    Examples:
      | passengers | username            | password |
      | 1 Adult    | a.rossi@reply.co.uk | 1234     |

  @Sprint30 @FCPH-3676 @TeamC
  Scenario: Return price to the channel
    Given I add the flight to the basket with passenger 1 Adult
    And I request for a car hire search
    Then I see car hire search results
    And I see car hire products including both credit and debit card prices

  @Sprint30 @FCPH-3676 @TeamC @regression @defect:FCPH-12045
  Scenario Outline:  Receive response from EI different currencies
    When I add the flight to the basket with <passengers> with currency <currency>
    And I request for a car hire search
    Then I see car hire search results with currency <expectedCurrency>
    Examples:
      | passengers | currency | expectedCurrency |
      | 1 Adult    | EUR      | EUR              |
      | 1 Adult    | GBP      | GBP              |
      | 1 Adult    | USD      | USD              |

  @Sprint30 @FCPH-3676 @TeamC @local
  Scenario: No car hire products returned available for the requested parameters BR_02600
    When I add the flight to the basket with passenger 1 Adult
    And I request for a car hire with out non mandatory fields
      | noCareHireProducts |
    Then I see an error message for car hire add basket SVC_100800_2001