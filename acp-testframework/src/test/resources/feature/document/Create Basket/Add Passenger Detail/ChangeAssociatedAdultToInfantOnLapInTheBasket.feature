Feature: Change associated adult to infant on lap in the basket

  @Sprint26 @FCPH-8704 @Sprint25 @FCPH-8703
  Scenario Outline: Generate Error if number of infant on own lap exceed the number of adults BR_00040
    Given I am using the channel <channel>
    And I searched a flight for <passenger>
    And I added it to the basket with <fareType> fare as <journey> journey
    And I want to associate another to infant on lap that have one infant
    When I send a request to associate adult to infant on lap
    Then I will receive an error with code '<error>'
    And I will not change the association
  @ADTeam
    Examples:
      | channel   | passenger           | journey  | fareType | error           |
      | ADAirport | 2 adult; 2,0 infant | outbound | Standard | SVC_100785_2005 |
    Examples:
      | channel         | passenger           | journey  | fareType | error           |
      | PublicApiMobile | 2 adult; 2,0 infant | outbound | Standard | SVC_100785_2005 |

  @Sprint25 @FCPH-8703
  Scenario Outline: Change infant on lap association
    Given I am using the channel <channel>
    And I searched a flight for <passenger>
    And I added it to the basket with <fareType> fare as <journey> journey
    And I want to associate <passengerAssociated> to infant on lap
    When I send a request to associate adult to infant on lap
    Then I will return an updated basket for infant
    Examples:
      | channel | passenger           | journey  | fareType | passengerAssociated |
      | Digital | 2 adult; 1,0 infant | outbound | Standard | adult               |

  @Sprint25 @FCPH-8703
  Scenario Outline: Generate error if the requested passenger is not of passenger type adult
    Given I am using the channel <channel>
    And I searched a flight for <passenger>
    And I added it to the basket with <fareType> fare as <journey> journey
    And I want to associate <passengerAssociated> to infant on lap
    When I send a request to associate adult to infant on lap
    Then I will receive an error with code '<error>'
    And I will not change the association
    Examples:
      | channel         | passenger                    | journey  | fareType | error           | passengerAssociated |
      | Digital         | 1 adult; 1 child; 1,0 infant | outbound | Standard | SVC_100785_2004 | child               |
      | PublicApiMobile | 1 adult; 2,1 infant          | outbound | Standard | SVC_100785_2004 | infant              |

  @Sprint25 @FCPH-8703
  Scenario Outline: Generate error if the requested passenger is not known
    Given I am using the channel <channel>
    And I searched a flight for <passenger>
    And I added it to the basket with <fareType> fare as <journey> journey
    And I want to associate <passengerAssociated> to infant on lap
    But the request contains <invalid> parameter
    When I send a request to associate adult to infant on lap
    Then I will receive an error with code '<error>'
    And I will not change the association
    Examples:
      | channel         | invalid             | passenger           | journey  | fareType | error           | passengerAssociated |
      | Digital         | invalid basketId    | 2 adult; 1,0 infant | outbound | Standard | SVC_100785_2000 | adult               |
      | PublicApiMobile | invalid passengerId | 2 adult; 1,0 infant | outbound | Standard | SVC_100785_2001 | adult               |

  @Sprint25 @FCPH-8703
  Scenario Outline: Generate error if the requested Infant ID is not known
    Given I am using the channel <channel>
    And I searched a flight for <passenger>
    And I added it to the basket with <fareType> fare as <journey> journey
    And I want to associate <passengerAssociated> to infant on lap
    But the request contains <invalid> parameter
    When I send a request to associate adult to infant on lap
    Then I will receive an error with code '<error>'
    And I will not change the association
    Examples:
      | channel | passenger           | journey  | fareType | error           | passengerAssociated | invalid          |
      | Digital | 2 adult; 1,0 infant | outbound | Standard | SVC_100785_2002 | adult               | invalid infantId |

  @Sprint26 @FCPH-8704
  Scenario Outline: Move associated product to the requested passenger
    Given the channel <channel> is used
    And I added a flight to the basket as an outbound journey for 2 adult; 1,0 infant
    And I want to associate <passengerAssociated> to infant on lap
    When I send a request to associate adult to infant on lap
    Then I will add the extras cabin bag to the new passenger with <fareType> on <channel>
  @regression
  @ADTeam
    Examples:
      | channel           | fareType | passengerAssociated |
      | ADCustomerService | Flexi    | adult               |
    Examples:
      | channel         | fareType | passengerAssociated |
      | Digital         | Standard | adult               |
      | ADAirport       | Standard | adult               |
      | PublicApiMobile | Flexi    | adult               |

  @Sprint26 @FCPH-8704 @TeamC @Sprint29 @FCPH-9518 @ADTeam
  Scenario Outline: Update Seating Service for the adult passenger when an infant on lap has been moved
    Given I am using the channel <channel>
    And I want to proceed with add purchased seat <seat>
    When I send a request to associate adult to infant on lap with <passenger> with <seat> and <fareType> fare type
    Then I will check that the infant is associate
    Examples:
      | channel   | passenger           | fareType | seat     |
      | ADAirport | 2 adult; 1,0 infant | Standard | STANDARD |