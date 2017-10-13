package com.hybris.easyjet.fixture.hybris.helpers;

import com.hybris.easyjet.database.hybris.dao.ChannelPropertiesDao;
import com.hybris.easyjet.database.hybris.models.ChannelPropertiesModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by dwebb on 1/11/2017.
 */
@Component
public class ChannelPropertiesHelper {

    private final ChannelPropertiesDao channelDao;

    @Autowired
    public ChannelPropertiesHelper(ChannelPropertiesDao channelDao) {
        this.channelDao = channelDao;
    }

    public List<ChannelPropertiesModel> getAllChannelProperties() {
        return channelDao.returnChannelProperties();
    }

    public List<ChannelPropertiesModel> getChannelProperties(String channel) {
        List<ChannelPropertiesModel> myPropModel = channelDao.returnChannelProperties();
        myPropModel.removeIf(channelPropertiesModel -> (!(channelPropertiesModel.getCode().equals(channel))));
        return myPropModel;
    }

    public String getPropertyValueByChannelAndKey(String aChannel, String aKey) {
        List<ChannelPropertiesModel> properties = getChannelProperties(aChannel);
        for (ChannelPropertiesModel model : properties) {
            if (model.getP_propertyname().equals(aKey)) {
                return model.getP_propertyvalue();
            }
        }
        return null;
    }
}
