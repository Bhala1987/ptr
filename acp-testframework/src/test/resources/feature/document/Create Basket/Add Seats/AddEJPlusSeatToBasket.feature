@FCPH-8627 @Sprint25
Feature: Add the Product bundle to the basket for EJ plus Customer bundle

  Scenario Outline: No Seat Purchased - Standard FareType for eJPlusCustomer
    Given I have added a LTN to ALC flight to basket with faretype <fareType> for <channel>
    When I receive a valid updatePassengerDetails with ejPlus type <userType>
    Then the seats are updated to <product> and seat price is "0"
    Examples:
      | fareType | channel   | userType | product       |
      | Standard | ADAirport | customer | Extra legroom |
      | Standard | Digital   | staff    | Up front      |
      | Flexi    | ADAirport | customer | Extra legroom |
      | Flexi    | Digital   | staff    | Up front      |

  Scenario Outline: Seat Purchased - Standard FareType - Purchase SPC-3 Seat for eJPlusCustomer
    Given I want to proceed with add purchased seat <seatType>
    And I have added a LTN to ALC flight to basket with faretype <fareType> for <channel>
    And I added a seat <seatType> to the first passenger
    When I receive a valid updatePassengerDetails with ejPlus type <userType>
    Then the seats are not changed still it is <product> and seat price is "0"
    Examples:
      | fareType | channel   | seatType      | userType | product       |
      | Standard | ADAirport | UPFRONT       | customer | Up front      |
      | Standard | Digital   | UPFRONT       | staff    | Up front      |
      | Standard | ADAirport | EXTRA_LEGROOM | customer | Extra legroom |
      | Flexi    | ADAirport | STANDARD      | customer | Standard      |
      | Flexi    | ADAirport | STANDARD      | staff    | Standard      |
      | Flexi    | Digital   | UPFRONT       | customer | Up front      |
      | Flexi    | Digital   | UPFRONT       | staff    | Up front      |

  Scenario Outline: Seat Purchased - Standard FareType - Purchase SPC-1 Seat for eJPlusStaff
    Given I want to proceed with add purchased seat EXTRA_LEGROOM
    And I have added a LTN to ALC flight to basket with faretype Standard for <channel>
    And I added a seat EXTRA_LEGROOM to the first passenger
    When I receive a valid updatePassengerDetails with ejPlus type staff
    Then the seats are not changed still it is "Extra legroom" but the price will be "SPC-1 MINUS SPC-2"
    Examples:
      | channel   |
      | ADAirport |

