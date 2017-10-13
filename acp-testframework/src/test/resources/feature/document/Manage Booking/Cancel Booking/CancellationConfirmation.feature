@FCPH-10510
@Sprint31
@TeamE
Feature: Send cancellation confirmation email

  @manual
  Scenario Outline: 1 - Generate cancellation confirmation to the channel BR_01960
    Given that the channel has initiated a Booking.cancelBooking
    When the booking has been marked as Customer cancelled
    Then the system will generate the <cancellationConfirmation> including URL for Cancellation
    And the system will generate in the language of the booking
    Examples:
      | cancellationConfirmation |
      |Booking Reference         |
      |Payment amount            |
      |Payment Method            |
      |Refund amount             |
      |Refund Method             |
      |Passenger Name            |
      |Flight Sector             |
      |Flight STD                |
      |Flight ETA                |
      |Flight Number             |
      |Useful Information        |
      |easyJet Trademark         |

  @manual
  Scenario: 2 - Send cancellation confirmation to the registered customer email address
    Given that the booking has been marked as Customer cancelled
    When the system generates the cancellation confirmation
    Then it will send the cancellation confirmation email to the customer registered email address

  @manual
  Scenario: 3 - Generate audit trail for the confirmation email
    Given that the booking has been marked as Customer cancelled
    When the system generates the cancellation confirmation
    Then it will generate a entity row on the booking history containing Document Type
    And Requesting Channel
    And Issue Date/Time
    And Requesting User ID - Agent or Customer
    And Issued To - email address(es)
