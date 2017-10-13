
Feature: Manage Booking - Add hold items and excess weights, sports equipments for existing Hold bags

  @Sprint29
  @TeamA
  @FCPH-9917
  Scenario: Error while adding excess weight for the passenger who is not locked
    Given I am using channel ADAirport
    And my basket contains "2 Adult"
    When I add "1" "Hold Bag" for all passengers on flight
    And commit the booking with hold items
    And I request an amendable basket for a passenger
    When I attempt to add 1kg Excess Weight for passenger who is not locked
    Then error SVC_100288_2019 should return while adding hold item

  @Sprint29
  @TeamA
  @FCPH-9935 @FCPH-9917
  Scenario: Add excess weight successfully to a passenger who is locked
    Given I am using channel ADAirport
    And my basket contains "2 Adult"
    When I add "1" "Hold Bag" for all passengers on flight
    And commit the booking with hold items
    And I request an amendable basket for a passenger
    When I attempt to add 1kg Excess Weight for passenger who is locked
    Then I should see hold item Excess weight added successfully

  @Sprint29
  @TeamA
  @FCPH-9915
  Scenario: Error while adding hold bag for the passenger who is not locked
    Given I am using channel ADAirport
    And I create a "COMPLETED" status booking for "2 Adult"
    And I request an amendable basket for a passenger
    When I attempt to add Hold Bag for passenger who is not locked
    Then error SVC_100288_2019 should return while adding hold item

  @Sprint29
  @TeamA
  @FCPH-9916
  Scenario: Error while adding sports equipment for the passenger who is not locked
    Given I am using channel ADAirport
    And I create a "COMPLETED" status booking for "2 Adult"
    And I request an amendable basket for a passenger
    When I attempt to add Large Sporting Equipment for passenger who is not locked
    Then error SVC_100288_2019 should return while adding hold item

  @Sprint29
  @TeamA
  @FCPH-9915
  Scenario: Add hold bag successfully to a passenger who is locked
    Given I am using channel ADAirport
    And I create a "COMPLETED" status return booking for "2 Adult"
    And I request an amendable basket for a passenger
    When I attempt to add Hold Bag for passenger who is locked
    Then I should see hold item Hold Bag added successfully

  @Sprint29
  @TeamA
  @FCPH-9916
  Scenario: Add sports equipment successfully to a passenger who is locked
    Given I am using channel ADAirport
    And I create a "COMPLETED" status return booking for "2 Adult"
    And I request an amendable basket for a passenger
    When I attempt to add Large Sporting Equipment for passenger who is locked
    Then I should see hold item Large Sporting Equipment added successfully

  @Sprint31 @TeamC @FCPH-10553
  Scenario Outline: Hold Item Stock Level Verification and Addition of it in Amendable Basket without any price changes
    Given I am using channel <channel>
    And I have made a booking with passenger <passengerMix> and fare <fareType> without seat
    And I add a product <product> whose price has not changed
    When I commit booking
    Then the stock level should be changed for product <product>
    And I should be able to see booking is successful
    Examples:
      | channel         | product         | passengerMix | fareType |
      | Digital         | hold bag        | 1 adult      | Standard |
      | PublicApiMobile | sport equipment | 1 adult      | Standard |
      | Digital         | excess weight   | 1 adult      | Standard |

  @Sprint31 @TeamC @FCPH-10553 @BR:BR_00075 @backoffice
  Scenario Outline: Inventory exceeds capped threshold for flight
    Given I have made a booking through <channel>
    And created an amendable basket
    When I add a <product> whose availability is less
    Then an error message should be displayed
    Examples:
      | channel         | product         |
      | PublicApiMobile | excess weight   |
      | Digital         | sport equipment |
      | PublicApiMobile | hold item       |

  @Sprint31 @TeamC @FCPH-10553 @backoffice
  Scenario Outline: Price of the product has changed allocate stock
    Given I have made a booking through <channel>
    And created an amendable basket
    When I add a <product> whose price has changed
    Then the stock level should be decremented
    And should perform a booking successfully with <product>
    Examples:
      | channel         | product         |
      | Digital         | sport equipment |
      | PublicApiMobile | excess weight   |
      | Digital         | hold item       |
