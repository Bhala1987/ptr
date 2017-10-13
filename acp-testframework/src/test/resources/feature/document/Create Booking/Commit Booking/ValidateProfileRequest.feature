@Sprint31 @TeamA @FCPH-10699
Feature: Creation of a temporary customer profile

  As a customer looking to travel with EasyJet
  I want to be able to book a flight without having explicitly registered an account
  So that I can book a flight without sharing my details and creating an account

  Scenario: Test that a temporary customer profile is created when a temporary registration is requested.
    Given I am using the channel Digital
    When I request for create temporary customer profile
    Then the customer profile is created
    And the response should not contain authentication details

  @negative
  Scenario: Test that a temporary customer cannot log in.
    Given I am using the channel Digital
    And I request for create temporary customer profile
    And the customer profile is created
    When I login as newly created customer
    Then I will receive an error with code 'SVC_100318_3003'

  Scenario: Test that a customers profile is changed from "Temporary" to "Registered" when they commit a successful booking.
    Given I am using the channel Digital
    And my basket contains flight with passengerMix "2 Adult"
    And I have updated the passenger information
    When I request for create temporary customer profile
    And I do commit booking for given basket
    Given I am using the channel ADAirport
    Then I request the customer profile

  @negative
  Scenario: Test that when 'empty basket' is called that the temporary customers profile is removed.
    Given I am using the channel Digital
    And my basket contains flight with passengerMix "1 Adult"
    And I have updated the passenger information
    When I request for create temporary customer profile
    And call empty basket service
    Then the temporary customer should be removed

  @negative
  Scenario: Test that the customer profile is kept as temporary if there is an error in the commit booking.
    Given I am using the channel Digital
    And my basket contains flight with passengerMix "1 Adult"
    And I request for create temporary customer profile
    And I do commit booking for given basket with unavailable inventory
    When I request the temporary customer profile
    Then the temporary profile is returned for the channel

  @manual
  Scenario: Test that the customer profile is removed when the session expires.
    Given I am using the channel Digital
    When I request for create temporary customer profile
    And the customer profile is created
    When the session times out at 20 mins
    Then the customer profile should be removed.