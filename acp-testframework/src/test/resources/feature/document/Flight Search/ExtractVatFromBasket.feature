@Sprint30 @TeamA @FCPH-2649
Feature: Extract VAT from products added to the basket

  @manual
  Scenario: Store VAT against passenger fare product in back office, When sector is VATable
    Given I have a basket that contains VATable fare product for a passenger
    And Sector being used is VATable with valid date range
    When I commit that basket to create a booking
    Then In the back office I should be able to see that the VAT against products are as follows:
      | Product Type     | Base Price | VAT   | VAT Amount |
      | FLEXI            | 110.95     | 10%   |  10.09     |
      | STANDARD         | 81.95      | 10%   |  7.45      |

  @manual
  Scenario: Store VAT against passenger seat product line items in back office, When sector is VATable
    Given I have a basket that contains seat product for a passenger
    And Sector being used is VATable with valid date range
    When I commit that basket to create a booking
    Then In the back office I should be able to see that the VAT against products are as follows:
      | Product Type     | Base Price | VAT   | VAT Amount |
      | Extra legroom    | 15.99      | 10%   |  1.45      |
      | Up front         | 5.33       | 10%   |  0.48      |
      | Standard         | 5.33       | 10%   |  0.48      |

  @manual
  Scenario: Store VAT against passenger Sport Equipment product line items in back office, When sector is VATable
    Given I have a basket that contains sports equipment for a passenger
    And Sector being used is VATable with valid date range
    When I commit that basket to create a booking
    Then In the back office I should be able to see that the VAT against products are as follows:
      | Product Type     | Base Price | VAT   | VAT Amount |
      | Snowboard        | 30         | 10%   |  2.73      |
      | Skis             | 35         | 10%   |  3.18      |

  @manual
  Scenario: Store VAT against passenger Hold Item product line items in back office, When sector is VATable
    Given I have a basket that contains Hold Bag and Excess weight for a passenger
    And Sector being used is VATable with valid date range
    When I commit that basket to create a booking
    Then In the back office I should be able to see that the VAT against products are as follows:
      | Product Type     | Base Price | VAT   | VAT Amount | Quantity |
      | 20kgbag          | 15         | 10%   |  1.36      |     1    |
      | 1kgextraweight   | 10         | 10%   |  1.82      |     2    |

  @manual
  Scenario: Store VAT against passenger fare product in back office, When sector is VATable
    Given I have a basket that contains SpeedyBoarding and FastTrackSecurity for a passenger part of Flexi bundle
    And Sector being used is VATable with valid date range
    When I commit that basket to create a booking
    Then In the back office I should be able to see that the VAT against products are as follows:
      | Product Type     | Base Price | VAT   | VAT Amount |
      | SpeedyBoarding   | 0          | 10%   |     0      |
      | FastTrackSecurity| 0          | 10%   |     0      |

  @manual
  Scenario: Store VAT against passenger Hold Item product line items in back office, When sector is VATable and Booking is Amend Booking
    Given I have a Amended basket that contains Hold Bag and Excess weight for a passenger
    And Sector being used is VATable with valid date range
    When I commit that basket to create a booking
    Then In the back office I should be able to see that the VAT against products are as follows:
      | Product Type     | Base Price | VAT   | VAT Amount |
      | 10kgbag          | 7          | 10%   |  0.64      |
      | 3kgextraweight   | 9          | 10%   |  0.82      |

  @manual
  Scenario: Store VAT against passenger fare product in back office, When sector is VATable and Request is comming from any channel
    Given I have a basket that contains VATable fare product for a passenger with any channel
    And Sector being used is VATable with valid date range
    When I commit that basket to create a booking
    Then In the back office I should be able to see that the VAT against products are as follows:
      | Product Type     | Base Price | VAT   | VAT Amount |
      | FLEXI            | 110.95     | 10%   |  10.09     |
      | STANDARD         | 81.95      | 10%   |  7.45      |

  @manual
  Scenario: Do not store VAT against passenger Sport Equipment product line items in back office, When sector is VATable but Sport Equipment product is Non VATable
    Given I have a basket that contains Non VATable sports equipment for a passenger
    And Sector being used is VATable with valid date range
    When I commit that basket to create a booking
    Then In the back office I shouldn't be able to see that the VAT is stored against that products

  @manual
  Scenario: Do not store VAT against passenger fare product in back office, When sector is Not VATable
    Given I have a basket that contains VATable fare product for a passenger
    And Sector being used is not VATable with valid date range
    When I commit that basket to create a booking
    Then In the back office I shouldn't be able to see that the VAT is stored against that products

  @manual
  Scenario: Do not store VAT against passenger seat product line items in back office, When sector is Not VATable
    Given I have a basket that contains VATable seat product for a passenger
    And Sector being used is not VATable with valid date range
    When I commit that basket to create a booking
    Then In the back office I shouldn't be able to see that the VAT is stored against that products

  @manual
  Scenario: Do not store VAT against passenger Sport Equipment product line items in back office, When sector is Not VATable
    Given I have a basket that contains VATable Sport Equipment for a passenger
    And Sector being used is not VATable with valid date range
    When I commit that basket to create a booking
    Then In the back office I shouldn't be able to see that the VAT is stored against that products

  @manual
  Scenario: Do not store VAT against passenger fare product in back office, When sector is VATable but Flight departure date not fall in date range
    Given I have a basket that contains VATable fare product for a passenger
    And Sector is VATable But Flight departure date is not in date range
    When I commit that basket to create a booking
    Then In the back office I shouldn't be able to see that the VAT is stored against that products

  @manual
  Scenario: Do not store VAT against passenger seat product line items in back office, When sector is VATable but Flight departure date not fall in date range
    Given I have a basket that contains VATable seat product for a passenger
    And Sector is VATable But Flight departure date is not in date range
    When I commit that basket to create a booking
    Then In the back office I shouldn't be able to see that the VAT is stored against that products

