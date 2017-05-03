package racearoundyou.ray;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by L4YOU on 17.04.2017.
 */

public class Event{
    private String status;
    @SerializedName("pushID")
    @Expose
    private String pushID;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longtitude")
    @Expose
    private Double longtitude;

    public String getStatus(){
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public MarkerOptions options(){
        LatLng position = new LatLng(getLatitude(), getLongtitude());
        MarkerOptions options = new MarkerOptions().position(position);
        return options;
    }

    public String getPushId() { return pushID; }

    public void setPushID(String pushId) {
        this.pushID = pushId;
    }

    public Double getLatitude() { return latitude; }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongtitude() { return longtitude; }

    public void setLongtitude(Double longtitude) {
        this.longtitude = longtitude;
    }

}
