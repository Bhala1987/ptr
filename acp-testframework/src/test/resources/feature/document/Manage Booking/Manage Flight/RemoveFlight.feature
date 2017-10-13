Feature: Remove Flight from Amendable Basket

  @Sprint28 @FCPH-9623 @manual @BR:BR_01099
  Scenario: Passenger is in 'Boarded' status
    Given the passenger has Boarded status after committed a booking
    When I send a remove flight request
    Then I receive an error message Cannot remove the flight because at least one passenger is in boarded status