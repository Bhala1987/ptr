@Sprint28
@FCPH-9931
Feature: Creation of the clone basket

  @manual
  Scenario: Clone the basket at the booking level
    Given I am using channel Digital
    And I searched a 'Flexi' flight with return for 2 adult; 1 child; 2,1 infant
    And I added it to the basket with Flexi fare as outbound/inbound journey
    When I add "maximum" "Hold Bag" for all passengers on flight
    And I add "maximum" "Large Sporting Equipment" for all passengers on flight
    And commit the booking with hold items
    When I request an amendable basket for a booking
    Then I should have the correct associations
    And the status is "same"

  @manual
  Scenario: Clone the basket at the passenger level
    Given I am using channel Digital
    And I searched a 'Flexi' flight with return for 1 adult
    And I added it to the basket with Standard fare as outbound/inbound journey
    When I add "maximum" "Hold Bag" for all passengers on flight
    And I add "maximum" "Large Sporting Equipment" for all passengers on flight
    And commit the booking with hold items
    When I request an amendable basket for a passenger
    Then I should have the correct associations
    And the status is "same"

  @manual
  Scenario: Multiple clones for the same booking
    Given I am using channel Digital
    And I am updating my passenger document details for "2 Adult, 1 Infant OL"
    When I process the request for updatePassengers with documents
    And I do the commit booking
    Then the booking is amendable at passenger level for Adult1 as basket1
    And out of the same booking we should be able to clone for adult2
    And out of the same booking we should be able to clone the whole booking
    And I request the amendable basket
    Then I should have the correct associations
    And the status is "same"

  @manual
  Scenario: Clone the basket at the passenger level
    Given I am using channel Digital
    And I searched a 'Flexi' flight with return for 2 adult; 1 child; 2,1 infant
    And I added it to the basket with Flexi fare as outbound/inbound journey
    When I add "maximum" "Hold Bag" for all passengers on flight
    And I add "maximum" "Large Sporting Equipment" for all passengers on flight
    And commit the booking with hold items
    When I request an amendable basket for a passenger
    Then I should have the correct associations
    And the status is "same"

  @manual
  Scenario: Verify SSRs are cloned
    When I add an SSR for a valid sector
    And I do the commit booking
    And the booking is amendable
    Then I should have the correct associations
    And the status is "same"

  @manual
  Scenario: Verify API details are cloned
     Given I am using channel Digital
     And I am updating my passenger document details for "1 Adult, 1 Child, 1 Infant OL"
     When I process the request for updatePassengers with documents
    And I do the commit booking
    And the booking is amendable
    Then I should have the correct associations
    And the status is "same"