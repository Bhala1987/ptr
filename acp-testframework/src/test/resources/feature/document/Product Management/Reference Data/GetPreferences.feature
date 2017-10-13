@FCPH-2752
Feature: Service returns available customer preferences

  @AsXml
  @regression @FCPH-2752
  Scenario: Request returns all contact method options
    Given I am using the channel PublicApiMobile
    When I call the get preference service
    Then It should see the following contact method options
      | EMAIL |
      | PHONE |
      | POST  |
      | SMS   |

  Scenario: Request returns all contact type options
    When I call the get preference service
    Then It should see the following contact type options
      | EASYJET_NEWS_LETTER          |
      | EASYJET_SEASON_NEW_ROUTE     |
      | FLIGHT_IMPORTANT_INFORMATION |
      | OFFERS_IMPORTANT_INFORMATION |
      | SALES_AND_OFFERS             |
      | SURVEYS_AND_RESEARCH         |

  Scenario: Request returns all frequency options
    When I call the get preference service
    Then It should see the following frequency options
      | DAILY   |
      | MONTHLY |
      | WEEKLY  |

  Scenario: Request returns all hold Bag Weight Options
    When I call the get preference service
    Then It should see the following hold bag weight options
      | _20 |
      | _23 |
      | _26 |
      | _29 |
      | _32 |

  Scenario: Request returns all marketing Communication Options
    When I call the get preference service
    Then It should see the following marketing Communication Options
      | OPT_OUT_3RD_PART_COMMUNICATION |
      | OPT_OUT_EJ_COMMUNICATION       |


  Scenario: Request returns all seating Preference Options
    When I call the get preference service
    Then It should see the following seating Preference Options
      | AISLE           |
      | BACK_11_ONWARDS |
      | EXTRA_LEGROOM   |
      | FRONT_10        |
      | FRONT_ROW_1     |
      | MIDDLE          |
      | UP_FRONT_SEAT   |
      | WINDOW          |

  Scenario: Request returns all travelling Season Options
    When I call the get preference service
    Then It should see the following travelling Season Options
      | AUTUMN      |
      | SPRING_TIME |
      | SUMMER      |
      | WINTER      |

  Scenario: Request returns all travelling When Options
    When I call the get preference service
    Then It should see the following travelling When Options
      | WITHIN_NEXT_MONTH     |
      | WITHIN_NEXT_SIX_MONTH |
      | WITHIN_NEXT_YEAR      |

  Scenario: Request returns all travelling With Options
    When I call the get preference service
    Then It should see the following travelling With Options
      | COUPLE |
      | FAMILY |
      | GROUP  |
      | SOLO   |

  Scenario: Request returns all trip Type Options
    When I call the get preference service
    Then It should see the following trip Type Options
      | BOOKITLIST   |
      | CITY         |
      | WINTER_SPORT |
      | BEACH        |
      | ROMANCE      |
      | ADVENTURE    |
      | FAMILY       |
      | CULTURE      |
