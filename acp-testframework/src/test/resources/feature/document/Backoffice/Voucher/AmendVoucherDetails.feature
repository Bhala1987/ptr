@TeamA @Sprint31
@backoffice:FCPH-9837
Feature: Amend the Voucher in the back office

  Scenario: Amend voucher details in the back office
    Given that there are vouchers created
    And  I am in the back office
    And  I search for a Voucher
    When I select to amend the voucher
    Then I can amend the email address, Active to and from dates, active flag

  Scenario:Create modification event against the voucher
    Given that there are vouchers created
    When I amend the voucher
    Then I must enter a free text reason why I have made a change
    And a modification event is created against the voucher
    And the Modification reason and  Modification Date and Time with User ID should be stored


  Scenario: Move the monies from the voucher to a fraudulent credit file
    Given that I have amended the voucher
    And added a reason of "Fraudulent"
    When I save the changes to the voucher
    Then I set the Outstanding Current Balance for the Voucher to zero
    And I transfer the balance to the Fraudulent Voucher Credit File Fund for the currency of the voucher
