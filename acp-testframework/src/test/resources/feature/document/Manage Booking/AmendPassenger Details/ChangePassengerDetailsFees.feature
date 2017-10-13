@Sprint27 @Sprint28
@FCPH-9241
Feature: Add change fee to basket for a Change Passenger Name and not Public API

  @BR:BR_01180
  Scenario: Change Passenger Title with a change fee, fees should not be added
    Given one of this channel ADAirport, ADCustomerService, Digital is used
    And I created an amendable basket
    When I change the title for a passenger
    Then the TitleFee is added to the passenger

  @BR:BR_01220,BR_01190
  Scenario Outline: Verify fees application for different number of characters and different days from departure
    Given one of this channel ADAirport, ADCustomerService, Digital is used
    And I want to book a flight <days> the threshold for name change based on departure
    And I created an amendable basket
    When I change <characters> than the minimum chargeable characters in the name of a passenger
    Then the NameFee <days> threshold is <chargeable> to the passenger
    Examples:
      | days   | characters | chargeable |
      | before | less       | not added  |
      | before | more       | added      |
      | after  | less       | not added  |
      | after  | more       | added      |

  Scenario Outline: Verify fees are added when multiple change reach threshold
    Given one of this channel ADAirport, ADCustomerService, Digital is used
    And I want to book a flight <days> the threshold for name change based on departure
    And I created an amendable basket
    And I changed less than the minimum chargeable characters in the name of a passenger
    And the NameFee <days> threshold is not added to the passenger
    When I change less than the minimum chargeable characters in the name of a passenger
    Then the NameFee <days> threshold is added to the passenger
    Examples:
      | days   |
      | before |
      | after  |

  @manual
  Scenario Outline: Verify fees application for different number of characters and different days from departure - multiple flights
    Given one of this channel ADAirport, ADCustomerService, Digital is used
    And I want to book a flight <days> the threshold for name change based on departure
    And I created an amendable basket with multiple flights
    When I change <characters> than the minimum chargeable characters in the name of a passenger
    Then the NameFee <days> threshold is <chargeable> to the passenger only for future flights
    Examples:
      | days   | characters | chargeable |
      | before | less       | not added  |
      | before | more       | added      |
      | after  | less       | not added  |
      | after  | more       | added      |