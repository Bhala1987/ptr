@Sprint26 @FCPH-396
Feature: Commit booking fulfillment using cash as the payment method.

  As a customer communicating via any/all channel(s)
  I want be able to complete my order using cash
  So that I can satisfy my order using a payment method that suites me.

  Scenario Outline: Generate an error if cash payments are not supported by the booking type.
    Given I am logged in via channel "<channel>"
    And I have added a flight with bookingType "<bookingType>" to the basket
    When I send a commit booking request with cash as the payment type
    Then I receive a payment error "SVC_100022_2124"
    Examples:
      | channel   | bookingType    |
      | ADAirport | SERIES_SEATING |
      | ADAirport | IMMIGRATION    |
      | ADAirport | MARKETING      |
      | ADAirport | CHARITY        |

  Scenario Outline: Generate an error if multiple *cash* payment methods are specified which contain different currencies.
    Given I am logged in via channel "<channel>"
    And I have added a flight with bookingType "<bookingType>" to the basket
    When I send a commit booking request with cash as the payment type on multiple payment methods in different currencies
    Then I receive a payment error "SVC_100022_2125, SVC_100022_2080"
    Examples:
      | channel   | bookingType       |
      | ADAirport | STANDARD_CUSTOMER |

  @TeamC @Sprint32 @FCPH-10067
  Scenario Outline: Successfully save the booking and record the cash payment method details.
    Given I am logged in via channel "<channel>"
    And I have added a flight with bookingType "<bookingType>" to the basket
    When I send a commit booking request with cash as the payment type
    Then I receive a booking confirmation and booking
    And I commit again the booking for the amendable basket
    And I receive a booking confirmation and booking
    Examples:
      | channel   | bookingType       |
      | Digital   | STANDARD_CUSTOMER |
      | ADAirport | STANDARD_CUSTOMER |
