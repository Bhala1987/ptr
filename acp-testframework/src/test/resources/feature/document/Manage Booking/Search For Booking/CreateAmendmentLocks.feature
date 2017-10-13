@Sprint30
@TeamA
Feature: Create amendment locks for a booking/Passenger

  @FCPH-2718
  Scenario Outline: Verify multiple locks for the same booking while Override lock is True
    Given I am using channel Digital
    And I create a "COMPLETED" status return booking for "1 Adult"
    And I have an existing lock <ExistingLock>
    And I moved to new session while previous session is still active
    And I am using channel <Channel>
    When I lock whole booking again with override lock <OverrideLock>
    Then I should see the <ExpectedResult>
    And old amendable basket should be deleted

    Examples:
      | ExistingLock                 | Channel           | OverrideLock | ExpectedResult |
      | any outbound adult passenger | ADAirport         | true         | New basket     |
      | whole booking                | ADAirport         | true         | New basket     |
      | any outbound adult passenger | ADCustomerService | true         | New basket     |
      | whole booking                | ADCustomerService | true         | New basket     |

  @FCPH-2718
  Scenario Outline: Verify multiple locks for the same booking while Override lock is False
    Given I am using channel Digital
    And I create a "COMPLETED" status return booking for "1 Adult"
    And I have an existing lock <ExistingLock>
    And I moved to new session while previous session is still active
    And I am using channel <Channel>
    When I lock whole booking again with override lock <OverrideLock>
    Then I should see the <ExpectedResult>

    Examples:
      | ExistingLock                 | Channel           | OverrideLock | ExpectedResult  |
      | any outbound adult passenger | ADAirport         | false        | SVC_100245_2004 |
      | whole booking                | ADAirport         | false        | SVC_100245_2004 |
      | any outbound adult passenger | ADCustomerService | false        | SVC_100245_2004 |
      | whole booking                | ADCustomerService | false        | SVC_100245_2004 |

  @FCPH-2718
  Scenario Outline: Verify multiple locks for the same booking for Single journey
    Given I am using channel Digital
    And I create a "COMPLETED" status booking for "<PassengerMix>"
    And I have an existing lock <ExistingLock>
    And I moved to new session while previous session is still active
    When I attempt to lock <NewRequestForLock>
    Then I should see the <ExpectedResult>

    Examples:
      | PassengerMix         | ExistingLock                       | NewRequestForLock                  | ExpectedResult  |
      | 1 Adult              | any outbound adult passenger       | same outbound adult passenger      | SVC_100245_2004 |
      | 2 Adult              | any outbound adult passenger       | same outbound adult passenger      | SVC_100245_2004 |
      | 2 Adult              | any outbound adult passenger       | different outbound adult passenger | Success         |
      | 2 Adult, 1 Child     | any outbound adult passenger       | any outbound child passenger       | Success         |
      | 2 Adult, 1 Child     | any outbound child passenger       | any outbound adult passenger       | Success         |
      | 1 Adult              | whole booking                      | Any outbound adult passenger       | SVC_100245_2004 |
      | 1 Adult              | any outbound adult passenger       | whole booking                      | SVC_100245_2004 |
      | 1 Adult, 1 Infant OL | any outbound adult passenger       | any outbound InfantOnLap Passenger | SVC_100245_2004 |
      | 1 Adult, 1 Infant OL | any outbound InfantOnLap Passenger | any outbound adult passenger       | Success         |
      | 3 Adult              | any two outbound adult passengers  | same outbound adult passenger      | SVC_100245_2004 |
      | 3 Adult              | any two outbound adult passengers  | different outbound adult passenger | Success         |


  @FCPH-2718
  Scenario Outline: Verify multiple locks for the same booking for Return journey
    Given I am using channel Digital
    And I create a "COMPLETED" status return booking for "<PassengerMix>"
    And I have an existing lock <ExistingLock>
    And I moved to new session while previous session is still active
    When I attempt to lock <NewRequestForLock>
    Then I should see the <ExpectedResult>

    Examples:
      | PassengerMix                               | ExistingLock                       | NewRequestForLock                     | ExpectedResult  |
      | 2 Adult, 1 child, 1 Infant OS, 1 Infant OL | any outbound adult passenger       | corresponding inbound adult passenger | SVC_100245_2004 |
      | 2 Adult                                    | any outbound adult passenger       | corresponding inbound adult passenger | SVC_100245_2004 |
      | 2 Adult                                    | any outbound adult passenger       | different inbound adult passenger     | Success         |
      | 2 Adult, 1 Child                           | any outbound adult passenger       | any inbound child passenger           | Success         |
      | 2 Adult, 1 Child                           | any outbound child passenger       | any inbound adult passenger           | Success         |
      | 1 Adult                                    | whole booking                      | Any inbound adult passenger           | SVC_100245_2004 |
      | 1 Adult                                    | any inbound adult passenger        | whole booking                         | SVC_100245_2004 |
      | 1 Adult, 1 Infant OL                       | any outbound adult passenger       | any inbound infantonlap passenger     | SVC_100245_2004 |
      | 1 Adult, 1 Infant OL                       | any outbound InfantOnLap Passenger | any inbound adult passenger           | Success         |

  @FCPH-9116
  Scenario Outline: Verify the lock removable from a booking when the session expires through logout
    Given I am using channel Digital
    And I am logged in as a standard customer
    And I create a "COMPLETED" status <Booking> for "<PassengerMix>"
    And I have an existing lock <ExistingLock>
    When I make session to expire through logout
    Then lock should have removed
    When I attempt to lock <NewRequestForLock>
    Then I should see the New Basket

    Examples:
      | PassengerMix         | Booking        | ExistingLock                 | NewRequestForLock                     |
      | 1 Adult              | booking        | whole booking                | whole booking                         |
      | 1 Adult              | booking        | whole booking                | any outbound adult passenger          |
      | 1 Adult              | return booking | whole booking                | any inbound adult passenger           |
      | 1 Adult              | booking        | any outbound adult passenger | whole booking                         |
      | 1 Adult              | return booking | any inbound adult passenger  | whole booking                         |
      | 1 Adult              | booking        | any outbound adult passenger | same outbound adult passenger         |
      | 1 Adult              | return booking | any outbound adult passenger | corresponding inbound adult passenger |
      | 1 Adult, 1 Infant OL | booking        | any outbound adult passenger | any outbound InfantOnLap passenger    |


