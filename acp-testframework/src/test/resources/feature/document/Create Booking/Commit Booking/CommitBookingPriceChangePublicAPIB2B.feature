@Sprint25
@FCPH-8419
Feature: Flight Price difference as part of commit booking for public API B2B

  Scenario Outline: Flight price difference verification for commit booking
    Given I am using channel <channel>
    When do commit booking with <criteria> via <channel>
    Then the channel will receive a warning with code <errorCode>
    And  I will delete the temp basket
    Examples:
      | channel      | criteria         | errorCode       |
      | PublicApiB2B | price change     | SVC_100022_3013 |
      | PublicApiB2B | multiple flights | SVC_100022_3013 |