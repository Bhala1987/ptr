@FCPH-319
@Sprtin25
Feature: Receive Request for Add Direct Flight (Mixed Bundle)

  Background:
    Given am using channel Digital

  @negative
  Scenario Outline: 1.Error if we mix the bundles in basket for standard customer
    Given I searched a '<Bundle>' flight with return for 1 adult
    And I added it to the basket with <Bundle> fare as outbound journey
    When I attempt to add the "new" flight with a "<NewBundle>" bundle
    Then I will return a error message "SVC_100012_3036" to the channel
    Examples:
      | Bundle   | NewBundle |
      | Standard | Flexi     |
      | Flexi    | Standard  |

  @negative
  Scenario Outline: 2.Error if we mix the bundles for the same flight in basket for standard customer BR_01013
    Given I searched a '<Bundle>' flight with return for 1 adult
    And I added it to the basket with <Bundle> fare as outbound journey
    When I attempt to add the "same" flight with a "<NewBundle>" bundle
    Then I will return a error message "SVC_100012_3036" to the channel
    Examples:
      | Bundle   | NewBundle |
      | Standard | Flexi     |
      | Flexi    | Standard  |

  @negative
  Scenario Outline: 2.Error if we mix the bundles for the same flight in basket for staff customer BR_01013
    Given a valid customer profile has been created
    And a valid request to associate staff member to member account
    And I have added a flight with "<StaffBundle>" bundle to the basket
    When I attempt to add the "same" flight with a "<NewStaffBundle>" bundle
    Then I will return a error message "SVC_100012_3037" to the channel
    Examples:
      | StaffBundle   | NewStaffBundle |
      | Staff         | StaffStandard  |
      | StaffStandard | Staff          |

  Scenario Outline: 3.Staff Bundle and Staff Standard is able to be added to the same basket
    And a valid customer profile has been created
    And a valid request to associate staff member to member account
    And I have added a flight with "<StaffBundle>" bundle to the basket
    When I attempt to add the "<Same_Or_New>" flight with a "<NewStaffBundle>" bundle
    Then I added it successfully
    Examples:
      | StaffBundle   | NewStaffBundle | Same_Or_New |
      | Staff         | Staff          | same        |
      | StaffStandard | StaffStandard  | same        |
      | Staff         | Staff          | new         |
      | Staff         | StaffStandard  | new         |

  @negative
  Scenario Outline: 4.Any other bundle not able to added to the same basket
    Given I have added a flight with "<OtherThanStaffOrStandard>" bundle to the basket
    And a valid customer profile has been created
    And a valid request to associate staff member to member account
    And I attempt to add the "<Same_Or_New>" flight with a "Staff" bundle
    Then I will return a error message "<Error>" to the channel
    Examples:
      | OtherThanStaffOrStandard | Same_Or_New | Error           |
      | Standard                 | same        | SVC_100012_3036 |
      | Standard                 | new         | SVC_100012_3038 |
#      | Flexi                    | same        | SVC_100012_3037 |
#      | Flexi                    | new         | SVC_100012_3038 |

  @negative
  Scenario: 4.Any other bundle not able to added to the same basket
    And a valid customer profile has been created
    And a valid request to associate staff member to member account
    And I have a flightKey
    And I have added a flight with "Standby" bundle to the basket
    When I attempt to add the "new" flight with a "Staff" bundle
    Then I will return a error message "SVC_100012_3038" to the channel
