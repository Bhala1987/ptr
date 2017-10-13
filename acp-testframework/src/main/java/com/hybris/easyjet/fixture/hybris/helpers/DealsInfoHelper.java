package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.database.hybris.dao.DealDao;
import com.hybris.easyjet.database.hybris.models.DealModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

import static com.hybris.easyjet.exceptions.EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.NO_DEALS_DATA_IN_HYBRIS;

/**
 * Created by tejal on 09/01/17.
 */

@Component
public class DealsInfoHelper {

    @Autowired
    private DealDao dealDao;

    private static final Random rand = new Random();


    private static <T> T getRandomItem(List<T> list) throws Throwable {

        try {
            return list.get(rand.nextInt(list.size()));
        } catch (Exception ex) {
            throw new EasyjetCompromisedException(NO_DEALS_DATA_IN_HYBRIS);
        }
    }

    DealModel findAValidDeals() throws Throwable {

        return getRandomItem((findValidDeals()));
    }

    private List<DealModel> findValidDeals() {

        return dealDao.getDeals(true, null, null);
    }

}