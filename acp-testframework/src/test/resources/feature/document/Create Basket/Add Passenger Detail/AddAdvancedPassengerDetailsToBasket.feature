@FCPH-344
Feature: Add SSR, NIF and contact details passenger details to add to a booking.

  Scenario: 2 – If I input the incorrect length of a NIF number then I receive an error message
    Given I am updating my passenger details
    When I provide a NIF number less than 9
    Then I should receive the "SVC_100000_3026" error message stating that the NIF number must be a minimum of 9 digits

  Scenario: 3 – I am only able to use a NIF number against one passenger
    Given I am updating my passenger details for at least two travellers
    When I provide the same NIF number to both travellers
    Then I should receive the "SVC_100045_3013" error message stating the NIF number con only be used against one passenger

  Scenario Outline: 4 – If I add a passenger details with more than X SSR’s then I will receive a message they have gone over the maximum number of SSR’s
    Given I am updating my passenger details
    When the number of SSRs exceed the maximum of <X>
    Then I should receive the "SVC_100012_3031" error message stating the number of SSRs is <X>
    Examples:
      | X |
      | 5 |

  Scenario Outline: 5 – SSR is not allowed for the sector
    Given I am updating my passenger details as <Channel>
    When I add an SSR for an invalid sector
    Then I should receive the "<Code>" <Type> message
    Examples:
      | Channel      | Type    | Code            |
      | Digital      | error   | SVC_100273_3015 |
      | ADAirport    | warning | SVC_100273_3015 |
      | PublicApiB2B | error   | SVC_100273_3015 |

  Scenario Outline: 6 – SSR is allowed for the sector
    Given I am updating my passenger details as <Channel>
    When I add an SSR for a valid sector
    Then the passenger details should be updated with the SSR
    Examples:
      | Channel           |
      | PublicApiMobile   |
      | PublicApiB2B      |
      | ADCustomerService |

  Scenario Outline: 7 – as a staff user I update passenger details of a dependant or significant other with incorrect details
    Given I am a "<customer type>" and logged in as user "a.rossi@reply.co.uk" and "1234"
    When I update passenger details for someone who is not a dependant or significant other of the staff member
    Then I receive an error stating the names do not match
    Examples:
      | customer type               |
      | staff member                |
      | nominated significant other |

  Scenario Outline: 9 – as a staff user I update passenger details of a dependant or significant other with correct details
    Given I am a "<customer type>" and logged in as user "a.rossi@reply.co.uk" and "1234"
    When I update passenger details for someone who is a "<passenger type>" of the staff customer
    Then the passenger details should be updated
    Examples:
      | customer type               | passenger type    |
      | staff member                | significant other |
      | nominated significant other | dependant         |