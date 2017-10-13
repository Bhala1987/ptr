Feature: Change purchased seat

  @Sprint32 @TeamC @FCPH-11967
  Scenario Outline: Add and change the purchased seat to the eJPlus passenger for the verification of seat price
    Given I am using the channel <channel>
    And I purchased a seat <seatProduct> for ejPlus membership <ejPlusCardNumber> passenger 1 adult for <fareType> fare
    And I should see seat number against the passenger
    And the price of the seat should be correct
    And the seat entry status should be CHANGED
    And the seat active flag should be TRUE
    When I change the purchased seat <newSeatProduct> to the passenger
    Then I should see seat number against the passenger
    And the price of the seat should be correct
    And the seat entry status should be CHANGED
    And the seat active flag should be TRUE
    Examples:
      | channel           | fareType | ejPlusCardNumber | seatProduct   | newSeatProduct |
      | PublicApiMobile   | Standard | customer         | EXTRA_LEGROOM | UPFRONT        |
      | ADCustomerService | Flexi    | staff            | UPFRONT       | STANDARD       |