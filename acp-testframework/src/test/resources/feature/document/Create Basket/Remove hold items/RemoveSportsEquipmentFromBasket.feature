@FCPH-7705
Feature: Remove Sports equipment from Basket

  Scenario Outline: Invalid request to remove sports equipment
    Given I am using the channel <channel>
    And I searched a flight for '<passengerMix>' with stock level for '<quantity>' '<product>'
    And I added it to the basket with '<fareType>' fare as '<journeyType>' journey
    And I added a sports equipment to first passenger
    And I want to remove the sport equipment from the basket
    But the request contains '<invalidValue>' for removeSportsEquipment()
    When I send a request to removeSportsEquipment()
    Then I will receive an error with code '<error>'
    Examples: Invalid request for ADAirport
      | channel   | passengerMix | quantity | product            | fareType | journeyType | invalidValue    | error            |
      | ADAirport | 1 adult      | 1        | LargeSportsProduct | Standard | single      | invalid basket  | SVC_100291_20010 |
#      | ADAirport | 1 adult      | 1        | LargeSportsProduct | Standard | single      | invalid product             | SVC_100291_2003  |
#      | ADAirport | 1 adult      | 1        | LargeSportsProduct | Standard | single      | invalid passenger           | SVC_100291_2004  |
#      | ADAirport | 1 adult      | 1        | LargeSportsProduct | Standard | single      | invalid flightKey           | SVC_100291_2005  |
#      | ADAirport | 1 adult      | 1        | LargeSportsProduct | Standard | single      | passenger not in the basket | SVC_100291_2006  |
#      | ADAirport | 1 adult      | 1        | LargeSportsProduct | Standard | single      | flightKey not in the basket | SVC_100291_2007  |
      | ADAirport | 2 adult      | 1        | LargeSportsProduct | Standard | single      | wrong passenger | SVC_100291_20012 |
    Examples: Invalid request for Digital
      | channel           | passengerMix | quantity | product            | fareType | journeyType | invalidValue                | error           |
#      | ADCustomerService | 1 adult      | 1        | LargeSportsProduct | Standard | single      | invalid basket              | SVC_100291_20010 |
      | ADCustomerService | 1 adult      | 1        | LargeSportsProduct | Standard | single      | invalid product             | SVC_100291_2003 |
#      | ADCustomerService | 1 adult      | 1        | LargeSportsProduct | Standard | single      | invalid passenger           | SVC_100291_2004  |
#      | ADCustomerService | 1 adult      | 1        | LargeSportsProduct | Standard | single      | invalid flightKey           | SVC_100291_2005  |
#      | ADCustomerService | 1 adult      | 1        | LargeSportsProduct | Standard | single      | passenger not in the basket | SVC_100291_2006  |
      | ADCustomerService | 1 adult      | 1        | LargeSportsProduct | Standard | single      | flightKey not in the basket | SVC_100291_2007 |
#      | ADCustomerService | 2 adult      | 1        | LargeSportsProduct | Standard | single      | wrong passenger             | SVC_100291_20012 |
    Examples: Invalid request for Digital
      | channel | passengerMix | quantity | product            | fareType | journeyType | invalidValue                | error           |
#      | Digital | 1 adult      | 1        | LargeSportsProduct | Standard | single      | invalid basket              | SVC_100291_20010 |
#      | Digital | 1 adult      | 1        | LargeSportsProduct | Standard | single      | invalid product             | SVC_100291_2003  |
      | Digital | 1 adult      | 1        | LargeSportsProduct | Standard | single      | invalid passenger           | SVC_100291_2004 |
#      | Digital | 1 adult      | 1        | LargeSportsProduct | Standard | single      | invalid flightKey           | SVC_100291_2005  |
      | Digital | 1 adult      | 1        | LargeSportsProduct | Standard | single      | passenger not in the basket | SVC_100291_2006 |
#      | Digital | 1 adult      | 1        | LargeSportsProduct | Standard | single      | flightKey not in the basket | SVC_100291_2007  |
#      | Digital | 2 adult      | 1        | LargeSportsProduct | Standard | single      | wrong passenger             | SVC_100291_20012 |
    Examples: Invalid request for Digital
      | channel      | passengerMix | quantity | product            | fareType | journeyType | invalidValue      | error            |
#      | PublicApiB2B | 1 adult      | 1        | LargeSportsProduct | Standard | single      | invalid basket              | SVC_100291_20010 |
#      | PublicApiB2B | 1 adult      | 1        | LargeSportsProduct | Standard | single      | invalid product             | SVC_100291_2003  |
#      | PublicApiB2B | 1 adult      | 1        | LargeSportsProduct | Standard | single      | invalid passenger           | SVC_100291_2004  |
      | PublicApiB2B | 1 adult      | 1        | LargeSportsProduct | Standard | single      | invalid flightKey | SVC_100291_2005  |
#      | PublicApiB2B | 1 adult      | 1        | LargeSportsProduct | Standard | single      | passenger not in the basket | SVC_100291_2006  |
#      | PublicApiB2B | 1 adult      | 1        | LargeSportsProduct | Standard | single      | flightKey not in the basket | SVC_100291_2007  |
      | PublicApiB2B | 2 adult      | 1        | LargeSportsProduct | Standard | single      | wrong passenger   | SVC_100291_20012 |
    Examples: Invalid request for Digital
      | channel         | passengerMix | quantity | product            | fareType | journeyType | invalidValue                | error           |
