@Sprint27 @FCPH-3442
Feature: FCPH-3442 - Amend passenger contact details after the booking has been made.

  As a customer who has made a booking
  I would like the ability to change the passenger details
  So that I can add to and amend my booking information

  Scenario Outline: Validate error codes generated when trying to update passenger details with invalid data.
    Given basket contains return flight for 1 Adult passengers Standard fare via the <channel> channel
    And I do the commit booking
    And the booking reference is returned with a COMPLETED status
    And the booking is amendable
    When passenger "1" requests to update their "<field>" to "<value>"
    Then the amend passenger request should fail with the error codes "<codes>"
    Examples: Using a mix of channels.
      | channel   | field       | value               | codes           |
      | Digital   | email       | @ej-test.com        | SVC_100012_3028 |
      | ADAirport | email       | testingej-test.com  | SVC_100012_3028 |
      | Digital   | email       | testing@            | SVC_100012_3028 |
      | ADAirport | phoneNumber | 12345               | SVC_100012_3020 |
      | Digital   | phoneNumber | 1234567891234567891 | SVC_100012_3020 |
      | ADAirport | phoneNumber | abcdefghi           | SVC_100012_3019 |

  Scenario Outline: Adding and updating and removing (setting to null) details with valid data.
    Given basket contains return flight for 2 Adult passengers Standard fare via the <channel> channel
    And I do the commit booking
    And the booking reference is returned with a COMPLETED status
    And the booking is amendable
    When passenger "1" requests to update their "email" to "<email>"
    And passenger "1" requests to update their "phoneNumber" to "<telephone>"
    Then the basket should be updated to include the following information for passenger "1":
      | email       | <email>     |
      | phoneNumber | <telephone> |
    Examples:
      | channel | email                    | telephone   |
      | Digital | 3442-valid@ejtesting.com | 03303655000 |

    Examples:
      | channel | email                    | telephone   |
      | Digital |                          |             |

  @BR:BR_00159
  Scenario: Asserting that a booking must always maintain at least one passenger with a telephone number and email address.
    Given basket contains return flight for 1 Adult passengers Standard fare via the ADAirport channel
    And I do the commit booking
    And the booking reference is returned with a COMPLETED status
    And the booking is amendable
    When a passenger attempts to delete all required contact information for passenger "1"
    Then the amend passenger request should fail with the error codes "SVC_100519_1001"