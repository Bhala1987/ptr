@FCPH-7402
Feature:Add Flight with an Additional Seat to the basket

  @negative
  Scenario Outline: Channel is allowed to purchase additional seat BR_01542
    Given I sent a request to FindFlight to "<channel>"
    When I sent a request to AddFlight with additional seat to "<channel>"
    Then I will generate a error message to inform the channel "<code>"
    Examples:
      | channel | code            |
      | Digital | SVC_100012_2019 |

  Scenario Outline: Allocate Inventory for AD requests BR_00950, BR_00960
    Given I sent a request to FindFlight to "<channel>"
    When I sent a request to AddFlight with additional seat to "<channel>"
    Then I will request allocation for the Passenger including the additional seat
    Examples:
      | channel   |
      | ADAirport |

  @regression
  Scenario Outline: Add additional seat to basket
    Given I sent a request to FindFlight to "<channel>"
    When I sent a request to AddFlight with additional seat to "<channel>"
    Then I will return an updated basket
    And I will associate the additional seat with the respective passenger
    And I will recalculate passenger totals, flight totals and basket totals
    Examples:
      | channel   |
      | ADAirport |

  @Sprint24 @FCPH-8051 @regression
  Scenario Outline: Return Add Additional Seat Response to Channel - Successful
    Given I have found a valid flight and added to basket for <passengerMix> for <channel>
    When I send addAdditionalFareToPassenger request for additional fares of passengers <additionalSeatMix> for <channel>
    Then I will return success response and an updated basket with additional fares added
    Examples:
      | channel           | passengerMix                   | additionalSeatMix    |
      | ADCustomerService | 4 adult, 1 child, 2 infant OOS | 2 adult 2, 1 child 5 |
      | ADAirport         | 2 adult, 1 infant OL           | 1 adult 1            |

  @Sprint24 @FCPH-8051
  Scenario Outline: Return Add Additional Seat Response to Channel - Failure
    Given I have found a valid flight and added to basket for <passengerMix> for <channel>
    When I send addAdditionalFareToPassenger request for additional fares of passengers <additionalSeatMix> for <channel>
    Then I will return the "<errorCode>" in the addAdditionalFareToPassenger service response
    Examples:
      | channel         | passengerMix                   | additionalSeatMix    | errorCode       |
      | Digital         | 2 adult, 1 infant OL           | 1 adult 3            | SVC_100389_2007 |
      | PublicApiB2B    | 4 adult, 1 child, 2 infant OOS | 2 adult 2, 1 child 5 | SVC_100389_2007 |
      | PublicApiMobile | 4 adult, 2 infant OOS          | 2 adult 1, 1 adult 3 | SVC_100389_2007 |

  @Sprint24 @FCPH-8051
  Scenario Outline: Generate error if the basket id or passenger id or zero fare or passenger type or request body is invalid
    Given I have found a valid flight and added to basket for <passengerMix> for <channel>
    When I send the addAdditionalFareToPassenger request for additional fares of passengers <additionalSeatMix> for <channel> and invalid <parameter>
    Then I will return the "<errorCode>" in the addAdditionalFareToPassenger service response
    Examples:
      | channel           | passengerMix                   | additionalSeatMix    | parameter     | errorCode       |
      | ADAirport         | 1 adult                        | 1 adult 1            | zeroFare      | SVC_100389_2008 |
      | ADAirport         | 4 adult, 1 child, 2 infant OOS | 2 adult 2, 1 child 5 | basketId      | SVC_100389_2001 |
      | ADCustomerService | 1 adult, 1 infant OOS          | 1 adult 2            | passengerType | SVC_100389_2009 |
      | ADAirport         | 3 adult, 2 child, 1 infant OOS | 1 adult 2, 2 child 4 | passengerId   | SVC_100389_2002 |

  @Sprint24 @FCPH-8051
  Scenario Outline: Return Add Additional Seat Response to Channel - Successful with Hold Bag / Sports Equipment
    Given I have added the flight with passengers as "<passengerMix>" and from the channel "<channel>" to the basket
    And I have received a valid addHoldBagProduct request for the channel "<channel>"
    And I validate the addHoldBagProduct request
    And I have received a valid addSportEquipment request for the channel "<channel>"
    And I valid the request to addSportsEquipment
    And the flight is added to basket along with the hold items for the channel <channel>
    When I send addAdditionalFareToPassenger request for additional fares of passengers <additionalSeatMix> for <channel>
    Then I will return success response and an updated basket with additional fares added
    Examples:
      | channel           | passengerMix | additionalSeatMix |
      | ADAirport         | 1 adult      | 1 adult 1         |
      | ADCustomerService | 2 adult      | 2 adult 2         |


  @Sprint27 @FCPH-8924
  Scenario Outline: Generate Error message if max number of additional seats has been reached
    Given I am using the channel ADAirport
    And I login as agent with username as "<username>" and password as "<password>"
    And I add the flight to my basket
    When I add 4 additonal seats that exceeds allowed for a passenger
    Then response returns "<errorCode>"
    Examples:
      | username | password | errorCode       |
      | rachel   | 12341234 | SVC_100389_2010 |

  @Sprint27 @FCPH-8924 @defect:FCPH-10864
  Scenario Outline: Generate error mesage when the passenger reached the max number of additional seats allowed
    Given I am using the channel ADAirport
    And I login as agent with username as "<username>" and password as "<password>"
    And I add the flight to my basket
    Then I add 3 additonal seats that exceeds allowed for a passenger
    When I attempt to add another seat for the passenger
    Then response returns "<errorCode>"
    Examples:
      | username | password | errorCode       |
      | rachel   | 12341234 | SVC_100389_2010 |

