Feature: Validate Change Flight Request with purchased seats

    # We do not control flights inventory
  @manual
  @TeamD
  @Sprint29
  @FCPH-9842
  Scenario: Add new flight to basket, no seat inventory, seat part of the faretype bundle, same seat and same seat band
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And the purchased seat is part of the faretype bundle
    And I created an amendable basket for 1 adult
    And I want to change a flight with another one
    But there is no seat inventory on that flight
    When I send the changeFlight request
    And the channel will receive an error with code SVC_100700_5000

  @manual
  @TeamD
  @Sprint29
  @FCPH-9842
  Scenario: Add new flight to basket, no seat inventory, seat not part of the faretype bundle, same seat and same seat band
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And the purchased seat is not part of the faretype bundle
    And I created an amendable basket for 1 adult
    And I want to change a flight with another one
    But there is no seat inventory on that flight
    When I send the changeFlight request
    And the channel will receive an error with code SVC_100238_3001

  @manual
  @TeamD
  @Sprint29
  @FCPH-9842
  Scenario: Add new flight to basket, same seat , new price <= old price, same seat and same seat band
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket for 1 adult
    And I want to change a flight with another one
    And the same seat is available in the new flight
    When I calculate the seat offer price
    And the new seat offer price is less than or equal to the old seat offer price
    Then I send the changeFlight request
    And I use the old seat price as the purchased seat price

  @manual
  @TeamD
  @Sprint29
  @FCPH-9842
  Scenario: Add new flight to basket, same seat , new price > old price, same seat and seat band
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket for 1 adult
    And I want to change a flight with another one
    And the same seat is available in the new flight
    When I calculate the seat offer price
    And the new seat offer price is greater than the old seat offer price
    Then I send the changeFlight request
    And I use the new seat price as the purchased seat price


