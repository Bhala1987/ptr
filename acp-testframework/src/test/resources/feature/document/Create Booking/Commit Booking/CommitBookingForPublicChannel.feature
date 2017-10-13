@FCPH-2618
Feature:Commit Booking for Public Channel

  Scenario Outline: Booking reference is returned after commit booking
    When I do the booking with valid basket content for <channel>
    Then a booking reference is returned
    Examples:
      | channel      |
      | PublicApiB2B |

  @Sprint26
  @FCPH-8436
  @BR:BR_00951
  @regression
  Scenario: Seats allocation for commit booking Public API B2B
    Given I am using the channel PublicApiB2B
    And I have basket content with seats
    When I do the booking with valid basket content

@defect:FCPH-11503
  Scenario Outline: Booking with multiple flights
    Given I am using channel <channel>
    When I do the commit booking with multiple flights via <channel>
    Then I should be able to create successful booking with reference number
    And Booking is created from Cart and it has the flight details
    Examples:
      | channel      |
      | PublicApiB2B |

  Scenario Outline: Booking with existing customer and customer profile and linked to booking
    Given I am using channel <channel>
    When I do the commit booking with existing customer via <channel>
    Then I should be able to create successful booking with reference number
    And customer profile is linked with the booking
    Examples:
      | channel      |
      | PublicApiB2B |

  Scenario Outline: Booking for non existing customer
    When I do the commit booking with non existing customer via <channel>
    Then I should be able to create successful booking with reference number
    And booking should have details of newly created customer
    And Booking is created from Cart and it has the flight details
    Examples:
      | channel      |
      | PublicApiB2B |

  Scenario Outline: Corporate booking with deal information for public channel
    When I do the corporate booking with deal for "<channel>"
    And Booking is created from Cart and it has the flight details
    Then passenger details are created with status as Booked
    Then created date time is stored
    And booking should have details of newly created customer
    Examples:
      | channel      |
      | PublicApiB2B |

  Scenario: Validation error message returned for the missing mandatory fields for public API
    When I call the commit booking with missing parameter then we get respective error as below
          | PaymentMismatched                           | SVC_100022_2020 |
          | InvalidPaymentAmount                        | SVC_100022_2020 |
          | MissingPaymentCurrency                      | SVC_100022_2007 |
#          | InvalidPaymentMethod                        | SVC_100022_2022 |
#          | BasketContent_ContentMissingOutboundDetails | SVC_100022_2040 |
#          | BasketContent_MissingCustomerContext        | SVC_100022_2032 |
#          | BasketContent_MissingCustomerAddress        | SVC_100022_2036 |
#          | BasketContent_MissingCustomerEmail          | SVC_100022_2030 |
#          | BasketContent_MissingPassengerList          | SVC_100022_2034 |
#          | BasketContent_InvalidFlightKey              | SVC_100022_2029 |
#          | BasketContent_InvalidPassengerId            | SVC_100022_2073 |
#          | BasketContent_MissingPassengerId            | SVC_100022_2059 |
#          | BasketContent_MissingDefaultCardType        | SVC_100022_2009 |
#          | BasketContent_InvalidCurrency               | SVC_100022_2027 |
#          | BasketContent_MissingFlightKey              | SVC_100022_2047 |
#          | BasketContent_MissingFlightDetails          | SVC_100022_2040 |
#          | BasketContent_MissingFlightNumber           | SVC_100022_2048 |
#          | MissingPaymentMethod                        | SVC_100022_2002 |
#          | MissingPaymentCode                          | SVC_100022_2005 |
#          | MissingPaymentAmount                        | SVC_100022_2006 |

  @Sprint27 @FCPH-7992 @FCPH-9715
  Scenario: Commit booking from the Public API channel with hold bags, sports equipment, excess weights and seats.
    Given I am using channel PublicApiB2B
    And creating booking for 1 Adult
    When I create a commit booking request with 1 hold bag, 1 excess weight, 1 sport equipment with seats
    And commit the booking
    Then a booking reference is returned
    And Booking is created from Cart and it has the flight details

  Scenario Outline: Error is returned if duplicate booking found
    When I do the booking with valid basket content for same passenger and flight via "<channel>"
    Then an error SVC_100022_2021 is returned for duplicate booking
    Examples:
      | channel      |
      | PublicApiB2B |

  @manual
  Scenario Outline: Inventory is allocated during the commit booking process
    When I do the booking with basket content for <channel>
    Then the inventory is allocated during the commit booking process
    Examples:
      | channel      |
      | PublicApiB2B |

  @TeamA @Sprint29 @FCPH-9618 @negative
  Scenario Outline: Validate passenger detail errors are returned.
    Given I am using channel PublicApiB2B
    When I create a valid basket with the following scenario: "<Scenario>"
    And I commit the booking with the erroneous basket data
    Then I will receive an error with code '<Error>'
    Examples:
      | Scenario           | Error           |
      | NifLength          | SVC_100000_3026 |
      | DuplicateNif       | SVC_100045_3013 |
      | SSRThreshold       | SVC_100012_3031 |
      | EJPlusLength       | SVC_100012_3027 |
      | StaffEJPlusLength  | SVC_100012_3027 |
      | SurnameToEJSurname | SVC_100000_2074 |
      | EJPlusExpiry       | SVC_100012_2078 |
      | DuplicateEJPlus    | SVC_100045_3015 |
      | EJMembershipStatus | SVC_100000_2088 |
      | DocumentNumber     | SVC_100273_3017 |
      | DocumentChars      | SVC_100273_3024 |
      | ApisAdultTooYoung  | SVC_100273_3018 |
      | ApisInfantTooYoung | SVC_100273_3019 |
      | ApisInfantTooOld   | SVC_100273_3020 |
      | ApisChildTooYoung  | SVC_100273_3023 |
      | ApisChildTooOld    | SVC_100273_3023 |
      | InfantExceedsAdult | SVC_100148_3006 |

  @TeamA @Sprint29 @FCPH-9618 @negative
  Scenario: SSRs cannot be added on sectors that don't support them.
    Given I am using channel PublicApiB2B
    And travelling from ALC to LGW
    When I create a valid basket with the following scenario: "SSRSector"
    And I commit the booking with the erroneous basket data
    Then I will receive an error with code 'SVC_100273_3015'

  @TeamA @Sprint29 @FCPH-9618
  Scenario: Warning is generated when a child is travelling alone.
    Given I am using channel PublicApiB2B
    When I create a valid basket with the following scenario: "WarningChildAlone"
    And I commit the booking with the erroneous basket data
    Then warning code SVC_100148_3008 should be returned