#  This has to be manual because we can't wait 20 minutes to time out on the session automatically
#  Reason separating out from the above table is because it doesn't like tags at the example level

  @FCPH-9116 @manual
  Scenario Outline: Verify the lock removable from a booking when the session expires through inactivity
    Given I am using channel Digital
    And I am logged in as a standard customer
    And I create a "COMPLETED" status <Booking> for "<PassengerMix>"
    And I have an existing lock <ExistingLock>
    When I make session to expire through inactivity
    Then lock should have removed
    When I attempt to lock <NewRequestForLock>
    Then I should see the New Basket

    Examples:
      | PassengerMix         | Booking        | ExistingLock                 | NewRequestForLock                     |
      | 1 Adult              | booking        | whole booking                | whole booking                         |
      | 1 Adult              | booking        | whole booking                | any outbound adult passenger          |
      | 1 Adult              | return booking | whole booking                | any inbound adult passenger           |
      | 1 Adult              | booking        | any outbound adult passenger | whole booking                         |
      | 1 Adult              | return booking | any inbound adult passenger  | whole booking                         |
      | 1 Adult              | booking        | any outbound adult passenger | same outbound adult passenger         |
      | 1 Adult              | return booking | any outbound adult passenger | corresponding inbound adult passenger |
      | 1 Adult, 1 Infant OL | booking        | any outbound adult passenger | any outbound InfantOnLap passenger    |