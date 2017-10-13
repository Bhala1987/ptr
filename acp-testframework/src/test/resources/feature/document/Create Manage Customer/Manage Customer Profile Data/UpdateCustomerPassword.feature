Feature: Update customer password
  Update status of the account to active for password reset/account locked

  @FCPH-3348
  Scenario Outline: Validation number of characters in submitted Password BR_00836
    Given I create a new customer and execute the login
    When I send a request to update password from "<channel>" with "<password>"
    Then I will return a error message to the channel "<code>"
    Examples:
      | channel | password                             | code            |
      | Digital | qwuf                                 | SVC_100047_2042 |
      | Digital | aaabbcccddd1aaabbcccddd1aaabbcccddd1 | SVC_100047_2043 |
      | Digital | aaabbcc cddd1aa1                     | SVC_100047_2039 |

  @FCPH-3348
  Scenario Outline: Validation of password is not part of guessable word list BR_00844
    Given I create a new customer and execute the login
    When I send a request to update password from "<channel>" with "<password>"
    Then I will return a error message to the channel "<code>"
    Examples:
      | channel | password | code            |
      | Digital | password | SVC_100047_2041 |
      | Digital | easyjet  | SVC_100047_2041 |
      | Digital | baseball | SVC_100047_2041 |

  @FCPH-3348
  Scenario Outline: Validate symbols allowed BR_00847
    Given I create a new customer and execute the login
    When I send a request to update password from "<channel>" with "<password>"
    Then I will return a error message to the channel "<code>"
    Examples:
      | channel | password     | code            |
      | Digital | aabbcccdd£   | SVC_100047_2040 |
      | Digital | €€easyjetere | SVC_100047_2040 |
      | Digital | ba##seb56all | SVC_100047_2040 |

  @FCPH-3348
  @BR:BR_00848,BR_00849,BR_00853,BR_00852,BR_00854,BR_1014
  Scenario Outline: Calculate the password strength score
    Given I create a new customer and execute the login
    When I send a request to update password from "<channel>" with "<password>"
    Then I can login with the new "<password>"
    And I should calculate the password "<strength>" score
  @regression
    Examples:
      | channel | password    | strength |
      | Digital | ACdfg-SCdge | Medium   |
    Examples:
      | channel | password             | strength |
      | Digital | PArdFG138~5AAbhj??sj | Strong   |
      | Digital | 123412345            | Weak     |

  @FCPH-2766
  Scenario Outline: Receieve a update password request with missing currentPassword & passwordResetToken manadatory field
    Given I create a new customer profile
    And I receive the request to updatePassword of the customer
    But the customer account status is locked
    And the request does not contain the field "<Field>"
    When I validate the request to updatePassword
    Then I will return a error message to the channel "SVC_100270_2005"
    Examples:
      | Field              |
      | passwordResetToken |

  @FCPH-2766
  Scenario Outline: Receieve a update password request with missing newPassword manadatory field
    Given I create a new customer profile
    And I receive the request to updatePassword of the customer
    But the customer account status is locked
    And the request does not contain the field "<Field>"
    When I validate the request to updatePassword
    Then I will return a error message to the channel "SVC_100270_2004"
    Examples:
      | Field       |
      | newPassword |

  @FCPH-2766
  Scenario Outline: Validation length (min) in submitted Password BR_00836
    Given I create a new customer profile
    And I receive the request to updatePassword of the customer
    But the customer account status is locked
    And the request contains a token
    And the request contains a new password
    But the new field "<Field>" has lenght "<Length>"
    When I validate the request to updatePassword
    Then I will return a error message to the channel "SVC_100047_2042"
    Examples:
      | Field        | Length |
      | fNewpassword | 1      |
      | fNewpassword | 5      |

  @FCPH-2766
  Scenario Outline: Validation length (sup) in submitted Password BR_00836
    Given I create a new customer profile
    And I receive the request to updatePassword of the customer
    But the customer account status is locked
    And the request contains a token
    And the request contains a new password
    But the new field "<Field>" has lenght "<Length>"
    When I validate the request to updatePassword
    Then I will return a error message to the channel "SVC_100047_2043"
    Examples:
      | Field        | Length |
      | fNewpassword | 21     |
      | fNewpassword | 40     |

  @FCPH-2766
  Scenario Outline: Validation space in submitted Password BR_00836
    Given I create a new customer profile
    And I receive the request to updatePassword of the customer
    But the customer account status is locked
    And the request contains a token
    And the request contains a new password
    But the field "<Field>" contains space
    When I validate the request to updatePassword
    Then I will return a error message to the channel "SVC_100047_2039"
    Examples:
      | Field        |
      | fNewpassword |

  @FCPH-2766
  Scenario Outline: Validation guessable word list in submitted Password BR_00844
    Given I create a new customer profile
    And I receive the request to updatePassword of the customer
    But the customer account status is locked
    And the request contains a token
    And the request contains a new password
    But the field "<Field>" is part of the guessable word list with value "<Value>"
    When I validate the request to updatePassword
    Then I will return a error message to the channel "SVC_100047_2041"
    Examples:
      | Field        | Value    |
      | fNewpassword | password |
      | fNewpassword | easyjet  |
