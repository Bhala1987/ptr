Feature: Create a new saved passenger or update an existing saved passenger in Manage Booking

  @Sprint31 @TeamC @FCPH-11211
  Scenario Outline: Manage update saved passenger in commit booking flow
    Given I am using Digital channel
    When I request to manage passenger with flag <saveToCustomerProfile> and saved passenger code <updateSavedPassengerCode>
    Then I see the the passenger details against the customer profile
    Examples:
      | saveToCustomerProfile | updateSavedPassengerCode |
      | true                  | false                    |
      | true                  | true                     |

  @Sprint31 @TeamC @FCPH-11211 @FCPH-10088
  Scenario Outline: Manage update saved passenger on amendable basket
    Given I am using Digital channel
    When I send a request to update basic details with flag <saveToCustomerProfile> and saved passenger code <savedPassengerCode>
    And I commit again the booking
    Then I see the the passenger details against the customer profile
    Examples:
      | saveToCustomerProfile | savedPassengerCode |
      | true                  | false              |
      | true                  | true               |

  @Sprint31 @TeamC @FCPH-10088
  Scenario Outline: Manage update saved passenger when details changed BR_00868
    Given I am using Digital channel
    When I request to amend the passenger details <field> stored in the customer profile
    Then the saved passenger details has been created <created> or update in the customer profile
    Examples:
      | field           | created |
      | Title           | true    |
      | FirstName       | true    |
      | Surname         | true    |
      | Age             | false   |
      | EJPlus          | false   |
      | Email           | false   |
      | TelephoneNumber | false   |
      | NIF             | false   |

  @Sprint31 @TeamC @FCPH-10088 @manual
  Scenario: Do not update the profile if the passenger is a dependent or significant other
    Given the passenger has been indicated as to be saved to the customer profile
    When the passenger ID provided links to a significant other or dependent in the customer profile
    Then no update has been done to the significant other or dependent in the profile
