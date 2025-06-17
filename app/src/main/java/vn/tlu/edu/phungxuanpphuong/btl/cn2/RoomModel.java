package vn.tlu.edu.phungxuanpphuong.btl.cn2;

public class RoomModel {
    private String roomId;
    private String roomNumber;
    private String type;
    private int price;
    private String status;
    private String description;
    private String imageUrl;

    public RoomModel() {
        // Required for Firebase
    }

    public RoomModel(String roomId, String roomNumber, String type, int price, String status, String description, String imageUrl) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.status = status;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getRoomId() { return roomId; }
    public String getRoomNumber() { return roomNumber; }
    public String getType() { return type; }
    public int getPrice() { return price; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }

    public void setRoomId(String roomId) { this.roomId = roomId; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public void setType(String type) { this.type = type; }
    public void setPrice(int price) { this.price = price; }
    public void setStatus(String status) { this.status = status; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}