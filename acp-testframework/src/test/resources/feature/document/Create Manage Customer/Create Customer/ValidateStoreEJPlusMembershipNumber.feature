Feature: Validate & Store eJ plus membership

  @FCPH-2807
  Scenario Outline: Number of characters passed for ej Plus validation BR_00430
    Given provide a valid request to create a customer profile
    And the employId in the request is missing
    And the length <length> of ejPlusCardNumber passed is less than "8" characters
    When I validate the request
    Then the customer profile service returns warning code SVC_100012_3027
    And the customer profile is created successfully
    Examples:
      | length |
      | 1      |
      | 7      |

  @FCPH-2807
  Scenario Outline: Number of character for staff ej plus membership number BR_00420
    Given provide a valid request to create a customer profile
    And the length <length> of ejPlusCardNumber passed is less than "6" characters
    When I validate the request
    Then the customer profile service returns warning code SVC_100012_3027
    And the customer profile is created successfully
    Examples:
      | length |
      | 1      |
      | 5      |

  @FCPH-2807
  Scenario Outline: Number of character for staff ej plus membership number BR_00420
    Given provide a valid request to create a customer profile
    And the first character of the ejPlusCarNumber is "<character>"
    When I validate the request
    Then the customer profile service returns warning code SVC_100000_2075
    And the customer profile is created successfully
    Examples:
      | character |
      | a         |
      | b         |
      | c         |
      | d         |

  @FCPH-7965
  Scenario: Valid 6 character number format for ej plus membership number
    Given provide a valid request to create a customer profile
    And the provided ejPlusCardNumber is:S123456
    When I validate the request
    Then the customer profile is created successfully

  @FCPH-2807
  Scenario: Validate the expiry date of the ej plus membership BR_3000
    Given provide a valid request to create a customer profile
    And the expiry date for the ejplusmemebership is in the past
    When I validate the request
    Then the customer profile service returns warning code SVC_100012_2078
    And the customer profile is created successfully

  @FCPH-2807
  Scenario: Validate surname must match the membership surname BR_00410
    Given provide a valid request to create a customer profile
    And the surname of the customer passed in the request does not match the surname on the ejPlusCardNumber
    When I validate the request
    Then the customer profile service returns warning code SVC_100000_2074

#	defected due to an error in the impex file
  @pending
  Scenario: Store ej Plus membership number against the profile
    Given provide a valid request to create a customer profile
    And the details passed in the request respect the criteria
    When I validate the request
    Then the customer profile is created successfully
    And the validation is successful
    And I will store the ej Plus membership number against the customer profile

  @FCPH-2807
  Scenario: Membership number not found in database lookup
    Given provide a valid request to create a customer profile
    And the ej Plus membership is not found in the database look up
    When I validate the request
    Then the customer profile service returns warning code SVC_100000_2075
    And the customer profile is created successfully

  @regression
  @Sprint27
  @FCPH-9479
  @BR:BR_4004
  @negative
  Scenario Outline: Warning message if eJ Plus status is not complete
    Given I am using the channel <channel>
    And I have valid request to create customer
    And I update the the EJPlusMembership number other than <status>
    When I request for create customer profile
    Then I should get the warning with code as SVC_100000_2088
    And customer profile is created successfully
    And I request the customer profile
    Then I should get the warning with code as SVC_100000_2088
    Examples:
      | channel   | status    |
      | Digital   | COMPLETED |
      | ADAirport | COMPLETED |