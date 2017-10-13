@FCPH-422 @FCPH-438 @FCPH-7465
Feature: Basket is cleared on request

  @regression
  Scenario Outline: Flights are de-allocated when a basket is cleared
    Given I have a basket with a valid flight with 1 adult added via <channel>
    When I clear the basket via "<channel>"
    And the basket is emptied
#    Then the flights are de-allocated via "<channel>"
    Examples:
      | channel   |
      | ADAirport |

  @FCPH-6999 @regression
  Scenario Outline: Flights are de-allocated when a basket with multiple flights is cleared
    Given I have a basket with valid multiple flights added via "<channel>"
    When I clear the basket via "<channel>"
    Then the basket is emptied
#    And all the flights are de-allocated via "<channel>"
    Examples:
      | channel           |
      | ADCustomerService |

  Scenario Outline: Flights are not de-allocated via some channels
    Given I have a basket with a valid flight with 1 adult added via <channel>
    When I clear the basket via "<channel>"
    Then the flights are not further de-allocated for <channel>
    Examples:
      | channel      |
      | Digital      |
      | PublicApiB2B |

  @FCPH-11032 @TeamE @Sprint30
  Scenario Outline: Deallocate all inventory when empty basket is called
    Given I have a basket with two valid flight with one adult added via <channel>
    And I add a "STANDARD" seat for each passenger
    When I clear the basket via "<channel>"
    Then the basket is emptied
    Examples:
      | channel      |
      | ADAirport    |
      | Digital      |

