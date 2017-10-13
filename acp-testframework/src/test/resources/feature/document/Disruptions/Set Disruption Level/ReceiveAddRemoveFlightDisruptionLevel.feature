  #   the event is received through an external queue subscription.
  @TeamD @Sprint32
  Feature: Receive event to set disruption level

    @FCPH-10796 @manual
    Scenario: 1 - Set the disruption level against the flight in the back office
      Given that a disruption level has been set in the master system
      And ACP received the event that inform of the disruption level
      When I search the flight in the back office
      Then the disruption level is set against the flight
      And the disruption reason is set against the flight

    @FCPH-10796 @manual
    Scenario: 2- Set the disruption level against the booking
      Given a disruption is set against a flight
      When I search a booking that contain that flight
      Then I will see the disruption level against the flight on the booking
      And I will see the disruption reason against the flight on the booking

    @FCPH-10796 @manual
    Scenario: 3 - Create a booking history entry on each booking for the disruption set
      Given that a disruption level has been set in the master system
      When ACP receive the event that inform of the disruption level
      Then a booking history entry is created for each booking that contain that flight
      And the entry will contain:
        | Channel = ACP                     |
        | Event Date                        |
        | Event Time                        |
        | User ID = External Update         |
        | Event Type = disruption level set |
        | Event Description = Flight Number |
        | Departure Airport                 |
        | Departure Date                    |
        | Arrival Airport                   |
        | Disruption Level                  |
        | Disruption Reason                 |

    @FCPH-10798 @manual
    Scenario: 1 - Remove the disruption level against the flight in the back office
      Given that a disruption level has been removed in the master system
      And ACP received the event that inform of the disruption level
      When I search the flight in the back office
      Then the disruption level is removed against the flight
      And the disruption reason is removed against the flight

    @FCPH-10798 @manual
    Scenario: 2- Remove the disruption level against the booking
      Given that a disruption level has been set in the master system
      And a disruption is removed against a flight
      When I search a booking that contain that flight
      Then I will see the disruption level removed against the flight on the booking
      And I will see the disruption reason removed against the flight on the booking

    @FCPH-10798 @manual
    Scenario: 3 - Create a booking history entry on each booking for the disruption removed
      Given that a disruption level has been set in the master system
      And the disruption level against the flight on the booking has been removes
      When ACP receive the event that inform of the disruption level
      Then a booking history entry is created for each booking that contain that flight
      And the entry will contain:
        | Channel = ACP                             |
        | Event Date                                |
        | Event Time                                |
        | User ID = External Update                 |
        | Event Type = disruption level set removed |
        | Event Description = Flight Number         |
        | Departure Airport                         |
        | Departure Date                            |
        | Arrival Airport                           |
        | Disruption Level                          |
        | Disruption Reason                         |

    @FCPH-10800 @manual
    Scenario: Return disruption level on the related booking
      Given a disruption is set against a flight on a related booking
      When I send the getAdvancedCustomerProfile request
      Then I will return additional information to the channel

    @FCPH-10800 @manual
    Scenario: Return disruption level on a booking
      Given a disruption is set against a flight on a related booking
      When I send the getBookingSummaries request
      Then I will return the disruption level against the flight

