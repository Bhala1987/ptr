@Sprint30 @FCPH-10472 @TeamC

Feature: Change associated Adult for the infant on own seat


  Scenario Outline: Generate Error if number of infant on own seat exceed the number of adults BR_01800
    Given I am using the channel <channel>
    When I send a request to associate adult to infant on seat with <passenger> and <fareType> fare type with valid parameter
    Then I will receive an error with code '<error>'
    Examples:
      | channel | passenger           | fareType | error           |
      | Digital | 2 adult; 6,4 infant | Standard | SVC_100785_2007 |


  Scenario Outline: Change infant on own seat association
    Given the channel <channel> is used
    When I send a request to associate adult to infant on seat with <passenger> and <fareType> fare type with valid parameter
    Then I will return an updated basket for infant on Seat
    Examples:
      | channel | passenger           | fareType |
      | Digital | 2 adult; 4,3 infant | Standard |

  Scenario Outline: Generate error if the requested passenger is not of passenger type adult
    Given I am using the channel <channel>
    When I send a request to associate adult to infant on seat with <passenger> and <fareType> fare type with child
    Then I will receive an error with code '<error>'
    Examples:
      | channel | passenger                    | fareType | error           |
      | Digital | 1 adult; 1 child; 2,2 infant | Standard | SVC_100785_2004 |


  Scenario Outline: Generate error if the requested passenger is not known
    Given I am using the channel <channel>
    When I send a request to associate adult to infant on seat with <passenger> and <fareType> fare type with <invalid>
    Then I will receive an error with code '<error>'
    Examples:
      | channel | passenger           | fareType | error           | invalid             |
      | Digital | 2 adult; 4,3 infant | Standard | SVC_100013_1001 | invalid basketId    |
      | Digital | 2 adult; 4,3 infant | Standard | SVC_100785_2001 | invalid passengerId |
      | Digital | 2 adult; 4,3 infant | Standard | SVC_100785_2002 | invalid infantId    |

