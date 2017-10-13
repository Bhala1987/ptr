Feature: Add Group bundle to basket

  @TeamD
  @Sprint31
  @FCPH-10769
  Scenario: Add group bundle to basket and verify admin fee and group booking fee
    Given one of this channel ADAirport, ADCustomerService is used
    When I added a flight to the basket with hold bag and with sport items for 1 adult; 1 child; 2,1 infant as group booking
    Then I verify the admin fee is not added
    And I verify the group booking internet discount is applied
    And I verify the group booking fee is applied
