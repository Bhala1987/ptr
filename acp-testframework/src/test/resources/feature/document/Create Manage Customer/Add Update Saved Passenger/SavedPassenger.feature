@FCPH-3203
Feature: Receive a request to Add/Update Saved Passenger

  Background: Add a passenger to an existing customer, usufull for the other scenarios
    Given I have added a passenger to an existing customer

  Scenario Outline: Saved Passenger Update request received in required format
    And I have received a valid updateSavedPassenger request with missing "<field>"
    When I process the request for updateSavedPassenger
    Then I will return an "SVC_100336_1002" error
    Examples:
      | field       |
      | type        |
      | title       |
      | firstName   |
      | lastName    |
      | age         |
      | phoneNumber |

  Scenario Outline: Age validation
    And I have received a valid updateSavedPassenger request
    But the request contains "<age>" as age and "<type>" as passenger type
    When I process the request for updateSavedPassenger
    Then I will return an "SVC_100012_3016" error
    Examples:
      | type   | age |
      | child  | 16  |
      | child  | 1   |
      | infant | -1  |
      | infant | 2   |

  Scenario Outline: fields length validation
    And I have received a valid updateSavedPassenger request
    But the field "<Field>" in the request has "<Length>" length
    When I process the request for updateSavedPassenger
    Then I will return an "<errorCode>" error
    Examples:
      | Field        | Length | errorCode       |
      | fPhonenumber | 1      | SVC_100012_3020 |
#      | fPhonenumber | 5      | SVC_100012_3020 |
#      | fPhonenumber | 19     | SVC_100012_3020 |
      | fPhonenumber | 40     | SVC_100012_3020 |
      | fFirstname   | 1      | SVC_100012_3022 |
      | fFirstname   | 31     | SVC_100012_3022 |
      | fLastname    | 51     | SVC_100012_3023 |
      | fLastname    | 1      | SVC_100012_3023 |
      | fNIF         | 8      | SVC_100000_3026 |
      | fNIF         | 1      | SVC_100000_3026 |

  Scenario Outline: Document number length validation
    And I have added a identity document to an existing passenger
    Given I have received a valid updateIdentityDocument request
    But the field "<Field>" in the request has "<Length>" length
    When I process the request for updateIdentityDocument
    Then I will return an "SVC_100012_3029" error
    Examples:
      | Field           | Length |
      | fDocumentnumber | 36     |
      | fDocumentnumber | 2      |

  Scenario Outline: field validation
    And I have received a valid updateSavedPassenger request
    But the field "<Field>" in the request contains "<SpecialChar>" symbol
    When I process the request for updateSavedPassenger
    Then I will return an "<errorCode>" error
    Examples:
      | Field      | SpecialChar | errorCode       |
      | fLastname  | \|          | SVC_100012_3025 |
      | fFirstname | %           | SVC_100012_3025 |
      | fLastname  | 0           | SVC_100012_3025 |
      | fPhonenumber | %           | SVC_100012_3019 |
#      | fFirstname   | \           | SVC_100012_3025 |
#      | fPhonenumber | \           | SVC_100012_3019 |
#      | fPhonenumber | /           | SVC_100012_3019 |
      | fPhonenumber | \|          | SVC_100012_3019 |
#      | fPhonenumber | +           | SVC_100012_3019 |
#      | fPhonenumber | ;           | SVC_100012_3019 |
#      | fPhonenumber | :           | SVC_100012_3019 |
      | fPhonenumber | !           | SVC_100012_3019 |
#      | fPhonenumber | ?           | SVC_100012_3019 |
#      | fPhonenumber | <           | SVC_100012_3019 |
#      | fPhonenumber | >           | SVC_100012_3019 |
#      | fPhonenumber | (           | SVC_100012_3019 |
      | fPhonenumber | )           | SVC_100012_3019 |
#      | fPhonenumber | .           | SVC_100012_3019 |
#      | fPhonenumber | ,           | SVC_100012_3019 |
#      | fPhonenumber | @           | SVC_100012_3019 |
#      | fPhonenumber | #           | SVC_100012_3019 |
      | fPhonenumber | $           | SVC_100012_3019 |
#      | fPhonenumber | £           | SVC_100012_3019 |
#      | fPhonenumber | ^           | SVC_100012_3019 |
#      | fPhonenumber | &           | SVC_100012_3019 |
      | fPhonenumber | *           | SVC_100012_3019 |
#      | fFirstname   | +           | SVC_100012_3025 |
#      | fFirstname   | /           | SVC_100012_3025 |
#      | fFirstname   | \|          | SVC_100012_3025 |
      | fFirstname   | 0           | SVC_100012_3025 |
#      | fFirstname   | 1           | SVC_100012_3025 |
#      | fFirstname   | 2           | SVC_100012_3025 |
#      | fFirstname   | 3           | SVC_100012_3025 |
#      | fFirstname   | 4           | SVC_100012_3025 |
      | fFirstname   | 5           | SVC_100012_3025 |
