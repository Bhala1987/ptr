@TeamA
Feature: Receive request to add a compensation

  @FCPH-10856 @Sprint31
  Scenario Outline: Error Scenarios Invalid Passenger,BasketID,FlightKey
    Given I am using ADAirport channel
    And I login as agent
    And my basket contains flight with passengerMix "1 Adults"
    And I have updated the passenger information
    And I get customer profile <profile>
    And I do commit booking for given basket
    And I amend the basket
    When create compensation process using invalid <item>
    Then create compensation service returns "<errorCode>"
    Examples:
      | profile     | errorCode       | item                   |
      | cus00000001 | SVC_100552_3001 | invalidbasketID        |
      | cus00000001 | SVC_100552_3003 | invalidFlightKey       |
      | cus00000001 | SVC_100552_3004 | invalidpassengerID     |
      | cus00000001 | SVC_100552_3004 | passengerNotOnFlight   |
      | cus00000001 | SVC_100552_3005 | incorrectPaymentMethod |

  @FCPH-10856 @Sprint31
  Scenario Outline: Mandatory Fields missing for payment method
    Given I am using ADAirport channel
    And I login as agent
    And my basket contains flight with passengerMix "1 Adults"
    And I have updated the passenger information
    And I get customer profile <profile>
    And I do commit booking for given basket
    And I amend the basket
    When create compensation omitting the mandatory "<item1>"
    Then create compensation service returns "<errorCode>"
    Examples:
      | profile     | errorCode       | item1                 |
      | cus00000001 | SVC_100552_2004 | emailAndnameOnVoucher |
      | cus00000001 | SVC_100552_2004 | nameAndAddress        |
      | cus00000001 | SVC_100552_2004 | BankDetails           |
      | cus00000001 | SVC_100552_2004 | creditFile            |
      | cus00000001 | SVC_100552_3006 | invalidEmail          |

  @schema @FCPH-11062 @Sprint32 @TeamA
  Scenario Outline: Booking Update event is sent when compensation is added
    Given I am using ADAirport channel
    And I login as agent
    And my basket contains flight with passengerMix "2 Adults"
    And I have updated the passenger information
    And I create a Customer
    And I do commit booking for given basket
    And I amend the basket
    And create compensation for a basket using paymentType <paymentType> and <isLeadPassenger>
    Then I do commit booking for given basket
    And I validate the json schema for updated booking event
    Examples:
      | paymentType  | isLeadPassenger |
      | voucher      | true            |
      | cheque       | true            |
      | bank Account | false           |
      | credit file  | false           |