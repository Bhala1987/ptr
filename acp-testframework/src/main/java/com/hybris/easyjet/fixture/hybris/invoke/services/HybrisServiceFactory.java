package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.config.EasyjetHybrisConfig;
import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.fixture.hybris.invoke.requests.*;
import com.hybris.easyjet.fixture.hybris.invoke.requests.booking.*;
import com.hybris.easyjet.fixture.hybris.invoke.requests.eventmessage.GenerateEventMessageRequest;
import com.hybris.easyjet.fixture.hybris.invoke.requests.refdata.*;
import com.hybris.easyjet.fixture.hybris.invoke.requests.savedpassenger.SavedPassengerRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.*;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.*;
import com.hybris.easyjet.fixture.hybris.invoke.services.customer.managepaymentdetails.PaymentMethodTypeService;
import com.hybris.easyjet.fixture.hybris.invoke.services.customer.managepaymentdetails.RemoveSavedPaymentService;
import com.hybris.easyjet.fixture.hybris.invoke.services.customer.managepaymentdetails.SavedPaymentMethodService;
import com.hybris.easyjet.fixture.hybris.invoke.services.eventmessagecreation.EventMessageService;
import com.hybris.easyjet.fixture.hybris.invoke.services.managebooking.PaymentBalanceService;
import com.hybris.easyjet.fixture.hybris.invoke.services.refdata.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by daniel on 02/12/2016.
 * this factory class is under spring control and therefore allows autowiring of configuration and jersey client in the
 * instantiation of new 'service' objects, calling get with a specific type of request as the argument will return the correct
 * service object, ready to be controlled, modified, invoked and queried
 */
@Component
public class HybrisServiceFactory {

    private final EasyjetHybrisConfig config;

    @Autowired
    private FeesAndTaxesDao feesAndTaxesDao;

    /**
     * @param config autowired configuration
     */
    @Autowired
    public HybrisServiceFactory(EasyjetHybrisConfig config) {
        this.config = config;
    }

    public BasketService addFlight(BasketRequest basketRequest) {
        return new BasketService(basketRequest, config.getHybrisAddBasketEndpoint());
    }

    public BasketService getBasket(BasketRequest basketRequest) {
        return new BasketService(basketRequest, config.getHybrisGetBasketEndpoint());
    }

    public DeleteBasketService deleteBasket(BasketRequest basketRequest) {
        return new DeleteBasketService(basketRequest, config.getHybrisGetBasketEndpoint());
    }

    public BasketTravellerService updatePassengers(BasketTravellerRequest basketTravellerRequest) {
        return new BasketTravellerService(basketTravellerRequest, config.getHybrisGetBasketEndpoint());
    }

    public FlightsService findFlight(FlightsRequest flightsRequest) {
        return new FlightsService(flightsRequest, config.getHybrisFlightsEndpoint());
    }

    public CustomerProfileService getCustomerProfile(ProfileRequest profileRequest) {
        return new CustomerProfileService(profileRequest, config.getHybrisCustomers());
    }

    public IdentifyCustomerService identifyCustomer(IdentifyCustomerRequest identifyCustomerRequest) {
        return new IdentifyCustomerService(identifyCustomerRequest, config.getHybrisCustomers());
    }

    public PaymentMethodsService getPaymentMethods(PaymentMethodsRequest paymentMethodsRequest) {
        return new PaymentMethodsService(paymentMethodsRequest, config.getHybrisGetPaymentMethodsEndPoint());
    }

    public CommitBookingService commitBooking(CommitBookingRequest commitBookingRequest) {
        return new CommitBookingService(commitBookingRequest, config.getHybrisBookingsEndpoint());
    }

    public GetBookingService getBookings(GetBookingRequest getBookingRequest) {
        return new GetBookingService(getBookingRequest, config.getHybrisBookingsEndpoint());
    }

    public GetBookingSummaryService getBookingSummaries(GetBookingSummaryRequest getBookingSummaryRequest) {
        return new GetBookingSummaryService(getBookingSummaryRequest, config.getHybrisCustomers());
    }

    public FindBookingService findBooking(FindBookingRequest findBookingRequest) {
        return new FindBookingService(findBookingRequest, config.getHybrisBookingsEndpoint());
    }

