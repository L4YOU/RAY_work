package racearoundyou.ray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by L4YOU on 06.05.2017.
 */

public class User{
    private String email;
    private String nickname;
    private Double latitude;
    private Double longtitude;
    private String uid;
    private boolean firstAuth;
    private Map<String, Integer> Interests;
    private Map <String, Boolean> Friends;
    private ArrayList<String> Clubs;

    public User(){}

    public boolean isFirstAuth() {
        return firstAuth;
    }

    public void setFirstAuth(boolean firstAuth) {
        this.firstAuth = firstAuth;
    }

    public String getUid(){return uid;}

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Double getLatitude() { return latitude; }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongtitude() { return longtitude; }

    public void setLongtitude(Double longtitude) {
        this.longtitude = longtitude;
    }

    public Map<String, Integer> getInterests() {
        return Interests;
    }

    public void setInterests(Map<String, Integer> interests) {
        Interests = interests;
    }

    public ArrayList<String> getClubs() {
        return Clubs;
    }

    public void setClubs(ArrayList<String> clubs) {
        Clubs = clubs;
    }

    public Map<String, Boolean> getFriends() {
        return Friends;
    }

    public void setFriends(Map<String, Boolean> friends) {
        Friends = friends;
    }
}


