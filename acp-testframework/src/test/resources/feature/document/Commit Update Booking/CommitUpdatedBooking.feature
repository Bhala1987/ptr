Feature: Update a booking for a Change flight with a standard bundle only

  @TeamC @Sprint30 @FCPH-10089
  Scenario Outline: Return confirmaiton to the channel, Add Booking History entry and Create a new version of the booking
    Given I am using <channel> channel
    When I have amendable basket for <fareType> fare and <passenger> passenger
    And I change the flight for amendable basket
    And I recommit the booking
    Then I see commit is successful
    And I see history added for the booking
    Examples:
      | channel | passenger | fareType |
      | Digital | 2 adult   | Standard |

  @Sprint31 @TeamC @FCPH-10517
  Scenario Outline: Receive commit Booking with allocation already given
    Given I am using <channel> channel
    When I have amendable basket for <fareType> fare and <passenger> passenger
    And I change the flight for amendable basket
    And I recommit the booking
    Then I see commit is successful
    And I see history added for the booking
    Examples:
      | channel | passenger | fareType |
      | Digital | 1 adult   | Standard |

  @Sprint31 @TeamC @FCPH-10517 @local
  Scenario Outline: No allocation for a flight received from abstraction layer
    Given I am using <channel> channel
    When I have amendable basket for <fareType> fare and <passenger> passenger
    And I change the flight for amendable basket
    And I recommit the booking with <xposId>
    Then I see message <messageCode> with new flight price
    Examples:
      | channel | passenger | fareType | xposId                               | messageCode     |
      | Digital | 1 adult   | Standard | 00000000-0000-0000-0000-907777777777 | SVC_100022_3012 |
      | Digital | 1 adult   | Standard | 00000000-0000-0000-0000-917777777777 | SVC_100022_3012 |
      | Digital | 1 adult   | Standard | 00000000-0000-0000-0000-927777777777 | SVC_100022_3012 |
      | Digital | 1 adult   | Standard | 00000000-0000-0000-0000-937777777777 | SVC_100022_3011 |
      | Digital | 1 adult   | Standard | 00000000-0000-0000-0000-897777777777 | SVC_100022_3012 |

  @Sprint31 @TeamC @FCPH-10090
  Scenario Outline:   Scenario Outline: Commit update booking for Change Passenger details - no infants
    Given I am using <channel> channel
    When I commit a booking with <fareType> fare and <passenger> passenger
    And I've checked in all passengers outbound flight
    When I create a request to generate the boarding pass
    And I create amendable basket for the booking created
    And I change below details
      | name             |
      | age              |
      | nif              |
      | email            |
      | ejPlusCardNumber |
    And I recommit the booking
    Then I see commit is successful
    And I see updated details in booking
    And I see additional information codes in commit booking SVC_100024_1002
    And I see history added for the booking
    And I see get booking additional information codes SVC_100024_1002
    And the bording Pass status is changed to <boardingPassStatus>
    Examples:
      | channel | fareType | passenger | boardingPassStatus |
      | Digital | Standard | 2 Adult   | NEED_TO_RERETRIEVE |

  @Sprint31 @TeamC @FCPH-10090
  Scenario Outline: Generate error message if the basket is no longer valid
    Given I am using <channel> channel
    When I commit a booking with <fareType> fare and <passenger> passenger
    And I try get invalid amendable basket code
    Then I see an invalid basket error
    Examples:
      | channel | fareType | passenger |
      | Digital | Standard | 2 Adult   |

  @Sprint31 @TeamC @FCPH-11122
  Scenario Outline: Add Booking History entry
    Given I am using <channel> channel
    When I commit a booking with <fareType> fare and <passenger> passenger
    And I create amendable basket for the booking created
    And I change below details for couple of passenger
      | name             |
      | age              |
      | nif              |
      | email            |
      | ejPlusCardNumber |
    And I recommit the booking
    Then I see commit is successful
    And I see booking version, history and field changed
    Examples:
      | channel | fareType | passenger |
      | Digital | Standard | 3 Adult   |

  @Sprint32 @TeamC @FCPH-11119 @manual
  Scenario: Booking history and amendment lock release
    Given I have committed a required booking
    When I recommit the booking after adding an additional fare to passenger with purchased seat
    Then the booking history should be updated
    And the amendment lock should be released
    And the new version of booking should be created

  @Sprint32 @TeamC @FCPH-11119 @local
  Scenario Outline: Update a booking with an additional fare to a passenger and checkin
    Given I am using channel <channel>
    And I commit a booking with <fareType> fare and <passenger> passenger without purchased seat
    And I've checked in all passengers outbound flight
    And I create a request to generate the boarding pass
    And I create amendable basket for the booking created
    And have the basket updated to add additional fare
    When I add 1 additional fare for each passenger in the amendable basket
    And I should receive a successful operation confirmation response with the basket id
    And the passenger should have additional seat in the basket
    And I commit the booking again
    Then the channel will receive a warning with code SVC_100024_1001
    And I get the booking details
    And the boarding pass status should be NEED_TO_RERETRIEVE
    And the additional seat should exists in the booking details
    And the additional seat entry status should be NEW
    And the additional seat active flag should be TRUE
    Examples:
      | channel   | fareType | passenger |
      | ADAirport | Standard | 2 adult   |

  @Sprint32 @TeamC @FCPH-11119
  Scenario Outline: Update a booking with purchased seat and additional seat for multiple passengers on multiple flights
    Given I am using channel <channel>
    And I commit a booking with <fareType> fare and <passenger> passenger without purchased seat for <n> flights
    And I get the booking details
    And I create amendable basket for the booking created
    And have the basket updated to add additional fare
    When I add 1 additional fare for each passenger in the amendable basket
    And I add purchased seat <seat> with additional seat <addlSeat> to the booking
    And I commit the booking again
    Then I get the booking details
    And the additional seat should exists in the booking details
    And the additional seat entry status should be NEW
    And the additional seat active flag should be TRUE
    Examples:
      | channel           | fareType | passenger | seat    | addlSeat | n |
      | ADCustomerService | Flexi    | 2 adult   | UPFRONT | 1        | 2 |

  @Sprint32 @TeamC @FCPH-11114
  Scenario Outline: Update a booking by adding a passenger on multiple flights for different fare bundles
    Given I am using channel <channel>
    And I commit a booking with <fareType> fare and 1 adult passenger without purchased seat for <numberOfFlights> flights
    And I get the booking details
    And I create amendable basket for the booking created
    When I add a passenger <passengerMix> and <fareType> fare to all flights
    And I commit the booking again
    Then I get the booking details
    And I should see that the passengers are added
    And the entry status for each passenger should be NEW
    And the active flag for each passenger should be TRUE
    Examples:
      | channel   | fareType | passengerMix         | numberOfFlights |
      | ADAirport | Flexi    | 1 adult, 1 infant OL | 1               |
      | Digital   | Standard | 1 adult, 1 child     | 2               |

  @Sprint32 @TeamC @FCPH-11114 @manual
  Scenario: Booking history and amendment lock release
    Given I have committed a required booking
    When I recommit the booking after adding a passenger to flights
    Then the booking history should be updated
    And the amendment lock should be released
    And the new version of booking should be created

  @Sprint32 @TeamC @FCPH-11113
  Scenario Outline: Update a booking by adding a new flight
    Given I am using channel <channel>
    And I commit a booking with <fareType> fare and 1 adult passenger without purchased seat
    And I get the booking details
    And I create amendable basket for the booking created
    When I add a new flight for <passengerMix> passenger and <fareType> fare to the booking
    Then I get the booking details
    And I should see that the new flight is added
    And the entry status for each passenger should be NEW
    And the active flag for each passenger should be TRUE
    Examples:
      | channel   | fareType | passengerMix                   |
      | Digital   | Standard | 2 adult, 1 child, 1 infant OOS |
      | ADAirport | Flexi    | 1 adult, 1 infant OL           |

  @Sprint32 @TeamC @FCPH-11113 @manual
  Scenario: Booking history and amendment lock release
    Given I have committed a required booking
    When I recommit the booking after adding new flight
    Then the booking history should be updated
    And the amendment lock should be released
    And the new version of booking should be created