package com.hybris.easyjet.fixture.hybris.invoke.services;

import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.IRequest;
import com.hybris.easyjet.fixture.IService;
import com.hybris.easyjet.fixture.hybris.asserters.FlightsAssertion;
import com.hybris.easyjet.fixture.hybris.helpers.DateFormat;
import com.hybris.easyjet.fixture.hybris.invoke.response.FindFlightsResponse;
import com.hybris.easyjet.fixture.hybris.invoke.response.common.AdditionalInformation;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

import static com.hybris.easyjet.exceptions.EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA;

/**
 * Created by daniel on 26/11/2016.
 */

public class FlightsService extends HybrisService implements IService {

    private FindFlightsResponse flightsResponse;

    /**
     * @param request  the request object required
     * @param endPoint the endpoint of the service
     *                 <p>
     */
    protected FlightsService(IRequest request, String endPoint) {
        super(request, endPoint);
    }

    @Override
    public FlightsAssertion assertThat() {
        assertThatServiceCallWasSuccessful();
        return new FlightsAssertion(flightsResponse);
    }

    public FlightsAssertion wasSuccessful() {
        assertThatServiceCallWasSuccessful();
        return new FlightsAssertion(flightsResponse);
    }

    @Override
    public FindFlightsResponse getResponse() {
        assertThatServiceCallWasSuccessful();
        return flightsResponse;
    }

    @Override
    protected void checkThatResponseBodyIsPopulated() {
        List<AdditionalInformation> additionalInformationList = flightsResponse.getAdditionalInformations();
        if(Objects.nonNull(additionalInformationList)) {
            List<AdditionalInformation> filtered = additionalInformationList.stream().filter(fl -> "SVC_100148_3012".equals(fl.getCode())).collect(Collectors.toList());
            Assert.assertTrue("No flights found", filtered.isEmpty());
        }
        checkThatResponseBodyIsPopulated(flightsResponse.getOutbound());
    }

    @Override
    protected void mapResponse() {
        flightsResponse = restResponse.as(FindFlightsResponse.class);
    }

    public FindFlightsResponse.Flight getOutboundFlight(String bundle) {
        return getResponse().getOutbound().getJourneys().stream()
                .flatMap(f -> f.getFlights().stream())
                .filter(g -> g.getFareTypes().stream().anyMatch(u -> u.getFareTypeCode().equals(bundle)))
                .findFirst()
                .orElse(null);
    }

    public FindFlightsResponse.Flight getOutboundFlight() throws EasyjetCompromisedException {
        if (getResponse().getOutbound().getJourneys().isEmpty()) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        }

        List<FindFlightsResponse.Flight> outbound = getOutBoundJourneys().stream().flatMap(f -> f.getFlights().stream()).collect(Collectors.toList());
        outbound.removeIf(flight -> {
            if(Objects.isNull(flight.getInventory()) || Objects.isNull(flight.getInventory().getAvailable()) || Objects.isNull(flight.getAvailableStatus())) {
                return true;
            }

            return flight.getInventory().getAvailable() < 0 || !flight.getAvailableStatus().equalsIgnoreCase("AVAILABLE");

        });

        if (outbound.isEmpty()) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        } else {
            return outbound.get(0);
        }
    }
    public List<FindFlightsResponse.Flight> getOutboundFlights() throws EasyjetCompromisedException {
        if (getResponse().getOutbound().getJourneys().isEmpty()) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        }

        List<FindFlightsResponse.Flight> outbound = getOutBoundJourneys().stream().flatMap(f -> f.getFlights().stream()).collect(Collectors.toList());
        outbound.removeIf(flight -> {
            boolean checkHour = false;
            try {
                checkHour = DateFormat.getDateF(flight.getDeparture().getDate()).before(DateUtils.addHours(new Date(), CommonConstants.NUMBER_OF_HOURS));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(Objects.isNull(flight.getInventory()) || Objects.isNull(flight.getInventory().getAvailable()) || Objects.isNull(flight.getAvailableStatus()) || checkHour) {
                return true;
            }
            return flight.getInventory().getAvailable() < 0 || !"AVAILABLE".equalsIgnoreCase(flight.getAvailableStatus());
        });

        if (outbound.isEmpty()) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        } else {
            return outbound;
        }
    }

    public List<FindFlightsResponse.Flight> getOutboundFlights(String fare) throws EasyjetCompromisedException {
        if (getResponse().getOutbound().getJourneys().isEmpty()) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        }

        List<FindFlightsResponse.Flight> outbound = getOutBoundJourneys().stream().flatMap(f -> f.getFlights().stream()).filter(g -> g.getFareTypes().stream().anyMatch(u -> u.getFareTypeCode().equals(fare))).collect(Collectors.toList());
        outbound.removeIf(flight -> {
            if(Objects.isNull(flight.getInventory()) || Objects.isNull(flight.getInventory().getAvailable()) || Objects.isNull(flight.getAvailableStatus())) {
                return true;
            }

            return flight.getInventory().getAvailable() < 0 || !flight.getAvailableStatus().equalsIgnoreCase("AVAILABLE");

        });

        if (outbound.isEmpty()) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        } else {
            return outbound;
        }
    }

    public List<FindFlightsResponse.Journey> getOutboundJourneys() throws EasyjetCompromisedException {
        if (getResponse().getOutbound().getJourneys().isEmpty()) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        }
        return getOutBoundJourneys();
    }

    public FindFlightsResponse.Flight getInboundFlight() throws EasyjetCompromisedException {
        if (Objects.isNull(getResponse().getInbound()) || getResponse().getInbound().getJourneys().isEmpty()) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        }
        List<FindFlightsResponse.Flight> inbound = getInBoundJourneys().stream().flatMap(f -> f.getFlights().stream()).collect(Collectors.toList());
        inbound.removeIf(flight -> {
            if(Objects.isNull(flight.getInventory()) || Objects.isNull(flight.getInventory().getAvailable()) || Objects.isNull(flight.getAvailableStatus()))
                return true;

            return flight.getInventory().getAvailable() < 0 || !flight.getAvailableStatus().equalsIgnoreCase("AVAILABLE");

        });

        if (inbound.isEmpty()) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        } else {
            return inbound.get(0);
        }
    }

    public List<FindFlightsResponse.Flight> getInboundFlights() throws EasyjetCompromisedException {
        if (getResponse().getInbound().getJourneys().isEmpty()) {
            throw new EasyjetCompromisedException(INSUFFICIENT_DATA);
        }
        return getInBoundJourneys().stream().flatMap(c -> c.getFlights().stream().filter(f->f.getAvailableStatus().equals("AVAILABLE"))).collect(Collectors.toList());
    }

    public List<FindFlightsResponse.Journey> getOutBoundJourneys() {
        return getResponse().getOutbound().getJourneys();
    }

    public List<FindFlightsResponse.Journey> getInBoundJourneys() {
        return getResponse().getInbound().getJourneys();
    }
}