@FCPH-3679
Feature: Save flight interest to a profile

  Scenario: Receive request to add flight interest to the customer profile
    Given staff Customer is logged in
    When I receive a request to add a flight interest to the profile
    Then I will validate that the request meets the service contract

  @pending
  @manual
  Scenario: Receive request to add flight interest to the customer profile
    Given staff Customer is logged in
    And flight standard departure time is less than the checkin time from now
    When I receive a request to add a flight interest on that flight to the profile
    Then I will return an error to the channel
    And  register flight interest is not added to the profile

  Scenario Outline:  Validate fare type for registered flight interest BR_00561*
    Given staff Customer is logged in
    When I receive a request to save a flight interest to the profile
    And the fare type "<fareType>" is not x= staff, staff standard or stand-by
    Then I will return a wrong fare type error to the channel
    And register flight interest is not added to the profile
    Examples:
      | fareType |
      | FLEXI    |

  Scenario Outline: Maximum number of registered flight interest BR_00560
    Given staff Customer is logged in
    And the customer already has "<interestNumber>" registered interest stored
    When I receive a request to add a new flight interest to the profile
    Then I will return a max number of registerd interests error to the channel
    And the register flight interest is not added to the profile
    Examples:
      | interestNumber |
      | 10             |

  Scenario:  Duplicate registered flight interest
    Given staff Customer is logged in
    And   a registered flight interest is stored
    When  I sent request to add registered flight interest to staff customer profile for same flight Same fare type
    Then  I will return a duplicated registration error to the channel
    And   register flight interest is not added to the profile

  Scenario: Store registered interest on the profile
    Given staff Customer is logged in
    When  I receive a valid request to add a flight interest to the profile
    Then  I will store registered flight interest to the profile
    And   return confirmation to the channel

  @regression
  Scenario Outline: Store multiple registered interests on the profile
    Given staff Customer is logged in
    When I receive a valid request to add multiple "<flightKeyNumber>" registered flight interest to staff customer profile
    Then I will store registered all flight interest to the profile
    And return all confirmation to the channel
    Examples:
      | flightKeyNumber |
      | 3               |

  Scenario Outline: Fail one flight interest from multiple registered interests on the profile
    Given staff Customer is logged in
    When I receive a request to add multiple "<flightKeyNumber>" registered flight interest to staff customer profile and one flight interest fails the validation
    Then I return confirmation to the channel
    And  I return error message in Additional Details for the failed flight interests
    And  I will store successful registered flight interests to the profile
    Examples:
      | flightKeyNumber |
      | 3               |

  Scenario: Error wen channel is not Digital
    When When I have receive a request from a Channel that is not Digital and I perform the validation
    Then I will return an error Incorrect Channel, not supported request