    public AirportsService getAirports(AirportsRequest airportsRequest) {
        return new AirportsService(airportsRequest, config.getHybrisAirportsEndpoint());
    }

    public CountriesService getCountries(CountriesRequest countriesRequest) {
        return new CountriesService(countriesRequest, config.getHybrisCountriesEndpoint());
    }

    public CurrenciesService getCurrencies(CurrenciesRequest currenciesRequest) {
        return new CurrenciesService(currenciesRequest, config.getHybrisCurrenciesEndpoint());
    }

    public LanguagesService getLanguages(LanguagesRequest languagesRequest) {
        return new LanguagesService(languagesRequest, config.getHybrisLanguagesEndpoint());
    }

    public GetTermsAndConditionsService getTermsAndConditions(GetTermsAndConditionsRequest getTermsAndConditionsRequest){
        return new GetTermsAndConditionsService(getTermsAndConditionsRequest, config.getHybrisTermsAndConditionsEndpoint());
    }

    public CommercialFlightScheduleService getCommercialFlightSchedule(CommercialFlightScheduleRequest commercialFlightScheduleRequest) {
        return new CommercialFlightScheduleService(commercialFlightScheduleRequest, config.getHybrisCommercialFlightScheduleEndpoint());
    }

    public MarketGroupsService getMarketGroups(MarketGroupsRequest marketGroupsRequest) {
        return new MarketGroupsService(marketGroupsRequest, config.getHybrisMarketGroupsEndpoint());
    }

    public PassengerTypesService getPassengerTypes(PassengerTypesRequest passengerTypesRequest) {
        return new PassengerTypesService(passengerTypesRequest, config.getHybrisPassengerTypesEndpoint());
    }

    public PassengerTitlesService getPassengerTitles(PassengerTitlesRequest passengerTitlesRequest) {
        return new PassengerTitlesService(passengerTitlesRequest, config.getHybrisPassengerTitlesEndpoint());
    }

    public RegisterCustomerService registerCustomer(RegisterNewCustomerRequest registerNewCustomerRequest) {
        return new RegisterCustomerService(registerNewCustomerRequest, config.getHybrisCustomers());
    }

    public ValidateStoreEJPlusMembService validateEJPlusMembership(ValidateStoreEJPlusMembRequest registerNewCustomerRequest) {
        return new ValidateStoreEJPlusMembService(registerNewCustomerRequest, config.getHybrisCustomers());
    }

    public ValidateMembershipNumberService validateMembershipNumberService(ValidateMembershipRequest validateMembershipRequest){
        return new ValidateMembershipNumberService(validateMembershipRequest, config.getHybrisValidateMembership());
    }

    public LoginDetailsService loginCustomer(LoginRequest loginRequest) {
        return new LoginDetailsService(loginRequest, config.getHybrisCustomerLoginEndPoint());
    }

    public AgentLoginService loginAgent(LoginRequest loginRequest) {
        return new AgentLoginService(loginRequest, config.getHybrisAgentLoginEndPoint());
    }

    public AgentLogoutService logoutAgent(LogoutRequest logoutRequest) {
        return new AgentLogoutService(logoutRequest, config.getHybrisAgentLogoutEndPoint());
    }

    public PreferencesService getPreferences(PreferencesRequest preferencesRequest) {
        return new PreferencesService(preferencesRequest, config.getHybrisPreferences());
    }

    public StaffMemberNewService checkStaffMember(StaffMemberNewRequest staffMemberNewRequest) {
        return new StaffMemberNewService(staffMemberNewRequest, config.getHybrisCustomers());
    }

    public RegisterStaffFaresService createStaffMember(RegisterStaffFaresRequest registerStaffFaresRequest) {
        return new RegisterStaffFaresService(registerStaffFaresRequest, config.getHybrisCustomers());
    }

    public SectorsService getSectors(SectorsRequest sectorsRequest) {
        return new SectorsService(sectorsRequest, config.getHybrisGetSectorsEndPoint());
    }

    public UpdatePasswordService getUpdatePassword(UpdatePasswordRequest updatePasswordRequest) {
        return new UpdatePasswordService(updatePasswordRequest, config.getHybrisCustomers());
    }

