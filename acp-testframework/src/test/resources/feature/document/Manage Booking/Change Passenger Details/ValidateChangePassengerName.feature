@TeamD
@Sprint31 @Sprint32
@FCPH-10116
Feature: Add change fee to basket for not the first Name Correction and not Public API B2B

  Scenario Outline: Add Change Name fee if not the "first time" name correction - single flight
    Given one of this channel ADAirport, ADCustomerService, Digital is used
    And I want to book a flight <days> the threshold for name change based on departure
    And I created an amendable basket
    And I changed less than the minimum chargeable characters in the name of a passenger
    And the NameFee <days> threshold is not added to the passenger
    And I have got payment method and proceed to commit the booking
    And I sent the request to createBasket service
    When I change less than the minimum chargeable characters in the name of a passenger
    Then the NameFee <days> threshold is added to the passenger
    Examples:
      | days   |
      | before |
      | after  |
