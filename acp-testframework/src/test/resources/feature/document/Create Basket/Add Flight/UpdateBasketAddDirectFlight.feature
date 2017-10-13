@FCPH-168 @FCPH-169
Feature: Update Basket Add Direct Flight - Base Price

  Scenario Outline: Update basket
    Given my basket contains flight with passengerMix "<passenger>" added via "<channel>"
    Then the base price is returned
    And the selected bundle is added to each passenger
  @regression
    Examples: Add direct flight to basket Base Price returned (No Fees nor Taxes)
      | passenger | channel |
      | 1 Adult   | Digital |
    Examples: Hybris adds flight bundle to the basket
      | passenger         | channel |
      | 2 Adults, 1 Child | Digital |

  @local
  Scenario: Select an alternate bundle
    Given my basket contains flight with multiple passengers for a "Flexi" bundle
    Then the selected bundle is added to each passenger

  Scenario: When there is more than 1 Adult, an infant on lap is allocated to the first Adult
    Given my basket contains flight with passengerMix "2 Adults, 1 Infant OL"
    Then the infant is autoallocated to first Adult

  Scenario: When there is 1 Adult, an infant on lap is allocated to the first Adult
    Given my basket contains flight with passengerMix "1 Adult, 1 Infant OL"
    Then the infant is autoallocated to only Adult

  @negative
  Scenario: Default currency is based on the first departure airport for Non-Airport agents
    Given there are multiple valid flights with different departure airports that have different default currencies
    When I add the flights to the basket
    Then the "SVC_100012_20015" error should be returned

  @pending
  @manual
  Scenario: Changing first departure airport changes Default currency for Non-Airport agents
    Given I multiple flights added to basket as a non-airport-agent
    When I change first departure airport to alternative with different currency
    Then the default currency is defined by first departure airport
    And price is converted to new currency

 #cannot automate, no way to currently change airport agent
  @pending
  @manual
  Scenario: Adjusting airport agent changes the basket currency
    Given I have a flight in my basket added via agent channel
    When I change the airport agent to one with a different default currency
    Then the basket price is shown in the new airport agent's currency