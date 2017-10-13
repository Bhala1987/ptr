Feature: Receive Request to Add or Change APIS for a passenger on a booking, not Public API

  @Sprint27 @FCPH-417
  Scenario Outline: Return error for invalid request format or missing mandatory fields
    Given I am using the channel <channel>
    And I do a commit booking with <flight> for <passenger> with <condition> APIS using <fareType>
    When I send a error request to setApis on <flight> flight with different <fieldToChange> for <typePassenger>
    Then the set APIs should fail with <warning> error
    Examples: Update with invalid valid values and get appropriate warnings
      | channel           | passenger           | fareType | flight | fieldToChange              | typePassenger | condition | warning         |
      | Digital           | 1 adult             | Flexi    | 1      | InvalidDateOfBirthNull     | adult         | false     | SVC_100201_2003 |
      | Digital           | 1 adult             | Flexi    | 1      | InvalidDocumentNumber      | adult         | true      | SVC_100012_3029 |
      | Digital           | 1 adult             | Flexi    | 1      | DocumentNumberHasSpclChars | adult         | true      | SVC_100012_3030 |
      | Digital           | 1 adult             | Flexi    | 1      | DocumentNumbernull         | adult         | true      | SVC_100201_2009 |
      | Digital           | 1 adult             | Flexi    | 1      | AdultAgeLessThan16         | adult         | true      | SVC_100201_2012 |
      | Digital           | 1 adult; 1,1 infant | Standard | 1      | InfantLessThan14days       | infant        | true      | SVC_100201_2013 |
      | ADCustomerService | 1 adult; 1 child    | Standard | 1      | ChildAgeLess               | child         | true      | SVC_100201_2017 |
      | ADCustomerService | 1 adult; 1 child    | Standard | 1      | ChildAgeMore               | child         | true      | SVC_100201_2017 |
      | ADCustomerService | 1 adult; 1,1 infant | Standard | 1      | InfantWithOutAdult         | adult         | true      | SVC_100201_2012 |


  @Sprint27 @FCPH-417
  Scenario Outline: duplicate documentId fields
    Given I am using the channel <channel>
    And I do a commit booking with <flight> for <passenger> with <condition> APIS using <fareType>
    And I send a request to update APIS with duplicate <duplicateField> for <typePassenger>
    Then the set APIs should fail with <warning> error

    Examples:
      | channel | passenger | fareType | flight | typePassenger | condition | warning         | duplicateField |
      | Digital | 2 adult   | Flexi    | 1      | adult         | true      | SVC_100201_2018 | documentId     |

  @Sprint27 @FCPH-417
  Scenario Outline: when multiple flights and error in APIS details then should not update APIS details for both flight
    Given I am using the channel <channel>
    And I do a commit booking with <flight> for <passenger> with <condition> APIS using <fareType>
    When I send a error request to setApis on <flight> flight with different <fieldToChange> for <typePassenger>
    Then I see APIs not updated for both flights
    Examples: Update with invalid valid values and get appropriate warnings
      | channel | passenger | fareType | flight | fieldToChange          | typePassenger | condition |
      | Digital | 1 adult   | Flexi    | 2      | InvalidDateOfBirthNull | adult         | false     |
