package feature.document.steps.services.productmanagementservices;

import com.hybris.easyjet.TestApplication;
import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.database.hybris.dao.AirportsDao;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.requests.refdata.SectorsRequest;
import com.hybris.easyjet.fixture.hybris.invoke.response.SectorResponse;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.refdata.SectorsService;
import net.thucydides.core.annotations.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.*;

/**
 * GetSectorsSteps handle the communication with the getSectors service.
 * It makes use of testData to store parameters that can be used by other steps.
 * It expose methods annotated with @Step to be used in compounded steps from other feature files
 * and uses steps from other step classes as well
 */
@ContextConfiguration(classes = TestApplication.class)
public class GetSectorsSteps {

    private static final List<String> sectorsWithAvailableFlights = Arrays.asList(Arrays.stream(GetSectorsSteps.Sectors.values()).map(Enum::name).toArray(String[]::new));

    @Autowired
    private SerenityFacade testData;
    @Autowired
    private HybrisServiceFactory serviceFactory;

    @Autowired
    private AirportsDao airportsDao;

    private SectorsService getSectorsService;

    private void invokeGetSectors() {
        HybrisHeaders.HybrisHeadersBuilder headers = testData.getData(HEADERS);
        getSectorsService = serviceFactory.getSectors(new SectorsRequest(headers.build(), null));
        getSectorsService.invoke();
    }

    @Step("Get sectors: with return {0}, APIS {1}, DCS {2}")
    public void getSectors(boolean withReturn, Boolean isAPIS, Boolean isDCS) {
        invokeGetSectors();

        // filter sector with impex data provided
        Stream<SectorResponse.Sector> usableSectorsStream = getSectorsService.getResponse().getSectors().stream()
                .filter(sector -> sectorsWithAvailableFlights.contains(sector.getCode()));

        if (withReturn) {
            usableSectorsStream = usableSectorsStream
                    .filter(origin -> getSectorsService.getResponse().getSectors().stream()
                            .anyMatch(destination -> destination.getDepartureAirport().equals(origin.getArrivalAirport())
                                    && destination.getArrivalAirport().equals(origin.getDepartureAirport())));
        }

        if (!Objects.isNull(isAPIS)) {
            usableSectorsStream = usableSectorsStream
                    .filter(sector -> sector.getIsAPIS().equals(isAPIS));
        }

        if (!Objects.isNull(isDCS)) {
            List<String> dcsAirports = airportsDao.getDCSAirports();
            if (isDCS) {
                usableSectorsStream = usableSectorsStream
                        .filter(sector ->
                                dcsAirports.contains(sector.getDepartureAirport()) ||
                                        dcsAirports.contains(sector.getArrivalAirport())
                        );
            } else {
                usableSectorsStream = usableSectorsStream
                        .filter(sector ->
                                !dcsAirports.contains(sector.getDepartureAirport()) &&
                                        !dcsAirports.contains(sector.getArrivalAirport())
                        );
            }
        }

        testData.setData(USABLE_SECTORS, usableSectorsStream.collect(Collectors.toList()));
    }

    public void setRandomSector() throws EasyjetCompromisedException {
        List<SectorResponse.Sector> sectors = testData.getData(USABLE_SECTORS);
        if (sectors.size() == 0) {
            throw new EasyjetCompromisedException(EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.INSUFFICIENT_DATA);
        }
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(sectors.size());
        SectorResponse.Sector sector = sectors.get(index);
        testData.setData(ORIGIN, sector.getDepartureAirport());
        testData.setData(DESTINATION, sector.getArrivalAirport());

        sectors.remove(index);
        testData.setData(USABLE_SECTORS, sectors);
    }

