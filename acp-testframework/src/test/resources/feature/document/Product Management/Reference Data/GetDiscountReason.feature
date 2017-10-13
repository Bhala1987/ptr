Feature: Provide Reference Data for Discount Reason codes
  Automatically disable Discount Reason

  @FCPH-3426 @AsXml
  Scenario Outline: Return active Discount reason codes
    Given The channel has initiated a getDiscountReason request using channel "<channel>"
    When I receive the request
    Then I will return a list of active Discount reason codes to the channel
  @regression
    Examples:
      | channel |
      | Digital |
    Examples:
      | channel           |
      | PublicApiB2B      |
      | ADCustomerService |
      | ADAirport         |

  @FCPH-3426
  Scenario Outline: Return active Discount reason codes
    Given The channel has initiated a getDiscountReason request using channel "<channel>" and specific currency
    When I receive the request
    Then I will return a list of active Discount reason codes to the channel
    Examples:
      | channel           |
      | Digital           |
      | PublicApiB2B      |
      | ADCustomerService |
      | ADAirport         |

  @FCPH-3426
  Scenario Outline: Generate Error for invalid request
    Given The channel has initiated a getDiscountReason request using channel "<channel>"
    And the request is not in the format defined in the service contract
    When I receive the request
    Then I will return an error message "SVC_100012_20013"
    Examples:
      | channel           |
      | Digital           |
      | PublicApiB2B      |
      | ADCustomerService |
      | ADAirport         |

  @FCPH-6662
  Scenario Outline: Make the discount reason disabled due to offline date is in the past
    Given The channel has initiated a getDiscountReason request using channel "<channel>"
    And I have discount reason which offline date is in the past from the current date
    When I receive the request
    Then the discount reason is not returned
    Examples:
      | channel           |
      | Digital           |
      | PublicApiB2B      |
      | ADCustomerService |
      | ADAirport         |