@FCPH-8380
Feature:Delete Saved Passenger APIS information

  Background:
    Given I have a customer with a saved passenger that has APIS

  Scenario: 1 – Remove APIS request received for Customer Profile
    Given the channel has initiated a updateSaved Passenger request
    When I receive the request
    Then I will check whether the request is in the format as defined in the service contract
    And will return an invalid request.

  Scenario: 2 - Remove APIS for a Saved Passenger
    Given that I have received a valid update Passenger request
    When I remove the APIS information of the saved Passenger in Customer profile
    Then I return confirmation on completion