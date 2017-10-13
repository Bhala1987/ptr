@FCPH-455
@Sprint27
Feature: Retrieve Adverts

  @manual
  Scenario: 1 - Generate request for adverts
    Given that a channel has initiated as request to generate a boarding pass
    When I receive a valid request
    Then I will generate a Request to INK including <<mandatory details>> in the request
      | Description     | Airport Code | Mandatory |
      | Origin Airport  | LTN          | Y         |
      | Arrival Airport | ALC          | Y         |

  @manual
  Scenario: 2 - Send Age range to the ink based on the passenger age
    Given that a channel has initiated as request to generate a boarding pass
    When there is no travel document associated to the requested traveller on the booking
    Then I will send a <<Age Passed>> to ink
      | Age of Passenger | Age Passed |
      | 1                | 1          |
      | 12               | 12         |
      | 26               | 26         |

  @manual
  Scenario: 3 - Send Date of Birth and Gender to the ink based on the passenger age
    Given that a channel has initiated as request to generate a boarding pass
    When there is travel document associated to the requested traveller on the booking
    Then I will send Date of birth of the traveller to ink
    And I will send Gender of the traveller to ink
      | Date of Birth | Format          |
      | DD/MM/YY      | YYYY-MM-DD      |
      | Gender        | Male or Female  |
      | Nationality   | 3 Digit ISO GBR |

  @manual
  Scenario: 4 - Send language to ink (see note in story on which code is needed for language
    Given that a channel has initiated as request to generate a boarding pass
    When I send the language parameter for the requested passenger
    Then I will send <<language>> to ink
      | Language | Language passed to ink | Mandatory |
      | English  | en                     | N         |
      | German   | de                     | N         |

  @manual
  Scenario: 5 - Send Departure Date to ink
    Given that a channel has initiated as request to generate a boarding pass
    When I send the Scheduled Departure Date parameter for the requested passenger and flight
    Then I will send <<Departure Date>> to ink
      | Parameter      | Departure Date Passed to ink | Mandatory |
      | Departure Date | 2000-30-02                   | N         |

  @manual
  Scenario: 6 - Receive response from INK
    Given that a channel has initiated as request to generate a boarding pass
    And I have generated a request to INK for adverts
    When I receive a response from INK
    Then I will place the adverts in the boarding pass
    And return the boarding pass to the channel

  @manual
  Scenario: 7 - INK service is not available
    Given that a channel has initiated as request to generate a boarding pass
    And I have generated a request to INK for adverts
    When I don't receive a response from INK
    Then I select the hybris default adverts
    And place the default adverts in the boarding pass
    And I will return the boarding pass to the channel
