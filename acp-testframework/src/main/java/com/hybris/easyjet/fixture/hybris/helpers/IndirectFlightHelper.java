package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.database.hybris.dao.IndirectFlightRoutesDao;
import com.hybris.easyjet.database.hybris.helpers.AirportsForIndirectRoutes;
import com.hybris.easyjet.database.hybris.helpers.IndirectRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.hybris.easyjet.database.hybris.helpers.IndirectRoute.setAirportListAs;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by ptr-kvijayapal on 2/9/2017.
 */
@Component
public class IndirectFlightHelper {

    @Autowired
    private IndirectFlightRoutesDao indirectFlightRoutesDao;

    private List<IndirectRoute> indirectRoutes;

    public IndirectFlightHelper readAllIndirectRoutesFromHybris() {

        List<AirportsForIndirectRoutes> airportsForIndirectRoutes = indirectFlightRoutesDao.getListOfAirports();
        setAirportListAs(airportsForIndirectRoutes);
        this.indirectRoutes = indirectFlightRoutesDao.getAllIndirectRoutes();
        return this;
    }

    private boolean isIndirectRoutesConfiguredInHybrisFor(String source, String destination) {

        return getNumberOfIndirectRoutesFor(source, destination) > 0;
    }

    public IndirectFlightHelper indirectRoutesConfiguredInHybrisFor(String source, String destination) {

        assertTrue(isIndirectRoutesConfiguredInHybrisFor(source, destination));
        return this;
    }

    private int getNumberOfIndirectRoutesFor(String source, String destination) {

        int count = 0;
        for (IndirectRoute indirectRoute : indirectRoutes) {
            if (indirectRoute.getSource().equals(source) && indirectRoute.getDestination().equals(destination)) {
                count++;
            }
        }
        return count;
    }

    public List<String> getAllConnectingAirportsForRoute(String source, String destination) {

        assertTrue(isIndirectRoutesConfiguredInHybrisFor(source, destination));
        return indirectFlightRoutesDao.getListOfViaAirportsFor(source, destination);
    }

    /*
    Return list of all airports with in the same market group as the airport we are requesting
     */
    public List<String> getListOfAlternateAirportFor(String airport) {
        return indirectFlightRoutesDao.getListOfAlternateAirportsForAnAirport(airport);
    }
}
