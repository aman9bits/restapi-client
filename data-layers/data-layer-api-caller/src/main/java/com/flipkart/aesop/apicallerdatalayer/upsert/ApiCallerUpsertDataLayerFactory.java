package com.flipkart.aesop.apicallerdatalayer.upsert;

import org.springframework.beans.factory.FactoryBean;

/**
 * Generates objects of {@link ApiCallerUpsertDataLayer } and ensures that it is singleton.
 * @author Jagadeesh Huliyar
 */
public class ApiCallerUpsertDataLayerFactory implements FactoryBean<ApiCallerUpsertDataLayer>
{

	public ApiCallerUpsertDataLayer getObject() throws Exception
    {
	    return new ApiCallerUpsertDataLayer();
    }

	public Class<?> getObjectType()
    {
	    return ApiCallerUpsertDataLayer.class;
    }

	public boolean isSingleton()
    {
	    return true;
    }
}
