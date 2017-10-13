Feature: Request release hold or sports equipment Inventory when products have changed

  @Sprint31 @TeamC @FCPH-10087
  Scenario Outline: Generate deallocate request while cancelling booking for hold item inventory
    Given I am using channel <channel>
    When I have receive a valid cancelBooking request containing the <holdItem>
    Then I check the stock level for the flight for the number of requested <holdItem> has been released.
    Examples:
      | holdItem        | channel   |
      | hold bag        | ADAirport |
      | sport equipment | Digital   |

  @Sprint31 @TeamC @FCPH-10087
  Scenario Outline: Generate deallocate request when a flight has been changed
    Given I am using channel <channel>
    And I have an amendable basket with a flight having <holdItem>
    When I change the flight with <holdItem> with a new flight
    Then I verify the stock level for the added <holdItem> has been released
    Examples:
      | holdItem        | channel   |
      | hold bag        | ADAirport |
      | sport equipment | Digital   |




