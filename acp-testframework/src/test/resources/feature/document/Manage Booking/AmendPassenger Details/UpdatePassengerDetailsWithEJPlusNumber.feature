Feature: Process Change eJ Plus Details Request with basket update - no purchased seat, not Public API

  @Sprint27 @FCPH-8999
  Scenario: Add Passenger eJ Plus Number no seat purchased
    Given I am using the channel Digital
    And I commit a booking with for 1 adult and with using Standard
    And the booking is amendable
    When I update with ejPlus customer
    Then I see all the benefits of the customer

  @Sprint27 @FCPH-8999
  Scenario Outline: Overwrite Passenger eJ Plus Number - same type
    Given I am using the channel Digital
    And I commit a booking with 1 adult and has ejPlus <type> member with Standard
    And the booking is amendable
    When I update with another ejPlus <updateUserType>
    Then I see all the benefits of the <updateUserType>
    Examples:
      | type     | updateUserType  |
      | customer | anotherCustomer |
      | staff    | customer        |
      | customer | staff           |

  @Sprint27 @FCPH-8999
  Scenario: eJ Plus Number is removed if Surname is change no seat purchased
    Given I am using the channel Digital
    And I commit a booking with 1 adult and has ejPlus customer member with Standard
    And the booking is amendable
    When I change last name
    Then I don't see any ejPlus benefits in my basked

  @TeamC @Sprint29 @FCPH-9621
  Scenario Outline: Generate warning message if saved passenger is not able to be identified
    Given I am using the channel <channel>
    When I send an updatePassenger with <parameter>, <booking> with <passenger> and <fareType>, <login> with <valid>
    Then I will receive an <warning> message
    Examples:
      | channel | passenger | fareType | booking    | login      | warning         | valid                    | parameter    |
      | Digital | 1 adult   | Standard | no Booking | with Login | SVC_100009_2004 | invalid SavedPassengerId | no parameter |


  @TeamC @Sprint29 @FCPH-9621
  Scenario Outline: Generate error message if the customer is not logged in
    Given I am using the channel <channel>
    When I send an updatePassenger with <parameter>, <booking> with <passenger> and <fareType>, <login> with <valid>
    Then I will receive an error with code '<error>'
    Examples:
      | channel | passenger | fareType | booking    | login    | error           | valid           | parameter    |
      | Digital | 1 adult   | Standard | no Booking | no Login | SVC_100009_2001 | valid Parameter | no parameter |


  @TeamC @Sprint29 @FCPH-9621 @ADTeam
  Scenario Outline: Create a saved passenger as part of commit Booking
    Given I am using the channel <channel>
    When I send an updatePassenger with <parameter>, <booking> with <passenger> and <fareType>, <login> with <valid>
    Then I update the old passenger in the customer profile with <parameter>
    Examples:
      | channel | passenger | fareType | booking      | login      | valid           | parameter        |
      | Digital | 1 adult   | Standard | with Booking | with Login | valid Parameter | age              |
      | Digital | 1 adult   | Standard | with Booking | with Login | valid Parameter | ejPlusCardNumber |
      | Digital | 1 adult   | Standard | with Booking | with Login | valid Parameter | email            |
      | Digital | 1 adult   | Standard | with Booking | with Login | valid Parameter | phoneNumber      |
      | Digital | 1 adult   | Standard | with Booking | with Login | valid Parameter | nifNumber        |
      | Digital | 1 adult   | Standard | with Booking | with Login | valid Parameter | ssr              |


  @TeamC @Sprint29 @FCPH-9621 @ADTeam
  Scenario Outline: Create or Update a saved passenger if a passenger details has been changed BR_00868
    Given I am using the channel <channel>
    When I send an updatePassenger with <parameter>, <booking> with <passenger> and <fareType>, <login> with <valid>
    Then I update the new passenger in the customer profile with <parameter>
    Examples:
      | channel | passenger | fareType | booking      | login      | valid           | parameter |
      | Digital | 1 adult   | Standard | with Booking | with Login | valid Parameter | title     |
      | Digital | 1 adult   | Standard | with Booking | with Login | valid Parameter | firstName |



