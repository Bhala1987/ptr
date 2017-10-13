@FCPH-2769
Feature: Validate and create customer profile creation request

  @Sprint31 @TeamA @FCPH-10699
  Scenario: Create registered customer profile
    Given a valid request to create a customer profile
    When I request creation of a customer profile
    Then the customer profile is created
#    this can be uncommnted once we resolve issue FCPH-11484
#    And I validate the json schema for created customer event

  @negative
  Scenario Outline: Validate that request by channel validate the fields exist in the request
    Given I have provided valid mandatory fields for "<channel>" with the missing field "<field>"
    When I request creation of a customer profile
    Then I will get a customer creation error for the missing field "<error>"
    Examples:
      | channel           | field        | error           |
      | Digital           | title        | SVC_100047_2034 |
#      | Digital           | firstName         | SVC_100047_2035 |
#      | Digital           | lastName          | SVC_100047_2036 |
#      | Digital           | email             | SVC_100047_2037 |
#      | Digital           | addressLine1      | SVC_100047_2007 |
#      | Digital           | city              | SVC_100047_2009 |
#      | Digital           | postalCode        | SVC_100047_2010 |
#      | Digital           | optedOutMarketing | SVC_100047_2011 |
#      | Digital           | phoneNumber       | SVC_100047_2012 |
#      | Digital           | password          | SVC_100047_2013 |
#      | ADAirport         | title             | SVC_100047_2034 |
#      | ADAirport         | firstName         | SVC_100047_2035 |
#      | ADAirport         | lastName          | SVC_100047_2036 |
#      | ADAirport         | email             | SVC_100047_2037 |
      | ADAirport         | addressLine1 | SVC_100047_2007 |
#      | ADAirport         | city              | SVC_100047_2009 |
#      | ADAirport         | postalCode        | SVC_100047_2010 |
#      | ADAirport         | optedOutMarketing | SVC_100047_2011 |
#      | ADAirport         | phoneNumber       | SVC_100047_2012 |
#      | ADCustomerService | title             | SVC_100047_2034 |
#      | ADCustomerService | firstName         | SVC_100047_2035 |
#      | ADCustomerService | lastName          | SVC_100047_2036 |
#      | ADCustomerService | email             | SVC_100047_2037 |
#      | ADCustomerService | addressLine1      | SVC_100047_2007 |
#      | ADCustomerService | city              | SVC_100047_2009 |
#      | ADCustomerService | postalCode        | SVC_100047_2010 |
#      | ADCustomerService | optedOutMarketing | SVC_100047_2011 |
      | ADCustomerService | phoneNumber  | SVC_100047_2012 |

  @pending
  Scenario Outline: Non Mandatory fields for a channel can be missing
    Given a valid request to create a customer profile
    And the optional field "<field>" is missing
    When I request creation of a customer profile
    Then the customer profile is created
    Examples:
      | field          |
      | NIF            |
      | EJ Plus        |
      | Altername Tele |
      | Travel Pref    |
      | Age            |

  @negative
  Scenario: Email address is already registered
    Given an existing customer profile with known e-mail address
    When I request creation of a new customer profile with the same e-mail address
    Then an email registered validation error is returned

  @pending
  @manual
  Scenario: Email address is available for recycle
    Given a valid request to create a customer profile
    And the email address is availabel for recycling
    When I request creation of a customer profile
    Then the customer profile is created

  @negative
  Scenario Outline: Format of the Customer Name
    Given a valid request to create a customer profile
    But the "<field>" is not valid because it contains "<invalidChars>"
    When I request creation of a customer profile
    Then I will get a Invalid character error for "<error>"
    Examples:
      | field             | invalidChars | error           |
      | customerfirstName | "            | SVC_100047_2016 |
