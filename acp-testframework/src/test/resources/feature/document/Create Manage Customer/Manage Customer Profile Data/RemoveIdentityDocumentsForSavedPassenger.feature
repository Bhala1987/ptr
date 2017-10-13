@FCPH-8380
@Sprint24
Feature: Receive request to remove APIS for Saved Passenger

  Scenario Outline: Remove APIS request received for Customer Profile
    Given am using Channel "<Channel>" for remove all identity documents of the saved passenger
    When I receive the updateSavedPassengers request to remove all identity documents of the saved passenger with invalid "<Parameter>"
    Then I will return the "<Error Code>" for removal of all identity documents of the saved passenger
    Examples:
      | Channel           | Parameter   | Error Code      |
      | Digital           | CustomerID  | SVC_100337_2008 |
#      | Digital           | PassengerID | SVC_100337_2009 |
#      | ADAirport         | CustomerID  | SVC_100337_2008 |
      | ADAirport         | PassengerID | SVC_100337_2009 |
      | ADCustomerService | CustomerID  | SVC_100337_2008 |
#      | ADCustomerService | PassengerID | SVC_100337_2009 |
#      | PublicApiB2B      | CustomerID  | SVC_100337_2008 |
      | PublicApiB2B      | PassengerID | SVC_100337_2009 |
      | PublicApiMobile   | CustomerID  | SVC_100337_2008 |
#      | PublicApiMobile   | PassengerID | SVC_100337_2009 |

  Scenario Outline: Remove APIS for a Saved Passenger
    Given am using Channel "<Channel>" for remove all identity documents of the saved passenger
    And I have 1 identity document added to the saved passenger
    When I receive the updateSavedPassengers request to remove all identity documents of the saved passenger
    Then I return confirmation on completion of removal of all identity documents of the saved passenger
    Examples:
      | Channel           |
      | Digital           |
      | ADAirport         |
      | ADCustomerService |
      | PublicApiB2B      |
      | PublicApiMobile   |

  Scenario Outline: Remove APIS for a Saved Passenger - This is to remove all identity documents
    Given am using Channel "<Channel>" for remove all identity documents of the saved passenger
    And I have <numberOfDocs> identity documents added to the saved passenger
    When I receive the updateSavedPassengers request to remove all identity documents of the saved passenger
    Then I return confirmation on completion of removal of all identity documents of the saved passenger
  @regression
    Examples:
      | Channel | numberOfDocs |
      | Digital | 2            |
    Examples:
      | Channel           | numberOfDocs |
      | ADAirport         | 3            |
      | ADCustomerService | 4            |
      | PublicApiB2B      | 5            |
      | PublicApiMobile   | 6            |
