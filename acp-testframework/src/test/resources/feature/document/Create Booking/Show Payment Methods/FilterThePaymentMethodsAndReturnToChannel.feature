@FCPH-317
Feature: Return the list of applicable payment methods

  @regression
  Scenario Outline: Return applicable payment methods to the channel
    Given I have all the payment types for <channel>
    Given that I have all the payment types defined in the back office
    And I search for flight with following details via <channel>
      | journey      | single  |
      | origin       | LTN     |
      | destination  | ALC     |
      | passengerMix | 1 adult |
    And I add flight to my basket with fare type as <faretype>
    When I call the service to retrieve "<channel>" payment methods
    Then the applicable payment methods for "<channel>" are returned
    Examples:
      | channel           | faretype |
      | ADCustomerService | Standard |
      | Digital           | Standard |
      | PublicApiB2B      | Standard |

  Scenario Outline: Filter out payment types not set up in the back office
    Given that I have all the payment types defined in the back office
    And I search for flight with following details via <channel>
      | journey      | single  |
      | origin       | LTN     |
      | destination  | ALC     |
      | passengerMix | 1 adult |
    And I add flight to my basket with fare type as <faretype>
    When I receive the response from the payment service
    Then I will filter out any payment types which are not set up in the back office
    Examples:
      | channel           | faretype |
      | Digital           | Standard |
      | ADAirport         | Standard |

  Scenario Outline: Filter out payment types based on booking type
    Given I have all the payment types for <channel>
    And I search for flight with following details via <channel>
      | journey      | single  |
      | origin       | LTN     |
      | destination  | ALC     |
      | passengerMix | 1 adult |
    And I add <journey> flight to my basket with <passengerMix>,fare type as <faretype> and booking type as <bookingtype>
    When I request the payment methods with booking type as <bookingtype>
    And response should not have payment methods "<filteredPaymentMethod>" for booking type
    And response should have the payment menthod as <paymentmethods>
    And I clear the basket
    Examples:
      | channel           | journey | faretype | passengerMix | bookingtype       | filteredPaymentMethod         | paymentmethods           |
      | ADCustomerService | single  | Standard | 1 adult      | STANDARD_CUSTOMER | CREDITFILEFUND                | CARD,CASH                |
      | ADAirport         | single  | Standard | 1 adult      | CHARITY           | CARD,CASH,APPLE_PAY           | CREDITFILEFUND           |
#      | ADAirport         | single  | Standard | 1 adult      | DUTY_TRAVEL       | Voucher,APPLE_PAY             | CARD,CASH,CREDIT_ACCOUNT |
#      | ADCustomerService | single  | Standard | 1 adult      | IMMIGRATION       | CARD,CASH,APPLE_PAY           | CREDIT_ACCOUNT           |
      | PublicApiB2B      | single  | Standard | 1 adult      | EASYJET_HOLIDAYS  | CASH,APPLE_PAY,CREDITFILEFUND | CARD                     |
#      | ADCustomerService | single  | Standard | 1 adult      | MARKETING         | CARD,CASH,APPLE_PAY           | CREDIT_ACCOUNT           |
#      | ADCustomerService | single  | Standard | 1 adult      | LOYALTY_SCHEME    | APPLE_PAY                     | CARD,CASH,CREDIT_ACCOUNT |
      | ADCustomerService | single  | Standard | 1 adult      | SUB_GROUP_BOOKING | CREDITFILEFUND                | CARD,CASH                |
      | ADAirport         | single  | Standard | 1 adult      | SERIES_SEATING    | CARD,CASH,APPLE_PAY           | CREDITFILEFUND           |

#    For the staff customer there if all the employees are associated to customer then association will fail
  Scenario Outline: Filter out payment types based on booking type
    Given I have all the payment types for <channel>
    And I have valid basket with booking type as <bookingtype> for <channel>
    When I request the payment methods with booking type as "<bookingtype>"
    And response should not have payment methods <filteredPaymentMethod> for booking type
    Examples:
      | channel           | bookingtype | filteredPaymentMethod |
      | ADAirport | STAFF       | CREDITFILEFUND        |
      | Digital           | BUSINESS    | APPLE_PAY             |

  Scenario Outline: Filter out payment types based on allowedDaysTillDeparture
    Given I have all the payment types for <channel>
    And the payment type <paymentMethod> has an value set for allowedDaysTillDeparture
    And I search for flight departing "before" the allowedDaysTillDeparture with following details
      | journey      | single  |
      | origin       | LTN     |
      | destination  | ALC     |
      | passengerMix | 1 adult |
    And I add flight departing before allowed days till departure to my basket with fare type <faretype>
    When I call the service to retrieve the payment methods
    And filter out payment methods which are less than the allowedDays till Departure
    Examples:
      | channel           | faretype | paymentMethod |
      | ADAirport         | Standard | EV            |
      | Digital           | Standard | EV            |

  Scenario Outline: Filter out payment types based on allowedMarketCountryCode
    Given I have all the payment types for <channel>
    And I search for flight with following details via <channel>
      | journey      | single  |
      | origin       | LTN     |
      | destination  | ALC     |
      | passengerMix | 1 adult |
    And I add flight to my basket with fare type as <faretype>
    When I request the payment methods with country as <countrycode>
    Then I should get the payment types based on country
    Examples:
      | channel           | faretype | countrycode |
      | Digital           | Standard | GBR         |
      | ADCustomerService | Standard | DEU         |

  Scenario Outline: Filter out payment types based on allowedMarketCountryCode and currency
    Given I have all the payment types for <channel>
    And I search for <journey> flight from <origin> to <destination> for <passengerMix> via <channel>
    And I add flight to my basket with fare type as <faretype>
    When I request the payment methods with country as <countrycode>
    Then I <shouldornot> have the payment method <paymentMethod>
    Examples:
      | channel           | faretype | journey | passengerMix | origin | destination | countrycode | paymentMethod | shouldornot |
      | Digital           | Standard | single  | 1 adult      | MAD    | LTN         | DEU         | EV            | should      |
      | Digital           | Standard | single  | 1 adult      | MAD    | LTN         | FRA         | CB            | should      |
      | Digital           | Standard | single  | 1 adult      | MAD    | LTN         | DEU         | CB            | should not  |
      | ADCustomerService | Standard | single  | 1 adult      | LTN    | ALC         | GBR         | SW            | should      |
      | PublicApiB2B      | Standard | single  | 1 adult      | MAD    | LTN         | DEU         | CB            | should      |

  Scenario Outline: Filter out payment types based on currency
    Given I have all the payment types for <channel>
    And I search for flight with following details via <channel>
      | journey      | single  |
      | origin       | LTN     |
      | destination  | ALC     |
      | passengerMix | 1 adult |
    And I add flight to my basket with fare type as <faretype>
    And I have the allowed currencies set for each payment type
    When I receive the response from the payment service
    Then I will filter out any payment types based on the currency set in the basket
    Examples:
      | channel         | faretype |
      | ADAirport       | Standard |
      | Digital         | Standard |

  Scenario Outline:  filter payment method based on combination of filter
    Given I have all the payment types for <channel>
    And I search for flight with following details via <channel>
      | journey      | single  |
      | origin       | LTN     |
      | destination  | ALC     |
      | passengerMix | 2 adult |
    And I add flight to my basket with fare type as <faretype>
    When I send request to payment service with filter criteria <filter>
    Then I should get the payment types based on filter as <filter>
    Examples:
      | channel           | faretype | filter                      |
      | Digital           | Standard | bookingType,country         |
      | ADCustomerService | Standard | basketType,country,currency |
