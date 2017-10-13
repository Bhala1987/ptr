Feature: Classify a Change Name where length changes, not Public API

  @Sprint28 @FCPH-9149
  Scenario Outline: Return error for invalid request format or missing mandatory fields
    Given I am using the channel <channel>
    And I commit a booking with my <first-name>, <last-name> for <passenger> using <fare-type> amended basket
    When I update my <updated-first-name> to <updated-last-name>
    Then I see basket price <price-status>
    Examples:
      | channel         | passenger | fare-type | first-name | last-name | price-status | updated-first-name | updated-last-name |
      | Digital         | 1 adult   | Flexi     | AA         | BBB       | nochange     | AACCC              | nochange          |
      | PublicApiMobile | 1 adult   | Standard  | AACCC      | BBB       | nochange     | nochange           | BBBDDD            |
      | ADAirport       | 1 adult   | Flexi     | EE         | BBDG      | increased    | EEHHHH             | nochange          |
      | Digital         | 1 adult   | Standard  | EEHHHH     | BBDG      | increased    | EEHHJJ             | KKDG              |