#      | fFirstname   | 6           | SVC_100012_3025 |
#      | fFirstname   | 7           | SVC_100012_3025 |
#      | fFirstname   | 8           | SVC_100012_3025 |
#      | fFirstname   | 9           | SVC_100012_3025 |
#      | fFirstname   | +           | SVC_100012_3025 |
      | fFirstname   | ;           | SVC_100012_3025 |
#      | fFirstname   | :           | SVC_100012_3025 |
#      | fFirstname   | !           | SVC_100012_3025 |
      | fFirstname   | ?           | SVC_100012_3025 |
#      | fFirstname   | <           | SVC_100012_3025 |
#      | fFirstname   | >           | SVC_100012_3025 |
#      | fFirstname   | (           | SVC_100012_3025 |
#      | fFirstname   | )           | SVC_100012_3025 |
      | fFirstname   | .           | SVC_100012_3025 |
#      | fFirstname   | ,           | SVC_100012_3025 |
      | fFirstname   | @           | SVC_100012_3025 |
#      | fFirstname   | #           | SVC_100012_3025 |
#      | fFirstname   | $           | SVC_100012_3025 |
#      | fFirstname   | £           | SVC_100012_3025 |
#      | fFirstname   | ^           | SVC_100012_3025 |
#      | fFirstname   | &           | SVC_100012_3025 |
      | fFirstname   | *           | SVC_100012_3025 |
#      | fLastname    | 1           | SVC_100012_3025 |
#      | fLastname    | 2           | SVC_100012_3025 |
#      | fLastname    | 3           | SVC_100012_3025 |
#      | fLastname    | 4           | SVC_100012_3025 |
#      | fLastname    | 5           | SVC_100012_3025 |
#      | fLastname    | 6           | SVC_100012_3025 |
      | fLastname    | 7           | SVC_100012_3025 |
#      | fLastname    | 8           | SVC_100012_3025 |
#      | fLastname    | 9           | SVC_100012_3025 |
#      | fLastname    | +           | SVC_100012_3025 |
      | fLastname    | ;           | SVC_100012_3025 |
#      | fLastname    | :           | SVC_100012_3025 |
#      | fLastname    | \           | SVC_100012_3025 |
#      | fLastname    | !           | SVC_100012_3025 |
      | fLastname    | ?           | SVC_100012_3025 |
#      | fLastname    | <           | SVC_100012_3025 |
#      | fLastname    | (           | SVC_100012_3025 |
#      | fLastname    | )           | SVC_100012_3025 |
      | fLastname    | .           | SVC_100012_3025 |
#      | fLastname    | ,           | SVC_100012_3025 |
#      | fLastname    | /           | SVC_100012_3025 |
      | fLastname    | @           | SVC_100012_3025 |
      | fLastname    | #           | SVC_100012_3025 |
#      | fLastname    | $           | SVC_100012_3025 |
#      | fLastname    | £           | SVC_100012_3025 |
#      | fLastname    | %           | SVC_100012_3025 |
#      | fLastname    | ^           | SVC_100012_3025 |
#      | fLastname    | &           | SVC_100012_3025 |
      | fLastname    | *           | SVC_100012_3025 |


  Scenario Outline: Document number fields allowed characters
    And I have added a identity document to an existing passenger
    And I have received a valid updateIdentityDocument request
    But the field "<Field>" in the request contains "<SpecialChar>" symbol
    When I process the request for updateIdentityDocument
    Then I will return an "<errorCode>" error
    Examples:
      | Field           | SpecialChar | errorCode       |
      | fDocumentNumber | %           | SVC_100012_3030 |
#      | fDocumentNumber | \           | SVC_100012_3030 |
#      | fDocumentNumber | /           | SVC_100012_3030 |
      | fDocumentNumber | \|          | SVC_100012_3030 |
#      | fDocumentNumber | +           | SVC_100012_3030 |
#      | fDocumentNumber | ;           | SVC_100012_3030 |
#      | fDocumentNumber | :           | SVC_100012_3030 |
#      | fDocumentNumber | !           | SVC_100012_3030 |
      | fDocumentNumber | ?           | SVC_100012_3030 |
#      | fDocumentNumber | <           | SVC_100012_3030 |
#      | fDocumentNumber | >           | SVC_100012_3030 |
#      | fDocumentNumber | (           | SVC_100012_3030 |
      | fDocumentNumber | )           | SVC_100012_3030 |
#      | fDocumentNumber | .           | SVC_100012_3030 |
#      | fDocumentNumber | ,           | SVC_100012_3030 |
#      | fDocumentNumber | @           | SVC_100012_3030 |
      | fDocumentNumber | #           | SVC_100012_3030 |
      | fDocumentNumber | $           | SVC_100012_3030 |
