package ca.ubc.cs.cpsc210.translink.providers;

import android.util.Log;
import ca.ubc.cs.cpsc210.translink.auth.TranslinkToken;
import ca.ubc.cs.cpsc210.translink.model.Stop;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Wrapper for Translink Arrival Data Provider
 */
public class HttpArrivalDataProvider extends AbstractHttpDataProvider {
    private Stop stop;

    public HttpArrivalDataProvider(Stop stop) {
        super();
        this.stop = stop;
    }

    @Override
    /**
     * Produces URL used to query Translink web service for expected arrivals at
     * the stop specified in call to constructor.
     *
     * @returns URL to query Translink web service for arrival data
     */
    protected URL getUrl() throws MalformedURLException {

//        http://api.translink.ca/rttiapi/v1/stops/60980/estimates?apikey=[APIKey]
        String request = "http://api.translink.ca/rttiapi/v1/stops/" + stop.getNumber() + "/estimates?apikey=" +
                TranslinkToken.TRANSLINK_API_KEY + "&timeframe=1440";

//        String request = "http://b210.herokuapp.com/rttiapi/v1/stops/" + stop.getNumber() + "/estimates?apikey=" +
//                TranslinkToken.TRANSLINK_API_KEY;

        Log.i("getURL", "URL is: " + request);
        return new URL(request);
        // TODO: Complete the implementation of this method (Task 8)
    }

    @Override
    public byte[] dataSourceToBytes() throws IOException {
        return new byte[0];
    }
}
