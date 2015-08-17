package com.flipkart.aesop.apicaller;

import com.flipkart.aesop.event.AbstractEvent;
import com.linkedin.databus.client.pub.ConsumerCallbackResult;

import java.net.URL;

/**
 * Created by aman.gupta on 10/08/15.
 */
public abstract class AbstractAPICaller {
    public URL url;
    public ConsumerCallbackResult call(AbstractEvent event) throws Exception{
        return null;
    }

    public AbstractAPICaller(URL url) {
        this.url = url;
    }
}
