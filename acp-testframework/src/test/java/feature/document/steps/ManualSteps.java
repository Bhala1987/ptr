package feature.document.steps;

import cucumber.api.java.en.*;

/**
 * ManualSteps contains empty steps for the manual tests
 */
public class ManualSteps {

    @And("^there is no more inventory on that flight$")
    public void thereIsNoMoreInventoryOnThatFlight() {
    }

    @And("^the passenger is in Checkin Status$")
    public void thePassengerIsInCheckinStatus() {
    }

    @And("^I will change the passenger status to Booked$")
    public void iWillChangeThePassengerStatusToBooked() {
    }

    @And("^I will change the passenger status to Booked for all the flights$")
    public void iWillChangeThePassengerStatusToBookedForAllTheFlights() {
    }

    @And("^I have found a valid flight for multiple passengers for channel \"([^\"]*)\" and bundle \"([^\"]*)\"$")
    public void iHaveFoundAValidFlightForMultiplePassengersForChannelAndBundle(String arg0, String arg1) {
    }

    @And("^the admin fee should be apportioned per passenger and rounded to the nearest pence for the first (\\d+) sectors$")
    public void theAdminFeeShouldBeApportionedPerPassengerAndRoundedToTheNearestPenceForTheFirstSectors(int arg0) {
    }

    @And("^Flight tax is apportioned per passenger$")
    public void flightTaxIsApportionedPerPassenger() {
    }

    @And("^credit card fee is added at order level based on language set$")
    public void creditCardFeeIsAddedAtOrderLevelBasedOnLanguageSet() {
    }

    @Given("^I setup the deal with different types of bundle in backoffice$")
    public void iSetupTheDealWithDifferentTypesOfBundleInBackoffice() {
    }

    @And("^I have found a valid flight with deal for different types of bundle$")
    public void iHaveFoundAValidFlightWithDealForDifferentTypesOfBundle() {
    }

    @When("^I added this flight to my basket for \"([^\"]*)\"$")
    public void iAddedThisFlightToMyBasketFor(String arg0) {
    }

    @Then("^the correct bundle is displayed in the basket$")
    public void theCorrectBundleIsDisplayedInTheBasket() {
    }

    @Given("^I setup the deal with discount and posfee as (.*) in backoffice$")
    public void iSetupTheDealWithDiscountAndPosfeeAsCurrencyInBackoffice(String param) {
    }

    @And("^I have found a valid flight with deal in \"([^\"]*)\" for \"([^\"]*)\"$")
    public void iHaveFoundAValidFlightWithDealInFor(String arg0, String arg1) {
    }

    @When("^I added this flight to my basket with \"([^\"]*)\"$")
    public void iAddedThisFlightToMyBasketWith(String arg0) {
    }

    @Then("^Discount Tier and POS fee are applied with \"([^\"]*)\"$")
    public void discountTierAndPOSFeeAreAppliedWith(String arg0) {
    }

    @Given("^I have added a flight to the basket with (.*)$")
    public void iHaveAddedAFlightToTheBasketWithHoldItem(String productType) {
    }

    @And("^I change the price of the '(.*)' in backoffice$")
    public void iChangeThePriceOfTheHoldBagInBackoffice(String productType) {
    }

    @When("^I initiate the recalculatePrices service$")
    public void iInitiateTheRecalculatePricesService() {
    }

    @Then("^I will check the '(.*)' price change$")
    public void iWillCheckTheHoldItemPriceChange(String productType) {
    }

    @And("^I added an excess weight item to the hold item$")
    public void iAddedAnExcessWeightItemToTheHoldItem() {
    }

    @Given("^I have added a flight to the basket$")
    public void iHaveAddedAFlightToTheBasket() {
    }

    @And("^I change the fees and taxes in backoffice$")
    public void iChangeTheFeesAndTaxesInBackoffice() {
    }

    @Given("^I have a cancelled flight$")
    public void iHaveACancelledFlight() {
    }

    @Then("^I should get (.*) error$")
    public void iShouldGetError(String error) {
    }

    @Then("^I should get warning as(.*)$")
    public void iShouldGetWarningAs(String warning) {
    }

    @Given("^I have a valid departure flight from an airport added to the basket$")
    public void iHaveAValidDepartureFlightFromAnAirportAddedToTheBasket() {
    }

    @When("^I add a new inbound flight with departure date/time before the departure date/time of the outbound$")
    public void iAddANewInboundFlightWithDepartureDateTimeBeforeTheDepartureDateTimeOfTheOutbound() {
    }

    @Given("^I have the property minTimeBeforeScheduledTimeDepartureInHours is set to (\\d+) hours for (.*)$")
    public void iHaveThePropertyMinTimeBeforeScheduledTimeDepartureInHoursIsSetToHoursFor(int arg0, String arg1) {
    }

    @And("^I have a flight departing within the valid booking hours$")
    public void iHaveAFlightDepartingWithinTheValidBookingHours() {
    }

    @Given("^I have the property (.*) is set to (.*) for (.*)$")
    public void iHaveThePropertyMinTimeBetweenInboundAndOutboundIsSetToHoursFor(String property, String arg0, String arg1) {
    }

    @When("^I try to add a new flight inbound within (\\d+) hours of arrival$")
    public void iTryToAddANewFlightInboundWithinHoursOfArrival(int arg0) {
    }

    @Given("^I have flights available$")
    public void iHaveFlightsAvailable() {
    }

    @When("^I request addFlight with no X-Test header$")
    public void iRequestAddFlightWithNoXTestHeader() {
    }

    @Then("^I should receive the error \"([^\"]*)\" and \"([^\"]*)\"$")
    public void iShouldReceiveTheErrorAnd(String arg0, String arg1) {
    }

