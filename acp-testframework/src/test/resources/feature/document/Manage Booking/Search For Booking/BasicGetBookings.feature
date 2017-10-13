@FCPH-2716 @FCPH-2614
Feature: Create and Retrieve full booking details

  Scenario Outline: Receive the booking details for given booking reference number
    Given I have an existing booking using channel "<Channel>" and passenger mix "<Mix>"
    When I search for a booking with reference number
    Then Booking details with matching reference number are returned
    Examples:
      | Channel           | Mix     |
      | ADAirport         | 1 Adult |
      | ADCustomerService | 1 Adult |
      | Digital           | 1 Adult |
      | PublicApiMobile   | 1 Adult |

  Scenario Outline: Verify Bookings details with Invalid booking reference
    Given I have an existing booking using channel "<Channel>" and passenger mix "<Mix>"
    When I search for a booking with invalid reference number
    Then I get error in response informing me that there are no bookings
    Examples:
      | Channel           | Mix     |
      | ADAirport         | 1 Adult |
      | ADCustomerService | 1 Adult |

  @pending
  Scenario: Verify the booking currency details are returned in the response
    Given I have an existing booking using channel "<Channel>" and passenger mix "<Mix>"
    When I search for a booking with reference number
    Then Booking details with matching reference number are returned
    And Currency details are returned in the response$

  @manual
  Scenario Outline: Verify that Booking has correct status to make changes based on channel
    Given I have an existing booking using channel "<channel>" and passenger mix "<mix>"
    And the booking status is "<status>"
    When I request the booking details for "<channel>"
    Then I get respective allowed functions "<function>" in response based on booking status and permission
    Examples:
      | channel | status                                 | function      | mix     |
      | AD      | Complete                               | CHANGE_FLIGHT | 1 Adult |
      | AD      | Customer Cancelled                     |               | 1 Adult |
      | AD      | Past                                   |               | 1 Adult |
      | AD      | Chargeback - Policy Revenue Protection |               | 1 Adult |
      | AD      | Chargeback - Fraud Revenue Protection  |               | 1 Adult |
      | Digital | Complete                               | CHANGE_FLIGHT | 1 Adult |
      | Digital | Customer Cancelled                     |               | 1 Adult |
      | Digital | Past                                   |               | 1 Adult |
      | Digital | Chargeback - Policy Revenue Protection |               | 1 Adult |
      | Public  | Past                                   |               | 1 Adult |

  @manual
  Scenario Outline:  Booking changes can be made based on time before departure and Agent permission
    Given I have booking which are departing at "<depart_diff>"
    When I request the booking details for "<agent>"
    Then I get the allowed functions "<functions>" in the booking details response based on departure time and user
    Examples:
      | depart_diff | agent                | functions     |
      | +2          | ADAirport            | CHANGE_FLIGHT |
      | -2          | ADAirport            | CHANGE_FLIGHT |
      | +2          | HeadOffice Agent     | CHANGE_FLIGHT |
      | -2          | HeadOffice Agent     | CHANGE_FLIGHT |
      | -1          | Contact Center Agent | CHANGE_FLIGHT |
      | +1          | Contact Center Agent |               |
      | -2          | Contact Center Agent |               |