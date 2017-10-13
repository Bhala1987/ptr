@TeamA @Sprint31 @FCPH-10865
Feature: Process messages sent by the payment service to update refund payment transactions.

  As a customer
  I'd like to receive refunds when I fully or partially cancel parts of my booking
  So that I can get my money back

  # This test is manual in order to reproduce it you'll need to either get the
  # integration guys to send messages into the queues or set up ActiveMQ (or similar) locally
  # and send the messages yourself.

  @manual
  Scenario Outline: Process refund message from the payment service.
    Given I have a booking that has refund transactions
    And those transactions have not yet been fulfilled
    When a message is received from the payment service to update the refund transactions to: <Refund Status>
    Then the refund transactions should have their status's updated to: <Refund Status>
    And the payment transaction modified date should be set to 'now'

    Examples:
      | Refund Status   |
      | Refund Accepted |
      | Refund Rejected |
      | Refund Failed   |
