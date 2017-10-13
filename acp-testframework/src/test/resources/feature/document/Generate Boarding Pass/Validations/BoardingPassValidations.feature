@TeamE
@Sprint31
@FCPH-10240
Feature: GetBoardingPass Validations

 @local
  Scenario Outline: Error if the non AD channels are not allowed to generate boarding pass for a passenger with a STANDBY fare
   Given the channel ADAirport is used
   And I logged in as agent
   And I have a check-in for <sector> sector with <fare> fare
   And the updated passenger status is <status>
   When the channel <channel> is used
   And I send a boarding pass request for updated customer
   Then I will receive a error message <errorCode>
   Examples:
    | channel           | status        |  errorCode      |  fare     | sector |
    | Digital           | CHECKED_IN    |  SVC_100173_006 |  Standby  | DCS    |
    | ADCustomerService | CHECKED_IN    |  SVC_100173_006 |  Standby  | DCS    |
    | PublicApiB2B      | CHECKED_IN    |  SVC_100173_006 |  Standby  | DCS    |


  @local
  Scenario Outline: Success when allowed AD channel is allowed to generate boarding pass for a passenger with a STANDBY fare
    Given the channel ADAirport is used
    And I logged in as agent
    And I have a check-in for <sector> sector with <fare> fare
    And the updated passenger status is <status>
    When the channel <channel> is used
    And I send a boarding pass request for updated customer
    Then I will receive the boarding pass for the requested passenger
    Examples:
      | channel   | status        | fare      | sector |
      | ADAirport | CHECKED_IN    | Standby   | DCS    |


    @local @BR:BR_03001
    Scenario Outline: Error if the channel is not allowed to generate boarding pass for a passenger with a SAG status
      Given the channel ADAirport is used
      And I set transaction id to <client> in order to receive a SAG status
      And I have a check-in with Standard fare
      And the updated passenger status is <status>
      When the channel <channel> is used
      And I send a boarding pass request for updated customer
      Then I will receive a error message <errorCode>
      Examples:
        | channel           | client       | status | errorCode      |
        | Digital           | F10240123456 | SAG    | SVC_100173_007 |
        | PublicApiB2B      | F10240123456 | SAG    | SVC_100173_007 |


  @local @BR:BR_03001
    Scenario Outline: Error when the not allowed ADCustomerService channel tries to generate boarding pass for a passenger with a SAG status
    Given the channel <channel> is used
    And I logged in as agent
    And I set transaction id to <client> in order to receive a SAG status
    And I have a check-in
    And the updated passenger status is <status>
    And I send a boarding pass request for updated customer
    Then I will receive a error message <errorCode>
    Examples:
      | channel           | client       | status | errorCode      |
      | ADCustomerService | F10240123456 | SAG    | SVC_100173_007 |



  @local
  Scenario Outline: Success when the allowed ADAirport channel tries to generate boarding pass for a passenger with a SAG status BR_03001
    Given the channel <channel> is used
    And I logged in as agent
    And I set transaction id to <client> in order to receive a SAG status
    And I have a check-in
    And the updated passenger status is <status>
    And I send a boarding pass request for updated customer
    Then I will receive the boarding pass for the requested passenger
    Examples:
      | channel   | client       | status |
      | ADAirport | F10240123456 | SAG    |

