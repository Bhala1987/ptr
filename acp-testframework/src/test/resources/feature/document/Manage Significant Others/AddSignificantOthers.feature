@FCPH-208
Feature: Receive Request to Add Significant Other

  Scenario Outline: Add Significant Other request received in required format
    Given I have received a request to add a Significant Other with missing "<Field>"
    When the Significant Other is added to the Staff Member for negative scenario
    Then I will receive a "<Error>" message to the channel
    Examples:
      | Field     | Error           |
      | type      | SVC_100319_2001 |
      | title     | SVC_100319_2002 |
      | firstName | SVC_100319_2003 |
      | lastName  | SVC_100319_2004 |
      | age       | SVC_100319_2005 |

  Scenario: Threshold of significant other reached BR_00032
    Given I add a Significant Other to the Staff customer
    But the number of remaining changes for the customer is "3"
    When the Significant Other is added to the Staff Member for negative scenario
    And I will receive a "SVC_100318_3002" message to the channel

  Scenario Outline: Length of the field is less than minimum allowed or more than maximum allowed
    Given I add a Significant Other to the Staff customer
    But the length of "<field>" is "<length>"
    When the Significant Other is added to the Staff Member for negative scenario
    Then I will get add Significant Other error for field length "<error>"
    Examples:
      | field     | length | error           |
      | firstName | 1      | SVC_100012_3022 |
      | lastName  | 51     | SVC_100012_3023 |
      | firstName | 31     | SVC_100012_3022 |
      | lastName  | 1      | SVC_100012_3023 |

  Scenario Outline: First Name and Last Name fields allowed characters BR_00835
    Given I add a Significant Other to the Staff customer
    But the field <field> is not valid because it contains <invalidChars>
    When the Significant Other is added to the Staff Member for negative scenario
    Then I will get a Significant Other Invalid character error for "<error>"
    Examples:
      | field     | invalidChars | error           |
      | firstName | "            | SVC_100012_3025 |
#      | firstName | \            | SVC_100012_3025 |
#      | firstName | %            | SVC_100012_3025 |
#      | firstName | +            | SVC_100012_3025 |
#      | firstName | /            | SVC_100012_3025 |
#      | firstName | \|           | SVC_100012_3025 |
#      | firstName | 0            | SVC_100012_3025 |
#      | firstName | 1            | SVC_100012_3025 |
#      | firstName | 2            | SVC_100012_3025 |
#      | firstName | 3            | SVC_100012_3025 |
#      | firstName | 4            | SVC_100012_3025 |
#      | firstName | 5            | SVC_100012_3025 |
#      | firstName | 6            | SVC_100012_3025 |
#      | firstName | 7            | SVC_100012_3025 |
#      | firstName | 8            | SVC_100012_3025 |
#      | firstName | 9            | SVC_100012_3025 |
#      | firstName | +            | SVC_100012_3025 |
#      | firstName | ;            | SVC_100012_3025 |
#      | firstName | :            | SVC_100012_3025 |
#      | firstName | !            | SVC_100012_3025 |
#      | firstName | ?            | SVC_100012_3025 |
#      | firstName | <            | SVC_100012_3025 |
#      | firstName | >            | SVC_100012_3025 |
#      | firstName | (            | SVC_100012_3025 |
#      | firstName | )            | SVC_100012_3025 |
#      | firstName | .            | SVC_100012_3025 |
#      | firstName | ,            | SVC_100012_3025 |
#      | firstName | @            | SVC_100012_3025 |
#      | firstName | #            | SVC_100012_3025 |
#      | firstName | $            | SVC_100012_3025 |
#      | firstName | £            | SVC_100012_3025 |
#      | firstName | ^            | SVC_100012_3025 |
#      | firstName | &            | SVC_100012_3025 |
#      | firstName | *            | SVC_100012_3025 |
#      | lastName  | \|           | SVC_100012_3025 |
#      | lastName  | 0            | SVC_100012_3025 |
#      | lastName  | 1            | SVC_100012_3025 |
#      | lastName  | 2            | SVC_100012_3025 |
#      | lastName  | 3            | SVC_100012_3025 |
#      | lastName  | 4            | SVC_100012_3025 |
#      | lastName  | 5            | SVC_100012_3025 |
#      | lastName  | 6            | SVC_100012_3025 |
#      | lastName  | 7            | SVC_100012_3025 |
#      | lastName  | 8            | SVC_100012_3025 |
#      | lastName  | 9            | SVC_100012_3025 |
#      | lastName  | +            | SVC_100012_3025 |
#      | lastName  | ;            | SVC_100012_3025 |
#      | lastName  | :            | SVC_100012_3025 |
#      | lastName  | \            | SVC_100012_3025 |
#      | lastName  | "            | SVC_100012_3025 |
#      | lastName  | !            | SVC_100012_3025 |
#      | lastName  | ?            | SVC_100012_3025 |
#      | lastName  | <            | SVC_100012_3025 |
#      | lastName  | >            | SVC_100012_3025 |
#      | lastName  | (            | SVC_100012_3025 |
#      | lastName  | )            | SVC_100012_3025 |
#      | lastName  | .            | SVC_100012_3025 |
#      | lastName  | ,            | SVC_100012_3025 |
#      | lastName  | /            | SVC_100012_3025 |
#      | lastName  | @            | SVC_100012_3025 |
#      | lastName  | #            | SVC_100012_3025 |
#      | lastName  | $            | SVC_100012_3025 |
#      | lastName  | £            | SVC_100012_3025 |
#      | lastName  | %            | SVC_100012_3025 |
#      | lastName  | ^            | SVC_100012_3025 |
#      | lastName  | &            | SVC_100012_3025 |
#      | lastName  | *            | SVC_100012_3025 |


  Scenario Outline: Age validation BR_00857
    Given I add a Significant Other to the Staff customer
    But the request contains "<Type>" as passenger type and "<Age>" as age
    When the Significant Other is added to the Staff Member for negative scenario
    Then I will return "<Error>" as error
    Examples:
      | Type   | Age | Error           |
      | child  | 16  | SVC_100012_3016 |
      | child  | 1   | SVC_100012_3016 |
      | infant | -1  | SVC_100012_3016 |
      | infant | 2   | SVC_100012_3016 |

  Scenario Outline: Telephone number length validation BR_00833
    Given I add a Significant Other to the Staff customer
    But the length of "<field>" is "<length>"
    When the Significant Other is added to the Staff Member for negative scenario
    Then I will get add Significant Other error for field length "<error>"
    Examples:
      | field       | length | error           |
      | phoneNumber | 5      | SVC_100012_3020 |
      | phoneNumber | 19     | SVC_100012_3020 |

  Scenario Outline: Telephone number numeric validation BR_04000
    Given I add a Significant Other to the Staff customer
    But the request contains "<Field>" as field and "<PhoneNumber>" as value
    When the Significant Other is added to the Staff Member for negative scenario
    Then I will return "SVC_100012_3019" as error
    Examples:
      | Field       | PhoneNumber |
      | phoneNumber | a           |