    public GeneratePasswordService getGeneratePassword(GeneratePasswordRequest generatePasswordRequest) {
        return new GeneratePasswordService(generatePasswordRequest, config.getHybrisCustomers());
    }

    public TravelDocumentTypesService getTravelDocumentTypes(TravelDocumentTypesRequest travelDocumentTypesRequest) {
        return new TravelDocumentTypesService(travelDocumentTypesRequest, config.getHybrisTravelDocumentTypes());
    }

    public SSRDataService getSSRData(SSRDataRequest ssrDataRequest) {
        return new SSRDataService(ssrDataRequest, config.getHybrisSSRData());
    }

    public CustomerLogoutService logoutCustomer(LogoutRequest logoutRequest) {
        return new CustomerLogoutService(logoutRequest, config.getHybrisCustomers());
    }

    public GetRecentSearchesService getRecentSearch(SaveRecentSearchRequest saveRecentSearchRequest) {
        return new GetRecentSearchesService(saveRecentSearchRequest, config.getHybrisCustomers());
    }

    public SetAPIService setApi(SetAPIRequest setAPIRequest) {
        return new SetAPIService(setAPIRequest, config.getHybrisCustomers());
    }

    public GetSavedPassengerService getSavedPassenger(SavedPassengerRequest savedPassengerRequest) {
        return new GetSavedPassengerService(savedPassengerRequest, config.getHybrisCustomers());
    }

    public UpdateSavedPassengerService updateSavedPassenger(SavedPassengerRequest savedPassengerRequest) {
        return new UpdateSavedPassengerService(savedPassengerRequest, config.getHybrisCustomers());
    }

    public UpdateIdentityDocumentService updateIdentityDocument(SavedPassengerRequest savedPassengerRequest) {
        return new UpdateIdentityDocumentService(savedPassengerRequest, config.getHybrisCustomers());
    }

    public UpdateCustomerDetailsService updateCustomerDetails(UpdateCustomerDetailsRequest updateCustomerDetailsRequest) {
        return new UpdateCustomerDetailsService(updateCustomerDetailsRequest, config.getHybrisCustomers());
    }

    public GetAvailableFareTypesService getAvailableFareTypesService(GetAvailableFareTypesRequest request) {
        return new GetAvailableFareTypesService(request, config.getGetAvailableFareTypes());
    }

    public DeleteCustomerProfileService deleteCustomerDetails(DeleteCustomerProfileRequest deleteCustomerProfileRequest) {
        return new DeleteCustomerProfileService(deleteCustomerProfileRequest, config.getHybrisCustomers());
    }

    public SaveFlightInterestService saveFlightInterest(ManageFlightInterestRequest manageFlightInterestRequest) {
        return new SaveFlightInterestService(manageFlightInterestRequest, config.getHybrisCustomers());
    }

    public RemoveFlightInterestService removeFlightInterest(RemoveFlightInterestRequest removeFlightInterestRequest) {
        return new RemoveFlightInterestService(removeFlightInterestRequest, config.getHybrisCustomers());
    }

    public SignificantOtherService getSignificantOtherService(GetSignificantOtherRequest getSignificantOtherRequest) {
        return new SignificantOtherService(getSignificantOtherRequest, config.getHybrisCustomers());
    }

    public SignificantOtherService addSignificantOtherService(AddSignificantOtherRequest addSignificantOtherRequest) {
        return new SignificantOtherService(addSignificantOtherRequest, config.getHybrisCustomers());
    }

    public SignificantOtherService updateSignificantOtherService(UpdateSignificantOtherRequest updateSignificantOtherRequest) {
        return new SignificantOtherService(updateSignificantOtherRequest, config.getHybrisCustomers());
    }

    public SignificantOtherService deleteSignificantOtherService(DeleteSignificantOtherRequest deleteSignificantOtherRequest) {
        return new SignificantOtherService(deleteSignificantOtherRequest, config.getHybrisCustomers());
    }

    public SignificantOtherIdDocumentService addIdentityDocumentService(AddIdentityDocumentRequest addIdentityDocumentRequest) {
        return new SignificantOtherIdDocumentService(addIdentityDocumentRequest, config.getHybrisCustomers());
    }

