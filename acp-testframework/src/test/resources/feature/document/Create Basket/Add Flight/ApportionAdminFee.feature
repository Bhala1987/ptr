@FCPH-2672 @FCPH-386 @FCPH-487
Feature: Apportion Admin Fee for Add Flight - same bundle

  Scenario Outline: Verify Admin fee is apportioned per passenger per sector
    Given my basket contains "<noOfFlights>" flights for "<noOfPassengers>" passengers with "Standard" fare added via the "<channel>" channel
    Then the admin fee should be apportioned per passenger per sector and wrapped up in the flight fare of the first flight Only
    Examples:
      | channel         | noOfFlights | noOfPassengers |
      | Digital         | 2           | 5              |
      | Digital         | 3           | 9              |
      | PublicApiMobile | 39          | 4              |
      | PublicApiB2B    | 10          | 4              |

  Scenario Outline: Verify Admin fee is apportioned per passenger for first two sectors only if it is a return pair
    Given my basket contains return flight for "<noOfPassengers>" passengers with "Standard" fare added via the "<channel>" channel
    When I add another "<noOfFlightsToAdd>" flight with "<noOfPassengersToAdd>" passengers to the basket via the "<channel>" channel
    Then the admin fee should be apportioned per passenger and rounded to the nearest pence for the first two sectors only
    Then the flight is added to the basket
    Examples:
      | channel | noOfPassengers | noOfFlightsToAdd | noOfPassengersToAdd |
      | Digital | 3              | 2                | 2                   |
#      | Digital | 4              | 3                | 5                   |
#      | Digital | 3              | 1                | 2                   |
#      | Digital | 5              | 2                | 4                   |
# May fail on scenario two as it will have an empty parameter for last two data sets. Let me know if you want me to furthe rinvestigate with Test/BA

  Scenario Outline: Verify that admin fee is stored at the booking level for Non OFT Pricing
    When I add the "<NumberOfFlights>" flights with "<NumberOfPassengers>" passengers with "Standard" bundle to my basket via "<channel>"
    Then the administration tax is at booking level
    And  the credit card fee for each passenger is correct for the "<channel>"
   #ADMIN FEE / (PASSENGERS * SECTORS)  ##question regarding PublicApiB2B  regarding distribution of Admin Fees it is not the same as AD
    Examples:
      | channel           | NumberOfFlights | NumberOfPassengers |
      | ADAirport         | 3               | 3                  |
      | PublicApiB2B      | 4               | 2                  |
#      | PublicApiMobile   | 1               | 1                  |
#      | ADCustomerService | 2               | 4                  |
#      | ADAirport         | 1               | 3                  |

  Scenario Outline: Verify Flight Tax is included in the Fare price per passenger
    Given my basket contains return flight that has a flight tax for "<noOfPassengers>" passengers added via the "<channel>" channel
    When I add another "<noOfFlightsToAdd>" flight that has a flight tax with "<noOfPassengersToAdd>" passengers to the basket via the "<channel>" channel
    Then the Flight Tax should be in the Fare Price per passenger
    Examples:
      | channel   | noOfPassengers | noOfFlightsToAdd | noOfPassengersToAdd |
      | ADAirport | 3              | 1                | 3                   |
      | Digital   | 3              | 1                | 2                   |

  @pending
  @manual
  Scenario Outline: Verify fees and taxes are apportioned per passenger
    Given I have found a valid flight for multiple passengers for channel "<channel>" and bundle "<bundleType>"
    When I add the flight to my basket
    Then the admin fee should be apportioned per passenger and rounded to the nearest pence for the first 2 sectors
    And Flight tax is apportioned per passenger
    And credit card fee is added at order level based on language set
    Examples:
      | channel           | bundleType |
      | PublicApiMobile   | Standard   |
      | ADAirport         | Flexi      |
      | ADCustomerService | Standard   |
