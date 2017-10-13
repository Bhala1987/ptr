@FCPH-3334
Feature: Receive request to Add APIS for the Customer to the profile

  Background:
    Given I am using channel Digital
    And that I have registered a new user

  Scenario Outline: Add APIS request received from channel in required format
    When I create a request without a required field "<requiredFieldNull>"
    Then I should add the validation error message with "<code>"
    Examples:
      | requiredFieldNull  | code            |
      | documentNumber     | SVC_100012_2082 |
      | documentType       | SVC_100012_2083 |
      | countryOfIssue     | SVC_100012_2079 |
      | dateOfBirth        | SVC_100012_2080 |
      | documentExpiryDate | SVC_100012_2081 |
      | gender             | SVC_100012_2084 |
      | nationality        | SVC_100012_2085 |

  Scenario Outline: Document number validation BR_00138 & Passports contains special characters BR_00139
    When I send a request to add an API with "<documentNumber>"
    Then I should add the validation error message with "<code>"
    Examples:
      | documentNumber                           | code            |
      | 12                                       | SVC_100050_2011 |
      | 1234512345123451234512345123451234512345 | SVC_100050_2012 |
      | doc num                                  | SVC_100050_2013 |
      | doc?num                                  | SVC_100050_2014 |

  Scenario Outline: Date of Birth field validation BR_00155
    When I send a request to add an API with a non matching date of birth "<dateOfBirth>"
    Then I should add the validation error message with "<code>"
    Examples:
      | dateOfBirth | code            |
      | 2099-12-29  | SVC_100047_2027 |
      | 1990-12-29  | SVC_100047_2028 |

  @regression
  Scenario: All the validation rules passes successfully
    When I send a valid request to add an API
    Then I should receive a confirmation response

  Scenario: One or more validation rules failed
    When I send a invalid request with more than one invalid field
      | documentNumber | documentType | countryOfIssue |
    Then  I should return the validation errors as per Manage APIS Validation
      | SVC_100012_2082 | SVC_100012_2083 | SVC_100012_2079 |
    And   I should not have added any API record

  Scenario: Adding another APIS record once all the validation rules passes successfully BR_00158
    When I send a valid request to add an API
    And I send a valid request to add an API
    Then I should receive a confirmation response
    And I should record and audit record including Record User ID, Date and time of creation
