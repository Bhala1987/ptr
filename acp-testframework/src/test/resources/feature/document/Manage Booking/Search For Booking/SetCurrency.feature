@Sprint28
@FCPH-9474
Feature: Set Currency for Basket Created to Manage a Booking- not Public API

  Scenario Outline:  Basket Currency is based on Channel BR_01980
    Given <channel> do the commit booking with "1 Adult"
    And I note down the booking currency
    When I request an amendable basket during manage booking
    Then the currency in the amendable basket should be same as booking currency
    Examples:
      | channel           |
      | Digital           |
      | ADCustomerService |
      | ADAirport         |