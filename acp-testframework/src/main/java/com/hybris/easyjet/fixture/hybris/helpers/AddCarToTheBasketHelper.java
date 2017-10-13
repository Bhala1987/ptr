package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.config.SerenityFacade;
import com.hybris.easyjet.config.constants.CommonConstants;
import com.hybris.easyjet.fixture.hybris.invoke.HybrisHeaders;
import com.hybris.easyjet.fixture.hybris.invoke.pathparams.BasketPathParams;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.basket.AddCarHireProductRequestBody;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.carhire.CarHireLocation;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.carhire.CarHireProduct;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.carhire.DriverContext;
import com.hybris.easyjet.fixture.hybris.invoke.requestbody.factory.AddCarToBasketRequestBodyFactory;
import com.hybris.easyjet.fixture.hybris.invoke.requests.AddCarToBasketRequest;
import com.hybris.easyjet.fixture.hybris.invoke.services.HybrisServiceFactory;
import com.hybris.easyjet.fixture.hybris.invoke.services.basket.AddCarToBasketService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

import static com.hybris.easyjet.config.SerenityFacade.DataKeys.SERVICE;

@Component
public class AddCarToTheBasketHelper {
    private static final String LOCATION = "location";
    private static final String CAR_HIRE_PRODUCT = "carHireProduct";
    private static final String DRIVER = "driver";
    @Autowired
    private BasketHelper basketHelper;
    @Autowired
    private HybrisServiceFactory serviceFactory;
    @Autowired
    private SerenityFacade testData;
    @Autowired
    private AddCarToBasketRequestBodyFactory addCarToBasketRequestBodyFactory;

    private AddCarHireProductRequestBody addCarHireProductRequestBody;

    public void addCarToBasket() {
        addCarHireProductRequestBody = addCarToBasketRequestBodyFactory.buildCarHireProductRequestBody();
        addCarToTheBasket();
    }

    public void addCarToBasket(String passengerType, int noOfCarEquipments, int noOfCarHireDays, int pickUpTime, int dropOffTime) {
        addCarHireProductRequestBody = addCarToBasketRequestBodyFactory.buildCarHireProductRequestBody(passengerType , noOfCarEquipments, noOfCarHireDays, pickUpTime, dropOffTime);
        addCarToTheBasket();
    }

    public String getBasketId() {
        return basketHelper.getBasketService().getResponse().getBasket().getCode();
    }

    public void addCarToTheBasket() {
        BasketPathParams pathParams = BasketPathParams.builder().basketId(getBasketId()).path(BasketPathParams.BasketPaths.ADD_CAR_TO_BASKET).build();
        AddCarToBasketService addCarToBasketService = serviceFactory.getAddCarToBasketService(new AddCarToBasketRequest(HybrisHeaders.getValid(testData.getChannel()).build(), pathParams, addCarHireProductRequestBody));
        addCarToBasketService.invoke();
        testData.setData(SERVICE, addCarToBasketService);
    }

    public void emptyMandatoryFieldsFromRequestBody(String mandatoryObjects, String field) {
        if (mandatoryObjects.equals(LOCATION))
            modifyAddCarRequestForLocationObject(field, "");
        else if (mandatoryObjects.equals(CAR_HIRE_PRODUCT))
            modifyAddCarRequestForCarHireProductObject(field, "");
        else if(mandatoryObjects.equals(DRIVER))
            modifyAddCarRequestForDriverContextObject(field, "");

        addCarToTheBasket();
    }


    public void modifyAddCarRequestForLocationObject(String field, String value) {
        CarHireLocation carHireLocation = addCarHireProductRequestBody.getLocation();
        try {
            BeanUtils.setProperty(carHireLocation,field,value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void modifyAddCarRequestForCarHireProductObject(String field, String value) {
        CarHireProduct carHireProduct = addCarHireProductRequestBody.getCarHireProduct();
        try {
            BeanUtils.setProperty(carHireProduct,field,value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void modifyAddCarRequestForDriverContextObject(String field, String value) {
        DriverContext driverContext = addCarHireProductRequestBody.getDriver();
        try {
            BeanUtils.setProperty(driverContext,field,value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void getBasicAddCartoTheBasketRequestBody() {
        addCarHireProductRequestBody = addCarToBasketRequestBodyFactory.buildCarHireProductRequestBody();
    }


}
