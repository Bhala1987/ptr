Feature: Add APIs for a passenger in the basket

  Background:
    Given I am using "ADAirport" channel

  @FCPH-346 @Sprint24@me
  Scenario Outline: 1 - Add APIs request is not in the specified format
    And I am updating my passenger document details
    When I failed to provide "<field>" in apis
    Then I should see error "SVC_100273_2012" with "<field>" missing
    Examples:
      | field              |
      | DocumentExpiryDate |
      | DocumentNumber     |
      | DocumentType       |
      | DateOfBirth        |
      | Gender             |
      | Nationality        |
      | CountryOfIssue     |

  @FCPH-346 @Sprint24
  Scenario Outline: 2 - Document number length validation BR_00138
    And I am updating my passenger document details
    When I provide document number length as "<DocumentNumberLength>"characters
    Then I should receive the "SVC_100273_3017" error message stating that document number length is invalid
    Examples:
      | DocumentNumberLength |
      | 2                    |
      | 36                   |

  @FCPH-346 @Sprint24
  Scenario Outline: 3 - Document special characters validation BR_00139
    And I am updating my passenger document details
    When I set the document number as "<DocumentNumber>"
    Then I should receive the "SVC_100273_3024" error message stating that document number has invalid characters
    Examples:
      | DocumentNumber  |
      | 1aWd7R2 768f    |
      | ?aWd;R:}{       |
      | Z%:3xV\U$Y7kERX |
      | AV4u*uE7rR`2:%3 |

  @FCPH-346 @Sprint24
  Scenario Outline: 5 - Infant age is less than 14 days old validation BR_00133
    And I am updating my passenger document details for "<passengerMix>"
    When I set the document date of birth for "<passengerToUpdate>" as "<age>" days old at the flight departure
    Then I should receive the "<error>" error message stating that date of birth does not match with passenger type

    Examples:
      | passengerMix         | passengerToUpdate | age | error           |
      | 1 Adult, 1 Infant OL | Infant            | 13  | SVC_100273_3019 |

  @FCPH-346 @Sprint24
  Scenario Outline: 4 - Adult age is less than 16 years old validation BR_00155
    And I am updating my passenger document details for "<passengerMix>"
    When I set the document date of birth for "<passengerToUpdate>" as "<age>" years old at the flight departure
    Then I should receive the "<error>" error message stating that date of birth does not match with passenger type

    Examples:
      | passengerMix | passengerToUpdate | age | error           |
      | 1 Adult      | Adult             | 15  | SVC_100273_3018 |

  @FCPH-346 @Sprint24
  Scenario Outline: 6 - Infant age is greater than 1 year old validation BR_00133
    And I am updating my passenger document details for "<passengerMix>"
    When I set the document date of birth for "<passengerToUpdate>" as "<ageInYears>" years old at the flight departure
    Then I should receive the "<error>" error message stating that date of birth does not match with passenger type
    Examples:
      | passengerMix         | passengerToUpdate | ageInYears | error           |
      | 1 Adult, 1 Infant OL | Infant            | 2          | SVC_100273_3020 |

  @FCPH-346 @Sprint24
  Scenario Outline: 7 - Child age is less than 2 years validation BR_00141
    And I am updating my passenger document details for "<passengerMix>"
    When I set the document date of birth for "<passengerToUpdate>" as "<ageInYears>" years old at the flight departure
    Then I should receive the "<error>" error message stating that date of birth does not match with passenger type

    Examples:
      | passengerMix | passengerToUpdate | ageInYears | error           |
      | 1 child      | Child             | 1          | SVC_100273_3023 |

  @FCPH-346 @Sprint24
  Scenario Outline: 8 - Child is greater 15 years validation BR_00141
    And I am updating my passenger document details for "<passengerMix>"
    When I set the document date of birth for "<passengerToUpdate>" as "<ageInYears>" years old at the flight departure
    Then I should receive the "<error>" error message stating that date of birth does not match with passenger type

    Examples:
      | passengerMix | passengerToUpdate | ageInYears | error           |
      | 1 child      | Child             | 16         | SVC_100273_3023 |

  @FCPH-8768 @Sprint25
  Scenario: 9 - Error at age of the Adult (X = 16) when travelling with an infant
    And I am updating my passenger document details for "1 Adult, 1 Infant OL"
    When I set the document date of birth for "Adult" as "15" years old at the flight departure
    Then I should receive the "SVC_100273_3022" error message

  @FCPH-8768 @Sprint25
  Scenario: 9 - Success at age of the Adult (X=16) when travelling with an infant
    And I am updating my passenger document details for "1 Adult, 1 Infant OL"
    When I set the document date of birth for "Adult" as "16" years old at the flight departure
    Then I should be able to store the documents successfully

  @FCPH-8768 @Sprint25
  Scenario: 10 - Validate that Infant apis cannot be added without an adding the adult
    And I am updating my passenger document details for "1 Adult, 1 Infant OL"
    When I attempt to set the "infant" apis without the "adult" apis
    Then I should receive the "SVC_100273_3021" error message

  @FCPH-8768 @Sprint25
  Scenario: 10 - Validate that Infant apis can be added if adults apis added in the previous request
    And I am updating my passenger document details for "1 Adult, 1 Infant OL"
    When I attempt to set the "adult" apis without the "infant" apis
    And I attempt to set the "infant" apis without the "adult" apis
    Then I should be able to store the documents successfully

  @FCPH-346 @Sprint24
  Scenario: 11 - All the validation rules passes successfully
    And I am updating my passenger document details for "1 Adult, 1 Child, 1 Infant OL"
    When I process the request for updatePassengers with documents
    Then I should be able to store the documents successfully

  @FCPH-346 @Sprint24
  Scenario: 12 - One or more validation rules failed
    And I am updating my passenger document details for "1 Adult, 1 Child, 1 Infant OL"
    When I set age 100 for "child" and no "adult" apis but for infant
    Then I should receive both "SVC_100273_3021" and  "SVC_100273_3023" error messages