#      | customerfirstName | \            | SVC_100047_2016 |
#      | customerfirstName | %            | SVC_100047_2016 |
#      | customerfirstName | +            | SVC_100047_2016 |
#      | customerfirstName | /            | SVC_100047_2016 |
#      | customerfirstName | \|           | SVC_100047_2016 |
#      | customerfirstName | 0            | SVC_100047_2016 |
#      | customerfirstName | 1            | SVC_100047_2016 |
#      | customerfirstName | 2            | SVC_100047_2016 |
#      | customerfirstName | 3            | SVC_100047_2016 |
#      | customerfirstName | 4            | SVC_100047_2016 |
#      | customerfirstName | 5            | SVC_100047_2016 |
#      | customerfirstName | 6            | SVC_100047_2016 |
#      | customerfirstName | 7            | SVC_100047_2016 |
#      | customerfirstName | 8            | SVC_100047_2016 |
#      | customerfirstName | 9            | SVC_100047_2016 |
#      | customerfirstName | +            | SVC_100047_2016 |
#      | customerfirstName | ;            | SVC_100047_2016 |
#      | customerfirstName | :            | SVC_100047_2016 |
#      | customerfirstName | !            | SVC_100047_2016 |
#      | customerfirstName | ?            | SVC_100047_2016 |
#      | customerfirstName | <            | SVC_100047_2016 |
#      | customerfirstName | >            | SVC_100047_2016 |
#      | customerfirstName | (            | SVC_100047_2016 |
#      | customerfirstName | )            | SVC_100047_2016 |
#      | customerfirstName | .            | SVC_100047_2016 |
#      | customerfirstName | ,            | SVC_100047_2016 |
#      | customerfirstName | @            | SVC_100047_2016 |
#      | customerfirstName | #            | SVC_100047_2016 |
#      | customerfirstName | $            | SVC_100047_2016 |
#      | customerfirstName | £            | SVC_100047_2016 |
#      | customerfirstName | ^            | SVC_100047_2016 |
#      | customerfirstName | &            | SVC_100047_2016 |
#      | customerfirstName | *            | SVC_100047_2016 |
#      | customerlastName  | \|           | SVC_100047_2017 |
#      | customerlastName  | 0            | SVC_100047_2017 |
#      | customerlastName  | 1            | SVC_100047_2017 |
#      | customerlastName  | 2            | SVC_100047_2017 |
#      | customerlastName  | 3            | SVC_100047_2017 |
#      | customerlastName  | 4            | SVC_100047_2017 |
#      | customerlastName  | 5            | SVC_100047_2017 |
#      | customerlastName  | 6            | SVC_100047_2017 |
#      | customerlastName  | 7            | SVC_100047_2017 |
#      | customerlastName  | 8            | SVC_100047_2017 |
#      | customerlastName  | 9            | SVC_100047_2017 |
#      | customerlastName  | +            | SVC_100047_2017 |
#      | customerlastName  | ;            | SVC_100047_2017 |
#      | customerlastName  | :            | SVC_100047_2017 |
#      | customerlastName  | \            | SVC_100047_2017 |
#      | customerlastName  | "            | SVC_100047_2017 |
#      | customerlastName  | !            | SVC_100047_2017 |
#      | customerlastName  | ?            | SVC_100047_2017 |
#      | customerlastName  | <            | SVC_100047_2017 |
#      | customerlastName  | >            | SVC_100047_2017 |
#      | customerlastName  | (            | SVC_100047_2017 |
#      | customerlastName  | )            | SVC_100047_2017 |
#      | customerlastName  | .            | SVC_100047_2017 |
#      | customerlastName  | ,            | SVC_100047_2017 |
#      | customerlastName  | /            | SVC_100047_2017 |
#      | customerlastName  | @            | SVC_100047_2017 |
#      | customerlastName  | #            | SVC_100047_2017 |
#      | customerlastName  | $            | SVC_100047_2017 |
#      | customerlastName  | £            | SVC_100047_2017 |
#      | customerlastName  | %            | SVC_100047_2017 |
#      | customerlastName  | ^            | SVC_100047_2017 |
#      | customerlastName  | &            | SVC_100047_2017 |
#      | customerlastName  | *            | SVC_100047_2017 |

  @negative
  Scenario Outline: Length of the field is less than minimum allowed or more than maximum allowed
    Given a valid request to create a customer profile
    But the "<field>" length is "<length>"
    When I request creation of a customer profile
    Then I will get a customer creation error for field length "<error>"
    Examples:
      | field        | length | error           |
#      | fName        | 1      | SVC_100047_2018 |
      | fName        | 31     | SVC_100047_2018 |
#      | lName        | 1      | SVC_100047_2019 |
      | lName        | 51     | SVC_100047_2019 |
#      | pNumber      | 5      | SVC_100047_2024 |
      | pNumber      | 19     | SVC_100047_2024 |
#      | aLine1       | 3      | SVC_100047_2020 |
      | aLine1       | 51     | SVC_100047_2020 |
#      | addressLine2 | 3      | SVC_100047_2021 |
      | addressLine2 | 51     | SVC_100047_2021 |
#      | acity        | 3      | SVC_100047_2044 |
      | acity        | 51     | SVC_100047_2044 |
#      | pCode        | 1      | SVC_100047_2023 |
      | pCode        | 16     | SVC_100047_2023 |

  @negative
  Scenario Outline: Email address format
    Given a valid request to create a customer profile
    But the email field is in an invalid format "<invalidEmailFormat>"
    When I request creation of a customer profile
    Then an Invalid Email Format validation error is returned
    Examples:
      | invalidEmailFormat |
      | bob19@.com         |
      | ted77ww.com        |
      | aa                 |
      | t72has@fghj        |

  @pending
  @manual
  Scenario Outline: Validate Passenger Age and passenger type
    Given a valid request to create a customer profile for "<passengerType>"
    But the "<passengerType>" age is not valid
    When I request creation of a customer profile
    Then an age validation error is returned
    Examples:
      | passengerType |
      | Adult         |
      | Child         |
      | Infant        |