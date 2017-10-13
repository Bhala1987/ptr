package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.BundleTemplateHelper;
import com.hybris.easyjet.fixture.hybris.helpers.dto.bundletemplate.BundleTemplateDTO;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.queryparams.FareTypeQueryParams;
import com.hybris.easyjet.fixture.hybris.invoke.requests.GetAvailableFareTypesRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.GetAvailableFareTypesService;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.hybris.easyjet.exceptions.EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA;


/**
 * Created by marco on 23/02/17.
 */
@ContextConfiguration(classes = TestApplication.class)

public class GetAvailableFaretypeSteps {

    private List<BundleTemplateDTO> availableBundles;
    private GetAvailableFareTypesService getAvailableFareTypesService;
    private Map<String, Integer> optionsSizesFromDB;

    @Autowired
    private BundleTemplateHelper bundleTemplateHelper;

    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Given("^that I have retrieved all the available fare types with descriptions and options$")
    public void thatIHaveRetrievedAllTheAvailableFareTypesWithDescriptionsAndOptions() throws Throwable {
        availableBundles = bundleTemplateHelper.getStagedBundleTemplates();
        if (CollectionUtils.isEmpty(availableBundles)) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        }
        availableBundles
                .forEach(availableBundle -> bundleTemplateHelper.removeProductTypeForBundle(availableBundle, "fare_product"));
    }

    @Given("^that I have retrieved all the available fare types with gds fare class \"([^\"]*)\"$")
    public void thatIHaveRetrievedAllTheAvailableFareTypesWithGdsFareClass(String gdsFareClass) throws Throwable {
        availableBundles = bundleTemplateHelper.getStagedBundleTemplatesWithGdsFareClass(gdsFareClass);
        if (CollectionUtils.isEmpty(availableBundles)) {
            availableBundles = Collections.emptyList();
        }

    }

    @When("^I create a request to retrieve available fare types$")
    public void iCreateARequestToTheRetrieveAvailableFareTypesRequest() throws Throwable {
        GetAvailableFareTypesRequest request = new GetAvailableFareTypesRequest(HybrisHeaders.getValid("Digital").build(), null);
        getAvailableFareTypesService = serviceFactory.getAvailableFareTypesService(request);
        getAvailableFareTypesService.invoke();
    }

    @When("^I create a request to retrieve available fare types with gds fare class \"([^\"]*)\"$")
    public void iCreateARequestToRetrieveAvailableFareTypesWithGdsFareClass(String gdsFareClass) throws Throwable {
        FareTypeQueryParams queryParams = FareTypeQueryParams.builder().gdsFareClass(gdsFareClass).build();
        GetAvailableFareTypesRequest request = new GetAvailableFareTypesRequest(HybrisHeaders.getValid("Digital").build(),
                queryParams);
        getAvailableFareTypesService = serviceFactory.getAvailableFareTypesService(request);
        getAvailableFareTypesService.invoke();
    }

    @Then("^I will get the description as part of the response$")
    public void iWillGetTheDescriptionAsPartOfTheResponse() throws Throwable {
        getAvailableFareTypesService.assertThat().assertHasDescriptions();
    }

    @And("^I will get all the options available with fare$")
    public void iWillGetAllTheOptionsAvailableWithFare() throws Throwable {
        getAvailableFareTypesService.assertThat().assertHasOnlyExpectedOptions(availableBundles);
    }

    @Then("^I will get only the fare types with the given gds fare class \"([^\"]*)\"$")
    public void iWillGetOnlyTheFareTypesWithTheGivenGdsFareClass(String gdsFareClass) throws Throwable {
        if (CollectionUtils.isNotEmpty(availableBundles)) {
            getAvailableFareTypesService.assertThat().assertHasOnlyExpectedGdsFareClass(gdsFareClass);
        }
    }
}
