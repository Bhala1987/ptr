@TeamA @Sprint31 @FCPH-8763
Feature: Display a list of allowed functions with the get booking response based on who is requesting the booking.

  As an agent, customer or passenger
  I should only be able to see and perform actions that are relevant to me
  So that I cannot do something that would be inappropriate or risk the integrity of the booking or platform.

  Scenario Outline: Test that permissions relating only to the Booker are returned on get booking.
    Given the channel <channel> is used
    And I have committed a booking with <fareType> fare
    When I send the request to getBooking service
    Then I expect to see only appropriate allowed functions returned with the booking for:
      | channel   | bookingType | accessType |
      | <channel> | <fareType>  | Booker     |

    Examples:
      | channel           | fareType |
      | Digital           | Staff    |
      | Digital           | Standard    |

  Scenario Outline: Test that permissions relating only to the Agent are returned on get booking.
    Given the channel <channel> is used
    And I logged in as agent
    And I have committed a booking with <fareType> fare
    When I send the request to getBooking service
    Then I expect to see only appropriate allowed functions returned with the booking for:
      | channel   | bookingType | accessType |
      | <channel> | <fareType>  | Agent      |

    Examples:
      | channel           | fareType |
      | ADCustomerService | Staff    |
      | ADAirport         | Standard |


