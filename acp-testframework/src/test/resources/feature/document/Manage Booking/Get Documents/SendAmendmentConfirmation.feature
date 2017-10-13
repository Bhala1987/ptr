@FCPH-7347
@Sprint32
@TeamE
Feature: Send the VAT invoice and create Booking history

  @manual
  Scenario Outline: 1 -  Display APIS section per new sector to the booking confirmation (When APIS set to false, Hide the section)
    Given that a new booking version has been created in hybris
    When the new verison of the booking has new flights added
    And one or more of the new sectors on the booking have APIS set to true
    Then the system will add a APIS section per sector to the Amendment confirmation
    And the section will have static <text> and a deep link
    Examples:
      |text                                                                                           |
      |URL - https://www.easyjet.com/ languageCode/boardingpass/viewboardingpasslist/BookingReference |
      |Example https://www.easyjet.com/en/boardingpass/viewboardingpasslist/ESDNS97                   |
      |Text to display                                                                                |
      |Next steps                                                                                     |
      |Add passport/ID details for all passengers                                                     |
      |Check in online                                                                                |
      |Get your boarding passes                                                                       |

  @manual
  Scenario Outline: 2 - Add SSR section to the booking confirmation
    Given that a new booking version has been created in hybris
    When the new verison of the booking has new flights added
    And one or more passenger had added a SSR
    Then the system will add a SSR section to the Amendment confirmation
    And the system will display passenger name and SSR <details> and SSR link (that leads to My Bookings) on the booking confirmation in the passenger details section
    Examples:
      |details                                                                              |
      |URL - https://www.easyjet.com/ languageCode/MyEasyJet/ViewBookingSSR/BookingReference|
      |Example - URL - https://www.easyjet.com/en/MyEasyJet/ViewBookingSSR/ESDNS97          |
      |Special assistance and nut allergy notification                                      |
      |<Title><FirstName><LastName>                                                         |
      |SSR Description                                                                      |

  @manual
  Scenario: 3 - Generate Amendment confirmation in language stored on the booking
    Given that a new booking version has been created in hybris
    When the system  generate the amendment confirmation
    Then It will generate localised information based on the language of the booking

  @manual
  Scenario: 4 - Send amend confirmation to the registered customer email address
    Given that a new booking version has been created in hybris
    When the system have generated the amendment confirmation
    Then It will send the amendment confirmation email to the customer registered email address
    And It will send the amendment confirmation to the passengers email address if known

  @manual
  Scenario: 5 - Generate Booking history entry for the confirmation email
    Given that a new booking version has been created in hybris
    When I have generated the amendment confirmation
    Then I will generate a entity a row on the booking history containing Document Type (eg: Amendment Confirmation) Requesting Channel, Issue Date / Time, Requesting User ID - Agent or Customer, Issued To - email address(es)