    @Given("^the property minTimeBetweenArrivalAndDeparture is configured to (\\d+) hours for \"([^\"]*)\"$")
    public void thePropertyMinTimeBetweenArrivalAndDepartureIsConfiguredToHoursFor(int arg0, String arg1) {
    }

    @And("^I have a valid departure flight to an airport added to the basket$")
    public void iHaveAValidDepartureFlightToAnAirportAddedToTheBasket() {
    }

    @When("^I add a new flight from a different airport within (\\d+) hours$")
    public void iAddANewFlightFromADifferentAirportWithinHours(int arg0) {
    }

    @Given("^the property maximumNumberOfAdditionalFlightsAdded is configured to (\\d+) for \"([^\"]*)\"$")
    public void thePropertyMaximumNumberOfAdditionalFlightsAddedIsConfiguredToFor(int arg0, String arg1) {
    }

    @Given("^a flight that is nearly full$")
    public void aFlightThatIsNearlyFull() {
    }

    @And("^the remaining seats have been allocated$")
    public void theRemainingSeatsHaveBeenAllocated() {
    }

    @When("^I search for the flight$")
    public void iSearchForTheFlight() {
    }

    @Then("^it is not returned in the list of available flights$")
    public void itIsNotReturnedInTheListOfAvailableFlights() {
    }

    @Given("^there is one seat unallocated on a flight$")
    public void thereIsOneSeatUnallocatedOnAFlight() {
    }

    @When("^two Agent Desktop channels attempt to add the flight to a basket at the same time$")
    public void twoAgentDesktopChannelsAttemptToAddTheFlightToABasketAtTheSameTime() {
    }

    @Then("^the one flight is allocated$")
    public void theOneFlightIsAllocated() {
    }

    @And("^the second returns an error$")
    public void theSecondReturnsAnError() {
    }

    @And("^the inventory exceeds the cap of the flight$")
    public void theInventoryExceedsTheCapOfTheFlight() {
    }

    @And("^the requesting channel is Agent Desktop$")
    public void theRequestingChannelIsAgentDesktop() {
    }

    @When("^I add product as (.*)$")
    public void iAddProductAsSportsEquipment(String productType) {
    }

    @And("^a override flag based if the channel is allowed to override the message$")
    public void aOverrideFlagBasedIfTheChannelIsAllowedToOverrideTheMessage() {
    }

    @Given("^the \"([^\"]*)\" has a bundle restriction set against the product$")
    public void theHasABundleRestrictionSetAgainstTheProduct(String arg0) {
    }

    @And("^the basket contains a bundle which is not allowed with the product$")
    public void theBasketContainsABundleWhichIsNotAllowedWithTheProduct() {
    }

    @And("^I will return a message to the channel unable to add due to bundle restrictions$")
    public void iWillReturnAMessageToTheChannelUnableToAddDueToBundleRestrictions() {
    }

    @Given("^the email is already linked to a recycled customer profile$")
    public void theEmailIsAlreadyLinkedToARecycledCustomerProfile() {
    }

    @When("^I do the booking with basket content for(.*)$")
    public void iDoTheBookingWithBasketContentForChannel() {
    }

    @Then("^the inventory is allocated during the commit booking process$")
    public void theInventoryIsAllocatedDuringTheCommitBookingProcess() {
    }

    @And("^I will create a audit record of the saved payment method being remove on the customer profile$")
    public void iWillCreateAAuditRecordOfTheSavedPaymentMethodBeingRemoveOnTheCustomerProfile() {
    }

    @And("^I will return confirmation to the channel$")
    public void iWillReturnConfirmationToTheChannel() {
    }

    @Given("^that the channel has initated a CheckInForFlight$")
    public void thatTheChannelHasInitatedACheckInForFlight() {
    }

    @When("^I have returned confirmation to the channel$")
    public void iHaveReturnedConfirmationToTheChannel() {
    }

    @Then("^I will create an history entry on the booking with the following details Channel initiated, Date$")
    public void iWillCreateAnHistoryEntryOnTheBookingWithTheFollowingDetailsChannelInitiatedDate() {
    }

    @And("^Time, User ID who initated the process, Event Type = Passenger checked in,$")
    public void timeUserIDWhoInitatedTheProcessEventTypePassengerCheckedIn() {
    }

    @And("^Event Description = including Passenger Name and flight Key who checked in$")
    public void eventDescriptionIncludingPassengerNameAndFlightKeyWhoCheckedIn() {
    }

    @And("^the requesting passenger has a bundle type of standby$")
    public void theRequestingPassengerHasABundleTypeOfStandby() {
    }

    @When("^the requesting \"([^\"]*)\" is not \"([^\"]*)\" to checkin the passenger$")
    public void theRequestingIsNotToCheckinThePassenger(String arg0, String arg1) {
    }

    @Then("^I will generate a error message to the channel$")
    public void iWillGenerateAErrorMessageToTheChannel() {
    }

    @And("^the number of infants on own seat equals the per adult on booking configuration$")
    public void theNumberOfInfantsOnOwnSeatExceedsThePerAdultOnBookingConfiguration() {
    }

    @And("^this equals the flight's limit of infants on their own seat$")
    public void thisExceedsTheFlightSLimitOfInfantsOnTheirOwnSeat() {
    }

    @And("^the flight to which the passenger is being added has operational status of \"([^\"]*)\"$")
    public void theFlightToWhichThePassengerIsBeingAddedHasOperationalStatusOf(String arg0) {
    }

    @And("^the flight in the basket is configured with the infant on seat per flight limit$")
    public void theFlightInTheBasketIsConfiguredWithTheInfantOnSeatPerFlightLimit() {
    }

    @And("^the flight to which the passenger is being added is today and departs in x hours$")
    public void theFlightToWhichThePassengerIsBeingAddedIsTodayAndDepartsInXHours() {
    }

