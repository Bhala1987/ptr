Feature: Request Refund

  @Sprint28 @FCPH-9211 @regression
  Scenario: Return primary and secondary reason codes to the channel
    Given a channel is used
    When I send the request to getRefundReasons service
    Then I should receive the list of reasons code