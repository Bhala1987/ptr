@TeamA
@Sprint31
@Sprint32
@FCPH-11081
@FCPH-10856
Feature: Receive request to add a compensation to Booking

  Scenario Outline: Add Compensation transaction to the basket
    Given I am using ADAirport channel
    And I login as agent
    And my basket contains flight with passengerMix "2 Adults"
    And I have updated the passenger information
    And I create a Customer
    And I do commit booking for given basket
    And I amend the basket
    And create compensation for a basket using paymentType <paymentType> and <isLeadPassenger>
    And compensation payment status REFUND_PENDING
    When I do commit booking for given basket
    Then booking contains payment transaction type REFUND_ACCEPTED
    Examples:
      | paymentType  | isLeadPassenger |
      | voucher      | true            |
      | cheque       | true            |
      | bank Account | false           |
      | credit file  | false           |

  @TeamA
  @Sprint32
  @FCPH-10908
  Scenario Outline: Create the Voucher
    Given I am using ADAirport channel
    When I have amendable basket for Standard fare and 2 Adults passenger
    And create compensation with voucher, <currency> and 100
    When I do commit booking for given basket
    Then booking contains payment transaction type REFUND_ACCEPTED
    Then I see voucher's unique code, email, amount, <currency>, balance same as original amount and active,end dates
    Examples:
      | currency |
      | GBP      |
      | EUR      |
      | MAD      |

  @TeamA
  @Sprint32
  @FCPH-10908
  @manual
  Scenario: Send voucher email to the passenger
    Given I am using ADAirport channel
    When I have amendable basket for Standard fare and 2 Adults passenger
    And create compensation with voucher, <currency> and 100
    When I do commit booking for given basket
    Then I generate an email with the voucher details
    Then I send the email the requested email address