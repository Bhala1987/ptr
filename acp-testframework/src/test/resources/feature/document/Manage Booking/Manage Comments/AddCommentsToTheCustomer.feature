@FCPH-9664 @Sprint28
Feature: Add comment against customer profile
  As hybris, I will apply Agent Desktop User Comments and return confirmation to Channel/Downstream systems

  Background:
    Given I create a new valid customer

  @negative
  Scenario Outline: Get added comment BR_01942
    Given <channel> <criteria> configured to add a customer comment
    And I login as agent
    And that a comment has been added to a Customer
    When I request the customer profile
    Then the updated comment <criteria> returned to channel
    @ADTeam
    Examples:
      | channel         | criteria |
      | ADAirport       | is       |
    Examples:
      | channel         | criteria |
      | PublicApiMobile | is not   |
      | PublicApiB2B    | is not   |

  Scenario: Error if the Customer is unable to be identified
    Given the channel ADAirport is used
    And I login as agent
    When I attempt to add a comment to a non-existing customer
    Then I will receive an error with code 'SVC_100542_3001'

  Scenario Outline: Error if the channel is not allowed to add a comment BR_01942
    Given <channel> is not configured to add a customer comment
    And I login as agent
    When I attempt to add a customer comment
    Then I will receive an error with code 'SVC_100542_2001'
    Examples:
      | channel         |
      | PublicApiMobile |
      | PublicApiB2B    |
      | Digital         |


  @regression  @ADTeam
  Scenario Outline: Add the comment on the Customer Profile
    Given <channel> is configured to add a customer comment
    And I login as agent
    When I add a comment to a customer
    And I request the customer profile
    Then I will return Channel, User ID, comment type and created DateTime Stamp for the added comment
    Examples:
      | channel   |
      | ADAirport |
