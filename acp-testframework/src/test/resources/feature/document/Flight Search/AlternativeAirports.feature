Feature: Alternative Airports

  @FCPH-242 @FCPH-8217
  Scenario Outline: Alternate airports
    When I send a request to alternate airports with the parameter "<departure>","<destination>","<distance>"
    Then I will return a error message on the channel "<code>"
    Examples:
      | departure | destination | distance | code            |
      |           | FAO         | 100      | SVC_100180_2006 |
      | LTN       |             | 100      | SVC_100180_2007 |
      | LTN       | FAO         |          | SVC_100180_2005 |

  @regression
  @FCPH-242
  @BR:BR_0183
  Scenario Outline: Supported sectors for the arrival airport
    When I send a request to alternate airports with the parameter "<departure>","<destination>","<distance>"
    Then distance between new departure airport and chosen departure is less than or equal 100 km
    And the list of departure airports sorted by distance ascending
    Examples:
      | departure | destination | distance |
      | LTN       | FAO         | 100      |

  @FCPH-242
  Scenario Outline: No results for alternate airports
    When I send a request to alternate airports with the parameter "<departure>","<destination>","<distance>"
    Then I will return an empty list of departure airports
    Examples:
      | departure | destination | distance |
      | ABZ       | VCE         | 40       |

  @FCPH-242
  Scenario Outline: Invalid data in the request
    When I send a request to alternate airports with the parameter "<departure>","<destination>","<distance>"
    Then I will return a error message on the channel "<code>"
    Examples:
      | departure | destination | distance | code            |
      | XXX       | FAO         | 100      | SVC_100180_2002 |
      | LTN       | XXX         | 100      | SVC_100180_2003 |
      | LTN       | FAO         | 0        | SVC_100180_2004 |

  @FCPH-8217
  Scenario Outline: No alternate departure airports within 40 km from departure airport to the destination
    When I send a request to alternate airports with the parameter "<departure>","<destination>","<distance>"
    Then the list of all alternative departure airports sorted alphabetically
    Examples:
      | departure | destination | distance |
      | LTN       | FAO         | 40       |