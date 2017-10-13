package com.hybris.easyjet;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.SimpleThreadScope;

/**
 * Created by jamie on 15/05/2017.
 */
public class TenantBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    private static ConfigurableListableBeanFactory theFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
        factory.registerScope("thread",new SimpleThreadScope());

        for(String beanName: factory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = factory.getBeanDefinition(beanName);
            beanDefinition.setScope("thread");
        }

        theFactory = factory;
    }

    public static ConfigurableListableBeanFactory getFactory(){
        return theFactory;
    }

}
