@Sprint25 @Sprint26
@FCPH-8450
Feature: Remove Flight from basket reapportion admin fee

  Scenario Outline: Flight removed is part of return pair and has part of admin fee apportioned and from-search-results is false
    Given I am using the channel <channel>
    And I searched a '<fareType>' flight with return for <passengerMix>
    And I added it to the basket with <fareType> fare as <journeyType> journey
    And I want to remove the '<remove>' flight
    But from-search-results is false
    When I send the request to removeFlight()
    Then I receive a confirmation response for the removeFlight()
    And the '<remove>' flight has been removed from the basket
    And the admin fee will be apportioned among the passenger of the '<remaining>' flight
    Examples: Basic flight with 1 adult passenger
      | channel         | passengerMix | fareType | journeyType      | remove   | remaining |
      | Digital         | 1 adult      | Standard | outbound/inbound | inbound  | outbound  |
      | PublicApiMobile | 1 adult      | Standard | outbound/inbound | outbound | inbound   |
    Examples: Flight with multiple passenger
      | channel         | passengerMix | fareType | journeyType      | remove   | remaining |
      | Digital         | 2 adult      | Standard | outbound/inbound | inbound  | outbound  |
      | PublicApiMobile | 2 adult      | Standard | outbound/inbound | outbound | inbound   |
  @regression
    Examples: Flight with different type of passenger
      | channel         | passengerMix                 | fareType | journeyType      | remove   | remaining |
      | PublicApiMobile | 1 adult; 1 child; 2,1 infant | Standard | outbound/inbound | outbound | inbound   |
    Examples: Flight with different type of passenger
      | channel         | passengerMix                 | fareType | journeyType      | remove   | remaining |
      | Digital         | 1 adult; 1 child; 2,1 infant | Standard | outbound/inbound | inbound  | outbound  |

  Scenario Outline: Flight being removed is part of a return pair and has part of the admin fee apportioned and from-search-results is true
    Given I am using the channel <channel>
    And I searched a '<fareType>' flight with return for <passengerMix>
    And I added it to the basket with <fareType> fare as <journeyType> journey
    And I want to remove the '<remove>' flight
    And from-search-results is true
    When I send the request to removeFlight()
    Then I receive a confirmation response for the removeFlight()
    And the '<remove>' flight has been removed from the basket
    And the admin fee will not be apportioned among the passenger of the '<remaining>' flight
    Examples: Basic flight with 1 adult passenger
      | channel         | passengerMix | fareType | journeyType      | remove   | remaining |
      | Digital         | 1 adult      | Standard | outbound/inbound | outbound | inbound   |
      | PublicApiMobile | 1 adult      | Standard | outbound/inbound | inbound  | outbound  |
    Examples: Flight with multiple passenger
      | channel         | passengerMix | fareType | journeyType      | remove   | remaining |
      | Digital         | 2 adult      | Standard | outbound/inbound | outbound | inbound   |
      | PublicApiMobile | 2 adult      | Standard | outbound/inbound | inbound  | outbound  |
    Examples: Flight with different type of passenger
      | channel         | passengerMix                 | fareType | journeyType      | remove   | remaining |
      | Digital         | 1 adult; 1 child; 2,1 infant | Standard | outbound/inbound | outbound | inbound   |
      | PublicApiMobile | 1 adult; 1 child; 2,1 infant | Standard | outbound/inbound | inbound  | outbound  |

  Scenario Outline: Flight being removed is part of a return pair and has part of the admin fee apportioned and the basket contains also another flight with return
    Given I am using the channel <channel>
    And I searched a '<fareType>' flight with return for <passengerMix>
    And I added it to the basket with <fareType> fare as <journeyType>' journey
    And I want to remove the '<remove>' flight
    And I searched a '<fareType>' flight with return for <passengerMix>
    And I added it to the basket with '<fareType>' fare as '<journeyType>' journey
    When I send the request to removeFlight()
    Then I receive a confirmation response for the removeFlight()
    And the '<remove>' flight has been removed from the basket
    And the admin fee will be apportioned among the passenger of the '<remaining>' flight
    Examples: Basic flight with 1 adult passenger
      | channel         | passengerMix | fareType | journeyType      | remove   | remaining |
      | Digital         | 1 adult      | Standard | outbound/inbound | inbound  | outbound  |
      | PublicApiMobile | 1 adult      | Standard | outbound/inbound | outbound | inbound   |
    Examples: Flight with multiple passenger
      | channel         | passengerMix | fareType | journeyType      | remove   | remaining |
      | Digital         | 2 adult      | Standard | outbound/inbound | inbound  | outbound  |
      | PublicApiMobile | 2 adult      | Standard | outbound/inbound | outbound | inbound   |
    Examples: Flight with different type of passenger
      | channel         | passengerMix                 | fareType | journeyType      | remove   | remaining |
      | Digital         | 1 adult; 1 child; 2,1 infant | Standard | outbound/inbound | inbound  | outbound  |
      | PublicApiMobile | 1 adult; 1 child; 2,1 infant | Standard | outbound/inbound | outbound | inbound   |

  @BR:BR_01262
  Scenario Outline: Flight being removed is part of a return pair and has part of the admin fee apportioned and the basket contains also a single flight
    Given I am using the channel <channel>
    And I searched a '<fareType>' flight with return for <passengerMix>
    And I added it to the basket with <fareType> fare as <journeyType> journey
    And I want to remove the '<remove>' flight
    And I searched a flight for <passengerMix>
    And I added it to the basket with '<fareType>' fare as 'single' journey
    When I send the request to removeFlight()
    Then I receive a confirmation response for the removeFlight()
    And the '<remove>' flight has been removed from the basket
    And the admin fee will be apportioned among the passenger of the '<remaining>' flight
    Examples: Basic flight with 1 adult passenger
      | channel         | passengerMix | fareType | journeyType      | remove   | remaining |
      | Digital         | 1 adult      | Standard | outbound/inbound | outbound | inbound   |
      | PublicApiMobile | 1 adult      | Standard | outbound/inbound | inbound  | outbound  |
    Examples: Flight with multiple passenger
      | channel         | passengerMix | fareType | journeyType      | remove   | remaining |
      | Digital         | 2 adult      | Standard | outbound/inbound | outbound | inbound   |
      | PublicApiMobile | 2 adult      | Standard | outbound/inbound | inbound  | outbound  |
    Examples: Flight with different type of passenger
      | channel         | passengerMix                 | fareType | journeyType      | remove   | remaining |
      | Digital         | 1 adult; 1 child; 2,1 infant | Standard | outbound/inbound | outbound | inbound   |
      | PublicApiMobile | 1 adult; 1 child; 2,1 infant | Standard | outbound/inbound | inbound  | outbound  |