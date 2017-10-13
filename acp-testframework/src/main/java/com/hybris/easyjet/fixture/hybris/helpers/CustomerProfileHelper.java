package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.database.hybris.dao.CustomerDao;
import com.hybris.easyjet.database.hybris.models.CustomerModel;
import com.hybris.easyjet.exceptions.EasyjetCompromisedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

import static com.hybris.easyjet.exceptions.EasyjetCompromisedException.EasyJetCompromisedExceptionMessages.NO_CUSTOMER_DATA_IN_HYBRIS;


/**
 * Created by dwebb on 12/2/2016.
 */
@Component
public class CustomerProfileHelper {

    private CustomerDao hybrisCustomerDao;

    @Autowired
    public CustomerProfileHelper(CustomerDao hybrisDealsDao) {
        this.hybrisCustomerDao = hybrisDealsDao;
    }

    private static <T> T getRandomItem(List<T> list) throws EasyjetCompromisedException {
        Random rand = new Random(System.currentTimeMillis());
        try {
            return list.get(rand.nextInt(list.size()));
        } catch (Exception ex) {//NOSONAR
            throw new EasyjetCompromisedException(NO_CUSTOMER_DATA_IN_HYBRIS);
        }
    }

    public CustomerModel getCustomerById(String id) throws EasyjetCompromisedException {
        return findValidCustomerProfilesWithShippingAddress().stream()
                .filter(customerModel -> customerModel.getUid().equals(id))
                .findFirst().orElseThrow(() -> new EasyjetCompromisedException("The customer doesn't exist"));
    }

    public CustomerModel findAValidCustomerProfile() throws Throwable {

        return getRandomItem(findValidCustomerProfilesWithShippingAddress());
    }

    public CustomerModel findAValidCustomerProfileWithShippingAddress() throws Throwable {
        return getRandomItem(findValidCustomerProfilesWithShippingAddress());
    }

    public CustomerModel findAValidCustomerProfileByStatus(String status) throws EasyjetCompromisedException {
        return getRandomItem(hybrisCustomerDao.returnValidCustomerByStatus(status));
    }

    private List<CustomerModel> findValidCustomerProfilesWithShippingAddress() {
        return hybrisCustomerDao.returnValidCustomerWithShippingAddress();
    }

}
