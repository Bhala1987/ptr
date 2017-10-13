package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.SSRDataDao;
import com.hybris.easyjet.fixture.hybris.helpers.AddEJPlusSeatToBasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.helpers.TravellerHelper;
import com.hybris.easyjet.fixture.hybris.helpers.traveller.Passengers;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.SavedSSRs;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by giuseppedimartino on 26/04/17.
 */

@ContextConfiguration(classes = TestApplication.class)
public class AddTravellersSteps {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private TravellerHelper travellerHelper;
    @Autowired
    private AddEJPlusSeatToBasketHelper addEJPlusSeatToBasketHelper;
    private Passengers requestBody;

    @Autowired
    SSRDataDao ssrDataDao;

    @Given("^I have updated the passenger information$")
    public void iHaveUpdatedThePassengerInformation() throws Throwable {
        Passengers requestBody = travellerHelper.createValidRequestToAddAllPassengersForBasket(basketHelper.getBasketService().getResponse());
        basketHelper.updatePassengersForChannel(requestBody, testData.getChannel(), basketHelper.getBasketService().getResponse().getBasket().getCode());
//        pause(3000);
    }

    @And("^add travellers to cart and save traveller \"([^\"]*)\" to customer profile$")
    public void addTravellersToCartSavetravellertoCustomerProfile(boolean savestatus) throws Throwable {
        requestBody = travellerHelper.createValidRequestSaveCustomerToProfile(basketHelper.getBasketService().getResponse());
        travellerHelper.setSaveToCustomerProfile(requestBody.getPassengers(),savestatus);
    }

    @And("^update traveller's surname \"([^\"]*)\" and ejplusCardnumber \"([^\"]*)\"$")
    public void updateTravellersSurnameEjpluscardnumber(String surname, String ejplusNo) throws Throwable {
        List<SavedSSRs.Ssr> mySsrList = new ArrayList<>();
        SavedSSRs.Ssr mySsr = new SavedSSRs.Ssr();

        List<String> SSRCode = ssrDataDao.getSSRDataActive(true, 1);

        for (String code : SSRCode) {
            mySsr.setCode(code);
            mySsrList.add(mySsr);
        }

        requestBody.getPassengers().get(0).getSpecialRequests().setSsrs(mySsrList);

        requestBody.getPassengers().get(0).getPassengerDetails().getName().setLastName(surname);
        requestBody.getPassengers().get(0).getPassengerDetails().setEjPlusCardNumber(ejplusNo);

        basketHelper.updatePassengersForChannel(requestBody, testData.getChannel(), basketHelper.getBasketService().getResponse().getBasket().getCode());
//        pause(3000);
    }
}