#      | phoneNumber | b           |
#      | phoneNumber | c           |
#      | phoneNumber | d           |
#      | phoneNumber | e           |
#      | phoneNumber | f           |
#      | phoneNumber | g           |
#      | phoneNumber | h           |
#      | phoneNumber | i           |
#      | phoneNumber | l           |
#      | phoneNumber | m           |
#      | phoneNumber | n           |
#      | phoneNumber | o           |
#      | phoneNumber | p           |
#      | phoneNumber | q           |
#      | phoneNumber | l           |
#      | phoneNumber | s           |
#      | phoneNumber | t           |
#      | phoneNumber | u           |
#      | phoneNumber | v           |
#      | phoneNumber | z           |
#      | phoneNumber | "           |
#      | phoneNumber | \           |
#      | phoneNumber | %           |
#      | phoneNumber | +           |
#      | phoneNumber | /           |
#      | phoneNumber | \|          |
#      | phoneNumber | ;           |
#      | phoneNumber | :           |
#      | phoneNumber | !           |
#      | phoneNumber | ?           |
#      | phoneNumber | <           |
#      | phoneNumber | >           |
#      | phoneNumber | (           |
#      | phoneNumber | )           |
#      | phoneNumber | .           |
#      | phoneNumber | ,           |
#      | phoneNumber | @           |
#      | phoneNumber | #           |
#      | phoneNumber | $           |
#      | phoneNumber | £           |
#      | phoneNumber | ^           |
#      | phoneNumber | &           |
#      | phoneNumber | *           |

  Scenario Outline: NIF Number length validation BR_00741
    Given I add a Significant Other to the Staff customer
    But the length of "<field>" is "<length>"
    When the Significant Other is added to the Staff Member for negative scenario
    Then I will get add Significant Other error for field length "<error>"
    Examples:
      | field     | length | error           |
      | nifNumber | 8      | SVC_100000_3026 |
      | nifNumber | 1      | SVC_100000_3026 |

  Scenario Outline: eJ Plus Membership Number length validation BR_00420
    Given I add a Significant Other to the Staff customer
    And the request contains a eJPlusMembership Number
    But the length of "<Field>" is "5"
    And the first character "<Character>" of the "<Field>" is not a "S"
    When the Significant Other is added to the Staff Member for negative scenario
    Then I will return "SVC_100012_3027" as error
    Examples:
      | Field            | Character |
      | ejPlusCardNumber | b         |