    public SignificantOtherIdDocumentService updateIdentityDocumentService(UpdateIdentityDocumentRequest updateIdentityDocumentRequest) {
        return new SignificantOtherIdDocumentService(updateIdentityDocumentRequest, config.getHybrisCustomers());
    }

    public SignificantOtherIdDocumentService deleteIdentityDocumentService(DeleteSignificantOtherRequest deleteSignificantOtherRequest) {
        return new SignificantOtherIdDocumentService(deleteSignificantOtherRequest, config.getHybrisCustomers());
    }

    public GetDependantsService getDependantsService(DependantsRequest dependantsRequest) {
        return new GetDependantsService(dependantsRequest, config.getHybrisCustomers());
    }

    public UpdateDependantsService updateDependantsService(UpdateDependantsRequest updateDependantsRequest) {
        return new UpdateDependantsService(updateDependantsRequest, config.getHybrisCustomers());
    }

    public GetAlternateAirportsService getAlternateAirports(GetAlternateAirportsRequest getAlternateAirportsRequest) {
        return new GetAlternateAirportsService(getAlternateAirportsRequest, config.getHybrisAlternateAirportsEndpoint());
    }

    public ResetPasswordService getResetPasswordService(ResetPasswordRequest resetPasswordRequest) {
        return new ResetPasswordService(resetPasswordRequest, config.getHybrisCustomers());
    }

    public ResetPasswordService getAnonymousResetPasswordService(ResetPasswordRequest resetPasswordRequest) {
        return new ResetPasswordService(resetPasswordRequest, config.getHybrisAnonymousResetPassword());
    }

    public HoldItemsService getHoldItems(HoldItemsRequest holdItemsRequest) {
        return new HoldItemsService(holdItemsRequest, config.getHybrisHoldItems());
    }

    public AddSportToBasketService getAddSportEquipmentToBasket(AddHoldItemsToBasketRequest addHoldItemsToBasketRequest) {
        return new AddSportToBasketService(addHoldItemsToBasketRequest, config.getHybrisGetBasketEndpoint());
    }

    public GetCommentTypesService getCommentTypes(GetCommentTypesRequest getCommentTypesRequest) {
        return new GetCommentTypesService(getCommentTypesRequest, config.getHybrisCommentsEndpoint());
    }

    public AddCommentsToBookingService getAddCommentsToBooking(AddCommentToBookingRequest addCommentToBookingRequest) {
        return new AddCommentsToBookingService(addCommentToBookingRequest, config.getHybrisBookingsEndpoint());
    }

    public UpdateCommentsOnBookingService getUpdateCommentsOnBooking(UpdateCommentsOnBookingRequest updateCommentsOnBookingRequest) {
        return new UpdateCommentsOnBookingService(updateCommentsOnBookingRequest, config.getHybrisBookingsEndpoint());
    }

    public DeleteCommentOnBookingService getDeleteCommentsOnBooking(DeleteCommentOnBookingRequest deleteCommentOnBookingRequest) {
        return new DeleteCommentOnBookingService(deleteCommentOnBookingRequest, config.getHybrisBookingsEndpoint());
    }

    public GetCustomerAPIsService getCustomerAPIs(GetAPIsForCustomerRequest getAPIRequest) {
        return new GetCustomerAPIsService(getAPIRequest, config.getHybrisCustomers());
    }

    public AddHoldBagToBasketService addHoldBagToBasket(AddHoldItemsToBasketRequest addHoldItemsToBasketRequest) {
        return new AddHoldBagToBasketService(addHoldItemsToBasketRequest, config.getHybrisGetBasketEndpoint());
    }

    public RemoveFlightFromBasketService removeFlightFromBasket(RemoveFlightFromBasketRequest removeFlightFromBasketRequest) {
        return new RemoveFlightFromBasketService(removeFlightFromBasketRequest, config.getHybrisGetBasketEndpoint());
    }

    public DiscountReasonService getDiscountReasonService(DiscountReasonRequest discountReasonRequest) {
        return new DiscountReasonService(discountReasonRequest, config.getGetDiscountReason());
    }

