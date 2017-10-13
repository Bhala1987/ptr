package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.refdata.MarketGroupsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.refdata.MarketGroupsService;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by daniel on 21/09/2016.
 */
@ContextConfiguration(classes = TestApplication.class)

public class GetMarketGroupReferenceDataSteps {

    private MarketGroupsService marketGroupsService;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @When("^I call the get market data service$")
    public void iCallTheGetMarketDataService() throws Throwable {
        marketGroupsService = serviceFactory.getMarketGroups(new MarketGroupsRequest(HybrisHeaders.getValid("Digital").build()));
        marketGroupsService.invoke();
    }
}