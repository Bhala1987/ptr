@FCPH-3460
@Sprint28
Feature:  Save Abandon basket to customer profile digital channel

  Background:
    Given I am using channel Digital

  Scenario: 2. Delete the abandon basket when the customer already has another basket
    Given I created a basket as a logged in user
      | journey      | single  |
      | origin       | ALC     |
      | destination  | LTN     |
      | passengerMix | 1 child |
    And I logout
    And I created a basket as an anonymous user
      | journey      | single  |
      | origin       | LTN     |
      | destination  | ALC     |
      | passengerMix | 1 Adult |
    When I login as same user as before
    Then old basket is deleted
    And the new basket is rendered

#  Reason for this scenario has to be manual.
#  1. We can't wait in the automation until the flight is departed.
#  2. Thought can workout by updating the DB for the flight departure date to past, but because of the caching issues
#  even if we update directly on DB it won't take affect until we clear the cache manually
  @manual
  Scenario: 4. Remove single flight if the flight is already departed.
    Given as a logged in user I added a outbound flight to the basket
    And I logout
    And I wait until the flight to depart
    When I login as same user as before
    Then the flight is removed from the basket

#  Reason for this scenario has to be manual.
#  1. We can't wait in the automation until the flight is departed.
#  2. Thought can workout by updating the DB for the flight departure date to past, but because of the caching issues
#  even if we update directly on DB it won't take affect until we clear the cache manually
  @manual
  Scenario: 5. Remove whole journey if the outbound flight is already departed.
    Given as a logged in user I added a return flight to the basket
    And I logout
    And I wait until the outbound flight to depart
    When I login as same user as before
    Then the whole journey should be removed from the basket

  @local
  Scenario: 6. Remove single flight if the flight no longer has the inventory.
    Given I created a basket as a logged in user
      | journey      | single  |
      | origin       | LTN     |
      | destination  | ALC     |
      | passengerMix | 1 Adult |
    And I logout
    When I login back while flight inventory unavailable
    Then the flight is removed

  @local
  Scenario: 7. Remove whole journey if the outbound flight no longer has the inventory.
    Given I created a basket as a logged in user
      | journey      | return  |
      | origin       | LTN     |
      | destination  | ALC     |
      | passengerMix | 1 Adult |
    And I logout
    When I login back while flight inventory unavailable
    Then the whole journey is removed

  @local
  Scenario: 9. Update price if the flights base prices changes.
    Given I created a basket as a logged in user
      | journey      | single  |
      | origin       | LTN     |
      | destination  | ALC     |
      | passengerMix | 1 Adult |
    And I logout
    When I login back while flight price has changed
    Then the basket should update with the new price


#  Reason for this scenario has to be manual.
#  1. At the moment all the flights in Hybris has the configuration of 500,000 stock for hold bags,
#  so it is a never ending process to consume all the 500,000 hold bag inventory.
#  hence this has to be manual.
  @local @manual
  Scenario: 10. Remove hold bag if inventory is no longer available.
    Given I created a basket as a logged in user
      | journey      | single  |
      | origin       | LTN     |
      | destination  | ALC     |
      | passengerMix | 1 Adult |
    And I added a hold bag
    And I logout
    And all the hold bag inventory sold out for the same flight
    When I login as same user as before
    Then the hold bag should be removed from the basket

  @local
  Scenario: 11. Remove seat if seat inventory is not available - Not part of bundle
    And I am logged in as a standard customer
    And travelling from LTN to ALC
    And I want to proceed with add purchased seat STANDARD
    And my basket contains flight with passengerMix "1 Adult" added via "Digital"
    When I make a request to add an available "STANDARD" seat product
    Then the seat product is added to the basket
    And I logout
    When I login back while seat inventory unavailable
    Then the seat is removed

  @local
  Scenario: 12. Update price if the seat base prices changes - Not part of bundle
    And I am logged in as a standard customer
    And travelling from LTN to ALC
    And I want to proceed with add purchased seat STANDARD
    And my basket contains flight with passengerMix "1 Adult" added via "Digital"
    When I make a request to add an available "STANDARD" seat product
    And I note the seat price
    And I logout
    When I login back while seat price has changed
    Then the seat price is changed

  @local
  Scenario: 13. Update price if the seat base prices changes - Part of bundle
    And I am logged in as a standard customer
    And travelling from LTN to ALC
    And my basket contains "1" flights for "1" passengers with "Flexi" fare added via the "Digital" channel
    When I make a request to add an available "EXTRA_LEGROOM" seat product for each passenger
    And I note the seat price
    And I logout
    When I login back while seat price has changed
    Then the seat price is changed

  @local
  Scenario: 14. Remove seat if seat inventory is not available - Part of bundle
    And I am logged in as a standard customer
    And travelling from LTN to ALC
    And my basket contains "1" flights for "1" passengers with "Flexi" fare added via the "Digital" channel
    When I make a request to add an available "EXTRA_LEGROOM" seat product for each passenger
    And I logout
    When I login back while seat inventory unavailable
    Then the seat is removed


