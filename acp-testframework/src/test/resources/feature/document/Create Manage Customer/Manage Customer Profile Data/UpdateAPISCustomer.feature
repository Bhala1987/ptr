@TeamC @Sprint29 @FCPH-8444
Feature: Update customer profile based on APIS being amended on a booking

  Scenario Outline: Create saved APIS against customer profile when APIS not retrieved
    Given I am using <channel> channel
    When I send a valid request to set new apis to customer profile
    Then new apis document should <should> be created against the customer
    Examples:
      | channel | should |
      | Digital | true   |

  Scenario Outline: Create saved APIS against the customer profile
    Given I am using <channel> channel
    And the travel document has been retrieved for a customer profile
    When I send a valid request to amend apis to customer profile <with> modification
    Then new apis document should <should> be created against the customer
  @regression
    Examples: APIS is retrieved and has been changed BR_00123
      | channel         | with | should |
      | PublicApiMobile | true | true   |
    Examples: APIS is retrieved and has not been changed
      | channel | with  | should |
      | Digital | false | false  |