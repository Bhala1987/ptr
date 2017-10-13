package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.FeesAndTaxesDao;
import com.hybris.easyjet.database.hybris.models.FeeTaxAndMissingCurrencyModel;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.FeesTaxesPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FeesTaxesQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.FeesTaxesRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.FeesTaxesResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.FeesTaxesService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.CHANNEL;


@DirtiesContext
@ContextConfiguration(classes = TestApplication.class)
public class FeesTaxesSteps {

   protected static Logger LOG = LogManager.getLogger(FeesTaxesSteps.class);

   @Autowired
   private HybrisServiceFactory serviceFactory;

   @Autowired
   private SerenityFacade testData;

   @Autowired
   private FeesAndTaxesDao feesAndTaxesDao;

   private FeesTaxesService feesTaxesService;
   private FeesTaxesResponse feesTaxesResponse;
   private String channel;
   private String taxCode;
   private String passengerType;
   private String sectorCode;
   private String currencyCode;
   private List<FeeTaxAndMissingCurrencyModel> feesAndTaxesWithoutCurrency;

   @And("^the channel has initated a getfeesandTaxes for invalid (.*)$")
   public void theChannelHasInitatedAGetfeesandTaxesForInvalidTaxCode(String taxCode) {
      this.taxCode = taxCode;
   }

   @When("^the fee/tax can not be identified$")
   public void theFeeTaxCanNotBeIdentified() {
      FeesTaxesPathParams pathParams = FeesTaxesPathParams.builder().feeTaxCode(taxCode).build();

      FeesTaxesQueryParams queryParams = FeesTaxesQueryParams
            .builder().build();
      feesTaxesService = serviceFactory.getFeesTaxesService(
            new FeesTaxesRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, queryParams));

      feesTaxesService.invoke();
   }

   @Then("^the system will generate an (.*)$")
   public void theSystemWillGenerateAnErrorMessage(String errorCode) throws Throwable {
      feesTaxesService.assertThatErrors().containedTheCorrectErrorMessage(errorCode);
   }

   @And("^that the channel has initated a getfeesandTaxes$")
   public void thatTheChannelHasInitatedAGetfeesandTaxes() throws Throwable {

      FeesTaxesPathParams pathParams = FeesTaxesPathParams.builder().build();
      FeesTaxesQueryParams queryParams = FeesTaxesQueryParams.builder().build();

      feesTaxesService = serviceFactory.getFeesTaxesService(
            new FeesTaxesRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, queryParams));

      feesTaxesService.invoke();
   }

   @When("^the system receives the request for all applicable fees and taxes reference data$")
   public void theSystemReceivesTheRequestForAllApplicableFeesAndTaxesReferenceData() {
      feesTaxesResponse = (FeesTaxesResponse) feesTaxesService.getResponse();
   }

   @Then("^it will return all the applicable fees/taxes reference data$")
   public void itWillReturnAllTheApplicableFeesTaxesReferenceData() throws Throwable {
      feesTaxesService.assertThat().responseHasAllFeesAndTaxes();
   }

   @When("^the system receives the request for applicable (.*)$")
   public void theSystemReceivesTheRequestForApplicableTaxCode(String taxCode) {
      this.taxCode = taxCode;
   }

   @And("^with channel (.*)$")
   public void withChannel(String channel) {
      this.channel = channel;
   }

   @And("^with passenger type (.*)$")
   public void withPassengerTypePassengerType(String passengerType) {
      this.passengerType = passengerType;
   }

   @And("^with sector (.*)$")
   public void withSectorSectorCode(String sectorCode) {
      this.sectorCode = sectorCode;
   }

   @And("^with currency (.*)$")
   public void withCurrencyCurrencyCode(String currencyCode) {
      this.currencyCode = currencyCode;
   }

   @And("^that the channel has initated a getfeesandTaxes for taxCode$")
   public void thatTheChannelHasInitatedAGetfeesandTaxesForTaxCode() {

      FeesTaxesPathParams pathParams = FeesTaxesPathParams.builder().feeTaxCode(taxCode).build();

      FeesTaxesQueryParams queryParams = FeesTaxesQueryParams.builder().channel(channel).currencyCode(currencyCode).passengerType(passengerType).sectorCode(sectorCode).build();

      feesTaxesService = serviceFactory.getFeesTaxesService(
            new FeesTaxesRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, queryParams));

      feesTaxesService.invoke();
   }

   @Then("^it will return all the applicable fees/taxes reference data for these requested parameters$")
   public void itWillReturnAllTheApplicableFeesTaxesReferenceDataForTheseRequestedParameters() {
      feesTaxesService.assertThat().responseHasExpectedValues(taxCode, channel, currencyCode, passengerType, sectorCode);
   }

   @When("^the channel has initiated a getfeesandTaxes for currencyCode$")
   public void theChannelHasInitiatedAGetfeesandTaxesForCurrencyCode() {
      FeesTaxesQueryParams queryParams = FeesTaxesQueryParams.builder().currencyCode(currencyCode).build();

      feesTaxesService = serviceFactory.getFeesTaxesService(
            new FeesTaxesRequest(HybrisHeaders.getValid(testData.getChannel()).build(), null, queryParams));

      feesTaxesService.invoke();
   }

   @Then("^it will return the price for the fee/taxes in that currency$")
   public void itWillReturnThePriceForTheFeeTaxesInThatCurrency() {
      feesTaxesService.assertThat().responseHasFeesTaxesInASpecificCurrency(currencyCode);
   }


   @And("^there are not fees or taxes with specific currency$")
   public void thereAreNotFeesOrTaxesWithSpecificCurrency() throws Throwable {
      feesAndTaxesWithoutCurrency = feesAndTaxesDao.getFeesAndTaxesWithoutCurrency();
   }

   @When("^request contains pricing context applied to a given fee/tax for a specific currency$")
   public void requestContainsPricingContextAppliedToAGivenFeeTaxForASpecificCurrency() throws Throwable {
      if(CollectionUtils.isNotEmpty(feesAndTaxesWithoutCurrency)) {
         taxCode = feesAndTaxesWithoutCurrency.get(0).getFeeCode();
         currencyCode = feesAndTaxesWithoutCurrency.get(0).getCurrencyIsocode();

         FeesTaxesPathParams pathParams = FeesTaxesPathParams.builder().feeTaxCode(taxCode).build();

         FeesTaxesQueryParams queryParams = FeesTaxesQueryParams
               .builder().currencyCode(currencyCode).build();

         feesTaxesService = serviceFactory.getFeesTaxesService(
               new FeesTaxesRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, queryParams));

         feesTaxesService.invoke();
      }
   }

   @Then("^the system will calculate the price based on the exchange rate for that currency$")
   public void theSystemWillCalculateThePriceBasedOnTheExchangeRateForThatCurrency() {
      feesTaxesService.assertThat().responseHasExpectedValues(taxCode, null, currencyCode, null, null);
   }

}
