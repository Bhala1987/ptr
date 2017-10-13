Feature: Process Change eJ Plus Details Request on passenger with a purchased seat, not Public API
  Validate Change eJ Plus Details Request, no basket update, not Public API

  @TeamC
  @Sprint29
  @FCPH-9001
  Scenario Outline: Manage eJ Plus Number with a purchased seat
    Given I am using <channel> channel
    And I want to proceed with add purchased seat STANDARD
    And I committed booking <fare> with purchased seat STANDARD for 1 Adult with <condition> EJPlus
    When I send update passenger details for <field>
    Then the eJ Plus Number has been <operation> to the passenger
    And the products associated with the eJ Plus Bundle has been <operation> to the basket
    And I will adjust the price of the purchased seat
    And the basket total has been updated
    Examples: Add eJ Plus Number with a purchased seat
      | channel | fare     | condition | field               | operation |
      | Digital | Standard | false     | addEJPlusMembership | added     |
    Examples: Remove eJ Plus Number with a purchased seat
      | channel         | fare  | condition | field                  | operation |
      | PublicApiMobile | Flexi | true      | removeEJPlusMembership | removed   |

  @TeamC
  @Sprint29
  @FCPH-9001
  Scenario Outline: Remove eJ Plus Number if the surname is change
    Given I am using <channel> channel
    And I want to proceed with add purchased seat UPFRONT
    And I committed booking <fare> with purchased seat UPFRONT for 1 Adult with <condition> EJPlus
    When I send update passenger details for <field>
    Then the eJ Plus Number has been removed to the passenger
    And a message SVC_100519_1002 has been returned to the channel providing another card
    And the products associated with the eJ Plus Bundle has been removed to the basket
    And I will adjust the price of the purchased seat
    And the basket total has been updated
    Examples:
      | channel | fare     | condition | field         |
      | Digital | Standard | true      | UpdateSurname |

  @TeamD
  @Sprint28 @Sprint29
  @FCPH-7342
  Scenario Outline: Receive Change Passenger eJ Plus Number Request
    Given <channel> do the commit booking with "2 Adult"
    And I have initiated change eJplus number for 1 st passenger
    When I send the request for change eJplus number
    Then I will validate the request is in the expected format
    Examples:
      | channel   |
      | ADAirport |
      | Digital   |

  @TeamD
  @Sprint28 @Sprint29
  @FCPH-7342
  Scenario Outline: Generate an error if eJ Plus Number has already been entered for another passenger BR_00400
    Given <channel> do the commit booking with "2 Adult"
    And I have added eJplus number for 1 st passenger
    When I send the request to change eJplus number for 2 nd passenger with same as 1 st
    Then I will get an <Error> error message
    Examples:
      | channel   | Error           |
      | ADAirport | SVC_100012_3040 |
      | Digital   | SVC_100012_3040 |

  @TeamD
  @Sprint28 @Sprint29
  @FCPH-7342
  Scenario Outline: Generate an error if passenger's last name does not match eJ Plus member name BR_00410
    Given <channel> do the commit booking with "2 Adult"
    And I have initiated change eJplus number for 1 st passenger
    When I send the request for change eJplus number with incorrect last name
    Then I will get an <Error> error message
    Examples:
      | channel   | Error           |
      | ADAirport | SVC_100000_2074 |
      | Digital   | SVC_100000_2074 |

  @TeamD
  @Sprint28 @Sprint29
  @FCPH-7342
  Scenario Outline: Generate an error if eJ Plus member expiry date is before the current date BR_00390
    Given <channel> do the commit booking with "2 Adult"
    And I have initiated change eJplus number for 1 st passenger
    When I send the request for change eJplus number which is expired
    Then I will get an <Error> error message
    Examples:
      | channel   | Error           |
      | ADAirport | SVC_100012_2078 |
      | Digital   | SVC_100012_2078 |

  @TeamD
  @Sprint28 @Sprint29
  @FCPH-7342
  Scenario Outline: Generate an error if eJ Plus Number is not the correct format BR_00430, BR_00420
    Given <channel> do the commit booking with "2 Adult"
    And I have initiated change eJplus number for 1 st passenger
    When I send the request for change eJplus number with incorrect format <incorrectFormat>
    Then I will get an <Error> error message
    Examples:
      | channel   | Error           | incorrectFormat |
      | ADAirport | SVC_100000_2075 | A1111           |
      | Digital   | SVC_100012_3027 | 1234567891      |

  @TeamD
  @Sprint28 @Sprint29
  @FCPH-7342
  Scenario Outline: Generate an error if eJ Plus status is not complete BR_4004
    Given <channel> do the commit booking with "2 Adult"
    And I have initiated change eJplus number for 1 st passenger
    When I send the request for change eJplus number which is not in complete status
    Then I will get an <Error> error message
    Examples:
      | channel   | Error           |
      | ADAirport | SVC_100000_2088 |
      | Digital   | SVC_100000_2088 |

  @TeamD
  @Sprint28 @Sprint29
  @FCPH-7342
  Scenario Outline: Return error to channel for an invalid change in eJ Plus
    Given <channel> do the commit booking with "2 Adult"
    And I have initiated change eJplus number with incorrect passenger id "123456"
    When I send the request for change eJplus number with incorrect passenger id
    Then I will get an <Error> error message
    Examples:
      | channel   | Error           |
      | ADAirport | SVC_100402_2003 |
      | Digital   | SVC_100402_2003 |

  @TeamD
  @Sprint28 @Sprint29
  @FCPH-7342
  Scenario Outline: Validate eJ Plus Number has already been entered for same passenger different flight
    Given <channel> do the commit booking with "2 Adult"
    And I have added eJplus number for 1 st passenger in first flight
    When I send the request to change the same eJplus number for same passenger in another flight
    Then the response should be successful
    Examples:
      | channel   |
      | ADAirport |
      | Digital   |



