@Sprint27 @FCPH-8579
Feature:Receive a request to save passengers to customer profile

  @ADTeam
  Scenario Outline: save passenger against the customer profile
    Given I am using the channel Digital
    And I am logged in as a staff member
    When add passengers"<passengers>" with bookingtype "<bookingType>" and faretype "<fareType>" to basket
    And add travellers to cart and save traveller "<saveStatus>" to customer profile
    And update traveller's surname "Porter" and ejplusCardnumber "03445610"
    Then passenger "Porter" has their details added to customer profile
    Examples:
      | passengers | fareType | bookingType       | saveStatus |
      | 2 Adults   | Standard | STANDARD_CUSTOMER | true       |