#      | fDocumentNumber | £           | SVC_100012_3030 |
#      | fDocumentNumber | ^           | SVC_100012_3030 |
#      | fDocumentNumber | &           | SVC_100012_3030 |
      | fDocumentNumber | *           | SVC_100012_3030 |

  Scenario Outline: SSR Threshold
    And I have received a valid updateSSR request
    And the request contains a SSRs codes
    But more than "<Number>" SSR have been passed in the request
    When I process the request for savedSSR
    Then I will return an "SVC_100012_3031" error
    Examples:
      | Number |
      | 10     |
      | 6      |

  Scenario Outline: eJ Plus Membership Number length validation
    And I have received a valid updateSavedPassenger request
    And the request contains a eJ Plus Membership Number
    But the field "<Field>" in the request has "<Length>" length
    When I process the request for updateSavedPassenger
    Then I will return an "SVC_100012_3027" error
    Examples:
      | Field       | Length |
      | fMembership | 1      |
      | fMembership | 4      |
      | fMembership | 5      |
      | fMembership | 7      |
      | fMembership | 9      |
      | fMembership | 12     |

  Scenario Outline: eJ Plus Membership Number length validation
    And I have received a valid updateSavedPassenger request
    And the request contains a eJ Plus Membership Number
    And the "<Field>" in the request has "6" length
    But the first character "<Character>" of the "<Field>" passed in the request is not a "S"
    When I process the request for updateSavedPassenger
    Then I will return an "SVC_100012_3027" error
    Examples:
      | Field       | Character |
      | fMembership | a         |
#      | fMembership | b         |
#      | fMembership | c         |
#      | fMembership | d         |
#      | fMembership | e         |
      | fMembership | f         |
#      | fMembership | g         |
#      | fMembership | h         |
#      | fMembership | i         |
#      | fMembership | j         |
#      | fMembership | k         |
#      | fMembership | l         |
#      | fMembership | m         |
#      | fMembership | n         |
      | fMembership | o         |
#      | fMembership | p         |
#      | fMembership | q         |
#      | fMembership | r         |
#      | fMembership | t         |
#      | fMembership | u         |
#      | fMembership | v         |
      | fMembership | x         |
#      | fMembership | y         |
#      | fMembership | z         |


  Scenario: eJ Plus Membership Number does not match with Surname BR_00410
    And I have received a valid updateSavedPassenger request
    And the request contains a eJ Plus Membership Number
    But the EJplusmemebership not match the surname of the passenger
    When I process the request for updateSavedPassenger
    Then I will return an "SVC_100000_2074" error

  Scenario Outline: eMail address field validation BR_00841
    And I have received a valid updateSavedPassenger request
    And the request contains an email with invalid format "<InvalidEmailFormat>"
    When I process the request for updateSavedPassenger
    Then I will return an "SVC_100012_3028" error
    Examples:
      | InvalidEmailFormat |
      | bob19@.com         |
      | ted77ww.com        |
      | aa                 |
      | t72has@fghj        |
      | @fghj              |

  Scenario: Date of Birth field validation BR_00155
    And I have added a identity document to an existing passenger
    And I have received a valid updateIdentityDocument request
    And the request contains a Date of Birth
    But this does not match with the passenger type
    When I process the request for updateIdentityDocument
    Then I will return an "SVC_100047_2027" error

  @regression
  Scenario: All the validation rules passes successfully for saved passenger
    And I have received a valid updateSavedPassenger request
    And the request respect the restriction of the BR
    When I process the request for updateSavedPassenger
    Then I will store the Saved passenger details
    And I will verify the successfully response

  Scenario: All the validation rules passes successfully for identity document
    And I have added a identity document to an existing passenger
    And I have received a valid updateIdentityDocument request
    When I process the request for updateIdentityDocument
    Then I will store the Identity document details
    And I will verify the successfully response for the new document

  Scenario: All the validation rules passes successfully for ssr data
    And I have received a valid updateSSR request
    When I process the request for savedSSR
    Then I will store the SSR data details
    And I will verify the successfully response

  Scenario: Remove saved passenger
    And I have received a valid removeSavedPassenger request
    When I process the request for removeSavedPassenger
    Then I will remove the saved passenger from the customer profile
    And I will return a "SVC_100338_3001" message

  @Sprint27
  @FCPH-9479
  Scenario Outline: Generate an error if eJ Plus status is not complete for updateSavedPassenger BR_4004
    Given I am using the channel <channel>
    And I have received a valid updateSavedPassenger request
    And the request contains a eJ Plus Membership Number with status other than <status>
    When I process the request for updateSavedPassenger
    Then I will return an "SVC_100000_2088" error
    Examples:
      | channel   | status    |
      | Digital   | COMPLETED |
      | ADAirport | COMPLETED |

  @Sprint27
  @FCPH-9479
  Scenario Outline: Generate an error if eJ Plus status is not complete for createSavedPassenger BR_4004
    Given I am using the channel <channel>
    And I have received a valid save passenger request
    And the request contains a eJ Plus Membership Number with status other than <status>
    When I added a passenger to an existing customer
    Then I will return an "SVC_100000_2088" error
    Examples:
      | channel      | status    |
      | PublicApiB2B | COMPLETED |
      | ADAirport    | COMPLETED |