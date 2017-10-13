@Sprint24 @Sprint25
@FCPH-405
@defect:FCPH-10499
Feature: Receive flight inventory not available for Digital & Public API Mobile Commit Booking

  Scenario Outline: No allocation for a flight received from abstraction layer
    Given I am using the channel <channel>
    And I have created a new customer
    And I searched a flight for 1 adult
    And I added it to the basket with 'Standard' fare as 'single' journey
    But there's no availability for that flight
    And I have updated the passenger information
    And I have got the payment method as 'STANDARD_CUSTOMER'
    When I do commit booking for given basket with unavailable inventory
#    Then I should receive a confirmation message with code 'SVC_100022_3011'
    And the flight should be removed from basket
    And all other products which are associated to the flight should be removed
    Examples:
      | channel         |
      | Digital         |
      | PublicApiMobile |

  Scenario Outline: Remove any associated linked passenger from the flights and reapportion admin fee
    Given I am using the channel <channel>
    And I have created a new customer
    And I searched a 'Standard' flight with return for 1 adult
    And I added it to the basket with 'Standard' fare as 'outbound/inbound' journey
    But there's no availability for the outbound flight
    And I have updated the passenger information
    And I have got the payment method as 'STANDARD_CUSTOMER'
      When I do commit booking for given basket with unavailable inventory
#    Then I should receive a confirmation message with code 'SVC_100022_3011'
    And the flight should be removed from basket
    And and the inbound journey type is now single
    And the association of the passenger has been removed from inbound flight
    And the information of the removed passengers should be removed
    And the admin fee will be apportioned among the passenger of the 'inbound' flight
    Examples:
      | channel         |
      | Digital         |
      | PublicApiMobile |

  @BR:BR_01266
  Scenario Outline: Flight being removed has full admin fee and the next flight is part of a pair
    Given I am using the channel <channel>
    And I have created a new customer
    And I searched a flight for 1 adult
    And I added it to the basket with 'Standard' fare as 'single' journey
    But there's no availability for that flight
    And I searched a 'Standard' flight with return for 1 adult
    And I added it to the basket with 'Standard' fare as 'outbound/inbound' journey
    And I have updated the passenger information
    And I have got the payment method as 'STANDARD_CUSTOMER'
    When I do commit booking for given basket with unavailable inventory
#    Then I should receive a confirmation message with code 'SVC_100022_3011'
    And the flight should be removed from basket
    And the association of the passenger has been removed from other flight
    And the admin fee will be apportioned among the passenger of the 'next pair of' flight
    Examples:
      | channel         |
      | Digital         |
      | PublicApiMobile |

  @BR:BR_01266
  Scenario Outline: Flight being removed has full admin fee and the next flight is not part of a pair
    Given I am using the channel <channel>
    And I have created a new customer
    And I searched a flight for 1 adult
    And I added it to the basket with 'Standard' fare as 'single' journey
    But there's no availability for that flight
    And I searched a flight for 1 adult
    And I added it to the basket with 'Standard' fare as 'single' journey
    And I have updated the passenger information
    And I have got the payment method as 'STANDARD_CUSTOMER'
    When I do commit booking for given basket with unavailable inventory
#    Then I should receive a confirmation message with code 'SVC_100022_3011'
    And the flight should be removed from basket
    And the admin fee will be apportioned among the passenger of the 'next' flight
    Examples:
      | channel         |
      | Digital         |
      | PublicApiMobile |