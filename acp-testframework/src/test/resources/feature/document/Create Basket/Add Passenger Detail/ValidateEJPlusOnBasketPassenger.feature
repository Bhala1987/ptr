@defect:FCPH-11148
Feature: Add EJ plus request to the basket passenger

  @Sprint24 @FCPH-347
  Scenario Outline: Number of characters passed for ej Plus validation BR_00430
    Given I have received a valid updatePassengerDetails request for channel "<channel>" and passenger "2 Adult"
    And the request contains a EJ plus membership number
    But the number of characters passed is less than 8 numeric characters
    When I validate the eJ Plus membership received
    Then I will return a message "SVC_100012_3027" of error
    Examples:
      | channel           |
      | ADCustomerService |
      | Digital           |
      | PublicApiMobile   |

  @Sprint24 @FCPH-347
  Scenario Outline: Incorrect format for staff ej plus membership number BR_00420 not start with S
    Given I have received a valid updatePassengerDetails request for channel "<channel>" and passenger "2 Adult"
    And the request contains a EJ plus membership number
    But the number starts with a S but is less than 6 numeric characters
    When I validate the eJ Plus membership received
    Then I will return a message "SVC_100012_3027" of error
    Examples:
      | channel      |
      | Digital      |
      | PublicApiB2B |
      | ADAirport    |

  @Sprint24 @FCPH-347
  Scenario Outline: Incorrect format for staff ej plus membership number BR_00420 lenght is less than 6
    Given I have received a valid updatePassengerDetails request for channel "<channel>" and passenger "2 Adult"
    And the request contains a EJ plus membership number
    But the number is more than 6 numeric characters but not starts with a "S"
    When I validate the eJ Plus membership received
    Then I will return a message "SVC_100012_3027" of error
    Examples:
      | channel      |
      | PublicApiB2B |
      | ADAirport    |
      | Digital      |

  @Sprint24 @FCPH-347
  Scenario Outline: Validate surname must match the membership surname BR_00410
    Given I have received a valid updatePassengerDetails request for channel "<channel>" and passenger "2 Adult"
    And the request contains a EJ plus membership number
    But the surname of the passenger passed in the request does not match the surname on the eJ plus membership
    When I validate the eJ Plus membership received
    Then I will return a message "SVC_100000_2074" of error
    Examples:
      | channel           |
      | ADCustomerService |
      | Digital           |

  @Sprint24 @FCPH-347
  Scenario Outline: Validate ej plus expiry date is not less than the current date BR_00390
    Given I have received a valid updatePassengerDetails request for channel "<channel>" and passenger "2 Adult"
    And the request contains a EJ plus membership number
    But the expiry date is in the past
    When I validate the eJ Plus membership received
    Then I will generate a warning message "SVC_100012_2078"
    Examples:
      | channel      |
      | ADAirport    |
      | Digital      |
      | PublicApiB2B |

  @Sprint24 @FCPH-347
  Scenario Outline: Validate ej plus must be entered only once BR_00400
    Given I have received a valid updatePassengerDetails request for channel "<channel>" and passenger "2 Adult"
    And the request contains a EJ plus membership number
    But the same number appears against a different passenger on the booking
    When I validate the eJ Plus membership received
    Then I will return a message "SVC_100045_3015" of error
    And the passenger details will not be updated
    Examples:
      | channel           |
      | ADCustomerService |
      | Digital           |
      | PublicApiMobile   |

  @Sprint24 @FCPH-347
  Scenario Outline: Validate ej plus must be entered only once same request body BR_00400
    Given I have received a valid updatePassengerDetails request for channel "<channel>" and passenger "2 Adult"
    And the request contains a EJ plus membership number
    But the passengers have the same value for EJ plus membership
    When I validate the eJ Plus membership received
    Then I will return a message "SVC_100045_3015" of error
    And the passenger details for the first passenger has been updated
    And the passenger details for the second passenger has not been updated
    Examples:
      | channel           |
      | ADCustomerService |
      | Digital           |
      | PublicApiB2B      |

  @Sprint24 @FCPH-347
  Scenario Outline: Add ej PLus membership to passenger
    Given I have received a valid updatePassengerDetails request for channel "<channel>" and passenger "2 Adult"
    And the request contains a EJ plus membership number
    When I validate the eJ Plus membership received
    Then I will store the membership number against the each instance of the passenger in the basket
    And I will return the updated basket
    Examples:
      | channel         |
      | ADAirport       |
      | Digital         |
      | PublicApiMobile |

  @Sprint24 @FCPH-347
  Scenario Outline: Add ej PLus membership across different passengers on different flight
    Given I have received a valid updatePassengerDetails request for channel "<channel>" and passenger "2 Adult"
    And the request contains a EJ plus membership number
    And I received same request for different passenger "1 Adult" on different flight on the same basket
    When I validate the eJ Plus membership received
    Then I will store the membership number against the each instance of the passenger in the basket
    And I will return the updated basket
    Examples:
      | channel           |
      | ADCustomerService |
      | Digital           |
      | PublicApiB2B      |

  @Sprint24 @FCPH-347
  Scenario Outline: Add ej PLus membership across different passengers on different flight same request body
    Given my basket contains 2 flight for passenger mix "1 Adult" using channel "<channel>"
    And I receive valid updatePassengerDetails request for all passenger on the basket
    And the request contains same EJ plus membership number for the passenger on different flight
    When I validate the eJ Plus membership received
    Then I will store the membership number against the passengers on different flight
    And I will return the updated basket for the passenger on different flight
    Examples:
      | channel      |
      | ADAirport    |
      | Digital      |
      | PublicApiB2B |

  @FCPH-9478 @Sprint27
  Scenario Outline: - Generate an error if eJ Plus status is not complete for update passenger BR_4004
    Given I am using the channel <channel>
    And I have received a valid updatePassengerDetails request for channel "<channel>" and passenger "2 Adult"
    And the request contains a EJ plus membership number with status other than <status>
    When I validate the eJ Plus membership received
    Then I will return a message "SVC_100000_2088" of error
    Examples:
      | channel           | status    |
      | ADCustomerService | COMPLETED |
      | Digital           | COMPLETED |
