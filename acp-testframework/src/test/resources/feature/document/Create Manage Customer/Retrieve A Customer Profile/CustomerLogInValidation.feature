@FCPH-205 @FCPH-9108
Feature: Receive request to Log in to member account

  @negative
  Scenario Outline: Validate all mandatory fields error validation
    Given a customer account exists with a known password
    When I login with missing "<parameter>"
    Then error for "<error>" is returned
    Examples:
      | parameter       | error           |
      | MissingEmail    | SVC_100046_2006 |
      | MissingPassword | SVC_100046_2007 |

  Scenario: Verify successful login
    Given I am using channel Digital
    And a customer account exists with a known password
    And I send a request to the logout service
    When I login with valid credentials
    Then I should be successfully logged in

  @Sprint26 @FCPH-9108
  Scenario: Verify successful login with remember me cookie.
    Given a customer account exists with a known password
    When I login with valid credentials and remember me
    Then I should be successfully logged in
    And I should have a remember me cookie

  @negative
  Scenario: Only Digital channel is allowed to login
    Given a customer account exists with a known password
    When I login with valid credentials using the "ADAirport" channel
    Then I am informed that only Digital can access this channel

  @negative
  Scenario: Verify login fail with invalid credentials
    Given I am using channel Digital
    And a customer account exists with a known password
    When I login with invalid credentials
    Then I should not be logged in

  @negative
  Scenario: Verify login fail with invalid email credentials
    Given a customer account exists with a known password
    When I provide a different email address
    Then I should not be logged in

  @manual
  Scenario: Verify time stores upon successful login
    Given a customer account exists with a known password
    When I login with valid credentials
    Then I should see time recorded

  @manual
  Scenario: Verify failed attempt doesn't increase after account is locked in a single session
    Given a customer account exists with a known password
    And configuration is in place for maximum number of failed attempts to get lock
    When I breach the maximum login attempts in a single session
    And the account is locked
    And I login with invalid credentials
    Then I should see failed attempt not increased than configured maximum number of failed attempts

  @negative
  Scenario: Verify account lock after maximum number of failed attempts in a single session
    Given a customer account exists with a known password
    And configuration is in place for maximum number of failed attempts to get lock
    When I breach the maximum login attempts in a single session
    Then the account is locked

  @negative
  Scenario: Verify account lock after maximum number of failed attempts over multiple sessions
    Given a customer account exists with a known password
    And configuration is in place for maximum number of failed attempts to get lock
    When I breach the maximum login attempts over multiple sessions
    Then the account is locked

  @manual
  Scenario Outline: Verify account time recorded after maximum number of failed attempts
    Given a customer account exists with a known password
    And configuration is in place for maximum number of failed attempts to get lock
    When it reaches to maximum failed attempts limit from "<session>"
    Then I should see account lock time recorded
    Examples:
      | session           |
      | single_session    |
      | multiple_sessions |

  @negative
  Scenario: Verify unable to login to a disabled account
    Given a customer account exists with a known password
    And the account has been disabled
    When I login with valid credentials
    Then I am informed that the account is disabled

  @manual
  Scenario: Verify staff member login
    Given a staff account exists with a known password
    When I login with staff credentials
    Then I should successfully logged in

  @manual
  Scenario: Verify success login after accounts get unlocked
    Given a customer account exists with a known password is disabled
    When the account is re-enabled
    And I try to login with valid credentials
    Then I should successfully logged in