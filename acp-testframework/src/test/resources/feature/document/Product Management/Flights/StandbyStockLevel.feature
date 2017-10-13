Feature: Manage Standby Stock Level

  @TeamD
  @Sprint30
  @backoffice:FCPH-10718
  Scenario: Fields to create a stock level
    Given that I'm on the global stock level folder
    When I select to create a global stock level
    Then I must select a code

  @TeamD
  @Sprint30
  @backoffice:FCPH-10718
  Scenario: Update global stock level
    Given that I'm on the global stock level folder
    When I select to update a global stock level
    Then I can update global stock level

  @TeamD
  @Sprint30
  @backoffice:FCPH-10718
  Scenario: Record creation date and time of stock level
    Given that I'm on the global stock level folder
    When I create global stock level
    Then the creation date, time and user ID will be stored

  @TeamD
  @Sprint30
  @backoffice:FCPH-10718
  Scenario: Record creation date and time of stock level
    Given that I'm on the global stock level folder
    When I update global stock level
    Then the modification date, time and user ID will be stored

  @manual
  @TeamD
  @Sprint30
  @FCPH-10718
  Scenario:  Inherit stock levels to the flight
    Given a file with flight schedule entry is provided
    When I upload the file
    Then the flight schedule entity will be created
    And the global stock levels is inherited at flight level

  @manual
  @TeamD
  @Sprint30
  @FCPH-10718
  Scenario: Stock levels are not updated on flights if updated on global stock leve
    Given that I have created a stock level for a product
    And the stock level has been inherited from at least one flight
    When the stock level is modified for a product
    Then any existing stock levels on flights will not be updated

  @manual
  @TeamD
  @Sprint30
  @FCPH-10718
  Scenario:  Stock levels are manually updated for a specific flight
    Given that I have created a stock level for a product
    And the stock level has been inherited from at least one flight
    When the stock level is modified for a product of a specific flight
    Then the stock level for that flight will be updated
    And the global stock levels will not be modified