    @Then("^I will check that the Adults Passenger Status is set to ‘Booked’$")
    public void iWillCheckThatTheAdultsPassengerStatusIsSetToBooked() {
    }

    @Then("^Booking history event set Date and Time,$")
    public void iWillSetDateAndTime() {
    }

    @Given("^I am using channel <channel>$")
    public void iAmUsingChannelChannel() {
    }

    @When("^the channel has initiated a CheckInForFlight for passenger$")
    public void theChannelHasInitiatedACheckInForFlightForPassenger() {
    }

    @Then("^I will create an history entry on the booking with the$")
    public void iWillCreateAnHistoryEntryOnTheBookingWithThe() {
    }

    @And("^following details Channel initiated, Date and Time,$")
    public void followingDetailsChannelInitiatedDateAndTime() {
    }

    @And("^User ID who initiated the process, Event Type = Passenger checked in,$")
    public void userIDWhoInitiatedTheProcessEventTypePassengerCheckedIn() {
    }


    @And("^Event Description including Passenger Name and flight key who checked in$")
    public void DescriptionIncludingPassengerNameAndFlightKeyWhoCheckedIn() {
    }

    @And("^Booking history event set Channel as Requesting Channel,$")
    public void iWillSetBookingHistoryChannelAsRequestingChannel() {
    }

    @And("^Booking history event set User as User Id,$")
    public void iWillSetBookingHistoryUserAsUserId() {
    }

    @And("^Booking history event set to 'APIS'$")
    public void iWillSetBookingHistoryEventToAPIS() {
    }

    @And("^Booking history event set Description to Flight Key, Passenger Last Name and First Name$")
    public void iWillSetBookingHistoryAndOtherDetailsSet() {
    }

    @And("^Booking history event set Version as Booking Version$")
    public void iWillSetBookingHistoryVersionAsBookingVersion() {
    }

    @And("^Booking history event set User as Anonymous,$")
    public void iWillSetBookingHistoryUserAsAnonymous() {
    }

    @And("^the change is from the Agent Desktop$")
    public void theChangeIsFromTheAgentDesktop() {
    }

    @And("^Booking history event set Agent ID as Agent Id,$")
    public void iWillSetBookingHistoryAgentIDAsAgentId() {
    }

    @And("^Booking history event set to 'APIS Added or Changed'$")
    public void iWillSetBookingHistoryEventToAPISAddedOrChanged() {
    }

    @And("^Booking history event set Agent ID as User Id,$")
    public void iWillSetBookingHistoryAgentIDAsUserId() {
    }

    @And("^Booking history event set Description to Flight Key, Passenger Last Name and First Name for each flight$")
    public void iWillSetBookingHistoryAndOtherDetailsSetForEachFlight() {
    }

    @Given("^I have made a booking$")
    public void iHaveMadeABooking() {
    }

    @And("^the booking date is > (\\d+)hrs in the past$")
    public void theBookingDateIsHrsInThePast(int arg0) {
    }

    @When("^I attempt to cancel that booking$")
    public void iAttemptToCancelThatBooking() {
    }

    @Then("^the refund amount returned should be (\\d+)$")
    public void theRefundAmountReturnedShouldBe(int arg0) {
    }

    @And("^the passenger is added to the flight in the basket$")
    public void thePassengerIsAddedToTheFlightInTheBasket() {
    }

    @When("^I attempt to add an infant passenger to the flight in the basket with override set to (true|false)$")
    public void iAttemptToAddAnInfantPassengerToTheFlightInTheBasket(String aOverride) {
    }

    @And("^the passenger is not added to the flight in the basket$")
    public void thePassengerIsNotAddedToTheFlightInTheBasket() {
    }

    @Given("^I login as customer \"([^\"]*)\"$")
    public void iLoginAsCustomer(String arg0) {
    }

    @And("^I create a basket by adding a flight$")
    public void iCreateABasketByAddingAFlight() {
    }

    @And("^I verify the created basket has the id \"([^\"]*)\"$")
    public void iVerifyTheCreatedBasketHasTheId(String arg0) {
    }

    @And("^I force the last modified date of the basket with id \"([^\"]*)\" to \"([^\"]*)\" days before the current date$")
    public void iForceTheLastModifiedDateOfTheBasketWithIdToDaysBeforeTheCurrentDate(String arg0, String arg1) {
    }

    @When("^I run the ejCoreSite-CartRemovalJob cron job$")
    public void iRunTheEjCoreSiteCartRemovalJobCronJob() {
    }

    @And("^I can't retrieve anymore the basket with id \"([^\"]*)\"$")
    public void iCanTRetrieveAnymoreTheBasketWithId(String arg0) {
    }

    @And("^I the last modified date of the basket with id \"([^\"]*)\" is less then <x> days before the current date$")
    public void iTheLastModifiedDateOfTheBasketWithIdIsLessThenXDaysBeforeTheCurrentDate(String arg0) {
    }

    @And("^I retrieve succesfully the basket with id \"([^\"]*)\"$")
    public void iRetrieveSuccesfullyTheBasketWithId(String arg0) {
    }

    @Then("^I receive an error message (.*)$")
    public void iReceiveAnErrorMessage(String error) {
    }

    @Given("^the passenger has (.*) status after committed a booking$")
    public void thePassengerHasBoardedStatusAfterCommittedABooking(String status) {
    }

    @When("^I send a remove flight request$")
    public void iSendARemoveFlightRequest() {
    }

    @And("^I receive a no inventory available response$")
    public void iReceiveANoInventoryAvailableResponse() {
    }

    @And("^today is x days less than or equal to the departure date of flight being removed$")
    public void todayIsXDaysLessThanOrEqualToTheDepartureDateOfFlightBeingRemoved() {
    }

