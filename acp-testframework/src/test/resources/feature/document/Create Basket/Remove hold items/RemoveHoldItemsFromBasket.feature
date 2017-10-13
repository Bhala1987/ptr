@FCPH-3978
Feature: Remove Hold Items from Basket

  Scenario Outline: Invalid request to remove hold item
    Given I am using the channel <channel>
    And I searched a flight for '<passengerMix>' with stock level for '<quantity>' '<product>'
    And I added it to the basket with '<fareType>' fare as '<journeyType>' journey
    And I added an hold bag to first passenger
    And I want to remove the hold bag from the basket
    But the request contains '<invalidValue>'
    When I send a request to removeHoldBagProduct()
    Then I will receive an error with code '<error>'
    Examples: Invalid request for Digital
      | channel | passengerMix | quantity | product        | fareType | journeyType | invalidValue                | error           |
      | Digital | 1 adult      | 1        | HoldBagProduct | Standard | single      | invalid basket              | SVC_100289_2010 |
      | Digital | 1 adult      | 1        | HoldBagProduct | Standard | single      | invalid product             | SVC_100289_2003 |
      | Digital | 1 adult      | 1        | HoldBagProduct | Standard | single      | invalid passenger           | SVC_100289_2004 |
      | Digital | 1 adult      | 1        | HoldBagProduct | Standard | single      | invalid flightKey           | SVC_100289_2005 |
      | Digital | 1 adult      | 1        | HoldBagProduct | Standard | single      | passenger not in the basket | SVC_100289_2006 |
      | Digital | 1 adult      | 1        | HoldBagProduct | Standard | single      | flightKey not in the basket | SVC_100289_2007 |
      | Digital | 2 adult      | 1        | HoldBagProduct | Standard | single      | wrong passenger             | SVC_100289_2016 |

  Scenario Outline: Invalid request to remove hold item associated with a bundle
    Given I am using the channel <channel>
    And I searched a flight for '<passengerMix>' with stock level for '<quantity>' '<product>'
    And I added it to the basket with '<fareType>' fare as '<journeyType>' journey
    And I want to remove the hold bag from the basket
    But the request contains '<invalidValue>'
    When I send a request to removeHoldBagProduct()
    Then I will receive an error with code '<error>'
    Examples:
      | channel | passengerMix | quantity | product        | fareType | journeyType | invalidValue                   | error           |
      | Digital | 1 adult      | 1        | HoldBagProduct | Flexi    | single      | product associated with bundle | SVC_100289_2009 |

  @local
  Scenario Outline: Remove hold item from the basket
    Given I am using the channel <channel>
    And I searched a flight for '<passengerMix>' with stock level for '<quantity>' '<product>'
    And I added it to the basket with '<fareType>' fare as '<journeyType>' journey
    And I added an hold bag to all passengers
    And I want to remove the hold bag from the basket for <remove>
    When I send a request to removeHoldBagProduct()
    Then I will receive a confirmation
    And the hold bag has been removed from the basket for <remove>
    And total amount will be reduced by hold bag price
    And inventory is <not> deallocated
    Examples:
      | channel | passengerMix | quantity | product        | fareType | journeyType | remove                          | not |
      | Digital | 2 adult      | 2        | HoldBagProduct | Standard | single      | first passenger in first flight | not |
      | Digital | 2 adult      | 2        | HoldBagProduct | Standard | single      | all passenger in first flight   | not |
      | Digital | 2 adult      | 2        | HoldBagProduct | Standard | single      | all passenger in all flight     | not |
#   Tagged @local as this was causing a MySQL lock in ACP due to DataModel

  Scenario Outline: Invalid request to remove hold item with excess weight
    Given I am using the channel <channel>
    And I searched a flight for '<passengerMix>' with stock level for '<quantity>' '<product>'
    And I added it to the basket with '<fareType>' fare as '<journeyType>' journey
    And I added an hold bag to all passengers with excess weight
    And I want to remove the hold bag with excess weight from the basket for <remove>
    But the request contains '<invalidValue>'
    When I send a request to removeHoldBagProduct()
    Then I will receive an error with code '<error>'
    Examples: Invalid request for Digital
      | channel | passengerMix | quantity | product        | fareType | journeyType | remove                          | invalidValue                            | error           |
      | Digital | 2 adult      | 2        | HoldBagProduct | Standard | single      | first passenger in first flight | invalid excess weight product           | SVC_100289_2011 |
      | Digital | 2 adult      | 2        | HoldBagProduct | Standard | single      | first passenger in first flight | excess weight product not in the basket | SVC_100289_2012 |
      | Digital | 2 adult      | 2        | HoldBagProduct | Standard | single      | first passenger in first flight | invalid parameters       | SVC_100289_2019 |
    Examples:
      | channel | passengerMix | quantity | product        | fareType | journeyType | remove                          | invalidValue                            | error           |
      | Digital | 2 adult      | 2        | HoldBagProduct | Standard | single      | first passenger in first flight | invalid excess weight product quantity  | SVC_100289_2012 |

  Scenario Outline: Remove hold item from the basket with excess weight
    Given I am using the channel <channel>
    And I searched a flight for '<passengerMix>' with stock level for '<quantity>' '<product>'
    And I added it to the basket with '<fareType>' fare as '<journeyType>' journey
    And I added an hold bag to all passengers with '<EWquantity>' '<EWproduct>' excess weight
    And I want to remove the hold bag with excess weight from the basket for <remove>
    When I send a request to removeHoldBagProduct()
    Then I will receive a confirmation
    And the hold bag with excess weight has been removed from the basket for <remove>
    And total amount will be reduced by hold bag price
    And inventory is <not> deallocated
    Examples:
      | channel | passengerMix | quantity | product        | fareType | journeyType | EWquantity | EWproduct      | remove                          | not |
#      | Digital | 2 adult      | 2        | HoldBagProduct | Standard | single      | 1          | 3kgextraweight | first passenger in first flight | not |
#      | Digital | 2 adult      | 2        | HoldBagProduct | Standard | single      | 1          | 3kgextraweight | all passenger in first flight   | not |
      | Digital | 2 adult      | 2        | HoldBagProduct | Standard | single      | 1          | 3kgextraweight | all passenger in all flight     | not |
#      | Digital | 2 adult      | 2        | HoldBagProduct | Standard | single      | 1          | 3kgextraweight | only excess weight for specific product    | not |
