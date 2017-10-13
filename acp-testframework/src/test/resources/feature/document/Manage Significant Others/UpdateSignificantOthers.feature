Feature: Receive Request to UpdateSignificant Other

  @FCPH-3344
  Scenario Outline: 1.Update Significant Other request received in required format
    Given I have received a request to update a Significant Other with missing "<Field>"
    When the Significant other is updated
    Then I will receive one "<error>" message to the channel
    Examples:
      | Field     | error           |
      | title     | SVC_100319_2002 |
      | type      | SVC_100319_2001 |
      | firstName | SVC_100319_2003 |
      | lastName  | SVC_100319_2004 |
      | age       | SVC_100319_2005 |

  @FCPH-3344
  Scenario Outline: 2.Age validation BR_00857
    Given I have received a valid request to update a Significant Others to the Staff customer
    But the request contains the value "<Age>" as age and "<Type>" as passenger type
    When the Significant other is updated
    Then I will return error "<error>"
    Examples:
      | Type   | Age | error           |
      | child  | 16  | SVC_100012_3016 |
      | child  | 1   | SVC_100012_3016 |
      | infant | -1  | SVC_100012_3016 |
      | infant | 2   | SVC_100012_3016 |

  @FCPH-3344
  Scenario Outline: 3.Telephone number length validation BR_00833
    Given I have received a valid request to update a Significant Others to the Staff customer
    But "<field>" length is "<length>"
    When the Significant other is updated
    Then I will get update Significant Other error for field length "<error>"
    Examples:
      | field       | length | error           |
      | phoneNumber | 5      | SVC_100012_3020 |
      | phoneNumber | 19     | SVC_100012_3020 |

  @FCPH-3344
  Scenario Outline: 4.Telephone number numeric validation BR_04000
    Given I have received a valid request to update a Significant Others to the Staff customer
    But request contains "<Field>" as field and "<PhoneNumber>" as value
    When the Significant other is updated
    Then I will return error "<error>"
    Examples:
      | Field       | PhoneNumber | error           |
      | phoneNumber | b           | SVC_100012_3019 |
#      | phoneNumber | a           | SVC_100012_3019 |
#      | phoneNumber | c           | SVC_100012_3019 |
#      | phoneNumber | d           | SVC_100012_3019 |
#      | phoneNumber | e           | SVC_100012_3019 |
#      | phoneNumber | f           | SVC_100012_3019 |
#      | phoneNumber | g           | SVC_100012_3019 |
#      | phoneNumber | h           | SVC_100012_3019 |
#      | phoneNumber | i           | SVC_100012_3019 |
#      | phoneNumber | l           | SVC_100012_3019 |
#      | phoneNumber | m           | SVC_100012_3019 |
#      | phoneNumber | n           | SVC_100012_3019 |
#      | phoneNumber | o           | SVC_100012_3019 |
#      | phoneNumber | p           | SVC_100012_3019 |
#      | phoneNumber | q           | SVC_100012_3019 |
#      | phoneNumber | l           | SVC_100012_3019 |
#      | phoneNumber | s           | SVC_100012_3019 |
#      | phoneNumber | t           | SVC_100012_3019 |
#      | phoneNumber | u           | SVC_100012_3019 |
#      | phoneNumber | v           | SVC_100012_3019 |
#      | phoneNumber | z           | SVC_100012_3019 |
      | phoneNumber | "           | SVC_100012_3019 |
#      | phoneNumber | \           | SVC_100012_3019 |
#      | phoneNumber | %           | SVC_100012_3019 |
#      | phoneNumber | +           | SVC_100012_3019 |
#      | phoneNumber | /           | SVC_100012_3019 |
#      | phoneNumber | \|          | SVC_100012_3019 |
#      | phoneNumber | ;           | SVC_100012_3019 |
#      | phoneNumber | :           | SVC_100012_3019 |
#      | phoneNumber | !           | SVC_100012_3019 |
#      | phoneNumber | ?           | SVC_100012_3019 |
#      | phoneNumber | <           | SVC_100012_3019 |
#      | phoneNumber | >           | SVC_100012_3019 |
#      | phoneNumber | (           | SVC_100012_3019 |
#      | phoneNumber | )           | SVC_100012_3019 |
#      | phoneNumber | .           | SVC_100012_3019 |
#      | phoneNumber | ,           | SVC_100012_3019 |
#      | phoneNumber | @           | SVC_100012_3019 |
#      | phoneNumber | #           | SVC_100012_3019 |
#      | phoneNumber | $           | SVC_100012_3019 |
#      | phoneNumber | £           | SVC_100012_3019 |
#      | phoneNumber | ^           | SVC_100012_3019 |
#      | phoneNumber | &           | SVC_100012_3019 |
      | phoneNumber | *           | SVC_100012_3019 |

  @FCPH-3344
  Scenario Outline: 5.Length of the field is less than minimum allowed or more than maximum allowed
    Given I have received a valid request to update a Significant Others to the Staff customer
    But "<field>" length is "<length>"
    When the Significant other is updated
    Then I will get update Significant Other error for field length "<error>"
    Examples:
      | field     | length | error           |
      | firstName | 31     | SVC_100012_3022 |
      | lastName  | 51     | SVC_100012_3023 |
      | firstName | 1      | SVC_100012_3022 |
      | lastName  | 1      | SVC_100012_3023 |

  @FCPH-3344
  Scenario Outline: 6.First Name and Last Name fields allowed characters BR_00835
    Given I have received a valid request to update a Significant Others to the Staff customer
    But field <field> is not valid because it contains <invalidChars>
    When the Significant other is updated
    Then I will get a Invalid char error for "<error>"
    Examples:
      | field     | invalidChars | error           |
      | firstName | \            | SVC_100012_3025 |
