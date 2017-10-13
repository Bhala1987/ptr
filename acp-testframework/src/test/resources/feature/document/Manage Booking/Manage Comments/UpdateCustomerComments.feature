@FCPH-9646 @Sprint28
Feature: Update comment against customer profile
  As hybris, I will update Agent Desktop User Comments and return confirmation to Channel/Downstream systems

  Background:
    Given I create a new valid customer

  Scenario: Generate an error message if the Customer is unable to be identified
    Given the channel ADAirport is used
    And I login as agent
    When I attempt to update a comment to a non-existing customer
    Then I will receive an error with code 'SVC_100542_3001'

  Scenario: Generate a error message if the comment ID is not able to be identified
    Given the channel ADAirport is used
    And I login as agent
    When I attempt to update a comment with a non-existing commentID
    Then I will receive an error with code 'SVC_100542_2004'

  Scenario: Generate error message if the comment ID is not associated to the requested Customer id
    Given the channel ADAirport is used
    And I login as agent
    When I attempt to update a comment with a commentID not matching the customerID
    Then I will receive an error with code 'SVC_100542_2004'

  Scenario Outline: Generate a error message if the channel is not allowed to update a comment BR_01940
    Given <channel> is not configured to update a customer comment
    And I login as agent
    When I attempt to update a customer comment
    Then I will receive an error with code 'SVC_100542_2002'
    Examples:
      | channel         |
      | PublicApiMobile |
      | PublicApiB2B    |
      | Digital         |

  Scenario Outline: Update the comment on the Customer
    Given <channel> is configured to update a customer comment
    And I login as agent
    When I update a customer comment
    And I request the customer profile
    Then I will return Channel, User ID, comment type and created DateTime Stamp for the updated comment
    Examples:
      | channel           |
      | ADAirport         |
      | ADCustomerService |

  @regression
  @FCPH-9664
  @BR:BR_01940
  Scenario Outline: Add and update comment in get Customerprofile
    Given I login as agent
    And I have updated a customer comment
    When <channel> <criteria> configured to update a customer comment
    And I login as newly created customer
    And I request the customer profile
    Then the updated comment <criteria> returned to channel
    Examples:
      | channel         | criteria |
      | ADAirport       | is       |
      | PublicApiB2B    | is not   |
      | PublicApiMobile | is not   |
