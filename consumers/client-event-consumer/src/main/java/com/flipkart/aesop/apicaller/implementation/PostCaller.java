package com.flipkart.aesop.apicaller.implementation;

import com.flipkart.aesop.apicaller.AbstractAPICaller;
import com.flipkart.aesop.event.AbstractEvent;
import com.linkedin.databus.client.pub.ConsumerCallbackResult;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by aman.gupta on 10/08/15.
 */
public class PostCaller extends AbstractAPICaller {
    private final String USER_AGENT = "Mozilla/5.0";

    public PostCaller(URL url) {
        super(url);
    }

    public ConsumerCallbackResult call(AbstractEvent event) throws Exception{
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        String urlParameters=processEvent(event);

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        switch(responseCode){
            case 200:
                return ConsumerCallbackResult.SUCCESS;
            case 201:
                return ConsumerCallbackResult.SUCCESS;
            case 202:
                return ConsumerCallbackResult.SUCCESS;
            default:
                return ConsumerCallbackResult.ERROR;
        }
    }

    public String processEvent(AbstractEvent event){
        return event.toString();
    }
}
