@TeamA
@Sprint31
@FCPH-9689
Feature: Refund using Credit File payment method for cancellation
  Scenario Outline: Generate an error if the payment method in the request is not matched with payment transaction
    Given I am using <channel> channel
    And I have added a flight with bookingType "<bookingType>" to the basket
    When I send a valid commit booking request with credit fund "<fundName>" as payment type
    Then I receive a booking confirmation and booking
    And cancel my booking with a refund issued
    And booking response returns svc_100545_1005

    Examples:
      | channel           | fundName   | bookingType |
      | ADAirport         | ACTIVE_GBP | BUSINESS    |

  Scenario Outline:Cancel booking and verify the CreditFile amount has incremented or not and status "Refund accepted"
    Given I am using <channel> channel
    And I have added a flight with bookingType "<bookingType>" to the basket
    And I will verify the credit file current balance using "<fundName>"
    When I send a valid commit booking request with credit fund "<fundName>" as payment type
    Then I receive a booking confirmation and booking
    And I cancel the booking with refund and will get 200 response code
    And I will verify the amount for "<fundName>" and status as REFUND_ACCEPTED

    Examples:
      | channel           | fundName   | bookingType |
      | ADAirport         | ACTIVE_GBP | BUSINESS    |

  Scenario Outline:Cancel booking with different currency and credit the amount into the original credit file
    Given I am using <channel> channel
    And I have added a flight with bookingType "<bookingType>" to the basket
    And I will verify the credit file current balance using "<fundName>"
    When I send a valid commit booking request with credit fund "<fundName>" as payment type
    Then I receive a booking confirmation and booking
    And I cancel the booking with refund and will get 200 response code
    And I will use currency conversion for "<fundName>" and status as REFUND_ACCEPTED

    Examples:
      | channel           | fundName   | bookingType |
      | ADCustomerService | ACTIVE_EUR | BUSINESS    |
