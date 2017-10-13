Feature: Receive Request to update Customer Personal Details

  Background:
    Given I am using channel Digital
    And I create a new valid customer

  @FCPH-214
  Scenario Outline: Customer profile update request received in required format
    Given the request not contain the <field>
    When I send a request to the update customer service
    Then I will return <error> for missing mandatory <field>
    Examples:
      | field                    | error           |
      | email                    | SVC_100060_2001 |
      | type                     | SVC_100060_2001 |
      | age                      | SVC_100060_2001 |
      | title                    | SVC_100060_2001 |
      | first name               | SVC_100060_2001 |
      | last name                | SVC_100060_2001 |
      | EJPlus card number       | SVC_100060_2001 |
      | nif number               | SVC_100060_2001 |
      | phone number             | SVC_100060_2001 |
      | alternative phone number | SVC_100060_2001 |
      | flight club id           | SVC_100060_2001 |
      | flight club expiry date  | SVC_100060_2001 |

  @FCPH-214
  Scenario Outline: Age validation against passenger type
    Given the <age> does not match with the <passengerType>
    When I send a request to the update customer service
    Then I should add the validation error message for <error> to the return message
    Examples:
      | passengerType | age | error           |
      | adult         | 15  | SVC_100047_2027 |
      | child         | 16  | SVC_100047_2028 |
      | infant        | 2   | SVC_100047_2028 |

  @FCPH-214
  Scenario Outline: Telephone number validation
    Given the new value for <field> is "<updated>"
    When I send a request to the update customer service
    Then I should add the validation error message for <error> to the return message
    Examples:
      | field                    | updated             | error           |
      | phone number             | 1                   | SVC_100047_2024 |
      | phone number             | 1234567890123456789 | SVC_100047_2024 |
      | phone number             | 123456789a          | SVC_100047_2024 |
      | alternative phone number | 1                   | SVC_100047_2025 |
      | alternative phone number | 1234567890123456789 | SVC_100047_2025 |
      | alternative phone number | 123456789a          | SVC_100047_2025 |

  @FCPH-214
  Scenario Outline: Name validation
    Given the new value for <field> is "<updated>"
    When I send a request to the update customer service
    Then I should add the validation error message for <error> to the return message
    Examples:
      | field      | updated                                             | error           |
      | first name | a                                                   | SVC_100012_3022 |
      | first name | qwertyuioplkjhgfdsazxcvbnmqwert                     | SVC_100012_3022 |
      | first name | $                                                   | SVC_100012_3022 |
      #			| first name | contains any of the following characters 0-9+;:\"\|!?<>().,/@#$£%^&* | First name |
      | last name  | a                                                   | SVC_100012_3023 |
      | last name  | qwertyuioplkjhgfdsazxcvbnmqwertyuioplkjhgfdsazxcvbn | SVC_100012_3023 |
      | last name  | $                                                   | SVC_100012_3023 |
