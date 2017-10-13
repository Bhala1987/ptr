@FCPH-7353
Feature: getSectors request and response

  @AsXml
  @regression
  Scenario Outline: Sectors marked as active are returned
    Given there are active sectors in the database
    When I call the getSectors service for the <channel>
    Then I will return all the active sectors to the channel
    Examples:
      | channel           |
      | ADCustomerService |
      | ADAirport         |
      | Digital           |
      | PublicApiB2B      |
      | PublicApiMobile   |

  Scenario Outline: Specifying an origin-airport-code all the sector marked as active for that airport are returned
    Given there are active sectors in the database for <origin-airport-code>
    When I call the getSectors service for the <channel> for <origin-airport-code>
    Then I will return all the active sectors to the channel
    Examples:
      | channel           | origin-airport-code |
      | Digital           | LTN                 |
      | ADAirport         | LTN                 |
      | ADCustomerService | LTN                 |
      | PublicApiB2B      | LTN                 |
      | PublicApiMobile   | LTN                 |

  @manual
  Scenario: If there is no active sector, no data is returned
    Given there are no active sectors in the database
    When I call the getSectors service for the a channel
    Then I will return a no data to return response to the channel