#      | firstName | "            | SVC_100012_3025 |
#      | firstName | %            | SVC_100012_3025 |
#      | firstName | +            | SVC_100012_3025 |
#      | firstName | /            | SVC_100012_3025 |
#      | firstName | \|           | SVC_100012_3025 |
      | firstName | 0            | SVC_100012_3025 |
#      | firstName | 1            | SVC_100012_3025 |
#      | firstName | 2            | SVC_100012_3025 |
#      | firstName | 3            | SVC_100012_3025 |
#      | firstName | 4            | SVC_100012_3025 |
#      | firstName | 5            | SVC_100012_3025 |
#      | firstName | 6            | SVC_100012_3025 |
#      | firstName | 7            | SVC_100012_3025 |
#      | firstName | 8            | SVC_100012_3025 |
#      | firstName | 9            | SVC_100012_3025 |
      | firstName | +            | SVC_100012_3025 |
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
#      | firstName | \*           | SVC_100012_3025 |
#      | lastName  | \|           | SVC_100012_3025 |
      | lastName  | 0            | SVC_100012_3025 |
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

  @FCPH-3344
  Scenario Outline: 7.NIF Number length validation BR_00741
    Given I have received a valid request to update a Significant Others to the Staff customer
    But "<field>" length is "<length>"
    When the Significant other is updated
    Then I will get update Significant Other error for field length "<error>"
    Examples:
      | field     | length | error           |
      | nifNumber | 8      | SVC_100000_3026 |
      | nifNumber | 1      | SVC_100000_3026 |

  @FCPH-3344
  Scenario Outline: 8.eJ Plus Membership Number length validation BR_00420
    Given I have received a valid request to update a Significant Others to the Staff customer
    And the request contains an eJ Plus Membership Number
    But "<Field>" length is "6"
    But the first character "<Character>" of the "<Field>" passed in the request is not a 'S'
    When the Significant other is updated
    Then I will return error "<error>"
    Examples:
      | Field            | Character | error           |
      | ejPlusCardNumber | b         | SVC_100012_3027 |
