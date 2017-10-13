Feature: Request to partial cancel a booking and issue a refund

  As a passenger on a flight
  I want to be able to partial cancel my booking
  and be refunded the amount due


  @FCPH-11057 @TeamA @Sprint31
  Scenario Outline: Generate error if cash is not original payment method on the booking
    Given the channel <channel> is used
    And I login as agent
    And my basket contains flight with passengerMix "2 Adults"
    And I have updated the passenger information
    And I do commit booking with card and <Payment Details>
    And I will generate a make payment request to the payment service
    And I amend the basket
    When I send a request to Remove a passenger for single flight <removeFromAllFlights>
    And I send a commit booking request with cash as the payment type after remove passenger
    Then the commit booking should fail with error SVC_100022_2132
    Examples:
      | channel   | removeFromAllFlights | Payment Details                              |
      | ADAirport | false                | DM-5573471234567898-123-08-2018-Testing card |

  @FCPH-11057 @TeamA @Sprint31
  Scenario Outline: Generate error if the refund amount does not match the payment amount
    Given the channel <channel> is used
    And I login as agent
    And my basket contains flight with passengerMix "2 Adults"
    And I have updated the passenger information
    And I send a commit booking request with cash as the payment type
    And I amend the basket
    When I send a request to Remove a passenger for single flight <removeFromAllFlights>
    And I send a commit booking request with cash as the payment type and invalid amount due <refundAmount>
    Then the commit booking should fail with error SVC_100022_2130
    Examples:
      | channel   | removeFromAllFlights | refundAmount |
      | ADAirport | false                | 10000.00     |

  @FCPH-11057 @TeamA @Sprint31
  Scenario Outline: Generate error if the refund amount does not match what is due
    Given the channel <channel> is used
    And I login as agent
    And my basket contains flight with passengerMix "2 Adults"
    And I have updated the passenger information
    And I send a commit booking request with cash as the payment type
    And I amend the basket
    When I send a request to Remove a passenger for single flight <removeFromAllFlights>
    And I send a commit booking request with cash as the payment type and invalid amount due <refundAmount>
    Then the commit booking should fail with error SVC_100022_2131
    Examples:
      | channel   | removeFromAllFlights | refundAmount |
      | ADAirport | false                | 0.00         |

  @FCPH-11057 @TeamA @Sprint31
  Scenario Outline: Create payment transaction on the amended booking
    Given the channel <channel> is used
    And I login as agent
    And my basket contains flight with passengerMix "2 Adults"
    And I have updated the passenger information
    And I send a commit booking request with cash as the payment type
    And I amend the basket
    When I send a request to Remove a passenger for single flight <removeFromAllFlights>
    And I send a commit booking request with cash as the payment type after remove passenger
    And channel send getbooking request
    Then I will set the transaction "<tranStatus>"
    Examples:
      | channel   | tranStatus      | removeFromAllFlights |
      | ADAirport | REFUND_ACCEPTED | false                |

  @FCPH-11285 @TeamA @Sprint31
  Scenario Outline: Add eJ cancellation fee when less than and equal to 24 hrs after booking - passenger
    Given the channel <channel> is used
    And I login as agent
    And my basket contains flight with passengerMix "2 Adults"
    And I have updated the passenger information
    And I do commit booking with card and <Payment Details>
    And I will generate a make payment request to the payment service
    And I amend the basket
    When I send a request to Remove a passenger for single flight <removeFromAllFlights>
    Then I will get the cancel fee <cancelFee>
    Examples:
      | channel   | cancelFee | removeFromAllFlights | Payment Details                              |
      | ADAirport | 14.0      | false                | DM-5573471234567898-123-08-2018-Testing card |

  @FCPH-11285 @TeamA @Sprint31
  Scenario Outline: Do NOT add eJ cancellation fee when less than and equal to 24 hrs after booking to prevent double charging in the same session - passenger
    Given the channel <channel> is used
    And I login as agent
    And my basket contains flight with passengerMix "3 Adults"
    And I have updated the passenger information
    And I do commit booking with card and <Payment Details>
    And I will generate a make payment request to the payment service
    And I amend the basket
    When I send a request to Remove 2 passengers for single flight <removeFromAllFlights>
    Then I will not add the cancel fee <cancelFee>
    Examples:
      | channel   | cancelFee | removeFromAllFlights | Payment Details                              |
      | ADAirport | 14.0      | false                | DM-5573471234567898-123-08-2018-Testing card |

  @FCPH-11285 @TeamA @Sprint31
  Scenario Outline: Update basket to include refund amount if the passenger is removed
    Given the channel <channel> is used
    And I login as agent
    And my basket contains flight with passengerMix "2 Adults"
    And I have updated the passenger information
    And I do commit booking with card and <Payment Details>
    And I will generate a make payment request to the payment service
    And I amend the basket
    When I send a request to Remove a passenger for single flight <removeFromAllFlights>
    And I do commit booking with card and <Payment Details>
    And I will generate a make payment request to the payment service with refund fee
    And channel send getbooking request
    Then I will set the transaction "<tranStatus>"
    Examples:
      | channel   | tranStatus     | removeFromAllFlights | Payment Details                              |
      | ADAirport | REFUND_PENDING | false                | DM-5573471234567898-123-08-2018-Testing card |


  @manual
  @FCPH-11285 @TeamA @Sprint31
  Scenario Outline: Calculate and add cancellation fee when more than 24 hrs after booking - passenger
    Given the channel <channel> is used
    And I login as agent
    And my basket contains flight with passengerMix "2 Adults"
    And I have updated the passenger information
    And I do commit booking with card and <Payment Details>
    And I will generate a make payment request to the payment service
    And I will amend the basket after 24 hrs
    When I send a request to Remove a passenger for amended basket
    Then I will add the cancel fee
    Examples:
      | channel   | Payment Details                              |
      | ADAirport | DM-5573471234567898-123-08-2018-Testing card |


  @manual
  @FCPH-11286 @TeamA @Sprint32
  Scenario Outline: Calculate and add cancellation fee when more than 24 hrs after booking - flight
    Given the channel Digital is used
    And I have a basket with 2 flights with infant on lap on one flight with <payment>
    And I will amend the basket after 24 hrs
    When I remove 1 flights
    Then I will add the cancel fee
    Examples:
      | payment   |
      | creditCard |
      | debitCard |


  @FCPH-11286 @Sprint32 @TeamA
  Scenario Outline: Add eJ cancellation fee when less than and equal to 24 hrs after booking - flight
    Given I am using <channel> channel
    And I have a basket with 2 flights with infant on lap on one flight with <payment>
    And I amend the basket
    When I remove 1 flights
    Then the correct refund amount is applied to <payment>

    Examples:
      | channel   | payment    |
      | ADAirport | debitCard  |
      | ADAirport | creditCard |


  @FCPH-11286 @Sprint32 @TeamA
  Scenario Outline: I remove multiple flights and see only one cancellation fee
    Given I am using <channel> channel
    And I have a basket with 3 flights with infant on lap on one flight with <payment>
    And I amend the basket
    When I remove 2 flights
    Then the correct refund amount is applied to <payment>

    Examples:
      | channel   | payment    |
      | ADAirport | debitCard  |
      | ADAirport | creditCard |
