package feature.document.steps.data;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.SerenityReporter;
import com.hybris.easyjet.database.hybris.dao.ChannelPropertiesDao;
import com.hybris.easyjet.database.hybris.models.ChannelPropertiesModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.models.PassengerMix;
import cucumber.api.java.en.And;
import net.thucydides.core.annotations.Steps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * ChannelPropertiesHelper handle the retrieval of channel properties from the DB.
 * It makes use of testData to store parameters that can be used by other steps.
 *
 * @author gd <g.dimartino@reply.it>
 */
@ContextConfiguration(classes = TestApplication.class)
public class ChannelPropertiesHelper {

    private static final String MAX_PASSENGERS = "maxPassengers";

    @Steps
    private SerenityReporter reporter;

    @Autowired
    private SerenityFacade testData;

    @Autowired
    private ChannelPropertiesDao channelDao;

    /**
     * Get the maximum allowed passengers property from the db and set the passenger mix in testData to be greater than that
     * @throws EasyjetCompromisedException if the property is not defined for the channel used (specified through testData)
     */
    @And("^I want to search more than the maximum allowed passengers$")
    public void iWantToSearchMoreThanTheMaximumAllowedPassengers() throws EasyjetCompromisedException {
        Optional<ChannelPropertiesModel> optionalMaxAllowedPassengers = channelDao.returnChannelProperties().stream()
                .filter(property ->
                        property.getCode().equals(testData.getData(CHANNEL))
                                && property.getP_propertyname().equals(MAX_PASSENGERS)
                ).findFirst();

        ChannelPropertiesModel maxAllowedPassengers = null;
        if (optionalMaxAllowedPassengers.isPresent()) {
            maxAllowedPassengers = optionalMaxAllowedPassengers.get();
        } else {
            throw new EasyjetCompromisedException("No maxAllowedPassengers defined for " + testData.getData(CHANNEL) + " channel");
        }


        testData.setData(PASSENGER_MIX, (Integer.valueOf(maxAllowedPassengers.getP_propertyvalue()) + 1) + " adult");
        testData.setData(PASSENGERS, new PassengerMix(testData.getData(PASSENGER_MIX)));
        reporter.info(maxAllowedPassengers.getP_propertyvalue() + " at most passenger allowed");
    }

}