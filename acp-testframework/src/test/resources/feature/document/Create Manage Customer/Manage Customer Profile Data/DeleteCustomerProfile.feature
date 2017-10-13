@FCPH-3349
Feature: Receive request to delete customer profile

  Scenario Outline: Receive invalid Customer Profile delete request
    Given The customer ID not related to a registered customer
    When I send a request to the delete customer profile with "<channel>"
    Then I will return a error message in the channel "<code>"
    Examples:
      | channel           | code            |
      | ADAirport         | SVC_100339_3001 |
      | ADCustomerService | SVC_100339_3001 |

  Scenario Outline: Return error message if request comes in from Digital BR_00861 or Public API B2B/mobile BR_00861
    Given I create a customer
    When I send a request to the delete customer profile with "<channel>"
    Then I will return a error message in the channel "<code>"
    Examples:
      | channel         | code            |
      | Digital      | SVC_100000_2068 |
#      | PublicApiMobile | SVC_100012_2068 |
      | PublicApiB2B | SVC_100000_2068 |

    @manual
  Scenario Outline: Receive valid Customer Profile delete request
    Given I create a customer
#    And I did the login with an Agent "<channel>"
    And I sent a request to SearchFlights "<channel>"
    And I sent a request to AddFlights "<channel>"
    And I sent a request to Commit Booking "<channel>"
    When I send a request to the delete customer profile with "<channel>"
    Then I will delete the Customer Profile APIS details, SSR details, Saved Payments, Saved Passengers, associated with the Profile
    And change the Customer profile status to Deleted
    And The booking is still available "<channel>"
    Examples:
      | channel           |
      | ADAirport         |
      | ADCustomerService |
