@FCPH-3425
Feature: Password validation for create customer

  @negative
  Scenario Outline: Validation number of characters in submitted Password
    Given a valid request to create a profile for customer
    But the password length is "<passwordLength>"
    When the request is validated
    Then a "<passwordLengthError>" error is returned
    Examples:
      | passwordLength | passwordLengthError |
      | 5              | SVC_100047_2042     |
      | 21             | SVC_100047_2043     |

  @negative
  Scenario: Validate the password does not contain a space
    Given a valid request to create a customer profile
    But the password has a space in it
    When the request is validated
    Then a "SVC_100047_2039" error is returned

  @negative
  Scenario Outline: Validation of password is not part of guessable word list BR_00844
    Given a valid request to create a customer profile
    But the password has a guessable word "<word>"
    When the request is validated
    Then a "SVC_100047_2041" error is returned
    Examples:
      | word       |
      | easyjet    |
      | 123456     |
#      | password   |
#      | 12345      |
#      | 12345678   |
#      | qwerty     |
#      | 1234567890 |
#      | baseball   |
#      | dragon     |
#      | football   |
#      | 1234567    |
#      | monkey     |
#      | letmein    |
#      | abc123     |
#      | 111111     |
#      | mustang    |
#      | access     |
#      | shadow     |
#      | master     |
#      | michael    |
#      | superman   |
#      | 696969     |
#      | 123123     |
#      | batman     |
#      | trustno1   |

  @negative
  Scenario Outline: Validate symbols allowed BR_00847
    Given a valid request to create a customer profile
    But the password has symbol "<symbol>"
    When the request is validated
    Then a illegal password "<error>" error is returned
    Examples:
      | symbol | error           |
      | £      | SVC_100047_2040 |
      | €      | SVC_100047_2040 |
      | #      | SVC_100047_2040 |
      | &      | SVC_100047_2040 |
      | +      | SVC_100047_2040 |

  Scenario Outline: Calculate the password strength score and check returned password strength
    Given a valid request to create a customer profile
    But the password is set to "<password>"
    When the request is validated
    Then a password strength "<strength>"is returned
    Examples:
      | password             | strength |
      | hellos               | WEAK     |
      | 1aWd7R2768ftJuk      | MEDIUM   |
      | ?aWd;R:}{ftJuk       | MEDIUM   |
      | Z%:3xV\U$Y7kERX      | STRONG   |
      | AV4u*uE7rN7qNgR`2:%3 | STRONG   |