    @Then("^the Change Flight Fee for Standard fare for less than x days of departure per passenger per seat will be added$")
    public void theChangeFlightFeeForLessThanXDaysOfDeparturePerPassengerPerSeatWillBeAdded() {
    }

    @And("^today is x days more than the departure date of flight being removed$")
    public void todayIsXDaysMoreThanTheDepartureDateOfFlightBeingRemoved() {
    }

    @Then("^the Change Flight Fee for Standard fare for more than x days of departure per passenger per seat will be added$")
    public void theChangeFlightFeeForMoreThanXDaysOfDeparturePerPassengerPerSeatWillBeAdded() {
    }

    @When("^I check the operational status of both sectors$")
    public void iCheckTheOperationalStatusOfBothSectors() {
    }

    @And("^one or more of the sectors has a operational status of cancelled$")
    public void oneOrMoreOfTheSectorsHasAOperationalStatusOfCancelled() {
    }

    @Then("^I will return a error to the channel$")
    public void iWillReturnAErrorToTheChannel() {
    }

    @And("^the journey will not be added to the basket$")
    public void theJourneyWillNotBeAddedToTheBasket() {
    }

    @And("^I have requested the allocation for the requested sectors$")
    public void iHaveRequestedTheAllocationForTheRequestedSectors() {
    }

    @When("^I receive a response that one of the flights in the pair is no longer available$")
    public void iReceiveAResponseThatOneOfTheFlightsInThePairIsNoLongerAvailable() {
    }

    @And("^generate request to deallocate other flight$")
    public void generateRequestToDeallocateOtherFlight() {
    }

    @And("^Error message is returned to the channel$")
    public void errorMessageIsReturnedToTheChannel() {
    }

    @When("^I receive a response that price has changed for one or more sectors$")
    public void iReceiveAResponseThatPriceHasChangedForOneOrMoreSectors() {
    }

    @Then("^I will continue to add the flights to the basket$")
    public void iWillContinueToAddTheFlightsToTheBasket() {
    }

    @And("^generate a Price Change message in the expected format$")
    public void generateAPriceChangeMessageInTheExpectedFormat() {
    }

    @And("^return the price change message to the channel$")
    public void returnThePriceChangeMessageToTheChannel() {
    }

    @When("^the flight has been successfully added to the basket$")
    public void theFlightHasBeenSuccessfullyAddedToTheBasket() {
    }

    @And("^for one of the flights has exceeded the number of infants booked on own seats$")
    public void forOneOfTheFlightsHasExceededTheNumberOfInfantsBookedOnOwnSeats() {
    }

    @Then("^I will return Error message and code returned with an Override flag returned$")
    public void iWillReturnErrorMessageAndCodeReturnedWithAnOverrideFlagReturned() {
    }

    @And("^the STD of the first sector of the journey is within (\\d+) hours of the arrival time of a flight already in the basket$")
    public void theSTDOfTheFirstSectorOfTheJourneyIsWithinHoursOfTheArrivalTimeOfAFlightAlreadyInTheBasket(int arg0) {
    }

    @And("^is departing from a different airport$")
    public void isDepartingFromADifferentAirport() {
    }

    @Then("^I will return a warning message to the channel$")
    public void iWillReturnAWarningMessageToTheChannel() {
    }

    @And("^the request is for a inbound journey$")
    public void theRequestIsForAInboundJourney() {
    }

    @And("^the basket already contains a outbound journey$")
    public void theBasketAlreadyContainsAOutboundJourney() {
    }

    @And("^the STD of the first sector of the of journey is before the outbound already in the basket$")
    public void theSTDOfTheFirstSectorOfTheOfJourneyIsBeforeTheOutboundAlreadyInTheBasket() {
    }

    @And("^return a error to the channel$")
    public void returnAErrorToTheChannel() {
    }

    @And("^the he STD of the first sector of the of journey is x hours of the outbound journey last sector arrival time$")
    public void theHeSTDOfTheFirstSectorOfTheOfJourneyIsXHoursOfTheOutboundJourneyLastSectorArrivalTime() {
    }

    @Then("^I will return a warning message$")
    public void iWillReturnAWarningMessage() {
    }

    @And("^the first sector STD is departing within (\\d+) hours$")
    public void theFirstSectorSTDIsDepartingWithinHours(int arg0) {
    }

    @Given("^that I am on the ejPayment types in the back office$")
    public void thatIAmOnTheEjPaymentTypesInTheBackOffice() {
    }

    @When("^I set up voucher as a payment method$")
    public void iSetUpVoucherAsAPaymentMethod() {
    }

    @Then("^I set which channels are allowed to use voucher as a payment method$")
    public void iSetWhichChannelsAreAllowedToUseVoucherAsAPaymentMethod() {
    }

    @Then("^bookings within (.+) before the last past flight's STD are returned$")
    public void bookingsWithinBeforeTheLastPastFlightsStdAreReturned(String period) {
    }

    @And("^I have the bookings available with the (.+)$")
    public void iHaveTheBookingsAvailableWithThe(String status) {
    }

    @And("^I (.+) bookings with any future flights based on status$")
    public void iBookingsWithAnyFutureFlightsBasedOnStatus(String result) {
    }

    @Given("^the hold items price is different$")
    public void theHoldItemsPriceIsDifferent() {
    }

    @Given("^hold items are not available on that flight$")
    public void holdItemsAreNotAvailableOnThatFlight() {
    }

    @And("^I will store Channel, User ID, comment type, Free Text Comment, created Date Time Stamp$")
    public void iWillStoreChannelUserIdCommentTypeFreeTextCommentCreatedDateTimeStamp() {
    }

    @And("^I\"ve my return flight more than and try to book car more than (\\d+) days$")
    public void iVeMyReturnFlightMoreThanAndTryToBookCarMoreThanDays(int noOfDays) {
    }

