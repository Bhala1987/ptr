Feature: Provide Reference data to create a saved passenger

  @AsXml
  @FCPH-3235
  Scenario Outline: Receive request to for SSR (Special Service Requests) for valid sector
    Given I have access to SSR for "<channel>" for the channel
    And The sector passed in the request is a valid "<sector>" sector
    When I select SSR
    Then I will return a list of active SSR for the requesting channel and sectors
  @regression
    Examples:
      | channel           | sector |
      | ADCustomerService | LGWALC |
    Examples:
      | channel         | sector |
      | ADAirport       | LTNFAO |
      | PublicApiMobile | LTNFAO |

  @FCPH-343
  Scenario Outline: SSR for Valid sector to show T&C
    Given I have access to SSR for "<channel>" for the channel
    And The sector passed in the request is a valid "<sector>" sector
    When I select SSR
    Then I will return a list of active SSR for the requesting channel and sectors
    And I will show which SSR require T&C to be accepted
    Examples:
      | channel           | sector |
      | ADAirport         | LTNFAO |
      | ADCustomerService | LGWALC |
      | PublicApiMobile   | LGWALC |

  @FCPH-3235
  Scenario Outline: Receive request to for SSR (Special Service Requests) for empty sector
    Given I have access to SSR for "<channel>" for the channel
    And The sector passed in the request is empty
    When I select SSR
    Then I will return a list of active SSR for the requesting channel and sectors
    Examples:
      | channel           |
      | Digital           |
      | ADAirport         |
      | ADCustomerService |
      | PublicApiMobile   |

  @FCPH-3235
  Scenario Outline: Receive request to for SSR (Special Service Requests) for invalid sector
    Given I have access to SSR for "<channel>" for the channel
    And The sector passed in the request is an invalid sector
    When I select SSR
    Then I will return the "SVC_100029_2001" error
    Examples:
      | channel           |
      | ADCustomerService |
      | ADAirport         |
      | Digital           |
      | PublicApiMobile   |