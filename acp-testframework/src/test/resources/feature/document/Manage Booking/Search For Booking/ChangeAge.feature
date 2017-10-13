Feature: Change Age Request from Agent Desktop/Digital

  @Sprint27
  @FCPH-8455 @defect:FCPH-11741
  Scenario Outline: Change Age Request from Agent Desktop results in a booking being repriced - not checked in
    Given <channel> do the commit booking with "2 Adult, 1 Child, 1 Infant OOS"
    And I have initiated a change passenger age action from <From> to <To> for single flight
    When I process the request for change passenger age
    Then I will update the passenger age with the new value
    And I will update the passenger type with the new value
    And I will recalculate the fees and taxes like "UK APD - Band A"
    And I will update basket totals
    And I do not change the passenger status
    And I do not change the passengers APIS status
    And I do not change the passengers ICTS status
    And I will return "SVC_100009_2002" message
  @regression
  @ADTeam
    Examples:
      | channel   | From  | To       |
      | ADAirport | Child | Adult-25 |
  @ADTeam
    Examples:
      | channel   | From      | To       |
      | ADAirport | Adult     | Child-8  |
      | ADAirport | Infant OS | Adult-25 |
      | ADAirport | Infant OS | Child-8  |
      | ADAirport | Adult     | Child-8  |
    Examples:
      | channel | From  | To       |
      | Digital | Adult | Child-8  |
      | Digital | Child | Adult-25 |

  @manual
  @Sprint27
  @FCPH-8455
  Scenario Outline: Change Age Request from Agent Desktop results in a booking being repriced - checked in
    Given <channel> do the commit booking with "2 Adult, 1 Child, 1 Infant OOS"
    And the passenger is in Checkin Status
    And I have initiated a change passenger age action from <From> to <To> for single flight
    When I process the request for change passenger age
    Then I will update the passenger age with the new value
    And I will update the passenger type with the new value
    And I will recalculate the fees and taxes like "UK APD - Band A"
    And I will update basket totals
    And I will change the passenger status to Booked
    And I do not change the passengers APIS status
    And I do not change the passengers ICTS status
    And I will return "SVC_100009_2002" message
    Examples:
      | channel   | From      | To       |
      | ADAirport | Adult     | Child-8  |
      | ADAirport | Child     | Adult-25 |
      | ADAirport | Infant OS | Adult-25 |
      | ADAirport | Infant OS | Child-8  |
      | Digital   | Adult     | Child-8  |
      | Digital   | Child     | Adult-25 |

  @Sprint27
  @FCPH-8458
  Scenario Outline: Change Age Request from Digital results in a booking being repriced - not checked in
    Given <channel> do the commit booking with "2 Adult, 1 Child, 1 Infant OOS"
    And I have initiated a change passenger age action from <From> to <To> for all flights
    When I process the request for change passenger age
    Then I will update the passenger age for all the flights with the new value
    And I will update the passenger type for all the flights with the new value
    And I will recalculate the fees and taxes like "UK APD - Band A"
    And I will update basket totals
    And I do not change the passenger status for all the flights
    And I do not change the passengers APIS status for all the flights
    And I do not change the passengers ICTS status for all the flights
    And I will return "SVC_100009_2002" message
  @ADTeam
    Examples:
      | channel   | From      | To       |
      | ADAirport | Adult     | Child-8  |
      | ADAirport | Child     | Adult-25 |
      | ADAirport | Infant OS | Adult-25 |
      | ADAirport | Infant OS | Child-8  |
    Examples:
      | channel | From  | To       |
      | Digital | Adult | Child-8  |
      | Digital | Child | Adult-25 |

  @manual
  @Sprint27
  @FCPH-8458
  Scenario Outline: Change Age Request from Digital results in a booking being repriced - checked in
    Given <channel> do the commit booking with "2 Adult, 1 Child, 1 Infant OOS"
    And the passenger is in Checkin Status
    And I have initiated a change passenger age action from <From> to <To> for all flights
    When I process the request for change passenger age
    Then I will update the passenger age for all the flights with the new value
    And I will update the passenger type for all the flights with the new value
    And I will recalculate the fees and taxes like "UK APD - Band A"
    And I will update basket totals
    And I will change the passenger status to Booked
    And I do not change the passengers APIS status for all the flights
    And I do not change the passengers ICTS status for all the flights
    And I will return "SVC_100009_2002" message
    Examples:
      | channel   | From      | To       |
      | ADAirport | Adult     | Child-8  |
      | ADAirport | Child     | Adult-25 |
      | ADAirport | Infant OS | Adult-25 |
      | ADAirport | Infant OS | Child-8  |
      | Digital   | Adult     | Child-8  |
      | Digital   | Child     | Adult-25 |

  @Sprint27 @Sprint28
  @FCPH-8456
  Scenario Outline: Passenger Age Change results in changing a passenger to an infant on lap - single flight - not checked in
    Given <channel> do the commit booking with holditems for "2 Adult, 1 Child, 1 Infant OOS"
    And I have initiated a change passenger age action from <From> to <To> for single flight
    When I process the request for change passenger age
    Then I will remove the bundle associated to the passenger
    And I will remove any products with tax and fees associated to the passenger
    And I will add an infant on lap product to the basket
    And I will assign the infant on lap to first Adult
    And I will update basket totals
    And I do not change the passenger status
    And I do not change the passengers APIS status
    And I do not change the passengers ICTS status
    And I will return "SVC_100009_2002" message
    Examples:
      | channel   | From  | To             |
      | ADAirport | Child | InfantOnLap -0 |
      | ADAirport | Adult | InfantOnLap -0 |
      | Digital   | Child | InfantOnLap -0 |
      | Digital   | Adult | InfantOnLap -0 |

  @manual
  @Sprint27 @Sprint28
  @FCPH-8456
  Scenario Outline: Passenger Age Change results in changing a passenger to an infant on lap - single flight - checked in
    Given <channel> do the commit booking with holditems for "2 Adult, 1 Child, 1 Infant OOS"
    And the passenger is in Checkin Status
    And I have initiated a change passenger age action from <From> to <To> for single flight
    When I process the request for change passenger age
    Then I will remove the bundle associated to the passenger
    And I will remove any products with tax and fees associated to the passenger
    And I will add an infant on lap product to the basket
    And I will assign the infant on lap to first Adult
    And I will update basket totals
    And I will change the passenger status to Booked
    And I do not change the passengers APIS status
    And I do not change the passengers ICTS status
    And I will return "SVC_100009_2002" message
    Examples:
      | channel   | From  | To             |
      | ADAirport | Child | InfantOnLap -0 |
      | ADAirport | Adult | InfantOnLap -0 |
      | Digital   | Child | InfantOnLap -0 |
      | Digital   | Adult | InfantOnLap -0 |

  @Sprint27 @Sprint28
  @FCPH-8456
  Scenario Outline: Passenger Age Change results in changing a passenger to an infant on lap - multiple flight - not checked in
    Given <channel> do the commit booking with holditems for "2 Adult, 1 Child, 1 Infant OOS"
    And I have initiated a change passenger age action from <From> to <To> for all flights
    When I process the request for change passenger age
    Then I will remove the bundle associated to the passenger for all flights
    And I will remove any products with tax and fees associated to the passenger for all flights
    And I will add an infant on lap product to the basket for all flights
    And I will assign the infant on lap to first Adult for all flights
    And I will update basket totals
    And I do not change the passenger status for all the flights
    And I do not change the passengers APIS status for all the flights
    And I do not change the passengers ICTS status for all the flights
    And I will return "SVC_100009_2002" message
  @ADTeam
    Examples:
      | channel   | From  | To             |
      | ADAirport | Child | InfantOnLap -0 |
      | ADAirport | Adult | InfantOnLap -0 |
    Examples:
      | channel | From  | To             |
      | Digital | Child | InfantOnLap -0 |
      | Digital | Adult | InfantOnLap -0 |

  @manual
  @Sprint27 @Sprint28
  @FCPH-8456
  Scenario Outline: Passenger Age Change results in changing a passenger to an infant on lap - multiple flight - checked in
    Given <channel> do the commit booking with holditems for "2 Adult, 1 Child, 1 Infant OOS"
    And the passenger is in Checkin Status
    And I have initiated a change passenger age action from <From> to <To> for all flights
    When I process the request for change passenger age
    Then I will remove the bundle associated to the passenger for all flights
    And I will remove any products with tax and fees associated to the passenger for all flights
    And I will add an infant on lap product to the basket for all flights
    And I will assign the infant on lap to first Adult for all flights
    And I will update basket totals
    And I will change the passenger status to Booked for all the flights
    And I do not change the passengers APIS status for all the flights
    And I do not change the passengers ICTS status for all the flights
    And I will return "SVC_100009_2002" message
    Examples:
      | channel   | From  | To             |
      | ADAirport | Child | InfantOnLap -0 |
      | ADAirport | Adult | InfantOnLap -0 |
      | Digital   | Child | InfantOnLap -0 |
      | Digital   | Adult | InfantOnLap -0 |

  @Sprint27 @Sprint28
  @FCPH-8456
  Scenario Outline: Change Passenger Age results in a passenger becoming an infant on lap and there is no Adult without an infant on lap on booking
    Given <channel> do the commit booking with "<passengerMix>"
    And I have initiated a change passenger age action from <From> to <To> for all flights
    When I process this request for change passenger age
    Then I will return an <Error> that Adult to infant on lap ratio is violated
    Examples:
      | channel   | passengerMix                  | From  | To             | Error           |
      | ADAirport | 1 Adult, 1 Child, 1 Infant OL | Child | InfantOnLap -0 | SVC_100273_3014 |
      | ADAirport | 2 Adult, 1 Child, 1 Infant OL | Adult | InfantOnLap -0 | SVC_100273_3014 |
      | Digital   | 1 Adult, 1 Child, 1 Infant OL | Child | InfantOnLap -0 | SVC_100273_3014 |
      | Digital   | 2 Adult, 1 Child, 1 Infant OL | Adult | InfantOnLap -0 | SVC_100273_3014 |