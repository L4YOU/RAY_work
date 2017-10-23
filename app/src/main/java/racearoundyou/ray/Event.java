package racearoundyou.ray;

import java.util.Map;

/**
 * Created by L4YOU on 17.04.2017.
 */

public class Event{

    private String name;
    private Double latitude;
    private Double longtitude;
    private String pushID;
    private Map<String, Integer> Interests;
    private String startdate;
    private String starttime;
//    private int people_amount;

    public Event(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() { return latitude; }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongtitude() { return longtitude; }

    public void setLongtitude(Double longtitude) {
        this.longtitude = longtitude;
    }

    public String getPushID() { return pushID; }

    public void setPushID(String pushID) {
        this.pushID = pushID;
    }

    public Map<String, Integer> getInterests() {return Interests; }

    public void setInterests(Map<String, Integer> interests) {
        Interests = interests;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    //    public Date getStartDate() { return startdate; }
//
//    public void setStartDate(Date startdate) {
//        this.startdate = startdate;
//    }
//
//    public String getDesc() { return desc; }
//
//    public void setDesc(String desc) {
//        this.desc = desc;
//    }
//
//    public String getStyle() { return style; }
//
//    public void setStyle(String style) {
//        this.style = style;
//    }
//
//    public int getPeople_amount() { return people_amount; }
//
//    public void setPeople_amount(int people_amount) {
//        this.people_amount = people_amount;
//    }
}