    @And("^the change flight fee for Flexi fare will be added$")
    public void theChangeFlightFeeForFlexiFareWillBeAdded() {
    }

    @When("^I calculate the seat offer price$")
    public void iCalculateTheSeatOfferPrice() {
    }

    @And("^the purchased seat is part of the faretype bundle$")
    public void thePurchasedSeatIsPartOfTheFaretypeBundle() {
    }

    @And("^the purchased seat is not part of the faretype bundle$")
    public void thePurchasedSeatIsNotPartOfTheFaretypeBundle() {
    }

    @And("^the same seat is available in the new flight$")
    public void theSameSeatIsAvailableInTheNewFlight() {
    }

    @And("^the new seat offer price is less than or equal to the old seat offer price$")
    public void theNewSeatOfferPriceIsLessThanOrEqualToTheOldSeatOfferPrice() {
    }

    @And("^I use the old seat price as the purchased seat price$")
    public void iUseTheOldSeatPriceAsThePurchasedSeatPrice() {
    }

    @And("^the new seat offer price is greater than the old seat offer price$")
    public void theNewSeatOfferPriceIsGreaterThanTheOldSeatOfferPrice() {
    }

    @And("^I use the new seat price as the purchased seat price$")
    public void iUseTheNewSeatPriceAsThePurchasedSeatPrice() {
    }

    @But("^there is no seat inventory on that flight$")
    public void thereIsNoSeatInventoryOnThatFlight() {
    }

    @Then("^the flight from the customer's registered flight interest should permanently remove$")
    public void theFlightFromTheCustomerSRegisteredFlightInterestShouldPermanentlyRemove() {
    }

    @And("^I should receive the updated list$")
    public void iShouldReceiveTheUpdatedList() {
    }

    @Then("^the flight should be removed from the list$")
    public void theFlightShouldBeRemovedFromTheList() {
    }


    @Given("^the revenue protection comments file placed in hot folder$")
    public void theRevenueProtectionCommentsFilePlacedInHotFolder() {
    }

    @When("^an invalid booking reference mentioned for some items$")
    public void anInvalidBookingReferenceMentionedForSomeItems() {
    }

    @Then("^each failed item appear as an individual line in the error report generated$")
    public void eachFailedItemAppearAsAnIndividualLineInTheErrorReportGenerated() {
    }

    @And("^each item contains the reason why the data was not able to import$")
    public void eachItemContainsTheReasonWhyTheDataWasNotAbleToImport() {
    }

    @And("^each item includes date/time it failed to import$")
    public void eachItemIncludesDateTimeItFailedToImport() {
    }

    @And("^continue to process the other valid items on the file$")
    public void continueToProcessTheOtherValidItemsOnTheFile() {
    }

    @When("^a booking reference is blank for some items$")
    public void aBookingReferenceIsBlankForSomeItems() {
    }

    @When("^a valid booking reference mentioned$")
    public void aValidBookingReferenceMentioned() {
    }

    @And("^the comments will be added to the customer profile with <entries>$")
    public void theCommentsWillBeAddedToTheCustomerProfileWithEntries() {
    }

    @When("^there are failed items which are unable to load$")
    public void thereAreFailedItemsWhichAreUnableToLoad() {
    }

    @Then("^send the list of failed items which have been unable to load in an email$")
    public void sendTheListOfFailedItemsWhichHaveBeenUnableToLoadInAnEmail() {
    }

    @Then("^the comments will be added to the Booking with (.*)$")
    public void theCommentsWillBeAddedToTheBookingWithEntries(String entries) {
    }

    @Then("^the comments will be added to the customer profile with (.*)$")
    public void theCommentsWillBeAddedToTheCustomerProfileWithEntries(String entries) {
    }

    @When("^the session times out at (\\d+) mins$")
    public void theSessionTimesOutAtMins(int mins) {
    }

    @Then("^the customer profile should be removed.$")
    public void theCustomerProfileShouldBeRemoved() {
    }

    @Given("^a customer profile exists with a full set of Data$")
    public void aCustomerProfileExistsWithAFullSetOfData() {
    }

    @Given("^that the channel has initiated a commitBooking request$")
    public void thatTheChannelHasInitiatedACommitBookingRequest() {
    }

    @When("^the request contains a paymentFeeattribute for a payment$")
    public void theRequestContainsAPaymentFeeattributeForAPayment() {
    }

    @Then("^the paymentFee attribute should be stored in PaymentTransactionEntry$")
    public void thePaymentFeeAttributeShouldBeStoredInPaymentTransactionEntry() {
    }

    @When("^I search for the profile with the prefer header settled to \"([^\"]*)\" ,X-POS-Id settled to \"([^\"]*)\" and pathParameter sections settled to \"([^\"]*)\"$")
    public void iSearchForTheProfileWithThePreferHeaderSettledToXPOSIdSettledToAndPathParameterSectionsSettledTo(String prefer, String channel, String sections) {
    }

    @Then("^The advanced profile is returned accordingly to the prefer header settled to \"([^\"]*)\" ,X-POS-Id settled to \"([^\"]*)\" and pathParameter sections settled to \"([^\"]*)\"$")
    public void theAdvancedProfileIsReturnedAccordinglyToThePreferHeaderSettledToXPOSIdSettledToAndPathParameterSectionsSettledTo(String prefer, String channel, String sections) {
    }

    @And("^set the session timeout to (\\d+) seconds in the properties file$")
    public void setTheSessionTimeoutToSecondsInThePropertiesFile(int arg0) {
    }

    @And("^I login with the customer$")
    public void iLoginWithTheCustomer() {
    }

    @And("^I do flight search and add a flight to basket$")
    public void iDoFlightSearchAndAddAFlightToBasket() {
    }

