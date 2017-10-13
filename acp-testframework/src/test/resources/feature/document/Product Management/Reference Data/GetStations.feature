Feature: All stations for car hire

  @Sprint31 @TeamC @FCPH-10404 @regression
  Scenario Outline: All stations for car hire are returned for a particular country code
    Given I am using channel <channel>
    When I call the getStations service for that particular country <code>
    Then the stations for car hire purpose are returned
    Examples:
      | channel | code |
      | Digital | GBR  |

  @Sprint31 @TeamC @FCPH-10404
  Scenario Outline: Return error for an invalid country code
    Given I am using channel <channel>
    When I call the getStations service for that particular country invalid
    Then the channel will receive an error with code SVC_100215_1002
    Examples:
      | channel         |
      | PublicApiMobile |

  @Sprint31 @TeamC @FCPH-10404 @BR:BR_00860
  Scenario Outline: Return error if drop off location is not same country as pick up location
    Given I am using channel <channel>
    And I add the flight to the basket with passenger 1 Adult
    When I call the findCars service with drop off location is not same country as pick up location
    Then the channel will receive an error with code SVC_100800_1011
    Examples:
      | channel |
      | Digital |