@Sprint29
@FCPH-9935
@TeamA
Feature: Get Amendable Basket

  Scenario: Get Amendable Basket at basket level
    Given I am using channel Digital
    And I create a "COMPLETED" status return booking for "1 Adult, 1 Child, 1 Infant OL"
    And I request an amendable basket for a booking
    When I get the amendable basket
    Then I should see all the passenger details
    And line item prices will remain unchanged

  Scenario: Get Amendable Basket at passenger level when there is an infant on lap
    Given I am using channel Digital
    And I create a "COMPLETED" status return booking for "1 Adult, 1 Child, 1 Infant OL"
    And I request an amendable basket for a passenger
    When I get the amendable basket
    Then I should see that specific passenger details and their associates
    And line item prices will remain unchanged

  Scenario: Get Amendable Basket at passenger level
    Given I am using channel Digital
    And I searched a 'Flexi' flight with return for 2 adult
    And I added it to the basket with Flexi fare as outbound/inbound journey
    When I add "maximum" "Hold Bag" for all passengers on flight
    And I add "maximum" "Large Sporting Equipment" for all passengers on flight
    And commit the booking with hold items
    And I request an amendable basket for a passenger
    When I get the amendable basket
    Then I should see that specific passenger details and their associates
    And line item prices will remain unchanged
