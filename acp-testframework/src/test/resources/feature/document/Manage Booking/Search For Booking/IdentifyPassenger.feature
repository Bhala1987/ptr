@Sprint32
@TeamD
@FCPH-11150
Feature: Identify Passenger from the booking

  Scenario: Generate error message if the passenger is not known on the booking
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I have committed a booking for 2 adult
    And I want to search by passenger surname which is not exist in the booking
    When I send the identifyPassenger request
    Then the channel will receive an error with code SVC_100046_2008

  Scenario: Return a successful response when one passenger matches
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I have committed a booking for 2 adult
    And I want to search by passenger surname which is exist in the booking
    When I send the identifyPassenger request
    Then I will get the authentication details in the response

  Scenario: Return a list passenger name which match the requested passenger surname
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I added a flight to the basket for 2 adult
    And I updated the passenger information with same surname
    And I send the request to commitBooking service
    And the booking is completed
    And I want to search by passenger surname which is matching more than one passenger in the booking
    When I send the identifyPassenger request
    Then I will get all the passenger details matching the surname in the response

  Scenario: Return a successful response when search by passenger id
    Given one of this channel ADAirport, ADCustomerService, Digital, PublicApiMobile is used
    And I have committed a booking for 2 adult
    And I want to search by passenger id which is exist in the booking
    When I send the identifyPassenger request
    Then I will get the authentication details in the response


