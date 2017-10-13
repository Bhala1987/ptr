Feature: Retrieve Flights and Offer Prices

  @Sprint28
  @FCPH-3360
  Scenario Outline: Calculate the new flight offer price with corporate credentials
    Given one of this channel ADAirport, ADCustomerService is used
    And I created an amendable basket
    And I want to search a flight to change an existing one using <deal> application id and <deal> office id and <deal> corporate id deal information
    When I sent the request to getFlights service
    Then list of available flight for change is returned <exist> deal
    Examples:
      | deal    | exist   |
      | valid   | with    |
      | invalid | without |