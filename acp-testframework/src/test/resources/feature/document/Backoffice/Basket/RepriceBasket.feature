
Feature: Request to Reprice the basket Digital and AD

  @backoffice:FCPH-425
  Scenario: 5 - Check price of the hold item in the basket
    Given I have added a flight to the basket with hold item
    And I change the price of the 'hold bag' in backoffice
    When I initiate the recalculatePrices service
    Then I will check the 'hold item' price change

  @backoffice:FCPH-425
  Scenario: 6 - Check price of the excess weight item in the basket
    Given I have added a flight to the basket with hold item
    And I added an excess weight item to the hold item
    And I change the price of the 'excess weight' in backoffice
    When I initiate the recalculatePrices service
    Then I will check the 'excess weight' price change

  @backoffice:FCPH-425
  Scenario: 7 - Check price of the sports equipment in the basket
    Given I have added a flight to the basket with sports equipment
    And I change the price of the 'sports equipment' in backoffice
    When I initiate the recalculatePrices service
    Then I will check the 'sports equipment' price change

  @backoffice:FCPH-425
  Scenario: 8 - Check price of the Fees and Taxes in the basket
    Given I have added a flight to the basket
    And I change the fees and taxes in backoffice
    When I initiate the recalculatePrices service
    Then I will check the 'fees and taxes' price change