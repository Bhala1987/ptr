Feature: Change associated adult to infant on lap after comitted booking

  @Sprint27 @Sprint28
  @FCPH-7341 @FCPH-10109
  Scenario Outline:  Move Infant on Lap to another Adult on the booking
    Given the channel <channel> is used
    And I created an amendable basket with <fareType> fare for <passenger>
    When I send a request to associate infant to another Adult
    Then  will return an updated basket and check the total with <fareType>
    Examples:
      | channel   | passenger           | fareType |
      | ADAirport | 2 adult; 1,0 infant | Standard |
      | Digital   | 3 adult; 1,0 infant | Standard |

  @manual
  @Sprint27 @Sprint28
  @FCPH-7341 @FCPH-10109
  Scenario Outline:  Move Infant on Lap to another Adult on the booking check status
    Given the channel <channel> is used
    And I created an amendable basket with <fareType> fare for <passenger>
    When I send a request to associate infant to another Adult
    Then I will check that the Adults Passenger Status is set to ‘Booked’
    Examples:
      | channel | passenger           | fareType |
      | Digital | 2 adult; 1,0 infant | Standard |

  @Sprint27
  @FCPH-8723
  Scenario Outline: Receive 'Move Infant on Lap' request in an incorrect format
    Given the channel <channel> is used
    And I created an amendable basket with <fareType> fare for <passenger>
    When I send a request wrong to associate infant to another Adult with <invalid> invalid
    Then I will receive an error with code '<error>'
    Examples:
      | channel   | passenger           | fareType | invalid             | error           |
      | Digital   | 2 adult; 1,0 infant | Standard | invalid basketId    | SVC_100785_2000 |
      | ADAirport | 2 adult; 1,0 infant | Standard | invalid passengerId | SVC_100785_2001 |
      | Digital   | 2 adult; 1,0 infant | Standard | invalid infantId    | SVC_100785_2002 |
      | ADAirport | 2 adult; 1,0 infant | Standard | associate adult     | SVC_100785_2003 |

  @Sprint27
  @FCPH-8723
  Scenario Outline: Receive 'Move Infant on Lap' request where new adult passenger is not an adult
    Given the channel <channel> is used
    And I created an amendable basket with <fareType> fare for <passenger>
    When I send a request to associate infant to another Infant
    Then I will receive an error with code '<error>'
    Examples:
      | channel | passenger           | fareType | error           |
      | Digital | 1 adult; 2,1 infant | Standard | SVC_100785_2004 |

  @Sprint27
  @FCPH-8723
  Scenario Outline: Receive 'Move Infant on Lap' request where adult infant ratio is exceeded BR_00040
    Given the channel <channel> is used
    And I created an amendable basket with <fareType> fare for <passenger>
    When I send a request to associate infant to another Adult that have one infant
    Then I will receive an error with code '<error>'
    Examples:
      | channel | passenger           | fareType | error           |
      | Digital | 2 adult; 2,0 infant | Standard | SVC_100785_2005 |


  @TeamC @Sprint29 @FCPH-9170
  Scenario Outline: Move Infant on lap - new adult has a purchased seat and seat is suitable
    Given I am using <channel> channel
    And I have amendable basket for <fareType> fare and <passenger> passenger
    When I send a request to associate infant to another Adult with seat
    Then the basket should be updated with the new association
    Examples:
      | channel | passenger            | fareType |
      | Digital | 2 adult, 1 infant OL | Standard |

  @TeamC @Sprint29 @FCPH-9170
  Scenario Outline: Move Infant on lap - new adult has a purchased seat, seat is no longer suitable with emergency seat
    Given the channel <channel> is used
    And I have an amendable basket with <fareType> fare for <passenger> where first adult associated a emergency exit seat and the second adult associated the infant
    When I send a request to associate infant to another Adult with different seat
    Then I will receive an error with code '<error>'
    And I will not change the association
    Examples:
      | channel   | passenger            | fareType | error           |
      | ADAirport | 2 adult, 1 infant OL | Standard | SVC_100600_1012 |

  @Sprint32 @TeamC @FCPH-10589
  Scenario Outline:  Move Infant on Lap to another Adult on the update booking
    Given I am using <channel> channel
    And I have amendable basket for <fareType> fare and <passenger> passenger
    And I send a request to associate infant to another Adult
    And the basket should be updated with the new association
    When I commit booking
    Then I should be able to see booking is successful
    And the booking should be updated with the new association
    Examples:
      | channel | passenger            | fareType |
      | Digital | 2 adult, 1 infant OL | Standard |