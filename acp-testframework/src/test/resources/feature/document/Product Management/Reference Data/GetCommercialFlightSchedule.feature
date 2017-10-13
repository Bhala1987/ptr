@FCPH-2662
Feature: Get commercial flight schedules

  @AsXml
  Scenario Outline: I can retrieve a commercial flight schedule
    Given that I am using channel: <channel>
    When I make a request for the flight schedules using all parameters
    Then I receive the flight schedules for the route and date range provided
    Examples:
      | channel           |
      | PublicApiB2B      |
      | ADAirport         |
      | Digital           |

  Scenario Outline: A flight schedule request without mandatory From-Date produces an error
    Given that I am using channel: <channel>
    When I make a request for the flight schedules without the From-Date
    Then a missing From-Date error is returned in the response
    Examples:
      | channel           |
      | ADAirport         |
      | PublicApiMobile   |
      | PublicApiB2B      |
      | ADCustomerService |
      | Digital           |

  Scenario Outline: Return all flight schedules for only the chosen day
    Given that I am using channel: <channel>
    When I make a request for the flight schedules using only From-date
    Then I receive the flight schedules for the chosen day
  @regression
    Examples:
      | channel         |
      | PublicApiMobile |
    Examples:
      | channel           |
      | ADAirport         |
      | PublicApiB2B      |
      | ADCustomerService |
      | Digital           |

  Scenario Outline: Return all flight schedules for the chosen day and origin
    Given that I am using channel: <channel>
    When I make a request for the flight schedules using only From-date and Origin
    Then I receive the flight schedules for the origin and chosen day
    Examples:
      | channel           |
      | ADCustomerService |
      | PublicApiMobile   |
      | ADAirport         |
      | PublicApiB2B      |
      | Digital           |

  Scenario Outline: Return all flight schedules for the chosen day and destination
    Given that I am using channel: <channel>
    When I make a request for the flight schedules using only From-date and Destination
    Then I receive the flight schedules for the destination and chosen day
    Examples:
      | channel           |
      | Digital           |
      | PublicApiB2B      |
      | PublicApiMobile   |
      | ADAirport         |
      | ADCustomerService |

  Scenario Outline: Flight schedules can be requested with no route and date range less than X days
    Given that I am using channel: <channel>
    And the day range is configured for flight schedules
    When I make a request for the flight schedules using a day range of 1 day below the configuration value
    Then I receive the flight schedules for the date range provided
    Examples:
      | channel           |
      | PublicApiMobile   |
      | PublicApiB2B      |
      | ADAirport         |
      | ADCustomerService |
      | Digital           |

  @defect:FCPH-8409
  Scenario Outline: A flight schedule request with a date range greater than X days and no destination or no arrival, will return an error
    Given that I am using channel: <channel>
    And the day range is configured for flight schedules
    When I make a request for the flight schedules using a day range of 1 day above the configuration value
    Then an arrival and destination should be provided error is returned in the response
    Examples:
      | channel           |
      | PublicApiB2B      |
      | PublicApiMobile   |
      | ADAirport         |
      | ADCustomerService |
      | Digital           |

  Scenario Outline: A flight schedule request with a departure, arrival and date range greater than X days will return maximum of Y days of results
    Given that I am using channel: <channel>
    And the maximum number of days is configured for flight schedules
    When I make a request for the flight schedules using Origin, Destination and a day range of 180 days
    Then I receive the maximum configured days of flight schedule results
    Examples:
      | channel           |
      | PublicApiMobile   |
      | PublicApiB2B      |
      | ADAirport         |
      | ADCustomerService |
      | Digital           |

  Scenario Outline: A flight schedule can be requested with only a From-Date, To-Date and Origin
    Given that I am using channel: <channel>
    When I make a request for the flight schedules using only From-Date, To-Date and Origin
    Then I receive the flight schedules for the origin and date range provided
    Examples:
      | channel           |
      | PublicApiB2B      |
      | ADCustomerService |
      | PublicApiMobile   |
      | ADAirport         |
      | Digital           |

  Scenario Outline: A flight schedule can be requested with only a From-Date,To-Date and Destination
    Given that I am using channel: <channel>
    When I make a request for the flight schedules using only From-Date, To-Date and Destination
    Then I receive the flight schedules for the destination and date range provided
    Examples:
      | channel           |
      | ADCustomerService |
      | PublicApiB2B      |
      | PublicApiMobile   |
      | ADAirport         |
      | Digital           |

  Scenario Outline: Flight schedules can be requested for dates in the past
    Given that I am using channel:<channel>
    When I make a request for the flight schedules using a past date
    Then I receive the flight schedules for the route and date range provided
    Examples:
      | channel           |
      | PublicApiMobile   |
      | PublicApiB2B      |
      | ADAirport         |
      | ADCustomerService |
      | Digital           |