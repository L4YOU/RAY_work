package racearoundyou.ray;

import java.util.HashMap;
import java.util.List;

/**
 * Created by L4YOU on 17.06.2017.
 */

public class Club {
    private Integer UserAmount;
    private String name;
    private String description;
    private HashMap<String, String> Users;
    private String id;

    public Club(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() { return description; }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUserAmount() {
        return UserAmount;
    }

    public void setUserAmount(Integer userAmount) {
        UserAmount = userAmount;
    }

    public HashMap<String, String> getUsers() {
        return Users;
    }

    public void setUsers(HashMap<String, String> users) {
        Users = users;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private List<Club> ClubList;

}
