@FCPH-3458
Feature: Hold Items and Prices

  Scenario Outline: Receive request to return hold items to channel
    Given I am using the channel <channel>
    But my request contains '<error>'
    When I send a request to the findHoldItems service
    Then I will receive an error with code '<code>'
    Examples: Possible wrong request
      | channel   | error             | code             |
      | ADAirport | no currency       | SVC_100095_2001  |
      | ADAirport | invalid currency  | SVC_100012_20013 |
      | ADAirport | invalid bundle    | SVC_100000_2067  |
      | ADAirport | invalid sector    | SVC_100022_2027  |
      | ADAirport | invalid flightKey | SVC_100095_2004  |

  Scenario Outline: Return hold items based on requesting channel
    Given I am using the channel <channel>
    And my request currency value is 'GBP'
    And my request bundle value is '<bundle>'
    And my request sector value is '<sector>'
    And my request flight key value is '<flightKey>'
    When I send a request to the findHoldItems service
    Then I will the list of valid products
    @regression
    Examples: Single parameter searches with GBP currency for Digital
      | channel | bundle | sector | flightKey |
      | Digital | empty  | empty  | empty     |
    Examples: Bundle and second parameter searches with GBP currency for Digital
      | channel | bundle   | sector | flightKey |
      | Digital | Standard | valid  | empty     |
      | Digital | Flexi    | empty  | empty     |
      | Digital | Flexi    | valid  | empty     |
    Examples: Searches with GBP currency for the other channel
      | channel           | bundle   | sector | flightKey |
      | ADAirport         | empty    | empty  | empty     |
      | ADAirport         | Standard | empty  | empty     |
      | ADCustomerService | empty    | valid  | empty     |
      | ADCustomerService | empty    | empty  | valid     |
      | PublicApiB2B      | empty    | empty  | valid     |
      | PublicApiB2B      | Standard | valid  | empty     |
      | PublicApiB2B      | Standard | empty  | valid     |
      | PublicApiMobile   | empty    | empty  | empty     |
      | PublicApiMobile   | Standard | empty  | empty     |
      | PublicApiMobile   | empty    | valid  | empty     |
      | ADAirport         | Flexi    | valid  | empty     |
      | ADAirport         | Flexi    | empty  | valid     |
      | ADCustomerService | Flexi    | valid  | empty     |
      | ADCustomerService | Flexi    | empty  | valid     |
      | PublicApiB2B      | Flexi    | empty  | empty     |
      | PublicApiMobile   | Flexi    | valid  | empty     |
      | PublicApiMobile   | Flexi    | empty  | valid     |
