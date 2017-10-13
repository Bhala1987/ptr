Feature: Request to cancel a booking and issue a refund

  As a passenger on a flight
  I want to be able to cancel my full booking
  and be refunded a fee

  @regression @Sprint29 @FCPH-9158 @TeamA

  Scenario: Generate error message with invalid booking reference
    Given I am using Digital channel
    When cancel my booking with an invalid booking ref number
    Then booking response returns SVC_100545_1001

  @Sprint29 @FCPH-9158 @TeamA

  Scenario Outline: Generate error message if the requested refund amount does not match what is due
    Given I am using Digital channel
    And the channel has initiated a CheckInForFlight for "<passengers>"
    When cancel my booking with an incorrect refund amount
    Then booking response returns SVC_100545_1003
    Examples:
      | passengers |
      | 1 Adult    |

  @regression @Sprint29 @FCPH-3962 @Sprint31 @TeamA
  Scenario Outline: Change booking status to cancelled
    Given I am using Digital channel
    And the channel has initiated a CheckInForFlight for "<passengers>"
    When cancel my booking with a refund issued
    And channel send getbooking request
    Then updated booking status should return "<status>"

    Then I will set the transaction "<tranStatus>"

    Examples:
      | passengers | status    | tranStatus     |
      | 1 Adult    | CANCELLED | REFUND_PENDING |

  @Sprint31 @FCPH-3962 @TeamA
  Scenario Outline: Refund with an Invalid currency or CardType
    Given I am using Digital channel
    And the channel has initiated a CheckInForFlight for "<passengers>"
    When cancel my booking using an "<currency>" and "<paymentMethod>"
    Then booking response returns <errorCode>

    Examples:
      | passengers | currency | paymentMethod | errorCode       |
      | 1 Adult    | EUR      | CARD          | SVC_100545_1004 |
      | 1 Adult    | GBP      | ELV           | SVC_100545_1005 |

  @Sprint31 @FCPH-3962 @TeamA
  Scenario Outline:Generate refund request to payment service using ELV payment method
    Given I am using Digital channel
    And commit booking with passengerMix "<passengerMix>" using "<payment type>" and "<payment details>"
    When cancel my booking using an "<currency>" and "<paymentMethod>"
    And channel send getbooking request
    Then updated booking status should return "<status>"
    Then I will set the transaction "<tranStatus>"
    Examples:
      | passengerMix | payment type | payment details                         | status    | tranStatus     | currency | paymentMethod |
      | 2 Adults     | elv          | tester-1234567890- -12345678- -Barclays | CANCELLED | REFUND_PENDING | EUR      | ELV           |

  @Sprint31 @FCPH-3962 @TeamA
  Scenario Outline: Refund with an Invalid currency or CardType
    Given I am using Digital channel
    And the channel has initiated a CheckInForFlight for "<passengers>"
    When cancel my booking with a refund issued
    And channel send getbooking request
    Then updated booking status should return "<status>"
    Then I will set the transaction "<tranStatus>"
    Examples:
      | passengerMix | payment type | payment details                         | status    | tranStatus     | currency | paymentMethod |
      | 2 Adults     | elv          | tester-1234567890- -12345678- -Barclays | CANCELLED | REFUND_PENDING | EUR      | ELV           |


  @schema @Sprint31 @FCPH-3962 @TeamA @FCPH-10848 @Sprint32
  Scenario Outline: Issue Refund on Cancelled booking will receive unsuccessful synchronous response from payment service
    Given I am using Digital channel
    And the channel has initiated a CheckInForFlight for "<passengers>"
    And cancel my booking with a refund issued
    And channel send getbooking request
    Then updated booking status should return "<status>"
    And I validate the json schema for booking cancelled event
    And the booking cancelled event contains the correct customer name
    When attempt to cancel a cancelled booking and issue another refund
    Then booking response returns SVC_100022_3065

    Examples:
      | passengers | status    |
      | 1 Adult    | CANCELLED |

  @schema @FCPH-10848 @Sprint32 @TeamA
  Scenario Outline: Cancellation of booking by agent returns a booking cancelled event and the event contains the agent name
    Given I am using "<channel>" channel
    And I login as agent with username as "dan" and password as "12341234"
    And the channel has initiated a CheckInForFlight for "<passengers>"
    When cancel my booking with a refund issued
    And channel send getbooking request
    Then updated booking status should return "<status>"
    And I validate the json schema for booking cancelled event
    And the booking cancelled event contains the correct agent name
    Examples:
      | passengers | status    | channel   |
      | 1 Adult    | CANCELLED | ADAirport |

  @schema @FCPH-10848 @Sprint32 @TeamA
  Scenario Outline: Cancellation of AD booking paid using credit file creates booking cancelled event message
    Given I am using "<channel>" channel
    And I login as agent with username as "dan" and password as "12341234"
    And I have created a new customer
    And I have added a flight with bookingType "<bookingType>" to the basket
    When I send a valid commit booking request with credit fund "<fundName>" as payment type
    And I receive a booking confirmation and booking
    When cancel my booking with a creditfilefund issued
    Then updated booking status should return "CANCELLED"
    And I validate the json schema for booking cancelled event
    And the booking cancelled event contains the correct agent name
    Examples:
      | channel           | fundName   | bookingType |
      | ADAirport         | ACTIVE_GBP | BUSINESS    |
      | ADCustomerService | ACTIVE_GBP | BUSINESS    |

  @TeamA @Sprint32 @FCPH-11058
  Scenario Outline:Generate partial cancellation
    Given I am using <channel> channel
    And commit booking with passengerMix "<passengerMix>" using "<payment type>" and "<payment details>"
    And I create amendable basket for the booking created
    And I send a request to Remove first passenger
    And I recommit booking with <payment type> with partial refund
    Then I see commit is successful
    Then I will set the transaction "<tranStatus>"
    Examples:
      | channel           | passengerMix | payment type | payment details                             | tranStatus     |
      | Digital           | 2 Adults     | elv          | tester-1234567890- -12345678- -Barclays     | REFUND_PENDING |
      | ADCustomerService | 2 Adults     | elv          | tester-1234567890- -12345678- -Barclays     | REFUND_PENDING |
      | ADCustomerService | 2 Adults     | card         | DM-5573471234567898-123-8-2018-Testing card | REFUND_PENDING |
      | Digital           | 2 Adults     | card         | DM-5573471234567898-123-8-2018-Testing card | REFUND_PENDING |

  @TeamA @Sprint32 @FCPH-11058
  Scenario Outline: Generate error if the refund amount does not match the payment amount
    Given I am using <channel> channel
    When commit booking with passengerMix "<passengerMix>" using "<payment type>" and "<payment details>"
    And I create amendable basket for the booking created
    And I send a request to Remove first passenger
    And I recommit booking with  <payment type> with incorrect partial refund
    Then I see booking error SVC_100022_2131 message is displayed
    Examples:
      | channel           | passengerMix | payment type | payment details                             |
      | Digital           | 2 Adults     | elv          | tester-1234567890- -12345678- -Barclays     |
      | ADCustomerService | 2 Adults     | card         | DM-5573471234567898-123-8-2018-Testing card |


  @TeamA @Sprint32 @FCPH-11058 @local
  Scenario Outline: Receive unsuccessful synchronous response from payment service
    Given I am using <channel> channel
    When commit booking with passengerMix "<passengerMix>" using "<payment type>" and "<payment details>"
    And I create amendable basket for the booking created
    And I send a request to Remove first passenger
    And I recommit booking when payment service down with  <payment type> with <xposId>
    Then I see booking error SVC_100022_3070 message is displayed
    Examples:
      | channel | passengerMix | payment type | payment details                         | xposId                               |
      | Digital | 2 Adults     | elv          | tester-1234567890- -12345678- -Barclays | abcdabcd-abcd-abcd-abcd-abcdabcdabcd |


  @FCPH-11059 @Sprint32 @TeamA
  Scenario Outline: Partial refund with credit file
    Given I am using <channel> channel
    Given I've amendable basket with with credit fund <fundName> as payment type
    And I send a request to Remove first passenger
    And I recommit booking with <fundName> with partial refund
    Then I see commit is successful
    Then I will set the transaction "REFUND_ACCEPTED"
    Examples:
      | channel           | fundName   |
      | ADAirport         | ACTIVE_GBP |
      | ADCustomerService | ACTIVE_GBP |


  @FCPH-11059 @Sprint32 @TeamA
  Scenario Outline: Refund amounts more than original or more than refund amounts
    Given I am using <channel> channel
    Given I've amendable basket with with credit fund <fundName> as payment type
    And I send a request to Remove first passenger
    And I recommit with incorrect refund <refundError>  fund <fundName> with partial refund
    Then I see booking error <errorCode> message is displayed
    Examples:
      | channel           | fundName   | refundError           | errorCode       |
      | ADAirport         | ACTIVE_GBP | MoreThanOriginalAmt   | SVC_100022_2130 |
      | ADCustomerService | ACTIVE_GBP | MoreThanRefundAmt     | SVC_100022_2131 |
      | ADCustomerService | ACTIVE_GBP | PaymentMethodNotMatch | SVC_100022_2134 |

  @FCPH-11059 @Sprint32 @TeamA
  Scenario Outline: Partial refund with credit file with different currency
    Given I am using <channel> channel
    Given I've amendable basket with with credit fund <fundName> as payment type
    And I send a request to Remove first passenger
    And I recommit booking with <fundName> with partial refund with different <currency> currency
    Then I will convert the requested refund amount to the currency of the credit file using the spot rate
    Then I see commit is successful
    Then I will set the transaction "REFUND_ACCEPTED"
    Examples:
      | channel   | fundName   | currency   |
      | ADAirport | ACTIVE_EUR | ACTIVE_EUR |


  @TeamA @Sprint32 @FCPH-4085 @local
  Scenario Outline: Request KANA approval for special refunds, single payment - AD Only
    Given I am using <channel> channel
    When I have amendable basket for Standard fare and <passengerMix> passenger
    And I send a request to remove all passengers except one passenger
    And I recommit booking with <payment type> with partial refund
    Then I will set the transaction "PENDING_KANA_APPROVAL"
    Examples:
      | channel   | passengerMix | payment type |
      | ADAirport | 10 Adults     | card         |


  @TeamA @Sprint32 @FCPH-4085 @local
  Scenario Outline: KANA case not created
    Given I am using <channel> channel
    When I have amendable basket for Standard fare and <passengerMix> passenger
    And I send a request to remove all passengers except one passenger
    And I recommit booking when KANA is down  <payment type> with <xposId>
    Then I see booking error SVC_100125_1001 message is displayed
    Examples:
      | channel   | passengerMix | payment type | xposId                               |
      | ADAirport | 20 Adults     | card         | 00000000-0000-0000-0000-000000000001 |



  @SPR31OPTrials1 @FCPH-10060
  Scenario Outline: Generate an event to downstream systems when a refund has given
    Given I am using ADAirport channel
    And my basket contains flight with passengerMix "2 Adults"
    And I have updated the passenger information
    And I get customer profile cus00000001
    And I do commit booking for given basket
    And I amend the basket
    When I want to remove passenger for "<numFlight>"
    Then the passenger should be removed
    And I recommit booking with <payment type> with partial refund
    And I validate the json schema for updated booking event
    Examples:
      | numFlight | payment type |
      | 1 Flight  | card         |

  @SPR31OPTrials1 @manual @FCPH-10060
  Scenario Outline: Generate event to down stream system when a refund transation status has been updated
    Given I am using ADAirport channel
    And my basket contains flight with passengerMix "2 Adults"
    And I do commit booking for given basket
    And I amend the basket
    And I want to remove passenger for "<numFlight>"
    Then the passenger should be removed
    And I recommit booking with <payment type> with partial refund
    Then I do commit booking for given basket
    Then the payment service return a status refund accepted
    And I validate the json schema for updated booking event
    Examples:
      | numFlight | payment type |
      | 1 Flight  | card         |