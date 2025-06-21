package vn.tlu.edu.phungxuanpphuong.btl.cn2;

public class BookingModel {
    private String type;
    private int beds;
    private int price;
    private String status;
    private String payment;
    private String guests;
    private String check_in;
    private String check_out;
    private String customer_name;
    private String customer_phone;

    public BookingModel() {
        // Required for Firebase
    }

    // Getter & Setter đầy đủ
    public String getType() { return type; }
    public int getBeds() { return beds; }
    public int getPrice() { return price; }
    public String getStatus() { return status; }
    public String getPayment() { return payment; }
    public String getGuests() { return guests; }
    public String getCheck_in() { return check_in; }
    public String getCheck_out() { return check_out; }
    public String getCustomer_name() { return customer_name; }
    public String getCustomer_phone() { return customer_phone; }

    public void setType(String type) { this.type = type; }
    public void setBeds(int beds) { this.beds = beds; }
    public void setPrice(int price) { this.price = price; }
    public void setStatus(String status) { this.status = status; }
    public void setPayment(String payment) { this.payment = payment; }
    public void setGuests(String guests) { this.guests = guests; }
    public void setCheck_in(String check_in) { this.check_in = check_in; }
    public void setCheck_out(String check_out) { this.check_out = check_out; }
    public void setCustomer_name(String customer_name) { this.customer_name = customer_name; }
    public void setCustomer_phone(String customer_phone) { this.customer_phone = customer_phone; }
}

