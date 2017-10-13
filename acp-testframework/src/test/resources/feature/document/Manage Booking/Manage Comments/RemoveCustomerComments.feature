@Sprint28
Feature: Remove comment from a customer profile
  As hybris, I will remove Agent Desktop User Comments and return confirmation to Channel/Downstream systems

  Background:
    Given I create a new valid customer

  @FCPH-9661
  Scenario: Error if the Customer is unable to be identified
    Given the channel ADAirport is used
    And I login as agent
    When I attempt to remove a comment for a non-existing customer
    Then I will receive an error with code 'SVC_100542_3001'

  @FCPH-9661
  Scenario: Error if the comment ID is not able to be identified
    Given the channel ADAirport is used
    And I login as agent
    When I attempt to remove a comment with a non-existing commentID
    Then I will receive an error with code 'SVC_100542_2004'

  @FCPH-9661
  Scenario Outline: Error if the channel is not allowed to delete a comment BR_01942
    Given <channel> is not configured to delete a customer comment
    And I login as agent
    When I attempt to remove a customer comment
    Then I will receive an error with code 'SVC_100542_2003'
    Examples:
      | channel         |
      | PublicApiMobile |
      | PublicApiB2B    |
      | Digital         |

  @FCPH-9661
  Scenario: Error if the comment ID is not associated to the requested customer id
    Given the channel ADAirport is used
    And I login as agent
    When I attempt to remove a comment with a commentID not matching the customerID
    Then I will receive an error with code 'SVC_100542_2004'

  @FCPH-9661 @FCPH-10235
  @BR:BR_01942
  Scenario Outline: Delete the comment on the booking
    Given <channel> is configured to delete a customer comment
    And I login as agent
    When I remove a customer comment
    And I request the customer profile
    Then I will return Channel, User ID, comment type and created DateTime Stamp for the deleted comment
    Examples:
      | channel           |
      | ADAirport         |
      | ADCustomerService |

  @FCPH-10235
  @BR:BR_01942
  Scenario: Comments are not returned in get Customerprofile for Digital channel
    Given ADAirport is configured to delete a customer comment
    And I login as agent
    When I remove a customer comment
    And I request the customer profile from Digital, PublicApiMobile or PublicApiB2B
    Then the response doesn't contains the comment section

  @FCPH-10235
  @BR:BR_01942
  Scenario Outline: Generate an error message if AD channel tries to delete a Comment flagged as Deleted
    Given <channel> is configured to delete a customer comment
    And I login as agent
    And I remove a customer comment
    When I remove the customer comment
    Then the channel will receive an error with code SVC_100542_2006
    Examples:
      | channel           |
      | ADAirport         |
      | ADCustomerService |

  @FCPH-10235
  @BR:BR_01942
  Scenario: Generate an error message if non-AD channel tries to delete a Comment flagged as Deleted
    Given ADAirport is configured to delete a customer comment
    And I login as agent
    And I remove a customer comment
    When I remove the customer comment from Digital, PublicApiMobile or PublicApiB2B
    Then the channel will receive an error with code SVC_100542_2003

  @FCPH-10235
  @BR:BR_01942
  Scenario Outline: Generate an error message AD the channel tries to update a Comment flagged as Deleted
    Given <channel> is configured to delete a customer comment
    And I login as agent
    And I remove a customer comment
    When I update the customer comment
    Then the channel will receive an error with code SVC_100542_2007
    Examples:
      | channel           |
      | ADAirport         |
      | ADCustomerService |

  @FCPH-10235
  @BR:BR_01942
  Scenario: Generate an error message AD the channel tries to update a Comment flagged as Deleted
    Given ADAirport is configured to delete a customer comment
    And I login as agent
    And I remove a customer comment
    When I update the customer comment from Digital, PublicApiMobile or PublicApiB2B
    Then the channel will receive an error with code SVC_100542_2002