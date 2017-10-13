Feature: Validate Add Passenger Details Basic Request
  I want to be able to add passenger details in the basket
  So that I can see passenger information on the basket

  @FCPH-267 @FCPH-268 @FCPH-269 @negative
  Scenario Outline: Verify that ACP responds with the correct error code and message when the request lack one of the mandatory fields
    Given my basket contains "1 Adult"
    When I fail to provide the passenger field "<field>"
    Then I should see the "<error>" error message
    Examples:
      | field         | error           |
      | passengertype | SVC_100273_2003 |
      | title         | SVC_100273_2007 |
      | firstname     | SVC_100273_2005 |
      | lastname      | SVC_100273_2006 |
      | age           | SVC_100273_2008 |
      | code          | SVC_100273_2011 |

  @FCPH-267 @FCPH-268 @FCPH-269
  Scenario Outline: Verify that ACP respond with the correct error code and message when the request contains unmatching passenger type and title - BR_00876
    Given my basket contains "1 Adult"
    When I set the passenger type as "<type>" and the title as "<title>"
    Then I should see the "SVC_100012_3015" error message
    Examples:
      | type   | title  |
      | adult  | Infant |
      | child  | Mrs    |
      | child  | Ms     |
      | child  | Infant |
      | infant | Mr     |
      | infant | Mrs    |
      | infant | Miss   |
      | infant | Ms     |

  @FCPH-267 @FCPH-268 @FCPH-269
  Scenario Outline: Verify that ACP respond with the correct error code and message when the request contains unmatching passenger type and age
    Given my basket contains "1 Adult"
    When I set the passenger type as "<type>" and the age as "<age>"
    Then I should see an "<error>" <information> message
    Examples:
      | type   | age    | error           | information |
      | adult  | child  | SVC_100012_3016 | warning     |
      | child  | adult  | SVC_100012_3016 | warning     |
      | adult  | infant | SVC_100148_3013 | error       |
      | infant | child  | SVC_100012_3015 | error       |
      | infant | adult  | SVC_100012_3015 | error       |

  @FCPH-267 @FCPH-268 @FCPH-269
  Scenario Outline: Field validation errors
    Given my basket contains "1 Adult"
    When I update passenger "<field>" details as "<value>"
    Then I should see the "<errorCode>" error message
    Examples: Error when the request contains incorrect "first name" format
      | field     | value                                              | errorCode       |
      | firstname | x                                                  | SVC_100012_3022 |
      | firstname | Pneumono Ultra Microscopic Silico Volcano Coniosis | SVC_100012_3022 |
      | firstname | 123256                                             | SVC_100012_3022 |
      | firstname | *^(&£                                              | SVC_100012_3022 |
    Examples:Error code when the request contains incorrect "last name" format
      | field    | value                                                        | errorCode       |
      | lastname | a                                                            | SVC_100012_3023 |
      | lastname | Pneumono Ultra Microscopic Silico Volcano Coniosis Last Name | SVC_100012_3023 |
      | lastname | 97845                                                        | SVC_100012_3023 |
      | lastname | @-+/=                                                        | SVC_100012_3023 |
    Examples:Error code when the request contains incorrect "email" format
      | field | value         | errorCode       |
      | email | john.smith    | SVC_100012_3028 |
      | email | john@         | SVC_100012_3028 |
      | email | johnyahoo.com | SVC_100012_3028 |
      | email | john@yahoo    | SVC_100012_3028 |
    Examples:Error code when the request contains incorrect "phone number" format
      | field       | value               | errorCode       |
      | phoneNumber | 44                  | SVC_100273_3001 |
      | phoneNumber | 4407541236528545126 | SVC_100273_3001 |
      | phoneNumber | +440754123652854    | SVC_100273_3001 |
      | phoneNumber | ghsjakgfjk          | SVC_100273_3001 |
      | phoneNumber | *^(&£ @-+/=         | SVC_100273_3001 |

  @FCPH-267 @FCPH-268 @FCPH-269
  Scenario Outline: ACP to validate the title when passenger type changes on the fly because of age
    Given my basket contains "<passenger_mix>"
    When I set the passenger age as "<age>" and the title as "<title>"
    Then I should see the "<errorCode>" error message
    Examples:
      | passenger_mix | age    | title  | errorCode       |
      | 1 adult       | child  | infant | SVC_100012_3015 |
      | 1 child       | adult  | Infant | SVC_100012_3015 |
      | 1 adult       | infant | mr     | SVC_100148_3013 |
      | 1 child       | Infant | mr     | SVC_100148_3013 |

  @FCPH-8370 @Sprint25
  Scenario Outline: Generate Error if number of infant on own lap exceed the number of adults BR_00040
    Given my basket contains "<passengerMix>" using channel "<channel>"
    And I receive an update passenger request that changes an Adult passenger to an infant with ratio that exceeds the threshold
    When I validate the request to updatePassenger
    Then I should see the "<error>" error message
    And I will not update the passenger details
    Examples:
      | channel           | passengerMix         | error           |
      | ADCustomerService | 3 Adult              | SVC_100148_3006 |
      | PublicApiMobile   | 3 Adult              | SVC_100148_3006 |
      | ADAirport         | 3 Adult, 2 Infant OL | SVC_100148_3006 |
      | Digital           | 5 Adult, 4 Infant OL | SVC_100148_3006 |
      | ADCustomerService | 2 Adult, 1 Infant OL | SVC_100148_3006 |
#      | ADCustomerService | 1 Adult              | SVC_100148_3013 |
#      | PublicApiMobile   | 1 Adult              | SVC_100148_3013 |
      | Digital           | 1 Adult              | SVC_100148_3013 |
      | ADAirport         | 1 Adult              | SVC_100148_3013 |

  @FCPH-8370 @Sprint25
  Scenario Outline: Generate Error if the add flight request is for staff or standby bundle type but the passenger mix is only children and infant BR_00031
    Given I am a staff customer logged in with credential "a.rossi@reply.co.uk" and "1234"
    And my basket contains "1 Adult" with fare type "<fare-type>" using channel "<channel>"
    And I receive an update passenger request that changes all passenger to a "<passenger-type>" for the required fare
    When I validate the request to updatePassenger
    Then I should see the "<error>" error message
    And I will not update the passenger details
    Examples:
      | channel           | fare-type | passenger-type | error           |
      | ADCustomerService | Staff     | child          | SVC_100273_3025 |
      | ADAirport         | Staff     | infant         | SVC_100148_3013 |
      | Digital           | Staff     | infant         | SVC_100148_3013 |
      | Digital           | Staff     | child          | SVC_100273_3025 |

  @FCPH-8370 @Sprint25
  @BR:BR_00030
  Scenario Outline: Generate a warning message if child is travelling alone
    Given my basket contains "1 Adult" using channel "<channel>"
    And I receive an update passenger request that changes all passenger to a "child"
    When I validate the request to updatePassenger
    Then I should see the "SVC_100148_3008" warning message
    And I will update the passenger details
    Examples:
      | channel   |
      | ADAirport |
      | Digital   |

  @FCPH-8714 @Sprint26
  Scenario Outline: The purchased seat not change for passenger type change Adult and Child
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat STANDARD
    And my basket contains "<passenger>" using channel "<channel>"
    And I make a request to add an available "STANDARD" seat for all passenger
    And I receive a request to change passenger from "<passengerFromChange>" to a "<passengerChanged>"
    When I validate the request to updatePassenger
    And I will update the passenger details
    Then The passenger have the same seat as before
    Examples:
      | channel           | passenger        | passengerFromChange | passengerChanged |
      | ADAirport         | 2 adult          | adult               | child            |
      | ADCustomerService | 1 adult; 1 child | child               | adult            |

  @Sprint29 @FCPH-9518 @TeamC
  Scenario Outline: update Seating Service for an adult passenger when the associated infant on lap has been removed based on a changing the passenger's age
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat <seat>
    When I send a request to addSeat and change Age with <passenger> with <seat> and <fareType> fare type and <passengerFromChange> to <passengerChanged> with <additionalSeat> additional Seat
    Then I will check that the new passenger don't have seat
    Examples:
      | channel   | passenger             | fareType | passengerFromChange | passengerChanged | seat     | additionalSeat |
      | ADAirport | 1,2 adult; 1,0 infant | Standard | infant              | child            | STANDARD | 2              |
      | ADAirport | 1,2 adult; 1,0 infant | Standard | infant              | adult            | STANDARD | 2              |


