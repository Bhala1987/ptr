package feature.document;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.helpers.BasketHelper;
import com.hybris.easyjet.fixture.hybris.invoke.response.Errors;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.BasketService;
import com.hybris.easyjet.fixture.hybris.invoke.services.booking.CommitBookingService;
import cucumber.api.java.en.And;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;
import java.util.stream.Stream;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.BASKET_SERVICE;
import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;

/**
 * Created by christianmilia on 12/05/2017.
 */

@ContextConfiguration(classes = TestApplication.class)
public class PriceChangeFromCommitBooking {

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private BasketHelper basketHelper;

    @And("^I should receive the new price and I verify that the price of the basket is updated$")
    public void iShouldReceiveTheNewPrice() throws Throwable {

        CommitBookingService commitBooking = testData.getData(SERVICE);

        Stream<Errors.Error> errorStream = commitBooking.getErrors().getErrors().stream()
                .filter(priceMismatch -> CollectionUtils.isNotEmpty(priceMismatch.getAffectedData())
                        && priceMismatch.getAffectedData().stream()
                        .anyMatch(affectedData -> affectedData != null
                                && affectedData.getDataName()
                                .equals("flightKey")
                                && affectedData.getDataValue().equals(testData.getActualFlightKey())));

        CommitBookingService commitBookingService = testData.getData(SERVICE);

        final Errors.Error allocateInvError = errorStream.findFirst().orElse(null);

        Optional<Errors.AffectedData> affectedPrice = allocateInvError.getAffectedData().stream()
                .filter(priceAffected -> priceAffected.getDataName()
                        .equalsIgnoreCase("price")).findFirst();

        if (affectedPrice.isPresent()){

            Errors.AffectedData affectedData = affectedPrice.get();
//            commitBookingService.assertThat().priceChangeFound(affectedData);
            commitBookingService.assertThatErrors().containedTheAffectedData(affectedData.getDataName());

            BasketService basketService = testData.getData(BASKET_SERVICE);
            basketHelper.getBasket(basketService.getResponse().getBasket().getCode(), testData.getChannel());
            basketHelper.getBasketService().assertThat().flightBasePriceUpdated(affectedData.getDataValue(), testData.getActualFlightKey());

        }else {
            throw new EasyjetCompromisedException("AffectedData not populated");
        }
    }
}
