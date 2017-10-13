@TeamD
@Sprint30
@FCPH-10207
Feature: Enable Customer.validateMembershipNumber service for PublicAPI and Digital

  Scenario Outline: Generate Error message if the ej plus is not in the right format BR_00420 BR_00430
    Given I am using one of this channel Digital, PublicApiMobile
    And want to send ejPlusMembership number <invalid ejPlus> is less than expected size
    When I send the request to validateMembership service
    Then the channel will receive a warning with code SVC_100062_1002
    Examples:
      | invalid ejPlus |
      | 0000000        |
      | S00000         |

  Scenario Outline: Generate error if mandatory fields are not provided
    Given I am using one of this channel Digital, PublicApiMobile
    And want to send the request contains only <field>
    When I send the request to validateMembership service
    Then the channel will receive an error with code <error>
    Examples:
      | field        | error           |
      | ejPlusNumber | SVC_100062_1006 |
      | lastName     | SVC_100062_1000 |
      | customerId   | SVC_100062_1000 |

  Scenario: Generate error message if the surname and ej plus number not match what is stored on the membership file
    Given I am using one of this channel Digital, PublicApiMobile
    And want to send the request with surname and ejPlusnumber not match
    When I send the request to validateMembership service
    Then the channel will receive a warning with code SVC_100062_1003

  Scenario Outline: Generate error if unable to find EJ plus details stored on the membership file
    Given I am using one of this channel Digital, PublicApiMobile
    And want to send the request with ejPlus number <ejPlus> is not identified
    When I send the request to validateMembership service
    Then the channel will receive a warning with code SVC_100062_1005
    Examples:
      | ejPlus   |
      | 00000000 |
      | S000000  |

  Scenario: eJ plus expiry date is not less than the current date BR_00390
    Given I am using one of this channel Digital, PublicApiMobile
    And want to send the request with ejPlus number expiry date is in the past
    When I send the request to validateMembership service
    Then the channel will receive a warning with code SVC_100062_1004

  Scenario Outline: Return the ej plus membership details to the channel
    Given I am using one of this channel Digital, PublicApiMobile
    And want to send the request with <type> ejPlus number and surname is valid
    When I send the request to validateMembership service
    Then the ejPlus membership details returned to the channel
    Examples:
      | type     |
      | customer |
      | staff    |



