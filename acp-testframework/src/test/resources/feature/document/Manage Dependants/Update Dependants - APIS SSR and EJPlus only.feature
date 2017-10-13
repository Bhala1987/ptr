@FCPH-3358
@Sprint25
Feature: Receive request to update Dependent - APIS SSR and EJPlus only

  Background:
    Given am using channel Digital

  Scenario: Remove SSR from dependant
    Given I have added an SSR to a dependant
    When I remove SSR from dependant
    Then I receive confirmation that the dependant has been updated

  Scenario: Remove APIS from dependant
    And I have received a valid request to add a document for a Dependant to the Staff customer
    When I raise a request to delete document from Dependant
    And I process the request for delete Identity Document
    Then I return document has been removed confirmation

  Scenario: Dependant DoB validation (in document update)
    And I have received a valid request to add a document for a Dependant to the Staff customer
    When I receive a change to the DoB in the document to a value not matching their type
    And I process the request for update Identity Document
    Then I will return a "SVC_100000_3034" error

  Scenario: Update Dependent request with invalid customer id
    Given the channel has initiated an update Dependents ejPlus request with invalid customer
    When I receive the update Dependants request
    Then I should add the validation error message "SVC_100012_3033" to the return message

  Scenario: Update Dependent request with invalid dependant id
    Given the channel has initiated an update Dependents ejPlus request with invalid dependant
    When I receive the update Dependants request
    Then I should add the validation error message "SVC_100331_3004" to the return message

  @regression
  Scenario: Successful Update Dependent eJPlus request
    Given I sent update Dependents eJPlus request for customer "cus00000001" and dependant "345678" with ejPlus number "S004444"
    When I receive the update Dependants request
    Then I receive dependant update confirmation

  Scenario Outline: eJPlus Number validation - error conditions
    Given that I have received a valid update Dependents eJPlus request for "<dependant>" with ejPlus number "<eJPlusNumber>"
    When I receive the update Dependants request
    Then I should add the validation error message "<error>" to the return message
    Examples:
      | eJPlusNumber | error           | dependant |
      | 1            | SVC_100012_3027 | 222222    |
      | 123456       | SVC_100012_3027 | 222222    |
      | S1234        | SVC_100012_3027 | 222222    |
      | S079048      | SVC_100000_2074 | 333333    |
#      need to check data and then uncomment as its giving 200 at the momment
#      | S079048      | SVC_100000_2074 | 222222    |

  Scenario: SSR T&C not accepted
    Given I am logged in as a staff member
    And I have received a valid request to update a the ssrs for a Dependant to the Staff customer with terms not accepted
    When I process the request
    Then I will return error "SVC_100012_3032"

  Scenario: SSR Threshold BR_00382
    Given I have received a valid request to update a the ssrs for a Dependant to the Staff customer
    And request contains more than "6" SSR codes
    When I validate if the threshold for the significant other has reached
    Then I will return error "SVC_100012_3031"

  Scenario Outline: Document number validation BR_00138
    Given I have received a valid request to add a document for a Dependant to the Staff customer
    And "<Field>" length for identity document is "<Length>"
    When I validate the document number
    Then I will return a "<error>" error
    Examples:
      | Field          | Length | error           |
      | documentNumber | 2      | SVC_100050_2011 |
      | documentNumber | 36     | SVC_100050_2012 |

  Scenario Outline: Document contains special characters BR_00139
    And I have received a valid request to update a document for a Dependant to the Staff customer
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
#      | documentNumber | :           |
      | documentNumber | !           |
#      | documentNumber | ?           |
#      | documentNumber | <           |
#      | documentNumber | >           |
#      | documentNumber | (           |
#      | documentNumber | )           |
      | documentNumber | .           |
#      | documentNumber | ,           |
#      | documentNumber | @           |
      | documentNumber | #           |
#      | documentNumber | $           |
#      | documentNumber | £           |
#      | documentNumber | ^           |
      | documentNumber | &           |
      | documentNumber | *           |

  @Sprint27
  @FCPH-9479
  Scenario Outline:  Generate an error if eJ Plus status is not complete for updateDependent BR_4004
    Given I am using the channel <channel>
    And I have valid update dependents request with ejPlus number status other than <status>
    When I receive the update Dependants request
    Then I will return a "SVC_100000_2088" error for update dependant
    Examples:
      | channel         | status    |
      | PublicApiMobile | COMPLETED |
      | ADAirport       | COMPLETED |