    @And("^verify in the back office that the cart is associated to the logged in customer$")
    public void verifyInTheBackOfficeThatTheCartIsAssociatedToTheLoggedInCustomer() {
    }

    @And("^wait for (\\d+) seconds$")
    public void waitForSeconds(int arg0) {
    }

    @Then("^verify in the back office that the cart is associated to the anonymous user$")
    public void verifyInTheBackOfficeThatTheCartIsAssociatedToTheAnonymousUser() {
    }

    @And("^I add a (.*) to the basket$")
    public void iAddAProductToTheBasket(String item) {
    }

    @Given("^I have a basket that contains VATable fare product for a passenger$")
    public void iHaveABasketThatContainsVATableFareProductForAPassenger() {
    }

    @When("^I commit that basket to create a booking$")
    public void iCommitThatBasketToCreateABooking() {
    }

    @Given("^I have a basket that contains seat product for a passenger$")
    public void iHaveABasketThatContainsSeatProductForAPassenger() {
    }

    @And("^Sector being used is VATable with valid date range$")
    public void sectorBeingUsedIsVATableWithValidDateRange() {
    }

    @Given("^I have a basket that contains sports equipment for a passenger$")
    public void iHaveABasketThatContainsSportsEquipmentForAPassenger() {
    }

    @Given("^I have a basket that contains Hold Bag and Excess weight for a passenger$")
    public void iHaveABasketThatContainsHoldBagAndExcessWeightForAPassenger() {
    }

    @Given("^I have a basket that contains SpeedyBoarding and FastTrackSecurity for a passenger part of Flexi bundle$")
    public void iHaveABasketThatContainsSpeedyBoardingAndFastTrackSecurityForAPassengerPartOfFlexiBundle() {
    }

    @Given("^I have a Amended basket that contains Hold Bag and Excess weight for a passenger$")
    public void iHaveAAmendedBasketThatContainsHoldBagAndExcessWeightForAPassenger() {
    }

    @Given("^I have a basket that contains VATable fare product for a passenger with any channel$")
    public void iHaveABasketThatContainsVATableFareProductForAPassengerWithAnyChannel() {
    }

    @And("^Sector being used is not VATable with valid date range$")
    public void sectorBeingUsedIsNotVATableWithValidDateRange() {
    }

    @Then("^In the back office I shouldn't be able to see that the VAT is stored against that products$")
    public void inTheBackOfficeIShouldnTBeAbleToSeeThatTheVATIsStoredAgainstThatProducts() {
    }

    @Given("^I have a basket that contains Non VATable sports equipment for a passenger$")
    public void iHaveABasketThatContainsNonVATableSportsEquipmentForAPassenger() {
    }

    @Given("^I have a basket that contains VATable seat product for a passenger$")
    public void iHaveABasketThatContainsVATableSeatProductForAPassenger() {
    }

    @And("^Sector is VATable But Flight departure date is not in date range$")
    public void sectorIsVATableButFlightDepartureDateIsNotInDateRange() {
    }

    @Given("^I have a basket that contains VATable Sport Equipment for a passenger$")
    public void iHaveABasketThatContainsVATableSportEquipmentForAPassenger() {
    }

    @Then("^In the back office I should be able to see that the VAT against products are as follows:$")
    public void inTheBackOfficeIShouldBeAbleToSeeThatTheVATAgainstProductsAreAsFollows() {
    }

    @And("^I verify the flight and seat has been deallocated properly$")
    public void iVerifyTheFlightAndSeatHasBeenDeallocatedProperly() {
    }

    @And("^a CSV file will attached$")
    public void aCSVFileWillAttached() {
    }

    @When("^a Comment is blank for some items$")
    public void aCommentIsBlankForSomeItems() {
    }

    @Given("^that a new flight has been created$")
    public void thatANewFlightHasBeenCreated() {
    }

    @When("^I create the flight$")
    public void iCreateTheFlight() {
    }

    @Then("^I generate an event to inform downstream systems$")
    public void iGenerateAnEventToInformDownstreamSystems() {
    }

    @Then("^the session should end$")
    public void theSessionShouldEnd() {
    }

    @And("^the open baskets will be removed$")
    public void theOpenBasketsWillBeRemoved() {
    }

    @And("^the customer/agent should be removed from the session$")
    public void theCustomerAgentShouldBeRemovedFromTheSession() {
    }

    @And("^the allocated inventory should be deallocated$")
    public void theAllocatedInventoryShouldBeDeallocated() {
    }

    @And("^a session has been created$")
    public void aSessionHasBeenCreated() {
    }

    @Then("^a customer is logged in$")
    public void aCustomerIsLoggedIn() {
    }

    @And("^the basket should be saved in the customer profile$")
    public void theBasketShouldBeSavedInTheCustomerProfile() {
    }

    @When("^there has been no activity on the session for <TimeOut>$")
    public void thereHasBeenNoActivityOnTheSessionForTimeOut() {
    }

    @Given("^a <Channel> is used$")
    public void aChannelIsUsed() {
    }

    @Given("^I create one \"([^\"]*)\" voucher$")
    public void iCreateOneVoucher(String arg0) {
    }

    @And("^I set the active date in the past$")
    public void iSetTheActiveDateInThePast() {
    }

    @And("^I set the remaining voucher value greater than zero$")
    public void iSetTheRemainingVoucherValueGreaterThanZero() {
    }

    @When("^I run the transferUnUsedExpiredVocherFundsJob$")
    public void iRunTheTransferUnUsedExpiredVocherFundsJob() {
    }

    @Then("^It will automatically set the Voucher Remaining Balance to zero$")
    public void itWillAutomaticallySetTheVoucherRemainingBalanceToZero() {
    }

    @And("^It will transfer the money to an Unused Voucher Credit File Fund with \"([^\"]*)\" currency$")
    public void itWillTransferTheMoneyToAnUnusedVoucherCreditFileFundWithCurrency(String arg0) {
    }

