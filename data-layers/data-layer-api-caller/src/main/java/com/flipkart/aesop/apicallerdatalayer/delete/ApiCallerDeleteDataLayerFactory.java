package com.flipkart.aesop.apicallerdatalayer.delete;

import org.springframework.beans.factory.FactoryBean;

/**
 * Created by aman.gupta on 12/08/15.
 */
public class ApiCallerDeleteDataLayerFactory implements FactoryBean<ApiCallerDeleteDataLayer> {
    public ApiCallerDeleteDataLayer getObject() throws Exception
    {
        return new ApiCallerDeleteDataLayer();
    }

    public Class<?> getObjectType()
    {
        return ApiCallerDeleteDataLayer.class;
    }

    public boolean isSingleton()
    {
        return true;
    }
}