#      | ejPlusCardNumber | a         | SVC_100012_3027 |
#      | ejPlusCardNumber | c         | SVC_100012_3027 |
#      | ejPlusCardNumber | d         | SVC_100012_3027 |
#      | ejPlusCardNumber | e         | SVC_100012_3027 |
#      | ejPlusCardNumber | f         | SVC_100012_3027 |
#      | ejPlusCardNumber | g         | SVC_100012_3027 |
#      | ejPlusCardNumber | h         | SVC_100012_3027 |
#      | ejPlusCardNumber | i         | SVC_100012_3027 |
#      | ejPlusCardNumber | j         | SVC_100012_3027 |
#      | ejPlusCardNumber | k         | SVC_100012_3027 |
#      | ejPlusCardNumber | l         | SVC_100012_3027 |
#      | ejPlusCardNumber | m         | SVC_100012_3027 |
#      | ejPlusCardNumber | n         | SVC_100012_3027 |
#      | ejPlusCardNumber | o         | SVC_100012_3027 |
#      | ejPlusCardNumber | p         | SVC_100012_3027 |
#      | ejPlusCardNumber | q         | SVC_100012_3027 |
#      | ejPlusCardNumber | r         | SVC_100012_3027 |
#      | ejPlusCardNumber | t         | SVC_100012_3027 |
#      | ejPlusCardNumber | u         | SVC_100012_3027 |
#      | ejPlusCardNumber | v         | SVC_100012_3027 |
#      | ejPlusCardNumber | x         | SVC_100012_3027 |
#      | ejPlusCardNumber | y         | SVC_100012_3027 |
#      | ejPlusCardNumber | z         | SVC_100012_3027 |

  @FCPH-3344
  Scenario Outline: 9.Number of characters passed for ej Plus validation BR_00430
    Given I have received a valid request to update a Significant Others to the Staff customer
    And the request contains an eJ Plus Membership Number
    But "<Field>" length is "<Length>"
    When the Significant other is updated
    Then I will return error "<error>"
    Examples:
      | Field            | Length | error           |
      | ejPlusCardNumber | 1      | SVC_100012_3027 |
      | ejPlusCardNumber | 4      | SVC_100012_3027 |
      | ejPlusCardNumber | 5      | SVC_100012_3027 |
      | ejPlusCardNumber | 7      | SVC_100012_3027 |

  @FCPH-3344
  Scenario: 10.Validate surname must match the membership surname BR_00410
    Given I have received a valid request to update a Significant Others to the Staff customer
    And the request contains an eJ Plus Membership Number
    But surname does not match the Ej Plus surname membership
    When the Significant other is updated
    Then I will return error "SVC_100000_2074"

  @FCPH-3344
  Scenario Outline: 11.eMail address field validation BR_00841
    Given I have received a valid request to update a Significant Others to the Staff customer
    And request contains "<Email>" which is an email address with an invalid format
    When the Significant other is updated
    Then I will return error "<error>"
    Examples:
      | Email       | error           |
      | bob19@.com  | SVC_100012_3028 |
      | ted77ww.com | SVC_100012_3028 |
      | aa          | SVC_100012_3028 |
      | t72has@fghj | SVC_100012_3028 |
      | @fghj       | SVC_100012_3028 |

  @FCPH-3344
  Scenario: 12.Email is not a registered customer
    Given I have received a valid request to update a Significant Others to the Staff customer
    And request contains a valid email address
    But email is not linked to an existing customer profile
    When the Significant other is updated
    Then I will return error "SVC_100319_3003"

  @FCPH-3344
  Scenario: 13.email is linked to already registered to another Staff account
    Given I have received a valid request to update a Significant Others to the Staff customer
    And request contains a valid email address
    But the belongs to a customer already linked to an other Staff Customer
    When the Significant other is updated
    Then I will return error "SVC_100319_3004"

  @FCPH-3344
  Scenario: 14.Associate significant other to staff account
    Given I have received a valid request to update a Significant Others to the Staff customer
    And request contains a valid email address
    And email address is associated to a registered email account
    And email is not linked to another staff account
    When the Significant other is updated
    Then Significant Other is successfully associated to the Staff Customer

  @FCPH-208
  Scenario Outline: 15.SSR Threshold BR_00382
    Given I have received a valid request to update a the ssrs for a Significant Others to the Staff customer
    And request contains more than "<Threshold>" SSR codes
    When the Significant other is updated
    Then I will return error "SVC_100012_3031"
    Examples:
      | Threshold |
      | 6         |

  @FCPH-3344
  Scenario Outline: 16.Document number length validation BR_00138
    Given I have added a staff Customer with a Significant Other
    And I have received a valid add Identity Document request
    But "<Field>" length for identity document is "<Length>"
    When I process the request for add Identity Document
    Then I will return a "SVC_100012_3029" error
    Examples:
      | Field          | Length |
      | documentNumber | 2      |
      | documentNumber | 36     |

  @FCPH-3344
  Scenario Outline: 17.Document number fields allowed characters BR_00139
    Given I have added a staff Customer with a Significant Other
    And I have received a valid add Identity Document request to the Significant Other
    But field "<Field>" in the request contains "<SpecialChar>" symbol
    When I process the request for add Identity Document
    Then I will return a "<error>" error
    Examples:
      | Field          | SpecialChar | error           |
      | documentNumber | \           | SVC_100012_3030 |
#      | documentNumber | %           | SVC_100012_3030 |
#      | documentNumber | /           | SVC_100012_3030 |
#      | documentNumber | \|          | SVC_100012_3030 |
#      | documentNumber | +           | SVC_100012_3030 |
      | documentNumber | ;           | SVC_100012_3030 |
#      | documentNumber | :           | SVC_100012_3030 |
#      | documentNumber | !           | SVC_100012_3030 |
#      | documentNumber | ?           | SVC_100012_3030 |
      | documentNumber | <           | SVC_100012_3030 |
#      | documentNumber | >           | SVC_100012_3030 |
#      | documentNumber | (           | SVC_100012_3030 |
#      | documentNumber | )           | SVC_100012_3030 |
      | documentNumber | .           | SVC_100012_3030 |
