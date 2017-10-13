@FCPH-193
Feature: Search indirect flights

  Scenario Outline: Return indirect flights for a channel
    Given "<channel>" has configured to search for indirect flights
    And that indirect flights are configured for "LTN" to "BCN"
    When I request for indirect flights for that route from "<channel>"
    Then indirect flights are returned
    Examples:
      | channel           |
      | ADCustomerService |

  Scenario Outline: Validate incorrect channel receives an error
    Given "<channel>" has not configured to search for indirect flights
    And that indirect flights are configured for "LTN" to "BCN"
    When I request for indirect flights for that route from "<channel>"
    Then I should see invalid channel error message
    Examples:
      | channel      |
      | Digital      |
      | PublicApiB2B |

  Scenario: Outbound indirect flights that includes the alternate departure airports
    Given outbound indirect flights are available from alternate departure airports for "LTN" to "BCN"
    When I request for indirect flights for that route
    Then indirect flights are returned to include the alternate outbound departure airports

  Scenario: Inbound indirect flights that includes the alternate departure airports
    Given inbound indirect flights are available from alternate departure airports for "BCN" to "LTN"
    When I request for indirect flights for that route
    Then indirect flights are returned to include the alternate inbound departure airports

  Scenario: Ignore flexible days for indirect flights
    Given that indirect flights are available for "LTN" to "BCN" on "+-5" flexible days too
    When I request for indirect flights for "+-5" flexible days
    Then return indirect flights only for the requested day

  Scenario Outline: Verify maximum duration of journey indirect flights (LGWEDI)
    Given indirect flights are available for "LGW" to "SSH" for more than the maximum allowed duration
    When I request for indirect flights for that route with no inbound flight
    Then indirect flights with more than <maximum duration> minutes duration are not returned
    Examples:
      | maximum duration |
      | 1440             |

  Scenario Outline: Verify minimum connection time allows
    Given indirect flights are available for "LTN" to "BCN" with less connection time than configured
    When I request for indirect flights for that route
    Then indirect flights with less than than <minimum connection time> are not returned
    Examples:
      | minimum connection time |
      | 120                     |

  @manual
  Scenario: Verify sufficient inventory exists for all sectors for all passengers
    Given for one of the sectors there is an inventory available only for a given passenger mix
    And all other sectors has enough inventory to accommodate the given passenger mix plus 1 adult
    When I request for indirect flights for that route
    Then this indirect flight should not be returned


  Scenario: Verify the results return are in total journey time ascending
    Given that indirect flights are configured for "LTN" to "BCN"
    When I request indirect flights for outbound
    Then indirect flights are returned in journey time ascending
