@FCPH-11221
@Sprint31
@TeamE
Feature: Send cancellation confirmation email

  @manual
  Scenario: 1 - Set up the Vat number against the country
    Given that I'm on a country in the back office
    When I create a country
    Then I will be able to enter a <VatNumber> against the country

  @manual
  Scenario Outline: 2- Amend the VAT number again the country
    Given that I'm on a country in the back office
    When I amend a country
    Then I will be able to update a <VatNumber> against the country
    Examples:
    | VatNumber    |	VatAddress                                                                                 |
    |GB 745 3608 25|easyJet Airline Company Limited - Hangar 89, London Luton Airport, Luton LU2 9PF United Kingdom|
    |FR 51453172470|easyJet Airline Company Limited - Hangar 89, London Luton Airport, Luton LU2 9PF United Kingdom|
    |DE 235666159  |easyJet Airline Company Limited - Hangar 89, London Luton Airport, Luton LU2 9PF United Kingdom|
    |IT 05884230961|easyJet Airline Company Limited - Hangar 89, London Luton Airport, Luton LU2 9PF United Kingdom|
    |ES N0066592G  |easyJet Airline Company Limited - Hangar 89, London Luton Airport, Luton LU2 9PF United Kingdom|
    |PT 980 467 101|easyJet Airline Company Limited - Hangar 89, London Luton Airport, Luton LU2 9PF United Kingdom|