    @And("^I set the active date in the future$")
    public void iSetTheActiveDateInTheFuture() {
    }

    @Then("^It will not change neither the voucher or the Credit File$")
    public void itWillNotChangeNeitherTheVoucherOrTheCreditFile() {
    }

    @Given("^I have a booking that has refund transactions$")
    public void iHaveABookingThatHasRefundTransactions() {
    }

    @And("^those transactions have not yet been fulfilled$")
    public void thoseTransactionsHaveNotYetBeenFulfilled() {
    }

    @When("^a message is received from the payment service to update the refund transactions to: (.*)$")
    public void aMessageIsReceivedFromThePaymentServiceToUpdateTheRefundTransactionsTo() {
    }

    @Then("^the refund transactions should have their status's updated to: (.*)$")
    public void theRefundTransactionsShouldHaveTheirStatusSUpdatedToRefundStatus(String refundStatus) {
    }

    @And("^the payment transaction modified date should be set to 'now'$")
    public void thePaymentTransactionModifiedDateShouldBeSetToNow() {
    }

    @And("^the agent has never logged in before$")
    public void theAgentHasNeverLoggedInBefore() {
    }

    @And("^the messages are sort by message valid from date$")
    public void theMessagesAreSortByMessageValidFromDate() {
    }

    @Then("^return message of the days since the agent was last logged in$")
    public void returnMessageOfTheDaysSinceTheAgentWasLastLoggedIn() {
    }

    @And("^the agent has logged in previously on the same day$")
    public void theAgentHasLoggedInPreviouslyOnTheSameDay() {
    }

    @And("^I will amend the basket after 24 hrs$")
    public void iamendTheBasketAfter24hrs() {
    }

    @When("^I send a request to Remove a passenger for amended basket$")
    public void iSendARequestToRemoveAPassengerForAmendedBasket() {
    }

    @Then("^I will add the cancel fee$")
    public void iWillAddTheCancelFee() {
    }

    @When("^the session is expired$")
    public void theSessionIsExpired() {
    }

    @Then("^the payment service return a status refund accepted$")
    public void thePaymentServiceReturnAStatusRefundAccepted() {
    }

    @Given("^that a (fee|tax) row exists in the back office$")
    public void thatAFeeRowExistsInTheBackOffice() {
    }

    @When("^I change any (fee|tax) in the back office$")
    public void iChangeAnyFeeInTheBackOffice() {
    }

    @Then("^I will generate an event to inform downstream systems$")
    public void iWillGenerateAnEventToInformDownstreamSystems() {
    }

    @But("^the price for the requested seat has been changed$")
    public void thePriceForTheRequestedSeatHasBeenChanged() {
        // not to be implemented. Emulated throw wiremock
    }

    @Then("^I will determine the booking is not editable for the bookingtype \"([^\"]*)\"$")
    public void iWillDetermineTheBookingIsNotEditableForTheBookingtype(String arg0) {
        // not to be implemented. Emulated throw wiremock
    }

    @Then("^I will determine the booking is not editable for the accesstype \"([^\"]*)\"$")
    public void iWillDetermineTheBookingIsNotEditableForTheAccesstype(String arg0) {
        // not to be implemented. Emulated throw wiremock
    }

    @And("^the order version remains the same$")
    public void theOrderVersionRemainsTheSame() {
    }

    @When("^I see the Agent Permissions Set Up$")
    public void iSeeTheAgentPermissionsSetUp() {
    }

    @When("^I send a Group Quote Email request$")
    public void iSendAGroupQuoteEmailRequest() {
    }

    @Then("^the group quote email will be sent to the specified email address with the pdf attached$")
    public void theGroupQuoteEmailWillBeSentToTheSpecifiedEmailAddressWithThePdfAttached() {
    }

    @And("^it will be as per template provided$")
    public void itWillBeAsPerTemplateProvided() {
    }

    @And("^it will be the basket language$")
    public void itWillBeTheBasketLanguage() {
    }

    @Given("^that a disruption level has been set in the master system$")
    public void thatADisruptionLevelHasBeenSetInTheMasterSystem() {
    }

    @And("^ACP received the event that inform of the disruption level$")
    public void acpReceivedTheEventThatInformOfTheDisruptionLevel() {
    }

    @When("^I search the flight in the back office$")
    public void iSearchTheFlightInTheBackOffice() {
    }

    @Then("^the disruption level is set against the flight$")
    public void theDisruptionLevelIsSetAgainstTheFlight() {
    }

    @And("^the disruption reason is set against the flight$")
    public void theDisruptionReasonIsSetAgainstTheFlight() {
    }

    @Given("^a disruption is set against a flight( on a related booking)?$")
    public void aDisruptionIsSetAgainstAFlight() {
    }

    @When("^I search a booking that contain that flight$")
    public void iSearchABookingThatContainThatFlight() {
    }

    @Then("^I will see the disruption level against the flight on the booking$")
    public void iWillSeeTheDisruptionLevelAgainstTheFlightOnTheBooking() {
    }

    @And("^I will see the disruption reason against the flight on the booking$")
    public void iWillSeeTheDisruptionReasonAgainstTheFlightOnTheBooking() {
    }

    @When("^ACP receive the event that inform of the disruption level$")
    public void acpReceiveTheEventThatInformOfTheDisruptionLevel() {
    }

    @Then("^a booking history entry is created for each booking that contain that flight$")
    public void aBookingHistoryEntryIsCreatedForEachBookingThatContainThatFlight() {
    }

    @And("^the entry will contain:$")
    public void theEntryWillContain() {
    }

    @Then("^I see the below (.*) that can be controlled$")
    public void iSeeTheBelowThatCanBeControlled(String capability) {
        // not to be implemented. Emulated throw wiremock
    }


