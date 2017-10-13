@Sprint32
Feature: Return Fees and Taxes to the channel

  
  @TeamE
  @FCPH-9247
  Scenario Outline: Generate error message mandatory query are not passed
    Given I am using the channel Digital
    And the channel has initated a getfeesandTaxes for invalid <taxCode>
    When the fee/tax can not be identified
    Then  the system will generate an <errorCode>
    Examples:
      | taxCode   | errorCode         |
      | AAAAAA    | SVC_100133_2001   |


  @TeamE
  @FCPH-9247
  Scenario: Return a full list of fees/taxes
    Given I am using the channel Digital
    And that the channel has initated a getfeesandTaxes
    When the system receives the request for all applicable fees and taxes reference data
    Then it will return all the applicable fees/taxes reference data


  @TeamE
  @FCPH-9247
  Scenario Outline: Filter the fees/taxes based on the query parameters
    Given I am using the channel Digital
    When the system receives the request for applicable <taxCode>
    And with channel <channel>
    And with passenger type <passengerType>
    And with sector <sectorCode>
    And with currency <currencyCode>
    And that the channel has initated a getfeesandTaxes for taxCode
    Then it will return all the applicable fees/taxes reference data for these requested parameters
    Examples:
      | channel   | passengerType  | sectorCode   | currencyCode  | taxCode   |
      | all       | adult          | LTNALC       | CHF           | UKAPDA    |


  @TeamE
  @FCPH-9247
  Scenario Outline: Return prices for a specific fee/tax in a specific currency
    Given I am using the channel Digital
    And with currency <currencyCode>
    When the channel has initiated a getfeesandTaxes for currencyCode
    Then it will return the price for the fee/taxes in that currency
    Examples:
      | currencyCode  |
      | CHF           |


  @TeamE
  @FCPH-9247
  Scenario: Calculate the price of the fee/tax based on the conversion rate
    Given I am using the channel Digital
    And there are not fees or taxes with specific currency
    When request contains pricing context applied to a given fee/tax for a specific currency
    Then the system will calculate the price based on the exchange rate for that currency
