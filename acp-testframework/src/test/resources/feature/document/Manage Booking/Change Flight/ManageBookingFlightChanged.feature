Feature: Request release Flight inventory for products which have changed - partial cancellation

  @Sprint31 @TeamC @FCPH-10085 @manual
  Scenario Outline: Able to commit booking with ammendable basket and changing the flight
    Given I have an ammendable basket with the <channel>
    When I change the flight with fare type bundle line item active status is set to Inactive
    Then I will verify the allocate the new flight inventory request is sent to AL
    And I will verify the deallocate the old flight inventory request is sent to AL
    And I will continue with commit Booking process
    Examples:
      | channel         |
      | Digital         |
      | PublicAPiMobile |

  @Sprint31 @TeamC @FCPH-10085 @manual
  Scenario Outline: Able to commit booking with ammendable basket and remove the flight
    Given I have an ammendable basket with more than one flights with <channel>
    When I remove a  flight from the basket
    Then I will verify the deallocate flight inventory request is sent to AL
    And I will continue with commit Booking process
    Examples:
      | channel           |
      | ADCustomerService |
      | ADAirport         |