#      | documentNumber | ,           | SVC_100012_3030 |
#      | documentNumber | @           | SVC_100012_3030 |
#      | documentNumber | #           | SVC_100012_3030 |
      | documentNumber | $           | SVC_100012_3030 |
#      | documentNumber | £           | SVC_100012_3030 |
#      | documentNumber | ^           | SVC_100012_3030 |
#      | documentNumber | &           | SVC_100012_3030 |
      | documentNumber | *           | SVC_100012_3030 |


  @FCPH-3344
  Scenario Outline: 18.Document number length validation BR_00138
    Given I have added a staff Customer with a Significant Other and an Identity Document
    And I have received a valid update Identity Document request for the Significant Other
    But "<Field>" length for id document is "<Length>"
    When I process the request for update Identity Document
    Then I will return a "SVC_100012_3029" error
    Examples:
      | Field          | Length |
      | documentNumber | 2      |
      | documentNumber | 36     |

  @FCPH-3344
  Scenario Outline: 19.Document number fields allowed characters BR_00139
    Given I have added a staff Customer with a Significant Other and an Identity Document
    And I have received a valid update Identity Document request for the Significant Other
    But field "<Field>" in the request contains "<SpecialChar>"
    When I process the request for update Identity Document
    Then I will return a "SVC_100012_3030" error
    Examples:
      | Field          | SpecialChar |
      | documentNumber | %           |
#      | documentNumber | \           |
#      | documentNumber | /           |
      | documentNumber | \|          |
#      | documentNumber | +           |
#      | documentNumber | ;           |
      | documentNumber | :           |
#      | documentNumber | !           |
#      | documentNumber | ?           |
#      | documentNumber | <           |
#      | documentNumber | >           |
#      | documentNumber | (           |
      | documentNumber | )           |
#      | documentNumber | .           |
#      | documentNumber | ,           |
      | documentNumber | @           |
#      | documentNumber | #           |
#      | documentNumber | $           |
      | documentNumber | £           |
#      | documentNumber | ^           |
#      | documentNumber | &           |
      | documentNumber | *           |

  @FCPH-3344
  Scenario: 20.Date of Birth field validation BR_00155
    Given I have added a staff Customer with a Significant Other and an Identity Document
    And I have received a valid update Identity Document request for the Significant Other
    And request contains a Date of Birth
    But this does not match the passenger type
    When I process the request for update Identity Document
    Then I will return a "SVC_100000_3034" error

  @FCPH-3344
  Scenario: 21.Remove a single APIS from Significant other
    Given I have added a staff Customer with a Significant Other and an Identity Document
    And I have received a request to remove a single identity document
    When I process the request to remove the identity document
    Then the specified identity document information of the Significant in Customer profile are successfully removed

  @FCPH-3344
  Scenario: 22.Remove all APIS from Significant other
    Given I have added a staff Customer with a Significant Other and an Identity Document
    And I have received a request to remove all the identity documents
    When I process the request to remove the identity document
    Then the specified identity document information of the Significant in Customer profile are successfully removed

  @FCPH-3344
  Scenario: 23.Update significant other
    Given I have received a valid request to update a Significant Others to the Staff customer
    And customer is a valid staff customer
    When the Significant other is updated
    Then the significant other information is successfully updated to the Staff Customer

  @FCPH-3344
  Scenario: 24.Delete significant other
    Given I have added a staff Customer with a Significant Other
    And I delete a Significant Other to the Staff customer
    When the Significant other is deleted
    Then the significant other information is successfully deleted from the Staff Customer

  @FCPH-3344 @regression
  Scenario: 26.Updated information returned when profile retrieved again
    Given I have received a valid request to update a Significant Others to the Staff customer
    And I process the request to update Significant other
    And I have received a valid getCustomerProfile
    When I retrieve the customer profile
    Then I will return updated values for significant other
    And I will see the number of changes still allowed

  @FCPH-3344
  Scenario: 27.Updated information is returned when the profile is retrieved again
    Given I have added a staff Customer with a Significant Other with email
    And I delete a Significant Other to the Staff customer
    And I process the request to delete SignificantOthers
    And I have received an add request to add the same Significant Other with the email
    When I process the request to add the same Significant Other
    Then SignificantOther should be added or updated

  @Sprint27
  @FCPH-9479
  Scenario Outline: 28. Generate an error if eJ Plus status is not complete for updateSignificantOther BR_4004
    Given I have received a valid request to update a Significant Others to the Staff customer
    And the request contains an eJ Plus Membership Number with status other than <status>
    When the Significant other is updated
    Then I will return error "SVC_100000_2088"
    Examples:
      | status    |
      | COMPLETED |

