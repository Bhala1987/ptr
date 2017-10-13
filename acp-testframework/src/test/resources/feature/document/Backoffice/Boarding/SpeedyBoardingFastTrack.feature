@backoffice:FCPH-9676
@Sprint27
Feature: Add Speedy Boarding and Fast track to Bundles

  Scenario: 1 - Add Fast track and speedy boarding to fare type bundles
  Given that I'm in the back office
  When I associate the products which make up the fare type bundle
  Then I will add the <<products>> to the <<fare type bundle>>

  |Fare type bundle|Include Speedy Boarding|Include Fast Track|
  |Standard        |N                      |N                 |
  |Flexi           |Y                      |Y                 |
  |Inclusive       |N                      |N                 |
  |Staff           |N                      |N                 |
  |Staff Standard  |N                      |N                 |
  |StandBy         |N                      |N                 |

  Scenario: 2 - Add fast track and speedy boarding to product bundles
  Given that I'm in the back office
  When I associate the products which make up the product bundle
  Then I will add the <<products>> to the <<product bundle>>

  |Product bundle        |Include Speedy Boarding|Include Fast Track|
  |ejPlus Customer       |Y                      |Y                 |
  |ej Plus Staff Customer|N                      |Y                 |
  |Up front Seat         |Y                      |N                 |
  |extra leg room        |Y                      |N                 |


  Scenario: 3 - Add fast track and speedy boarding to the basket when flight is added to the basket
  Given that I have received a valid addFlight request
  When the <<FareType Bundle>> includes fast track and speedy boarding products
  Then I will add the products to the basket

  |Fare type bundle|Include Speedy Boarding|Include Fast Track|
  |Standard        |N                      |N                 |
  |Flexi           |Y                      |Y                 |
  |Inclusive       |N                      |N                 |
  |Staff           |N                      |N                 |
  |Staff Standard  |N                      |N                 |
  |StandBy         |N                      |N                 |

  Scenario: 4 - Add fast track and speedy boarding to the basket when seat is added to the basket
  Given that I have received a valid addSeat request
  When the <<Product Bundle>> includes fast track and speedy boarding products
  Then I will add the products to the basket

  |Product bundle|Include Speedy Boarding|Include Fast Track|
  |Up front Seat |Y                      |N                 |
  |extra leg room|Y                      |N                 |


  Scenario: 5 - Add fast track and speedy boarding to the basket when passenegr has ejPlus is added to the basket
  Given that I have received a valid updatePassenger request
  When the ejPlus membership is added to the basket
  Then I will add the <<product bundle>> to the basket

  |Product bundle        |Include Speedy Boarding|Include Fast Track|
  |ejPlus Customer       |Y                      |Y                 |
  |ej Plus Staff Customer|N                      |Y                 |
