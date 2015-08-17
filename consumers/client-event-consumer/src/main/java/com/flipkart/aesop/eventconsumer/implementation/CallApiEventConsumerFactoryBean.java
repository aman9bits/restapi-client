package com.flipkart.aesop.eventconsumer.implementation;

import com.flipkart.aesop.apicaller.AbstractAPICaller;
import com.flipkart.aesop.eventconsumer.EventConsumerFactoryBean;

/**
 * Created by aman.gupta on 11/08/15.
 */
public class CallApiEventConsumerFactoryBean extends EventConsumerFactoryBean<CallApiEventConsumerImpl> {
    protected AbstractAPICaller apiCaller;

    public void setApiCaller(AbstractAPICaller apiCaller) {
        this.apiCaller = apiCaller;
    }

    public CallApiEventConsumerImpl getEventConsumerObject()
    {

        return new CallApiEventConsumerImpl.
                Builder(sourceEventFactory)
                .withPreMappingTransformer(preMappingTransformer)
                .withApiCaller(apiCaller)
                .build();
    }
}
