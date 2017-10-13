Feature: ACP Add / Remove Infant

  @Sprint27
  @FCPH-8724
  Scenario: Remove an Infant on Lap from booking
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket for 1 adult; 1,0 infant
    And I want to remove an infant on lap
    When I send the request to removeInfantOnLap service
    Then the infant is removed from the cart

  @Sprint28
  @FCPH-9171
  Scenario Outline: Remove an Infant on Lap from booking
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I created an amendable basket for 3 adult; 3,1 infant
    And I want to remove an infant on lap
    But request to remove infants contains <invalidField>
    When I send the request to removeInfantOnLap service
    Then the channel will receive an error with code <error>
    Examples:
      | invalidField       | error           |
      | InvalidBasketId    | SVC_100522_2001 |
      | InvalidPassengerId | SVC_100522_2002 |
      | InvalidInfantId    | SVC_100522_2003 |
      | emptyInfantId      | SVC_100522_2004 |
      | InfantOnSeat       | SVC_100522_2005 |
      | InvalidInfantMap   | SVC_100522_2006 |