    private enum Sectors {
        AGPBRS,
        AGPLGW,
        AGPLPL,
        AGPLTN,
        AGPMAN,
        AGPSEN,
        AGPSTN,
        ALCBRS,
        ALCGLA,
        ALCGVA,
        ALCLGW,
        ALCLPL,
        ALCLTN,
        ALCMAN,
        ALCSEN,
        BCNBOD,
        BCNBRS,
        BCNBSL,
        BCNCDG,
        BCNGVA,
        BCNLGW,
        BCNLPL,
        BCNLTN,
        BCNLYS,
        BCNNCE,
        BFSAGP,
        BFSALC,
        BFSBHX,
        BFSBRS,
        BFSEDI,
        BFSGLA,
        BFSLGW,
        BFSLPL,
        BFSLTN,
        BFSMAN,
        BFSNCL,
        BFSPMI,
        BFSSTN,
        BHXBFS,
        BIOSTN,
        BIQCDG,
        BODBCN,
        BODBSL,
        BODGVA,
        BODLGW,
        BODLTN,
        BODLYS,
        BODNCE,
        BRSAGP,
        BRSALC,
        BRSBCN,
        BRSBFS,
        BRSCDG,
        BRSEDI,
        BRSGLA,
        BRSGVA,
        BRSINV,
        BRSMAD,
        BRSMJV,
        BRSNCE,
        BRSNCL,
        BRSPMI,
        BRSTLS,
        BSLAGP,
        BSLBCN,
        BSLBOD,
        BSLEDI,
        BSLLGW,
        BSLLTN,
        BSLMAD,
        BSLMAN,
        BSLNCE,
        BSLPMI,
        CDGAGP,
        CDGBCN,
        CDGBIQ,
        CDGBRS,
        CDGEDI,
        CDGGLA,
        CDGLGW,
        CDGLTN,
        CDGMAD,
        CDGMAN,
        CDGNCE,
        EDIBFS,
        EDIBRS,
        EDIBSL,
        EDICDG,
        EDIGVA,
        EDILGW,
        EDILTN,
        EDIMAD,
        EDISTN,
        GLAAGP,
        GLAALC,
        GLABFS,
        GLABRS,
        GLACDG,
        GLALGW,
        GLALTN,
        GLASTN,
        GVAAGP,
        GVAALC,
        GVABCN,
        GVABOD,
        GVABRS,
        GVAEDI,
        GVALGW,
        GVALTN,
        GVAMAN,
        GVANCE,
        GVANTE,
        GVAPMI,
        GVASCQ,
        GVATLS,
        INVBRS,
        INVLGW,
        INVLTN,
        IOMLGW,
        IOMLPL,
        JERLGW,
        LEILGW,
        LGWACE,
        LGWAGP,
        LGWALC,
        LGWBCN,
        LGWBFS,
        LGWBOD,
        LGWBSL,
        LGWCDG,
        LGWEDI,
        LGWGLA,
        LGWGVA,
        LGWINV,
        LGWIOM,
        LGWJER,
        LGWLEI,
        LGWLYS,
        LGWMAD,
        LGWMAH,
        LGWMJV,
        LGWMPL,
        LGWMRS,
        LGWNCE,
        LGWNTE,
        LGWPMI,
        LGWSVQ,
        LGWTFS,
        LGWTLS,
        LGWVLC,
        LGWZRH,
        LILNCE,
        LILTLS,
        LPLAGP,
        LPLBCN,
        LPLBFS,
        LPLIOM,
        LPLJER,
        LPLNCE,
        LPLPMI,
        LTNAGP,
        LTNALC,
        LTNBCN,
        LTNBFS,
        LTNBOD,
        LTNBSL,
        LTNCDG,
        LTNEDI,
        LTNGLA,
        LTNGVA,
        LTNINV,
        LTNMAD,
        LTNNCE,
        LTNPMI,
        LTNZRH,
        LYSBCN,
        LYSBOD,
        LYSLGW,
        LYSNTE,
        MADBRS,
        MADBSL,
        MADCDG,
        MADEDI,
        MADLGW,
        MADLTN,
        MAHLGW,
        MANAGP,
        MANALC,
        MANBFS,
        MANBSL,
        MANCDG,
        MANGVA,
        MANPMI,
        MJVBRS,
        MJVLGW,
        MPLLGW,
        MRSLGW,
        NCEBCN,
        NCEBOD,
        NCEBRS,
        NCEBSL,
        NCECDG,
        NCEGVA,
        NCELGW,
        NCELIL,
        NCELTN,
        NCENCL,
        NCENTE,
        NCEORY,
        NCESTN,
        NCLBFS,
        NCLBRS,
        NTEGVA,
        NTELGW,
        NTENCE,
        ORYGVA,
        ORYNCE,
        ORYTLS,
        PMIBFS,
        PMIBOD,
        PMIBRS,
        PMIBSL,
        PMIGVA,
        PMILGW,
        PMILPL,
        PMILTN,
        PMIMAN,
        SCQGVA,
        SENAGP,
        SENALC,
        SENPMI,
        STNAGP,
        STNBFS,
        STNBIO,
        STNEDI,
        STNGLA,
        STNNCE,
        STNPMI,
        SVQLGW,
        TFSLGW,
        TLSBRS,
        TLSGVA,
        TLSLIL,
        TLSLGW,
        TLSORY,
        VLCLGW,
        ZRHLGW,
        ZRHLTN
    }

}