#      | ejPlusCardNumber | a         |
#      | ejPlusCardNumber | c         |
#      | ejPlusCardNumber | d         |
#      | ejPlusCardNumber | e         |
#      | ejPlusCardNumber | f         |
#      | ejPlusCardNumber | g         |
#      | ejPlusCardNumber | h         |
#      | ejPlusCardNumber | i         |
#      | ejPlusCardNumber | j         |
#      | ejPlusCardNumber | k         |
#      | ejPlusCardNumber | l         |
#      | ejPlusCardNumber | m         |
#      | ejPlusCardNumber | n         |
#      | ejPlusCardNumber | o         |
#      | ejPlusCardNumber | p         |
#      | ejPlusCardNumber | q         |
#      | ejPlusCardNumber | r         |
#      | ejPlusCardNumber | t         |
#      | ejPlusCardNumber | u         |
#      | ejPlusCardNumber | v         |
#      | ejPlusCardNumber | x         |
#      | ejPlusCardNumber | y         |
#      | ejPlusCardNumber | z         |


  Scenario Outline: Number of characters passed for ej Plus validation BR_00430
    Given I add a Significant Other to the Staff customer
    And the request contains a eJPlusMembership Number
    But the length of "<Field>" is "<Length>"
    When the Significant Other is added to the Staff Member
    Then I will return "SVC_100012_3027" as error
    Examples:
      | Field            | Length |
      | ejPlusCardNumber | 4      |
      | ejPlusCardNumber | 1      |
      | ejPlusCardNumber | 5      |
      | ejPlusCardNumber | 7      |

  Scenario: Validate surname must match the membership surname BR_00410
    Given I add a Significant Other to the Staff customer
    And the request contains a eJPlusMembership Number
    But the surname does not match the Ej Plus surname membership
    When the Significant Other is added to the Staff Member for negative scenario
    Then I will return "SVC_100000_2074" as error

  Scenario Outline: eMail address field validation BR_00841
    Given I add a Significant Other to the Staff customer
    And the request contains "<Email>" which is an email address with an invalid format
    When the Significant Other is added to the Staff Member for negative scenario
    Then I will return "SVC_100012_3028" as error
    Examples:
      | Email       |
      | bob19@.com  |
      | ted77ww.com |
      | aa          |
      | t72has@fghj |
      | @fghj       |

  Scenario: Email is not a registered customer
    Given I add a Significant Other to the Staff customer
    And it contains a valid email address
    But the email is not linked to an existing customer profile
    When the Significant Other is added to the Staff Member for negative scenario
    Then I will return "SVC_100318_3003" as error

  Scenario: email is linked to already registered to another Staff account
    Given I add a Significant Other to the Staff customer
    And it contains a valid email address
    But the email belongs to a customer already linked to an other Staff Customer
    When the Significant Other is added to the Staff Member for negative scenario
    Then I will return "SVC_100318_3004" as error

  Scenario: Associate significant other to staff account
    Given I add a Significant Other to the Staff customer
    And it contains a valid email address
    And the email address is associated to a registered email account
    And the email is not linked to another staff account
    When the Significant Other is added to the Staff Member
    Then the Significant Other is successfully associated to the Staff Customer


  Scenario Outline: Document number length validation BR_00138
    Given I add a Significant Other to the Staff customer
    And the request contains a valid Identity Document
    But the length of "<Field>" is "<Length>"
    When the Significant Other is added to the Staff Member for negative scenario
    Then I will return "SVC_100012_3029" as error
    Examples:
      | Field          | Length |
      | documentNumber | 2      |
      | documentNumber | 36     |

  Scenario Outline: Document number fields allowed characters BR_00139
    Given I add a Significant Other to the Staff customer
    And the request contains a valid Identity Document
    But the field <field> is not valid because it contains <invalidChars>
    When the Significant Other is added to the Staff Member for negative scenario
    Then I will return "SVC_100012_3030" as error
    Examples:
      | field          | invalidChars |
      | documentNumber | \            |
#      | documentNumber | %            |
#      | documentNumber | /            |
#      | documentNumber | \|           |
#      | documentNumber | +            |
#      | documentNumber | ;            |
#      | documentNumber | :            |
#      | documentNumber | !            |
#      | documentNumber | ?            |
#      | documentNumber | <            |
#      | documentNumber | >            |
#      | documentNumber | (            |
#      | documentNumber | )            |
#      | documentNumber | .            |
#      | documentNumber | ,            |
#      | documentNumber | @            |
#      | documentNumber | #            |
#      | documentNumber | $            |
#      | documentNumber | £            |
#      | documentNumber | ^            |
#      | documentNumber | &            |
#      | documentNumber | *            |

  Scenario: Add significant other
    Given I add a Significant Other to the Staff customer
    And the customer is a valid staff customer
    When the Significant Other is added to the Staff Member
    Then the significant other information is successfully added to the Staff Customer

  Scenario Outline: Age validation BR_00857
    Given I add a Significant Other to the Staff customer
    And the customer is a valid staff customer
    And the request contains "<Type>" as passenger type and "<Age>" as age
    When the Significant Other is added to the Staff Member
    Then the significant other information is successfully added to the Staff Customer
    Examples:
      | Type   | Age |
      | child  | 15  |
      | child  | 2   |
      | infant | 0   |
      | infant | 1   |
      | adult  | 16  |

  Scenario: Increase Count for the number of changes BR_00033
    Given I add a Significant Other to the Staff customer
    And 3 Significant other added even if delete later
    When the Significant Other is added to the Staff Member
    Then I will update to the count of changes allowed

  @regression
  Scenario: Getting updated information when profile retrieved again
    Given I have a Staff customer with a complete Significant Other added
    And I have received a valid getCustomerProfile
    When I retrieve the customer profile
    Then I will return the updated values for the complete significant others
    And I will see the number of changes still allowed