    public GetSeatMapService getSeatMapService(GetSeatMapRequest getSeatMapRequest) {
        return new GetSeatMapService(getSeatMapRequest, config.getHybrisSeatMapEndpoint());
    }

    public RemoveProductService removeProductFromBasket(RemoveProductFromBasketRequest removeHoldItemsFromBasketRequest) {
        return new RemoveProductService(removeHoldItemsFromBasketRequest, config.getHybrisGetBasketEndpoint());
    }

    public SetReasonForTravelService setReasonForTravelService(BasketRequest basketRequest) {
        return new SetReasonForTravelService(basketRequest, config.getHybrisGetBasketEndpoint());
    }

    public PriceOverrideBasketService getPriceOverride(PriceOverrideRequest priceOverrideRequest) {
        return new PriceOverrideBasketService(priceOverrideRequest, config.getHybrisGetBasketEndpoint());
    }

    public UpdateCustomerDetailsService removeApisService(RemoveApisRequest removeApisRequest) {
        return new UpdateCustomerDetailsService(removeApisRequest, config.getHybrisCustomers());
    }

    public RemovePassengerService removePassengerFromBasket(RemovePassengerRequest removePassengerRequest) {
        return new RemovePassengerService(removePassengerRequest, config.getHybrisGetBasketEndpoint());
    }

    public PurchasedSeatService managePurchasedSeat(PurchasedSeatRequest purchasedSeatRequest) {
        return new PurchasedSeatService(purchasedSeatRequest, config.getHybrisGetBasketEndpoint());
    }

    public UpdateCustomerDetailsService addSSRService(AddSSRRequest addSSRRequest) {
        return new UpdateCustomerDetailsService(addSSRRequest, config.getHybrisCustomers());
    }

    public UpdateCustomerDetailsService updateSSRService(UpdateSSRRequest updateSSRRequest) {
        return new UpdateCustomerDetailsService(updateSSRRequest, config.getHybrisCustomers());
    }

    public ManageAdditionalFareToPassengerInBasketService manageAdditionalFareToPassengerInBasket(ManageAdditionalFareToPassengerInBasketRequest manageAdditionalFareToPassengerInBasketRequest) {
        return new ManageAdditionalFareToPassengerInBasketService(manageAdditionalFareToPassengerInBasketRequest, config.getHybrisGetBasketEndpoint());
    }

    public AddAdditionalFareToPassengerInBasketService addAdditionalFareToPassengerInBasketService(AddAdditionalFareRequest addAdditionalFareRequest) {
        return new AddAdditionalFareToPassengerInBasketService(addAdditionalFareRequest, config.getHybrisAdditionalFareEndpoint());
    }


    public ConvertBasketCurrencyService updateCurrency(ConvertBasketCurrencyRequest convertBasketCurrencyRequest) {
        return new ConvertBasketCurrencyService(convertBasketCurrencyRequest, config.getHybrisGetBasketEndpoint());
    }

    public InternalPaymentFundsService getPaymentFundsService(InternalPaymentFundsRequest internalPaymentFundsRequest) {
        return new InternalPaymentFundsService(internalPaymentFundsRequest, config.getHybrisGetInternalPaymentMethodsEndPoint());
    }

    public DeleteCustomerSSRService deleteCustomerSSRService(DeleteCustomerSSRRequest deleteCustomerSSRRequest) {
        return new DeleteCustomerSSRService(deleteCustomerSSRRequest, config.getHybrisCustomers());
    }

    public RecalculatePricesService recalculatePricesService(RecalculatePricesRequest recalculatePricesRequest) {
        return new RecalculatePricesService(recalculatePricesRequest, config.getHybrisReCalculatePricesEndpoint());
    }

    public AssociateInfantService associateInfant(AssociateInfantRequest associateInfantRequest) {
        return new AssociateInfantService(associateInfantRequest, config.getHybrisGetBasketEndpoint());
    }

    public AmendBasicDetailsService amendBasicDetails(AmendBasicDetailsRequest amendBasicDetailsRequest) {
        return new AmendBasicDetailsService(amendBasicDetailsRequest, config.getHybrisAmendBasicDetailsEndpoint());
    }

