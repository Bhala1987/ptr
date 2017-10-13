Feature: Remove Flight Interests From Customer Profile

  @Sprint29 @TeamC @FCPH-9959
  Scenario Outline: Generate error message if the customer is not hard logged in
    Given I am using channel <channel>
    When I send an removeFlightInterest with <customerID> customer ID, <flightKey> flight key, <bundle> bundle, <login> login for <numOfFlights> flights
    Then I receive forbidden error
    Examples:
      | channel | login   | customerID | flightKey | bundle | numOfFlights |
      | Digital | invalid | valid      | valid     | valid  | 1            |

  @Sprint29 @TeamC @FCPH-9959
  Scenario Outline: Error if the customer ID can not be identified
    Given I am using channel <channel>
    When I send an removeFlightInterest with <customerID> customer ID, <flightKey> flight key, <bundle> bundle, <login> login for <numOfFlights> flights
    Then I will receive an error with code 'SVC_100000_2086'
    Examples:
      | channel         | login | customerID | flightKey | bundle | numOfFlights |
      | PublicApiMobile | valid | invalid    | valid     | valid  | 2            |

  @Sprint29 @TeamC @FCPH-9959
  Scenario Outline: Error if flight key and bundle is unable to be identified
    Given I am using channel <channel>
    When I send an removeFlightInterest with <customerID> customer ID, <flightKey> flight key, <bundle> bundle, <login> login for <numOfFlights> flights
    Then I should receive an error with code SVC_100050_2030
    Examples:
      | channel         | login | customerID | flightKey | bundle  | numOfFlights |
      | Digital         | valid | valid      | invalid   | valid   | 1            |
      | PublicApiMobile | valid | valid      | valid     | invalid | 1            |

  @Sprint29 @TeamC @FCPH-9959 @regression @defect:FCPH-11689
  Scenario Outline: Remove flight interest from the profile
    Given I am using channel <channel>
    When I send an removeFlightInterest with <customerID> customer ID, <flightKey> flight key, <bundle> bundle, <login> login for <numOfFlights> flights
    Then I should get successful removal of flight interest response
    And I should not see the flight interest in the getFlight interest
    And I should not see the flight interest in the profile
    Examples:
      | channel | login | customerID | flightKey | bundle | numOfFlights |
      | Digital | valid | valid      | valid     | valid  | 2            |
