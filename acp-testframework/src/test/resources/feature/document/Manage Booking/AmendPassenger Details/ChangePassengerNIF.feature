@Sprint26
@FCPH-7735
Feature: Process and get updated NIF request for all channels except PublicApiB2B

  Scenario Outline: Generate an error code for NIF number
    Given I have a basket with a valid flight with 1 adult added via <channel>
    When I do the commit booking for All "<channel>"
    Then a booking reference is returned
    And verify the booking status changed to "COMPLETED"
    And verify the order is in edit mode
    And I add NIF number with "<condition>" and will receive an error code "<error>"

    Examples:
      | channel            |  condition               | error           |
      | Digital            |  incorrect_length        | SVC_100000_3026 |
      | Digital            |  incorrect_format        | SVC_100045_3003 |
      | ADAirport          |  incorrect_length        | SVC_100000_3026 |
      | ADAirport          |  incorrect_format        | SVC_100045_3003 |
      | ADCustomerService  |  incorrect_length        | SVC_100000_3026 |

  Scenario Outline: Generate an error code by adding NIF number which is already used on same booking (for more than one passenger)
    Given my basket contains flight with passengerMix "<passenger>" added via "<channel>"
    When I do the commit booking for All "<channel>"
    Then a booking reference is returned
    And verify the booking status changed to "COMPLETED"
    And verify the order is in edit mode
    And receive an error code "SVC_100045_3013" by adding new NIF "<nifNumber>" which is already used by other passenger

    Examples:
      | passenger | channel           |   nifNumber    |
      | 2 Adult   | Digital           |   2344566098A  |
      | 2 Adult   | ADAirport         |   2344566098b  |
      | 2 Adult   | ADCustomerService |   2344566098X  |

  Scenario Outline: Add, change and verify NIF number against the passenger
    Given I have a basket with a valid flight with 1 adult added via <channel>
    When I do the commit booking for All "<channel>"
    Then a booking reference is returned
    And verify the booking status changed to "COMPLETED"
    And verify the order is in edit mode
    And I add, update and verify NIF number "<addNIF>" "<updateNif>"

    Examples:
      | channel            |  addNIF      |   updateNif   |
      | Digital            |  098765430   |               |
      | Digital            |  778654870   |   1234567890  |
      | ADAirport          |  0987654321  |               |
      | ADAirport          |  0778654999  |   1234567A8   |
      | ADCustomerService  |  12345678A   |               |
      | ADCustomerService  |  12345678C   |   12345678dd  |