#			| last name  | contains any of the following characters 0-9+;:\"\|!?<>().,/@#$£%^&* | Last name  |
  @FCPH-214
  Scenario Outline: Address validation
    Given the new value for <field> is "<updated>"
    When I send a request to the update customer service
    Then I should add the validation error message for <error> to the return message
    Examples:
      | field        | updated                                             | error           |
      | addressLine1 | a                                                   | SVC_100047_2020 |
      | addressLine1 | qwertyuioplkjhgfdsazxcvbnmqwertyuioplkjhgfdsazxcvbn | SVC_100047_2020 |
      | addressLine2 | a                                                   | SVC_100047_2021 |
      | addressLine2 | qwertyuioplkjhgfdsazxcvbnmqwertyuioplkjhgfdsazxcvbn | SVC_100047_2021 |
      | addressLine3 | a                                                   | SVC_100047_2022 |
      | addressLine3 | qwertyuioplkjhgfdsazxcvbnmqwertyuioplkjhgfdsazxcvbn | SVC_100047_2022 |

  @FCPH-214
  Scenario Outline: Post Code validation
    And the new value for <field> is "<updated>"
    When I send a request to the update customer service
    Then I should add the validation error message for <error> to the return message
    Examples:
      | field    | updated          | error           |
      | PostCode | a                | SVC_100047_2023 |
      | PostCode | qwertyuioplkjhgf | SVC_100047_2023 |

  @FCPH-214
  Scenario Outline: NIF Number validation
    And the new value for <field> is "<updated>"
    When I send a request to the update customer service
    Then I should add the validation error message for <error> to the return message
    Examples:
      | field      | updated   | error           |
      | nif number | 1         | SVC_100060_2002 |
      | nif number | 12345678a | SVC_100060_2002 |

  @FCPH-214
  Scenario Outline: eJ Plus Membership Number validation for Customer
    And the new value for <field> is "<updated>"
    When I send a request to the update customer service
    Then I should add the validation error message for <error> to the return message
    Examples:
      | field              | updated   | error           |
      | EJPlus card number | 1         | SVC_100012_3027 |
      | EJPlus card number | 123456789 | SVC_100012_3027 |

     # It will fail if there is no HR staff member; at the moment there is no service to create a staff member
  @FCPH-214
  Scenario Outline: eJ Plus Membership Number validation for Staff invalid
    Given the Customer is a staff
    And the new value for <field> is "<updated>"
    When I send a request to the update customer service
    Then I should add the validation error message for <error> to the return message
    Examples:
      | field              | updated | error           |
      | EJPlus card number | 000000  | SVC_100012_3027 |
      | EJPlus card number | S00000  | SVC_100012_3027 |

  @FCPH-7965
  Scenario: eJ Plus Membership Number validation for Staff valid
    Given the Customer is a staff
    And I intend to update my profile with a valid Staff EJPlus number
    When I send a request to the update customer service
    Then I update the customer profile

  @regression
  @FCPH-7965 @schema
  Scenario: eJ Plus Membership Number validation for non-Staff
    Given I create a new valid customer
    And I intend to update my profile with a valid non-Staff EJPlus number
    When I send a request to the update customer service
    Then I update the customer profile
    And I validate the json schema for updated customer event

  @FCPH-214
  Scenario: eJ Plus Membership Number does not match with Surname
    Given the eJ Plus Membership Number not match with the stored Surname
    When I send a request to the update customer service
    Then I should add the validation error message for SVC_100000_2074 to the return message

  @FCPH-214
  Scenario Outline: eMail address validation
    And the new value for <field> is "<updated>"
    When I send a request to the update customer service
    Then I should add the validation error message for <error> to the return message
    Examples:
      | field | updated            | error           |
      | email | @abctest.com       | SVC_100012_3028 |
      | email | j.henryreply.co.uk | SVC_100012_3028 |
      | email | j.henry@           | SVC_100012_3028 |

  @FCPH-214
  Scenario: Email address is not registered
    Given the email is not already known
    When I send a request to the update customer service
    Then I update the customer profile

  @FCPH-214
  Scenario: email address is already registered
    Given the email is already linked to an active customer profile
    When I send a request to the update customer service
    Then I should add the validation error message for SVC_100060_3001 to the return message

  @manual @FCPH-214
  Scenario: Email address is available for recycle
    Given the email is already linked to a recycled customer profile
    When I send a request to the update customer service
    Then I update the customer profile

  @Sprint27
  @FCPH-9479
  Scenario Outline: Generate an error if eJ Plus status is not complete for updateCustomerProfile BR_4004
    Given I am using the channel <channel>
    And I intend to update my EJPlus number with status other than <status>
    When I send a request to the update customer service
    Then I should get error with code SVC_100000_2088
    Examples:
      | channel           | status    |
      | ADCustomerService | COMPLETED |
      | PublicApiMobile   | COMPLETED |

  @FCPH-7965 @schema
  Scenario: Update Customer Profile
    And I update the customer profile details
    Then I update the customer profile
    And I validate the json schema for updated customer event