package feature.document.steps.data;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisService;
import com.hybris.easyjet.models.PassengerMix;
import cucumber.api.java.en.But;
import feature.document.steps.CommonSteps;
import feature.document.steps.services.agentservices.LoginSteps;
import feature.document.steps.services.createbasketservices.AddFlightSteps;
import feature.document.steps.services.managecustomerservices.GetCustomerProfileSteps;
import net.thucydides.core.annotations.Steps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.PASSENGERS;

/**
 * InventoryHelper manage the inventory stock level stored on hybris Db.
 * It makes use of testData to store parameters that can be used by other steps.
 *
 * @author gd <g.dimartino@reply.it>
 */
@ContextConfiguration(classes = TestApplication.class)
public class InventoryHelper {

    @Autowired
    private SerenityFacade testData;

    @Steps
    private CommonSteps commonSteps;
    @Steps
    private LoginSteps agentLogin;
    @Steps
    private CustomerHelper customerHelper;
    @Steps
    private GetCustomerProfileSteps getCustomerProfileSteps;
    @Steps
    private AddFlightSteps addFlightSteps;

    @But("^the flight have no standby stock availability$")
    public void reserveAllStandbyStockForFlight() throws EasyjetCompromisedException {
        String currentCookie = HybrisService.theJSessionCookie.get();
        testData.storeTestData();
        HybrisService.theJSessionCookie.set("");
        commonSteps.channelSelection("ADAirport");
        customerHelper.findActiveCustomer("staff", null);
        agentLogin.succesfulLogin();
        getCustomerProfileSteps.getCustomerProfile();
        testData.setData(PASSENGERS, new PassengerMix("2 adult"));
        addFlightSteps.sendAddFlightRequest("single");
        testData.restoreTestData();
        HybrisService.theJSessionCookie.set(currentCookie);
    }
}