    public AmendPassengerSSRService amendPassengerSsr(AmendPassengerSSRRequest amendPassengerSSRRequest) {
        return new AmendPassengerSSRService(amendPassengerSSRRequest, config.getHybrisAmendBasicDetailsEndpoint());
    }

    public GetAmendableBookingService getAmendableBooking(GetAmendableBookingRequest getAmendableBookingRequest) {
        return new GetAmendableBookingService(getAmendableBookingRequest, config.getHybrisBookingsEndpoint());
    }

    public GenerateBoardingPassService getBoardingPassService(GenerateBoardingPassRequest boardingPassRequest) {
        return new GenerateBoardingPassService(boardingPassRequest, config.getHybrisBookingsEndpoint());
    }

    public BookingDocumentsService getBookingDocumentsService(BookingDocumentsRequest bookingDocumentsRequest) {
        return new BookingDocumentsService(bookingDocumentsRequest, config.getHybrisBookingsEndpoint());
    }

    public DeleteRecentSearchesService getRecentSearchesService(DeleteRecentSearchesRequest deleteRecentSearchesRequest) {
        return new DeleteRecentSearchesService(deleteRecentSearchesRequest, config.getHybrisCustomers());
    }

    public SetApisBookingService setApisBooking(SetAPIRequest setAPIRequest) {
        return new SetApisBookingService(setAPIRequest, config.getHybrisBookingsEndpoint());
    }

    public RemoveInfantOnLapService removeInfantOnLap(RemoveInfantOnLapRequest removeInfantOnLapRequest) {
        return new RemoveInfantOnLapService(removeInfantOnLapRequest, config.getHybrisGetBasketEndpoint());
    }

    public SavedPaymentMethodService addSavedPaymentMethod(PaymentMethodsRequest paymentMethodsRequest) {
        return new SavedPaymentMethodService(paymentMethodsRequest, config.getHybrisCustomers());
    }

    public PaymentMethodTypeService addPaymentType(PaymentMethodsRequest paymentMethodsRequest) {
        return new PaymentMethodTypeService(paymentMethodsRequest, config.getHybrisCustomers());
    }

    public RemoveSavedPaymentService removeSavedPayment(RemoveSavedPaymentRequest paymentMethodsRequest) {
        return new RemoveSavedPaymentService(paymentMethodsRequest, config.getHybrisCustomers());
    }

    public CheckInFlightService checkInFlightService(CheckInFlightRequest checkInFlightRequest) {
        return new CheckInFlightService(checkInFlightRequest, config.getHybrisBookingsEndpoint());
    }

    public AddInfantOnLapService addInfantOnLap(AddInfantOnLapRequest addInfantOnLapRequest) {
        return new AddInfantOnLapService(addInfantOnLapRequest, config.getHybrisGetBasketEndpoint());
    }

    public UpdateBasicDetailsService updateBasicDetails(UpdateBasicDetailsRequest updateBasicDetailsRequest) {
        return new UpdateBasicDetailsService(updateBasicDetailsRequest, config.getHybrisGetBasketEndpoint());
    }

    public InitiateCancelBookingService initiateCancelBooking(InitiateCancelBookingRequest initiateCancelBookingRequest) {
        return new InitiateCancelBookingService(initiateCancelBookingRequest, config.getHybrisBookingsEndpoint(), feesAndTaxesDao);
    }

    public CancelBookingRefundService cancelBookingRefund(CancelBookingRefundRequest cancelBookingRefundRequest) {
        return new CancelBookingRefundService(cancelBookingRefundRequest, config.getHybrisBookingsEndpoint());
    }

    public AddCommentsToCustomerService addCommentsToCustomer(AddCommentToCustomerRequest addCommentToCustomerRequest) {
        return new AddCommentsToCustomerService(addCommentToCustomerRequest, config.getHybrisCustomers());
    }

    public UpdateCommentsToCustomerService updateCommentsToCustomer(UpdateCommentToCustomerRequest updateCommentToCustomerRequest) {
        return new UpdateCommentsToCustomerService(updateCommentToCustomerRequest, config.getHybrisCustomers());
    }

