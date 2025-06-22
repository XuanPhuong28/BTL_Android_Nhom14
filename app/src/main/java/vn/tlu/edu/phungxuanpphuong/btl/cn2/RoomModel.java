package vn.tlu.edu.phungxuanpphuong.btl.cn2;

import java.io.Serializable;

public class RoomModel implements Serializable {
    private String roomId;
    private String roomNumber;
    private String type;
    private int price;
    private String status;
    private String description;
    private String imageUrl;
    private int beds;  // ✅ Thêm trường này để lấy số giường từ Firebase

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

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getBeds() {
        return beds;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setBeds(int beds) {
        this.beds = beds;
    }
}
