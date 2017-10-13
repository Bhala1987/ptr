@FCPH-8950
@Sprint27
Feature: Multilanguage
    @manual
    Scenario: 1 - Receive request for one or more than passenger
      Given that the channel initiates a generate boarding pass request
      When I receive the request
      And the request is more than one passenger
      Then I will generate the boarding pass for all requested passenger

    @manual
    Scenario: 2 - Store the Passenger Identifier
      Given that the channel initiates a generate boarding pass request
      When I receive a the passenger identifier from AL
      Then I will store this against the passenger

    @manual
    Scenario: 3 - Generate Boarding Pass
      Given that I have received a valid boarding pass request
      When I generate the boarding pass
      Then the boarding pass will contain Booking Reference Number
      And QR code/Barcode
      And Sequence Number
      And Flight details
      And Passenger Details
      And Fare Icon
      And Seat number
      And Baggage details
      And Board at Rear/ Front door
      And Additional information section
      And Adverts
      And I will generate localised information based on the <<language>> of the request
      And I will transliterate the passenger name to remove any special characters-
      |Language   |
      |Greek      |
      |Hungarian  |
      |English    |
      |Italian    |
      |French     |
      |Catalan    |
      |Czech      |
      |Danish     |
      |German     |
      |Spanish    |
      |Netherlands|
      |Polish     |
      |Portuguese |

    @manual
    Scenario: 10 - Boarding pass with additional messages/information
      Given that I have received a valid boarding pass request
      When additional information for airport has been created
      Then Additional information message will be included on the boarding pass

    @manual
    Scenario: 11 - Boarding pass pdf generation
      Given that I have received a valid boarding pass request
      When I generate the boarding pass
      Then store the boarding pass in a shared file system
      And generate URL to the location of the boarding pass
      And generate version for  boarding pass

    @manual
    Scenario: 12 - Send URL to the channel
      Given that I have received a valid boarding pass request
      When I generate the boarding pass
      Then I will send the URL to the channel to the location of the boarding pass

    @manual
    Scenario: 13 - store boarding pass details for flight against the booking
      Given that I have received a valid boarding pass request
      When the boarding pass has been generated
      Then I will store date and time generated
      And Version number
      And Channel it was generated from (eg web, mobile, airport)
      And User ID who  generated it (eg customer id, agent id)
      And action which generated the boarding pass (Check-in, reprint)
      And flight key the boarding pass was generated for
