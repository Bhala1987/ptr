Feature: Fulfill Request Payment

  @TeamD
  @Sprint31
  @FCPH-11033
  Scenario: Request allocation with old base price when there has been a currency conversion
    Given one of this channel Digital, PublicApiMobile is used
    And I added a flight to the basket for 1 adult
    And I changed the currency
    When I proceed to commit the booking
    Then the booking is completed
    And the original currency is stored against the booking

  @local
  @TeamD
  @Sprint31
  @FCPH-11033
  Scenario: New base price returned from abstraction layer Digital/public API mobile
    Given one of this channel Digital, PublicApiMobile is used
    And I added a flight to the basket for 1 adult
    And I changed the currency
    But I set transaction id to F11033000002 in order to trigger a flight price change
    When I proceed to commit the booking
    Then the channel will receive an error with code SVC_100022_3012
    And the affected data of the error SVC_100022_3012 contains numberAllocated = 0
    And the basket is updated with the new price

  @local
  @TeamD
  @Sprint31
  @FCPH-11033
  Scenario: New base price returned including allocation of inventory from abstraction layer for Digital Pubic API mobile
    Given one of this channel Digital, PublicApiMobile is used
    And I added a flight to the basket for 1 adult
    And I changed the currency
    But I set transaction id to F11033000003 in order to trigger a flight price change after allocation
    When I proceed to commit the booking
    Then the channel will receive an error with code SVC_100022_3012
    And the affected data of the error SVC_100022_3012 does not contains numberAllocated
    And the basket is updated with the new price