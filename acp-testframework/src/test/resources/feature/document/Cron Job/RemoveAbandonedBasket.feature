@FCPH-9264
@Sprint28
@manual
Feature: Remove abandoned basket older than given time

  Scenario: Run cron job for baskets older than the time threshold
    Given I login as customer "cus00000002"
    And I create a basket by adding a flight
    And I verify the created basket has the id "idNumber"
    And I force the last modified date of the basket with id "idNumber" to "x+1" days before the current date
    When I run the ejCoreSite-CartRemovalJob cron job
    Then I login as customer "cus00000002"
    And I can't retrieve anymore the basket with id "idNumber"

  Scenario: Run cron job for baskets old as the time threshold
    Given I login as customer "cus00000002"
    And I create a basket by adding a flight
    And I verify the created basket has the id "idNumber"
    And I force the last modified date of the basket with id "idNumber" to "x" days before the current date
    When I run the ejCoreSite-CartRemovalJob cron job
    Then I login as customer "cus00000002"
    And I can't retrieve anymore the basket with id "idNumber"

  Scenario: Run cron job for more baskets recent then the time threshold
    Given I login as customer "cus00000002"
    And I create a basket by adding a flight
    And I verify the created basket has the id "idNumber"
    And I the last modified date of the basket with id "idNumber" is less then <x> days before the current date
    When I run the ejCoreSite-CartRemovalJob cron job
    Then I login as customer "cus00000002"
    And I retrieve succesfully the basket with id "idNumber"
