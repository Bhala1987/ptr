@FCPH-7423
@Sprint26
Feature: Add significant other to user group

  Scenario: Add significant other to the user group
    Given I add a Significant Other to the Staff customer
      And it contains a valid email address
     When the Significant Other is added to the Staff Member
     Then the Significant Other user is allow to find flight with Staff fare

  Scenario: The significant other user contains all significant others from the staff customer
    Given a significant other customer
      And I have a valid customer profile
     When I retrieve the customer profile
     Then I get the significant other of the linked staff customer

  Scenario Outline: Staff fares to a significant other user
    Given a significant other customer
      And I login with the significant other customer
     When I find for a valid Flight with <fareType>
     Then Fare Type <fareType> is included in the results
    Examples:
      | fareType      |
      | Staff         |
      | StaffStandard |
      | Standby       |

  Scenario: Change user group back to standard customer updating email
    Given a significant other customer
      And I update Significant Other to delete the email
     When the Significant other is updated
     Then the previous Significant Other user is not allowed to find a flight with Staff fare

  Scenario: Change user group back to standard customer deleting Significant Other
    Given a significant other customer
      And I delete a Significant Other to the Staff customer
     When the Significant other is deleted
     Then the previous Significant Other user is not allowed to find a flight with Staff fare