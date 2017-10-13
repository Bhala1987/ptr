@Sprint28
@FCPH-7736
@manual
Feature: Add to booking history when APIS is added or changed for a passenger

  Scenario Outline: - Customer made change and is logged in
    Given I am using the channel <channel>
    And I do a commit booking with <flight> for <passenger> with <condition> APIS using <fareType>
    When I send a request to setApis on <flight> flight with different <fieldToChange> for <typePassenger>
    Then Booking history event set Date and Time,
    And Booking history event set Channel as Requesting Channel,
    And Booking history event set User as User Id,
    And Booking history event set to 'APIS'
    And Booking history event set Description to Flight Key, Passenger Last Name and First Name
    And Booking history event set Version as Booking Version
    Examples:
      | channel | passenger | condition | fareType | flight | fieldToChange | typePassenger |
      | Digital | 1 adult   | true      | Standard | 1      | IdentityCard  | adult         |

  Scenario Outline: Customer made change and is not logged in
    Given I am using the channel <channel>
    And I do a commit booking with <flight> for <passenger> with <condition> APIS using <fareType>
    And I am not logged in
    When I send a request to setApis on <flight> flight with different <fieldToChange> for <typePassenger>
    Then Booking history event set Date and Time,
    And Booking history event set Channel as Requesting Channel,
    And Booking history event set User as Anonymous,
    And Booking history event set to 'APIS'
    And Booking history event set Description to Flight Key, Passenger Last Name and First Name
    And Booking history event set Version as Booking Version
    Examples:
      | channel | passenger | condition | fareType | flight | fieldToChange | typePassenger |
      | Digital | 1 adult   | true      | Standard | 1      | IdentityCard  | adult         |

  Scenario Outline: Agent made change
    Given I am using the channel <channel>
    And I do a commit booking with <flight> for <passenger> with <condition> APIS using <fareType>
    And the change is from the Agent Desktop
    When I send a request to setApis on <flight> flight with different <fieldToChange> for <typePassenger>
    Then Booking history event set Date and Time,
    And Booking history event set Channel as Requesting Channel,
    And Booking history event set Agent ID as Agent Id,
    And Booking history event set to 'APIS Added or Changed'
    And Booking history event set Description to Flight Key, Passenger Last Name and First Name
    And Booking history event set Version as Booking Version

    Examples:
      | channel | passenger | condition | fareType | flight | fieldToChange | typePassenger |
      | Digital | 1 adult   | true      | Standard | 1      | IdentityCard  | adult         |

  Scenario Outline: Update to more than one flight for the same passenger
    Given I am using the channel <channel>
    And I do a commit booking with <flight> for <passenger> with <condition> APIS using <fareType>
    And the change is from the Agent Desktop
    When I send a request to setApis on <flight> flight with different <fieldToChange> for <typePassenger>
    Then Booking history event set Date and Time,
    And Booking history event set Channel as Requesting Channel,
    And Booking history event set Agent ID as User Id,
    And Booking history event set to 'APIS Added or Changed'
    And Booking history event set Description to Flight Key, Passenger Last Name and First Name for each flight
    And Booking history event set Version as Booking Version
    Examples:
      | channel | passenger | condition | fareType | flight | fieldToChange | typePassenger |
      | Digital | 1 adult   | true      | Standard | 2      | IdentityCard  | adult         |