package racearoundyou.ray.API;

import java.util.List;

import racearoundyou.ray.Event;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FunctionsAPI {
    @POST("/addMarker")
    Call<Event> insertMarker(@Query("lat") double lat, @Query("longtitude") double longtitude);

    @GET("/getMarkersFirstStart")
    Call<Event> getMarkers();
}
