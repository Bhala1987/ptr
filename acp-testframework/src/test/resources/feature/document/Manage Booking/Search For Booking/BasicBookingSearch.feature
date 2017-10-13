@FCPH-363 @FCPH-366
Feature: Basic booking search is returned
#Perform New Booking Search when valid
#Only email, title, first name, last name, postcode and travel from date included in this Story

  @regression @defect:FCPH-11902
  Scenario Outline: Verify Booking search results
    Given there are valid bookings using channel "<Channel>" and passenger mix "<Mix>"
    When I search for a booking for channel "<Channel>"
    Then the bookings matching the search criteria are returned
    And the booking has customer details
    And the booking has outbound date
    And the booking has booking date
    And the booking has sector details
    And the booking status is "COMPLETED"
    Examples:
      | Channel   | Mix     |
      | ADAirport | 1 Adult |

  Scenario Outline: Booking search is returned in date Time ascending order by default
    Given there are valid bookings using channel "<Channel>" and passenger mix "<Mix>"
    When I search for a booking for channel "<Channel>"
    Then the bookings are returned in date time order
    Examples:
      | Channel           | Mix     |
      | ADAirport         | 1 Adult |
      | ADCustomerService | 1 Adult |

  @manual
  Scenario: Verify that details of first flight are returned as part of find booking
    Given there are valid bookings
    When I search for a booking with multiple flights
    Then the booking has sector infomation of first flight on the booking

  @manual
  Scenario Outline: Return bookings with expected status to respective channels
    Given there are valid bookings using channel "<Channel>" and passenger mix "1 Adult"
    And the status of the booking is "<status>"
    When I search for the booking via "<channel>"
    Then the booking "<shouldornot>" be returned
    Examples:
      | status                                 | channel | shouldornot |
      | Complete                               | Digital | should      |
      | Complete                               | Public  | should      |
      | Complete                               | AD      | should      |
      | Pending Cancellation                   | Digital | should not  |
      | Pending Cancellation                   | Public  | should not  |
      | Past                                   | Digital | should      |
      | Customer Cancelled                     | Public  | should      |
      | Disruption Cancelled                   | Digital | should      |
      | Disruption Cancelled                   | AD      | should      |
      | Cancelled by Revenue Protection        | AD      | should      |
      | Cancelled by Revenue Protection        |         | should      |
      | Complete                               | AD      | should      |
      | Pending Cancellation                   | AD      | should      |
      | Past                                   | AD      | should      |
      | Chargeback - Policy Revenue Protection | AD      | should      |
      | Chargeback - Fraud Revenue Protection  | AD      | should      |
      | Customer Cancelled                     | AD      | should      |
      | Chargeback - Policy Revenue Protection | Digital | should not  |
      | Chargeback - Fraud Revenue Protection  | Public  | should not  |
