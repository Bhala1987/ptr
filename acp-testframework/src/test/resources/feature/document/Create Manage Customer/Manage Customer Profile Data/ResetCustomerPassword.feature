@FCPH-3347
Feature: Generate Password reset email

  Scenario: Receive Password reset request for invalid email ID
    Given I have valid customer profile
    And I have received a valid password reset request
    But the email ID is not associated to the customer
    When I process the request for resetPassword
    Then I should return an error "SVC_100066_2002" message

  Scenario: Receive Password reset request anonymous customer for invalid email ID
    Given I have received a valid password reset request for anonymous customer
    But the email ID is not associated to the customer
    When I process the request for resetPassword for anonymous customer
    Then I should return an error "SVC_100066_2002" message

  @regression
  Scenario: Generate a temporary single use token BR_00845
    Given I have valid customer profile
    And I have received a valid password reset request
    And I set the profile status to locked
    When I process the request for resetPassword
    Then I will generate a temporary Token

  @manual
  Scenario: Generate account Password reset email
    Given I have valid customer profile
    And I have received a valid password reset request
    And I set the profile status to locked
    When I process the request for resetPassword
    Then I will generate a temporary single use Token valid for hour "24"
    And I will generate a password reset email which will contain Customer Name, Password reset content, URL containing token for a single use
    And I will send the email to the customer registered email address
