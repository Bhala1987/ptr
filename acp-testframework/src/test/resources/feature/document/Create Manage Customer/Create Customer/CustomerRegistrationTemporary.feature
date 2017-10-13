@Sprint30 @TeamA @FCPH-10810
Feature:Temporary customer allowed to retrieve and update

  Scenario:Temporary customer not returned in search results
    Given I am using channel Digital
    And I request for create temporary customer profile
    When search for temporary registered customer using firstname and lastname
    Then Identify Customer service returns SVC_100123_1003

  Scenario: Retrieve temporary profile via Digital
    Given I am using channel Digital
    And I request for create temporary customer profile
    And I login as newly created customer
    When I request the temporary customer profile
    Then the temporary profile is returned for the channel

  Scenario Outline: Error Message if attempting to retrieve temporary profile via ADAirport
    Given I am using channel <channel>
    And I login as agent with username as "<usr>" and password as "<pwd>"
    And I request for create temporary customer profile
    When I request the temporary customer profile
    Then get temporary profile return SVC_100000_2069

    Examples:
      | usr    | pwd      | channel   |
      | rachel | 12341234 | ADAirport |

  Scenario: Update temporary profile via Digital
    Given I am using channel Digital
    And I request for create temporary customer profile
    When I update the customer profile details
    Then update confirmation is success


  Scenario Outline: Update temporary profile via ADAirport
    Given I am using channel <channel>
    And I login as agent with username as "<usr>" and password as "<pwd>"
    And I request for create temporary customer profile
    When I update the customer profile details
    Then update service return SVC_100000_2069 for update request attempt

    Examples:
      | usr    | pwd      | channel   |
      | rachel | 12341234 | ADAirport |