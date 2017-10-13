@FCPH-2817
Feature: Generate Customer Registration email

  @manual
  Scenario: Generate Customer Registration email
    When I create new customer profile
    Then I should generate the Customer Registration email containing Customer First Name and Surname
    And Email should have the static welcome message content
    And Email should have link to easyjet.com

  @manual
  Scenario: Send Registration email to cusomer registered email address
    When I create new customer profile
    Then I should send the Email to the registered email address of the customer