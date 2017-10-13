Feature: Generate and email a Group Quote - only AD

  @Sprint32 @TeamD @FCPH-10771
  Scenario Outline: 1 - Receive Group Booking Quote Request - invalid parameter
    Given one of this channel ADAirport, ADCustomerService is used
    And I create a new valid customer
    And I added a flight to the basket as group booking
    When I send a Group Quote Email request with <invalid> parameter
    Then the channel will receive an error with code <errorCode>

    Examples:
      | invalid           | errorCode       |
      | Invalid email     | SVC_100189_2001 |
      | invalid basket id | SVC_100189_2002 |

  @Sprint32 @TeamD @FCPH-10771
  Scenario: 2 - Receive the link of the PDF in the response
    Given one of this channel ADAirport, ADCustomerService is used
    And I create a new valid customer
    And I added a flight to the basket as group booking
    When I send a request to Group Quote Email service
    Then the response is correct

  @Sprint32 @TeamD @FCPH-10771 @manual
  Scenario: Generate the Group Quote
    Given one of this channel ADAirport, ADCustomerService is used
    And I create a new valid customer
    And I added a flight to the basket as group booking
    When I send a Group Quote Email request
    Then the group quote email will be sent to the specified email address with the pdf attached
    And it will be as per template provided
    And it will be the basket language

