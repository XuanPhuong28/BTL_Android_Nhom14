package vn.tlu.edu.phungxuanpphuong.btl.Model;

import java.io.Serializable;

public class RoomModel implements Serializable {
    private String roomId;
    private String roomNumber;
    private String type;
    private int price;
    private String status;
    private String description;
    private String imageUrl;
    private int beds;

    public RoomModel() {
        // Required for Firebase
    }

    public RoomModel(String roomId, String roomNumber, String type, int price,
                     String status, String description, String imageUrl, int beds) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.status = status;
        this.description = description;
        this.imageUrl = imageUrl;
        this.beds = beds;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getBeds() {
        return beds;
    }

    public void setBeds(int beds) {
        this.beds = beds;
    }
}