    public RemoveCommentsToCustomerService removeCommentsToCustomer(RemoveCommentToCustomerRequest removeCommentToCustomerRequest) {
        return new RemoveCommentsToCustomerService(removeCommentToCustomerRequest, config.getHybrisCustomers());
    }

    public GetRefundReasonsService getRefundReasons(GetRefundReasonsRequest getRefundReasonsRequest) {
        return new GetRefundReasonsService(getRefundReasonsRequest, config.getHybrisgetRefundReasonsEndPoint());
    }

    public AddPassengerToFlightService getAddPassengerToFlight(AddPassengerToFlightRequest addPassengerToFlight) {
        return new AddPassengerToFlightService(addPassengerToFlight, config.getHybrisGetBasketEndpoint());
    }

    public ChangeFlightService changeFlight(ChangeFlightRequest changeFlightRequest) {
        return new ChangeFlightService(changeFlightRequest, config.getHybrisGetBasketEndpoint());
    }

    public GetRefundablePaymentMethodsService getRefundablePaymentMethodsService(GetRefundablePaymentMethodsRequest getRefundablePaymentMethodsRequest) {
        return new GetRefundablePaymentMethodsService(getRefundablePaymentMethodsRequest, config.getHybrisBookingsEndpoint());
    }

    public CarHireService getCarHireService(CarHireRequest carHireRequest) {
        return new CarHireService(carHireRequest, config.getHybrisGetBasketEndpoint());
    }

    public CurrencyConversionService currencyConversionService(CurrencyConversionRequest currencyConversionRequest) {
        return new CurrencyConversionService(currencyConversionRequest, config.getHybrisCurrencyConversion());
    }

    public PaymentBalanceService paymentBalanceService(PaymentMethodsRequest paymentMethodBalanceRequest) {
        return new PaymentBalanceService(paymentMethodBalanceRequest, config.getHybrisGetBasketEndpoint());
    }

    public GetFlightInterestService getFlightInterest(ManageFlightInterestRequest manageFlightInterestRequest) {
        return new GetFlightInterestService(manageFlightInterestRequest, config.getHybrisCustomers());
    }

    public CreateCompensationService createCompensationService(CreateCompensationRequest createCompensationRequest){
        return new CreateCompensationService(createCompensationRequest, config.getHybrisGetBasketEndpoint());
    }

    public StationsForCarHireService getStations(StationsForCarHireRequest stationsForCarHireRequest) {
        return new StationsForCarHireService(stationsForCarHireRequest, config.getHybrisStationsForCarHireEndpoint());
    }

    public EventMessageService eventMessageService(GenerateEventMessageRequest eventMessageRequest) {
        return new EventMessageService(eventMessageRequest, config.getHybrisEvent());
    }

    public BulkTransferReasonsService bulkTransferReasonsService(GetBulkTransferReasonsRequest getBulkTransferReasonsRequest) {
        return new BulkTransferReasonsService(getBulkTransferReasonsRequest, config.getHybrisBulkTransferReasonsEndpoint());
    }

    public AdditionalSeatReasonsService getAdditionalSeatReasons(AdditionalSeatReasonsRequest additionalSeatReasonsRequest) {
        return new AdditionalSeatReasonsService(additionalSeatReasonsRequest, config.getHybrisAdditionalSeatReasons());
    }
    public AddCarToBasketService getAddCarToBasketService(AddCarToBasketRequest addCarToBasketRequest) {
        return new AddCarToBasketService(addCarToBasketRequest, config.getHybrisGetBasketEndpoint());
    }

    public FeesTaxesService getFeesTaxesService(FeesTaxesRequest feesTaxesRequest) {
        return new FeesTaxesService(feesTaxesRequest, config.getHybrisFeesTaxes(), feesAndTaxesDao);
    }

    public IdentifyPassengerService identifyPassengerService(IdentifyPassengerRequest identifyPassengerRequest){
        return new IdentifyPassengerService(identifyPassengerRequest, config.getHybrisIdentifyPassengerEndpoint());
    }

    public GroupBookingQuoteService groupBookingQuoteService(GroupBookingQuoteRequest groupBookingQuoteRequest){
        return new GroupBookingQuoteService(groupBookingQuoteRequest, config.getHybrisGetBasketEndpoint());
    }
}