    @And("^click add principal permissions$")
    public void clickAddPrincipalPermissions() {
        // not to be implemented. Emulated throw wiremock
    }

    @And("^in an backoffice wizard, select category,capability,usergroup$")
    public void inAnBackofficeWizardSelectCategoryCapabilityUsergroup() {
        // not to be implemented. Emulated throw wiremock
    }

    @Then("^I can choose either ALLOWED or DENIED as an access status and done to create a new User Group permission$")
    public void iCanChooseALLOWEDOrDENIEDAccessStatusToCreateANewUserGroupPermission() {
        // not to be implemented. Emulated throw wiremock
    }


    @And("^search with (.*),(.*),(.*) and an access status (.*)$")
    public void searchWithAndAnAccessStatus(String category, String capability, String usergroup, String accessStatus) {
        // not to be implemented. Emulated throw wiremock
    }

    @And("^I can see agent permission record with valid (.*),(.*),(.*),(.*) in editable mode$")
    public void iCanSeeAgentPermissionRecordValidInEditableMode(String category, String capability, String usergroup, String accessStatus) {
        // not to be implemented. Emulated throw wiremock
    }

    @Then("^I should be able change User Group permissions setup by specifying (.*) and save$")
    public void iShouldBeAbleChangeUserGroupPermissionsBySpecifying(String accessStatus) {
        // not to be implemented. Emulated throw wiremock
    }

    @Then("^ACP will generate the event to inform down stream systems of only the changes which have changed for the specific passenger$")
    public void iWillGenerateTheEventToInformDownStreamSystemsOfOnlyTheChangesWhichHaveChangedForTheSpecificPassenger() {
    }

    @Then("^I can choose (.*) as an access status and done to create a new User Group permission$")
    public void iCanChooseAccessStatusNCreateNewUserGroupPermission(String accessStatus) {
        // not to be implemented. Emulated throw wiremock
    }

    @Given("^that I am in the back office with (.*) login$")
    public void thatIAmInTheBackOfficeWithAdminLogin(String login) {
        // not to be implemented. Emulated throw wiremock
    }

    @When("^I select to create/modify a new Bulk transfer reasons$")
    public void iSelectToCreateModifyANewBulkTransferReasons() {
    }

    @Then("^I see bulk transfer reason code is mandatory$")
    public void iSeeBulkTransferReasonCodeIsMandatory() {
    }

    @And("^I see localised Name is mandatory$")
    public void iSeeLocalisedNameIsMandatory() {
    }

    @And("^than I'm in the bulk transfer reason folder$")
    public void thanIMInTheBulkTransferReasonFolder() {
    }

    @When("^I select to create/modify a new Bulk transfer reasons with valid details$")
    public void iSelectToCreateModifyANewBulkTransferReasonsWithValidDetails() {
    }

    @When("^I save the bulk transfer reason$")
    public void iSaveTheBulkTransferReason() {
    }

    @Then("^I will store creation/modification date, time User ID$")
    public void iWillStoreCreationModificationDateTimeUserID() {
    }

    @Given("^that a disruption level has been removed in the master system$")
    public void thatADisruptionLevelHasBeenRemovedInTheMasterSystem() {
    }

    @Then("^the disruption level is removed against the flight$")
    public void theDisruptionLevelIsRemovedAgainstTheFlight() {
    }

    @And("^the disruption reason is removed against the flight$")
    public void theDisruptionReasonIsRemovedAgainstTheFlight() {
    }

    @And("^a disruption is removed against a flight$")
    public void aDisruptionIsRemovedAgainstAFlight() {
    }

    @Then("^I will see the disruption level removed against the flight on the booking$")
    public void iWillSeeTheDisruptionLevelRemovedAgainstTheFlightOnTheBooking() {
    }

    @And("^I will see the disruption reason removed against the flight on the booking$")
    public void iWillSeeTheDisruptionReasonRemovedAgainstTheFlightOnTheBooking() {
    }

    @And("^the disruption level against the flight on the booking has been removes$")
    public void theDisruptionLevelAgainstTheFlightOnTheBookingHasBeenRemoves() {
    }

    @When("^I see all Agent Permission records are available in backoffice$")
    public void iSeeAllAgentPermissionsAvailableInBackoffice() {
        // not to be implemented. Emulated throw wiremock
    }

    @And("^compare agent permission records against spreadsheet for accessstatus$")
    public void compareAgentPermissionSpreadsheetForAccessstatus() {
        // not to be implemented. Emulated throw wiremock
    }

    @Then("^I will determine the datasetup is done correctly for each \"([^\"]*)\"$")
    public void iWillDetermineDatasetupIsCorrectlyForEach(String arg0) {
        // not to be implemented. Emulated throw wiremock
    }

    @And("^verify (.*) with the combination of (.*),(.*),(.*)$")
    public void verifyWithTheCombinationOf(String arg0, String arg1, String arg2, String arg3) {
        // not to be implemented. Emulated throw wiremock
    }

    @And("^I generate an email with the voucher details$")
    public void iGenerateAnEmailWithTheVoucherDetails() {
    }

    @And("^I send the email the requested email address$")
    public void iSendTheEmailTheRequestedEmailAddress() {
    }

    @When("^I send the getAdvancedCustomerProfile request$")
    public void iSendTheGetAdvancedCustomerProfileRequest() {
    }

    @Then("^I will return additional information to the channel$")
    public void iWillReturnAdditionalInformationToTheChannel() {
    }

    @When("^I send the getBookingSummaries request$")
    public void iSendTheGetBookingSummariesRequest() {
    }

    @Then("^I will return the disruption level against the flight$")
    public void iWillReturnTheDisruptionLevelAgainstTheFlight() {
    }
}