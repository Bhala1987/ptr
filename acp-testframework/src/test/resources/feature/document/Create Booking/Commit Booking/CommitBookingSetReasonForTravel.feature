@Sprint29 @FCPH-10339 @TeamA
Feature:  Booking reason in the basket

  As a customer I want to be set my
  reason for travel that is applicable to the my current booking type

  @FCPH-10339 @TeamA
  Scenario Outline: Error message for amendable basket
    Given I have a basket with a valid flight with 1 adult added via <channel>
    When set LEISURE as travel reason using amendable basket ID
    Then booking reason service returns errorcode "<errorcode>"
    Examples:
      | channel | errorcode       |
      | Digital | SVC_100529_2001 |

  @FCPH-10339 @TeamA
  Scenario Outline: Error message when basket ID is not identified
    Given I have a basket with a valid flight with 1 adult added via <channel>
    When the requested basket ID can not be identified
    Then booking reason service returns errorcode "<errorcode>"
    Examples:
      | channel   | errorcode       |
      | ADAirport | SVC_100529_2001 |


  @FCPH-10339 @TeamA
  Scenario Outline: Error message when booking reason is not correct for booking type
    Given I have a basket with a valid flight with 1 adult added via <channel>
    When set SKATING as travel reason using the booking ID
    Then booking reason service returns errorcode "<errorcode>"
    Examples:
      | channel           | errorcode       |
      | ADCustomerService | SVC_100529_3002 |

  @FCPH-10339 @TeamA
  Scenario Outline: Error message when the booking reason is for another booking type
    Given I have a basket with a valid flight with 1 adult added via <channel>
    When set <reason> as travel reason using the booking ID
    Then booking reason service returns errorcode "<errorcode>"
    Examples:
      | channel           | errorcode       | reason      |
      | ADCustomerService | SVC_100529_3002 | FLIGHT CREW |

  @FCPH-10339 @TeamA
  Scenario Outline: Error message when mandatory fields are not provided
    Given I have a basket with a valid flight with 1 adult added via <channel>
    When no reason is provide to set as travel reason using the booking ID
    Then booking reason service returns errorcode "<errorcode>"
    Examples:
      | channel | errorcode       |
      | Digital | SVC_100529_2002 |

  @FCPH-10339 @TeamA @regression
  Scenario Outline: Store booking reason and Booking type against the basket
    Given I have a basket with a valid flight with 1 adult added via <channel>
    And set LEISURE as travel reason using the booking ID
    When basket contain valid booking type and reason
    Examples:
      | channel |
      | Digital |