package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.FlightFinder;
import com.hybris.easyjet.database.hybris.dao.HoldItemsDao;
import com.hybris.easyjet.fixture.hybris.helpers.FlightHelper;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.HoldItemsQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.refdata.HoldItemsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.FlightsService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.refdata.HoldItemsService;
import cucumber.api.java.en.And;
import cucumber.api.java.en.But;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.INVALID;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;

/**
 * Created by giuseppe on 15/03/2017.
 */
@ContextConfiguration(classes = TestApplication.class)

public class GetHoldItemsSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private FlightFinder flightFinder;
    private FlightsService flightService;
    private HoldItemsService holdItemsService;
    private String channel;
    @Autowired
    private HoldItemsDao holdItemsDao;
    private HoldItemsQueryParams.HoldItemsQueryParamsBuilder queryParams = HoldItemsQueryParams.builder();
    @Autowired
    private FlightHelper flightHelper;

    @But("^my request contains '(.*)'$")
    public void myRequestContainsError(String error) throws Throwable {

        switch (error) {
            case "no currency":
                break;
            case "invalid currency":
                queryParams.currency("KFC");
                break;
            case "invalid bundle":
                testData.setData(INVALID, "A");
                queryParams.currency("GBP").bundleId(testData.getData(INVALID));
                break;
            case "invalid sector":
                testData.setData(INVALID, "A");
                queryParams.currency("GBP").sector(testData.getData(INVALID));
                break;
            case "invalid flightKey":
                testData.setData(INVALID, "A");
                queryParams.currency("GBP").flightKey(testData.getData(INVALID));
                break;
            case "sector and flightKey":
                flightService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), testData.getFareType(), testData.getCurrency());

                String sector = testData.getOrigin() + testData.getDestination();
//                String sector = availableFlight.getDeparts().concat(availableFlight.getArrives());
                queryParams
                        .currency("GBP")
                        .sector(sector)
                        .flightKey(flightService.getOutboundFlight().getFlightKey());
                break;
        }
    }

    @And("^my request currency value is '(\\w{3})'$")
    public void myRequestCurrencyValueIsCurrency(String currency) throws Throwable {
        queryParams.currency(currency);
    }

    @And("^my request bundle value is '(\\w+)'$")
    public void myRequestBundleValueIsBundle(String bundle) throws Throwable {

        if (!bundle.equals("empty")) {
            queryParams.bundleId(bundle);
        }
    }

    @And("^my request sector value is '(valid|empty)'$")
    public void myRequestSectorValueIsSector(String sector) throws Throwable {

        if (sector.equals("valid")) {
            flightService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), testData.getFareType(), testData.getCurrency());
            queryParams.sector(testData.getOrigin()+testData.getDestination());
        }
    }

    @And("^my request flight key value is '(valid|empty)'$")
    public void myRequestFlightKeyValueIsFlightKey(String flightKey) throws Throwable {

        if (flightKey.equals("valid")) {
            flightService = flightHelper.getFlights(testData.getChannel(), testData.getPassengerMix(), testData.getOrigin(), testData.getDestination(), testData.getFareType(), testData.getCurrency());
            queryParams.flightKey(flightService.getOutboundFlight().getFlightKey());
        }
    }

    @When("^I send a request to the findHoldItems service$")
    public void iSendARequestToTheFindHoldItemsService() throws Throwable {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(SerenityFacade.DataKeys.HEADERS);
        holdItemsService = serviceFactory.getHoldItems(new HoldItemsRequest(headers
                .build(), queryParams.build()));
        testData.setData(SERVICE, holdItemsService);
        holdItemsService.invoke();
    }

    @Then("^I will the list of valid products$")
    public void iWillTheListOfValidProducts() throws Throwable {

        HoldItemsQueryParams params = queryParams.build();
        String currency = params.getCurrency();
        String bundle = params.getBundleId();
        String sector = params.getSector();
        String flightKey = params.getFlightKey();

        Map<String, List<HashMap<String, Double>>> itemsList;

        if (StringUtils.isBlank(sector) && StringUtils.isBlank(flightKey)) {
            if (StringUtils.isBlank(bundle)) {
                itemsList = holdItemsDao.returnActiveProducts(testData.getChannel(), currency);
            } else {
                itemsList = holdItemsDao.returnActiveProductsByBundle(testData.getChannel(), currency, bundle);
            }
        } else {
            if (StringUtils.isBlank(flightKey)) {
                if (StringUtils.isBlank(bundle)) {
                    itemsList = holdItemsDao.returnActiveProductsBySector(testData.getChannel(), currency, sector);
                } else {
                    itemsList = holdItemsDao.returnActiveProductsBySectorWithBundle(testData.getChannel(), currency, sector, bundle);
                }
            } else {
                if (StringUtils.isBlank(bundle)) {
                    itemsList = holdItemsDao.returnActiveProductsByFlightKey(testData.getChannel(), currency, flightKey);
                } else {
                    itemsList = holdItemsDao.returnActiveProductsByFlightKeyWithBundle(testData.getChannel(), currency, flightKey, bundle);
                }
            }
        }

        holdItemsService.assertThat().returnedListIsCorrect(itemsList);
    }
}
