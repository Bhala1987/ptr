@backoffice:FCPH-8926
@Sprint25
Feature: Manage Global Stock Level

  Scenario: 1 Fields to create a stock level
    Given that I'm in the back office
    When I select to create a global stock level
    Then I must enter Product code, Available Amount, Oversell Amount

  Scenario: 2 - record creation date and time of stock level
    Given that I have created a stock level for a product
    When I select to save
    Then the creation date, time and user ID will be stored
    And the modification date, time and user ID will be stored

  Scenario: 3 - Modification of stock levels
    Given that I have created a stock level for a product
    When I select to edit
    Then I should be able to edit the available amount, oversell amount

  Scenario: 5 - Inherit stock levels to the flight
    Given that I have received data for a flight schedule entry
    When I create the new flight schedule entity
    Then I will inhert the global stock levels onto the flight schedule entry for each stock level created

  Scenario: 6 - Stock levels are not updated on flights if updated on global stock level
    Given that I have created a stock level for a product
    And the stock level has been inherited
    When a stock level is modified for a product
    Then I will not update any existing stock levels on flights

  Scenario: 7 - Stock levels are manually updated for a specific flight
    Given that I have created a global stock level for a product
    And the stock level has been inherited
    When a stock level is modified for a product of a specific flight
    Then I will update the stock level for that flight
    And not change it again if the global stock levels are modified again