#      | fNewpassword | baseball   |
#      | fNewpassword | football   |
#      | fNewpassword | easyjet    |
#      | fNewpassword | 123456     |
#      | fNewpassword | password   |
#      | fNewpassword | 12345      |
#      | fNewpassword | 12345678   |
#      | fNewpassword | qwerty     |
#      | fNewpassword | 1234567890 |
#      | fNewpassword | baseball   |
#      | fNewpassword | dragon     |
#      | fNewpassword | football   |
#      | fNewpassword | 1234567    |
#      | fNewpassword | monkey     |
#      | fNewpassword | letmein    |
#      | fNewpassword | abc123     |
#      | fNewpassword | 111111     |
#      | fNewpassword | mustang    |
#      | fNewpassword | access     |
#      | fNewpassword | shadow     |
#      | fNewpassword | master     |
#      | fNewpassword | michael    |
#      | fNewpassword | superman   |
#      | fNewpassword | 696969     |
#      | fNewpassword | 123123     |
#      | fNewpassword | batman     |
#      | fNewpassword | trustno1   |

  @FCPH-2766
  Scenario Outline: Validate symbols allowed in submitted Password BR_00847
    Given I create a new customer profile
    And I receive the request to updatePassword of the customer
    But the customer account status is locked
    And the request contains a token
    And the request contains a new password
    But the field "<Field>" contains a symbol "<SpecialChar>" which is not allowed
    When I validate the request to updatePassword
    Then I will return a error message to the channel "SVC_100047_2040"
    Examples:
      | Field        | SpecialChar |
      | fNewpassword | \|          |
      | fNewpassword | £           |
      | fNewpassword | €           |
      | fNewpassword | #           |
      | fNewpassword | +           |

  @FCPH-2766
  Scenario Outline: Calculate strength score in submitted Password BR_00848, BR_00849, BR_00853, BR_00852, BR_00854, FCPBR-1014
    Given I create a new customer profile
    And I receive the request to updatePassword of the customer
    But the customer account status is locked
    And the request contains a token
    And the field "<Field>" is part of the guessable word list with value "<Value>"
    When I validate the request to updatePassword
    And I should calculate the password "<Score>" score
    Examples:
      | Field        | Value                | Score  |
      | fNewpassword | 123412345            | Weak   |
      | fNewpassword | ACdfg-SCdge          | Medium |
      | fNewpassword | PArdFG1~385A-Abhjsj  | Strong |
      | fNewpassword | hellos               | Weak   |
      | fNewpassword | 1aWd7R2768ftJuk      | Medium |
      | fNewpassword | ?aWd;R:}{ftJuk       | Medium |
      | fNewpassword | Z%:3xV\U$Y7kERX      | Strong |
      | fNewpassword | AV4u*uE7rN7qNgR`2:%3 | Strong |

  @FCPH-2766
  Scenario Outline: Store password against the profile
    Given I create a new customer profile
    And I receive the request to updatePassword of the customer
    And the customer account status is locked
    And the request contains a token
    And the request contains a new password
    And the field "<Field>" contains a symbol "<SpecialChar>"
    When I validate the request to updatePassword
    Then I should store the password against the profile
    And set the account status to Active
    And I will verify any Saved APIS, Saved Payment methods, SSR and Saved Passengers details from the Customer's profile are present
    Examples:
      | Field        | SpecialChar |
      | fNewpassword | ~           |
#      | fNewpassword | !           |
#      | fNewpassword | -           |
      | fNewpassword | $           |
      | fNewpassword | @           |
#      | fNewpassword | _           |
      | fNewpassword | %           |
      | fNewpassword | \/          |
#      | fNewpassword | ^           |
#      | fNewpassword | =           |
      | fNewpassword | ;           |
#      | fNewpassword | :           |
      | fNewpassword | *           |
#      | fNewpassword | '           |
#      | fNewpassword | {           |
#      | fNewpassword | }           |
#      | fNewpassword | <           |
#      | fNewpassword | >           |
      | fNewpassword | ?           |
#      | fNewpassword | [           |
#      | fNewpassword | ]           |
#      | fNewpassword | (           |
#      | fNewpassword | )           |
      | fNewpassword | .           |
#      | fNewpassword | ,           |
      | fNewpassword | ?           |

  @FCPH-2766
  @manual
  Scenario: Verify audit record creation in customer audit against the reset password
    Given I create a new customer profile
    And I receive the request to updatePassword of the customer
    And the customer account status is locked
    And the request contains a token
    And the request contains a new password
    When I validate the request to updatePassword
    Then I should store the password against the profile
    And I will create an audit record with Date and Time, User Id, Reset Password event

  @FCPH-2766
  Scenario: Return confirmation and strength score
    Given I create a new customer profile
    And I receive the request to updatePassword of the customer
    And the customer account status is locked
    When I validate the request to updatePassword
    Then I should store the password against the profile
    And I will return confirmation to channel
    And Return strength score to the channel

  @FCPH-2766
  Scenario: Validate temporary single use token valid for "x" (x=24 hours) BR_00845
    Given I create a new customer profile
    And I receive the request to updatePassword of the customer
    And the customer account status is locked
    And the request contains a token
    And the request contains a new password
    When I validate the request to updatePassword
    And token has expired or used previously
    Then I will return a error message to the channel "SVC_100270_2002"




