package com.flipkart.aesop.eventconsumer.implementation;

import com.flipkart.aesop.apicaller.AbstractAPICaller;
import com.flipkart.aesop.event.AbstractEvent;
import com.flipkart.aesop.event.EventFactory;
import com.flipkart.aesop.eventconsumer.AbstractEventConsumer;
import com.flipkart.aesop.transformer.PreMappingTransformer;
import com.linkedin.databus.client.pub.ConsumerCallbackResult;
import com.linkedin.databus.client.pub.DbusEventDecoder;
import com.linkedin.databus.core.DbusEvent;
import com.linkedin.databus2.core.DatabusException;

/**
 * Created by aman.gupta on 07/08/15.
 */
public class CallApiEventConsumerImpl extends AbstractEventConsumer{

    private PreMappingTransformer preMappingTransformer;
    private AbstractAPICaller apiCaller;
    @Override
    public AbstractEvent decodeSourceEvent(DbusEvent event, DbusEventDecoder eventDecoder) throws DatabusException {
        return  sourceEventFactory.createEvent(event, eventDecoder);
    }

    @Override
    public ConsumerCallbackResult processSourceEvent(AbstractEvent event) {
        AbstractEvent sourceEventAfterTransformation = preMappingTransformer == null ? event :preMappingTransformer.transform(event);

        ConsumerCallbackResult result= null;
        try {
            result = apiCaller.call(sourceEventAfterTransformation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ConsumerCallbackResult.isFailure(result)) {
            return result;
        }
        return ConsumerCallbackResult.SUCCESS;
    }

    public void setApiCaller(AbstractAPICaller apiCaller) {
        this.apiCaller = apiCaller;
    }

    public static class Builder extends AbstractEventConsumer.Builder<CallApiEventConsumerImpl>
    {
        private PreMappingTransformer preMappingTransformer;
        private AbstractAPICaller apiCaller;
        public Builder(EventFactory sourceEventFactory) {
            super(sourceEventFactory,null, null);

        }

        @Override
        public CallApiEventConsumerImpl build() {
            return new CallApiEventConsumerImpl(this);
        }
        public PreMappingTransformer getPreMappingTransformer()
        {
            return preMappingTransformer;
        }

        public Builder withApiCaller(AbstractAPICaller apiCaller){
            this.apiCaller=apiCaller;
            return this;
        }

        public  AbstractAPICaller getApiCaller(){ return apiCaller;}

        public Builder withPreMappingTransformer(PreMappingTransformer preMappingTransformer)
        {
            this.preMappingTransformer = preMappingTransformer;
            return this;
        }
    }

    private CallApiEventConsumerImpl(Builder builder)
    {
        this.mapper = builder.getMapper();
        this.sourceEventFactory = builder.getSourceEventFactory();
        this.preMappingTransformer = builder.getPreMappingTransformer();
    }
}