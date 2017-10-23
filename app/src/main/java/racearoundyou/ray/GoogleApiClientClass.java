package racearoundyou.ray;

/**
 * Created by L4YOU on 16.05.2017.
 */

import com.google.android.gms.common.api.GoogleApiClient;

public class GoogleApiClientClass {
    private static final String TAG = "MyGoogleApiClient_Singleton";
    private static GoogleApiClientClass instance = null;

    private static GoogleApiClient mGoogleApiClient = null;

    protected GoogleApiClientClass() {

    }

    public static GoogleApiClientClass getInstance(GoogleApiClient aGoogleApiClient) {
        if(instance == null) {
            instance = new GoogleApiClientClass();
            if (mGoogleApiClient == null)
                mGoogleApiClient = aGoogleApiClient;
        }
        return instance;
    }

    public GoogleApiClient getGoogleApiClient(){
        return mGoogleApiClient;
    }
}