# AD allowed status - |Complete |Past|Customer Cancelled| Pending Cancellation|Cancelled by Revenue Protection|Chargeback - Policy Revenue Protection |Chargeback - Fraud Revenue Protection | Past|

  @manual
  Scenario Outline: Bookings within x period after last flight are returned to the requesting channel
    Given there are valid bookings using channel "<Channel>" and passenger mix "1 Adult"
    Given the period of the booking is "<period>"
    When I search for the booking via "<user>"
    Then the booking "<shouldornot>" be returned
    Examples:
      | period    | user             | shouldornot |
      | 24 months | AD-Airport       | should      |
      | 26 months | AD-CustomerAgent | should not  |
      | 6 months  | Digital          | should      |
      | 6 months  | Public           | should      |
      | 24 months | Public           | should not  |

  Scenario Outline: Booking search results are returned when using travelFromDate
    Given there are valid bookings using channel "<Channel>" and passenger mix "<Mix>"
    When I search for a booking using the travelfrom dates using channel "<Channel>"
    Then the bookings by travelfrom are returned
    Examples:
      | Channel           | Mix     |
      | ADAirport         | 1 Adult |
      | ADCustomerService | 1 Adult |

  Scenario Outline: Lastname and firstname cannot be used on its own as a search criteria
    Given there are valid bookings using channel "<channel>" and passenger mix "<mix>"
    When I search for the booking using "<field>" of customer using channel "<channel>"
    Then an <error> is returned informing me that I cannot search by only "<field>"
    Examples:
      | field     | error           | channel           | mix     |
      | lastName  | SVC_100144_2001 | ADAirport         | 1 Adult |
      | firstName | SVC_100144_2002 | ADCustomerService | 1 Adult |

  @regression
  Scenario Outline: Verify that search by field should returned all bookings scheduled to travel on given date
    Given there are valid bookings using channel "<Channel>" and passenger mix "<Mix>"
    When I search for the booking using "<Field>" of customer using channel "<Channel>"
    Then all the bookings which are scheduled to travel on date are returned
    Examples:
      | Channel           | Field                                  | Mix     |
      | ADAirport         | travelFromDate, travelToDate, lastName | 1 Adult |
      | ADCustomerService | travelFromDate, lastName               | 1 Adult |

  Scenario Outline: Booking search results are returned specific to channel
    Given there are valid bookings using channel "<channel>" and passenger mix "<mix>"
    When I search for the booking using "<parameters>" of customer via "<channel>"
    Then the bookings matching the search criteria based on search parameters "<parameters>" are returned for customer
    Examples:
      | parameters                          | channel           | mix     |
      | email                               | ADAirport         | 1 Adult |
      | title                               | ADCustomerService | 1 Adult |
      | title, lastName                     | ADAirport         | 1 Adult |
      | title,firstName,lastName            | ADCustomerService | 1 Adult |
      | postcode                            | ADAirport         | 1 Adult |
      | postcode, title                     | ADCustomerService | 1 Adult |
      | postcode, title, lastName           | ADAirport         | 1 Adult |
      | postcode, lastName ,firstName       | ADCustomerService | 1 Adult |
      | email, postcode                     | ADAirport         | 1 Adult |
      | travelfromdate                      | ADCustomerService | 1 Adult |
      | travelfromdate, firstName,lastName  | ADAirport         | 1 Adult |
      | travelfromdate, email               | ADCustomerService | 1 Adult |
      | travelfromdate, firstName, lastName | ADAirport         | 1 Adult |
      | travelfromdate, postcode            | ADAirport         | 1 Adult |
      | travelfromdate, title               | ADCustomerService | 1 Adult |
      | postcode                            | ADAirport         | 1 Adult |

  @pending
  @manual
  Scenario Outline: Bookings less than 6 years and 2 months from travel date are returned to the agent desktop based on permissions.
    Given there are valid bookings using channel "<channel>" and passenger mix "1 Adult"
    And the criteria for booking is "<criteria>"
    When I search for booking via <channel>
    Then the booking "<shouldornot>" be returned
    Examples:
      | criteria                      | channel          | shouldornot |
      | less than 2Y6M travel date    | AD-Airport       | should not  |
      | greator than 2Y6M travel date | AD-Airport       | should not  |
      | less than 2Y6M travel date    | AD-CustomerAgent | should      |
      | less than 2Y6M travel date    | AD-HeadOffice    | should      |
      | less than 2Y6M travel date    | AD-Airport       | should not  |
      | less than 2Y6M travel date    | Public           | should not  |


  @pending
  Scenario Outline: Booking search results are returned based on single search parameter
    When I request the booking details with <criteria> <value>
    Then All search result satisfying the criteria is returned with "success"
    Examples:
      | criteria   | value            |
      | email      | test@easyjet.com |
      | travelDate | 02/15/2018       |
      | postcode   | SL14DX           |

  @FCPH-364
  @pending
  Scenario Outline: Booking search results are returned based on single search parameter
    When I enter only LastName <lastname> as search criteria
    Then I should get error message below the field as "Please enter either a title or First Name of the customer"
    Examples:
      | lastname |
      | Francis  |
