Feature: Add a new flight to the booking, not public API B2B

  @Sprint28
  @FCPH-9622 @ADTeam
  Scenario Outline: check order items with the new flight to determine if fees are required - AD
    Given <channel> do the commit booking with "2 Adult"
    And I request an amendable basket for a booking
    And I notedown the admin fee in the basket
    And I search a flight for "1 adult" from "LTN" to "CDG"
    When I add the flight to my basket via the <channel>
    Then the admin fee should not change
    Examples:
      | channel           |
      | ADAirport         |
      | ADCustomerService |

  @Sprint28
  @FCPH-9622
  Scenario Outline: check order items with the new flight to determine if fees are required - Digital
    Given <channel> do the commit booking with "2 Adult"
    And I request an amendable basket for a booking
    And I search a flight for "1 adult" from "LTN" to "CDG"
    When I add the flight to my basket via the <channel>
    Then the admin fee should not apply for the new passenger for "LTNCDG"
    Examples:
      | channel |
      | Digital |
