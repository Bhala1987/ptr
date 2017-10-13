@TeamD
@Sprint31
Feature: Receive a log in/ log out request for an agent

  @FCPH-10846
  Scenario: Generate error message if the user does not exist for Agent login
    Given one of this channel ADAirport, ADCustomerService is used
    And I have an invalid agent username
    When I send the request to agent login service
    Then the channel will receive an error with code SVC_100046_2001

  @FCPH-10846
  Scenario: Generate error message if the password not correct for the Agent user
    Given one of this channel ADAirport, ADCustomerService is used
    And I have an invalid agent password
    When I send the request to agent login service
    Then the channel will receive an error with code SVC_100046_2001

  @FCPH-10846 @regression
  Scenario: Return successful response for Agent login
    Given one of this channel ADAirport, ADCustomerService is used
    When I send the request to agent login service
    Then the channel will receive the successful response for agent login

  @local
  @Sprint32
  @FCPH-10847
  Scenario: End agent session and de-allocate StandBy fares
    Given one of this channel ADAirport, ADCustomerService is used
    And I send the request to agent login service
    And I created a staff customer
    And I added a flight to the basket for DCS sector with Standby fare
    When I send a request to agent logout service
    Then the basket is removed
    And the flight standby stock level is released

  @Sprint32
  @FCPH-10847
  Scenario: Generate error when the agent is already logged out
    Given one of this channel ADAirport, ADCustomerService is used
    And I logged in as agent
    And I send a request to agent logout service
    When I send a request to agent logout service
    Then the channel will receive an error with code SVC_100348_2004

  @Sprint32
  @FCPH-10847
  Scenario: Generate error when invalid agent user identifier
    Given one of this channel ADAirport, ADCustomerService is used
    And I have an invalid agent username
    When I send a request to agent logout service
    Then the channel will receive an error with code SVC_100348_2005

  @manual
  @Sprint32
  @FCPH-10847
  Scenario: End agent session and de-allocate flight inventory and seating
    Given one of this channel ADAirport, ADCustomerService is used
    And I send the request to agent login service
    And I added a flight to the basket
    And I add a seatProduct to the basket
    When I send a request to agent logout service
    Then the basket is removed
    And I verify the flight and seat has been deallocated properly

  @Sprint32
  @FCPH-10847 @regression
  Scenario: End agent session and de-allocate additional products added
    Given one of this channel ADAirport, ADCustomerService is used
    And I send the request to agent login service
    And I added a flight to the basket with hold bag and with sport items for 1 adult
    When I send a request to agent logout service
    Then the basket is removed
    And hold bags are deallocated
    And sport items are deallocated

  @Sprint32
  @FCPH-10847 @regression
  Scenario: End agent session and update the infant levels
    Given one of this channel ADAirport, ADCustomerService is used
    And I send the request to agent login service
    And I added a flight to the basket for 1 adult; 1 infant
    And infants limits and consumed values are stored for the flight
    When I send a request to agent logout service
    Then the basket is removed
    And the number of infantsOnSeat for the flight will be released

  @FCPH-11061
  Scenario: Return message of the day since the agent last logged in
    Given one of this channel ADAirport, ADCustomerService is used
    When I send the request to agent login service
    Then the message of the days are returned

  @FCPH-11061 @manual
  Scenario: New user logs in
    Given one of this channel ADAirport, ADCustomerService is used
    And the agent has never logged in before
    When I send the request to agent login service
    Then return message of the days since the agent was last logged in
    And the messages are sort by message valid from date

  @FCPH-11061 @manual
  Scenario: Return Message of the day through out the day
    Given one of this channel ADAirport, ADCustomerService is used
    And the agent has logged in previously on the same day
    When I send the request to agent login service
    Then return message of the days since the agent was last logged in
    And I will not return the previous days messages