#      | PublicApiMobile | 1 adult      | 1        | LargeSportsProduct | Standard | single      | invalid basket              | SVC_100291_20010 |
#      | PublicApiMobile | 1 adult      | 1        | LargeSportsProduct | Standard | single      | invalid product             | SVC_100291_2003  |
#      | PublicApiMobile | 1 adult      | 1        | LargeSportsProduct | Standard | single      | invalid passenger           | SVC_100291_2004  |
#      | PublicApiMobile | 1 adult      | 1        | LargeSportsProduct | Standard | single      | invalid flightKey           | SVC_100291_2005  |
      | PublicApiMobile | 1 adult      | 1        | LargeSportsProduct | Standard | single      | passenger not in the basket | SVC_100291_2006 |
      | PublicApiMobile | 1 adult      | 1        | LargeSportsProduct | Standard | single      | flightKey not in the basket | SVC_100291_2007 |
#      | PublicApiMobile | 2 adult      | 1        | LargeSportsProduct | Standard | single      | wrong passenger             | SVC_100291_20012 |

  #There's no sport equipment that is part of a bundle
  @manual
  Scenario Outline: Invalid request to remove hold item associated with a bundle
    Given I am using the channel <channel>
    And I searched a flight for '<passengerMix>' with stock level for '<quantity>' '<product>'
    And I added it to the basket with '<fareType>' fare as '<journeyType>' journey
    And I want to remove the sport equipment from the basket
    But the request contains '<invalidValue>' for removeSportsEquipment()
    When I send a request to removeSportsEquipment()
    Then I will receive an error with code '<error>'
    Examples:
      | channel           | passengerMix | quantity | product            | fareType | journeyType | invalidValue                   | error           |
      | ADCustomerService | 1 adult      | 1        | LargeSportsProduct | Flexi    | single      | product associated with bundle | SVC_100291_2009 |
      | Digital           | 1 adult      | 1        | LargeSportsProduct | Flexi    | single      | product associated with bundle | SVC_100291_2009 |
      | PublicApiB2B      | 1 adult      | 1        | LargeSportsProduct | Flexi    | single      | product associated with bundle | SVC_100291_2009 |

  Scenario Outline: Remove hold item from the basket
    Given I am using the channel <channel>
    And I searched a flight for '<passengerMix>' with stock level for '<quantity>' '<product>'
    And I added it to the basket with '<fareType>' fare as '<journeyType>' journey
    And I added a sport equipment to all passengers
    And I want to remove the sport equipment from the basket for <remove>
    When I send a request to removeSportsEquipment()
    Then I will receive a confirmation for removeSportsEquipment()
    And the sport equipment has been removed from the basket for <remove>
    And total amount will be reduced by sport equipment price
    And inventory is <not> deallocated for removeSportsEquipment()
    Examples: Valid requests to remove sport equipment for first passenger in first flight
      | channel   | passengerMix | quantity | product            | fareType | journeyType | remove                          | not |
      | Digital   | 2 adult      | 2        | LargeSportsProduct | Standard | single      | all passenger in all flight     | not |
      | ADAirport | 2 adult      | 2        | LargeSportsProduct | Standard | single      | first passenger in first flight |     |
#      | ADCustomerService | 2 adult      | 2        | LargeSportsProduct | Standard | single      | all passenger in first flight   |     |
#      | ADCustomerService | 2 adult      | 2        | LargeSportsProduct | Standard | single      | first passenger in first flight |     |
#      | Digital           | 2 adult      | 2        | LargeSportsProduct | Standard | single      | first passenger in first flight | not |
#      | PublicApiB2B      | 2 adult      | 2        | LargeSportsProduct | Standard | single      | first passenger in first flight | not |
#      | PublicApiMobile   | 2 adult      | 2        | LargeSportsProduct | Standard | single      | first passenger in first flight | not |
    Examples: Valid requests to remove sport equipment for all passenger in first flight
      | channel   | passengerMix | quantity | product            | fareType | journeyType | remove                        | not |
      | ADAirport | 2 adult      | 2        | LargeSportsProduct | Standard | single      | all passenger in first flight |     |
      | Digital   | 2 adult      | 2        | LargeSportsProduct | Standard | single      | all passenger in first flight | not |
#      | PublicApiB2B    | 2 adult      | 2        | LargeSportsProduct | Standard | single      | all passenger in first flight | not |
#      | PublicApiMobile | 2 adult      | 2        | LargeSportsProduct | Standard | single      | all passenger in first flight | not |
    Examples: Valid requests to remove sport equipment for all passenger in all flight
      | channel         | passengerMix | quantity | product            | fareType | journeyType | remove                      | not |
      | ADAirport       | 2 adult      | 2        | LargeSportsProduct | Standard | single      | all passenger in all flight |     |
#      | ADCustomerService | 2 adult      | 2        | LargeSportsProduct | Standard | single      | all passenger in all flight |     |
#      | PublicApiB2B      | 2 adult      | 2        | LargeSportsProduct | Standard | single      | all passenger in all flight | not |
      | PublicApiMobile | 2 adult      | 2        | LargeSportsProduct | Standard | single      | all passenger in all flight | not |
