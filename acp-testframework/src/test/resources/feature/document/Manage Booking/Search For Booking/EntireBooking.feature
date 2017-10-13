Feature: Retrieve Full Booking Details - adding flight options, documents

  @schema
  Scenario Outline: Create and Get booking details with purchased and non-purchased seats along with additional seat, hold bag,sports equipment,excess weight
    Given I am using the channel <channel>
    And I have created a new customer
    And I searched a '<fareType>' flight with return for <passenger>
    And I added it to the basket with <fareType> fare as <journey> journey
    And I add valid passenger details with ejPlus type customer
    And I add product Hold Bag with 1 excess weight to all passengers
    And I add product Large Sporting Equipment to all passenger on all flights
    And  I make a request to add an available "<seat>" seat product for each passenger
    When I do commit booking for given basket
    And I login as agent with username as "rachel" and password as "12341234"
    And I have received a valid addComments request for type "PASSENGER" with comment "test"
    And I validate and return the result for addComments request
    Then I do get booking details via <channel>
    And booking has APIS details for each passenger
    And the booking has details of respective <products>
    And the booking has seat details for respective passengers
    And the booking has details of allowed documents
    And the booking has details of additional seat
    And I validate the json schema for created booking event
    And I validate the json schema for created customer event
    Examples:
      | channel   | passenger | journey          | fareType | seat          | products                                        |
      | ADAirport | 2,1 adult | outbound/inbound | Standard | EXTRA_LEGROOM | Hold Bag,Large Sporting Equipment,Excess Weight |
