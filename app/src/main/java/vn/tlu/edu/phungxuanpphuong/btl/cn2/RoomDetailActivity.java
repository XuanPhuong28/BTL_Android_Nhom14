package vn.tlu.edu.phungxuanpphuong.btl.cn2;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import vn.tlu.edu.phungxuanpphuong.btl.R;

public class RoomDetailActivity extends AppCompatActivity {
    private ImageView imgRoom;
    private TextView txtRoomNumber, txtType, txtPrice, txtStatus, txtDesc;
    private TextView txtCustomerName, txtCheckInDate, txtCheckOutDate, txtGuests, txtPayment, txtPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        // Ánh xạ view
        imgRoom = findViewById(R.id.imgRoom);
        txtRoomNumber = findViewById(R.id.txtRoomNumber);
        txtType = findViewById(R.id.txtType);
        txtPrice = findViewById(R.id.txtPrice);
        txtStatus = findViewById(R.id.txtStatus);
        txtDesc = findViewById(R.id.txtDesc);

        txtCustomerName = findViewById(R.id.txtCustomerName);
        txtCheckInDate = findViewById(R.id.txtCheckInDate);
        txtCheckOutDate = findViewById(R.id.txtCheckOutDate);
        txtGuests = findViewById(R.id.txtGuests);
        txtPayment = findViewById(R.id.txtPayment);
        txtPhone = findViewById(R.id.txtPhone);

        // Nhận dữ liệu phòng
        RoomModel room = (RoomModel) getIntent().getSerializableExtra("room");

        if (room != null) {
            txtRoomNumber.setText("Phòng " + room.getRoomNumber());
            txtType.setText("Loại: " + room.getType());
            txtPrice.setText("Giá: " + room.getPrice() + "đ/ngày");
            txtDesc.setText("Mô tả: " + room.getDescription());

            // Load ảnh nếu có
            if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(room.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(imgRoom);
            }

            // Hiển thị tình trạng và gọi thông tin booking nếu cần
            String status = room.getStatus();
            if ("booked".equals(status)) {
                txtStatus.setText("Tình trạng: Đã đặt");
                getBookingInfoByRoomNumber(room.getRoomNumber());
            } else if ("in-use".equals(status)) {
                txtStatus.setText("Tình trạng: Đã ở");
                getBookingInfoByRoomNumber(room.getRoomNumber());
            } else {
                txtStatus.setText("Tình trạng: Trống");
                txtCustomerName.setText("Chưa có thông tin đặt phòng.");
            }

        } else {
            Toast.makeText(this, "Không có dữ liệu phòng", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void getBookingInfoByRoomNumber(String roomNumber) {
        Log.d("DEBUG", "Đang truy vấn booking cho phòng: " + roomNumber);
        String roomKey = roomNumber;

        DatabaseReference bookingRef = FirebaseDatabase.getInstance("https://btlon-941fd-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("bookings")
                .child(roomKey);

        bookingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("DEBUG", "snapshot.exists() = " + snapshot.exists());
                Log.d("DEBUG", "snapshot value = " + new Gson().toJson(snapshot.getValue()));

                if (snapshot.exists()) {
                    String customerName = snapshot.child("customer_name").getValue(String.class);
                    String checkIn = snapshot.child("check_in").getValue(String.class);
                    String checkOut = snapshot.child("check_out").getValue(String.class);
                    String guests = snapshot.child("guests").getValue(String.class);
                    String payment = snapshot.child("payment").getValue(String.class);
                    String phone = snapshot.child("customer_phone").getValue(String.class);

                    txtCustomerName.setText("Khách hàng: " + customerName);
                    txtCheckInDate.setText("Ngày nhận phòng: " + checkIn);
                    txtCheckOutDate.setText("Ngày trả phòng: " + checkOut);
                    txtGuests.setText("Số lượng khách: " + guests);
                    txtPayment.setText("Thanh toán: " + payment);
                    txtPhone.setText("SĐT: " + phone);
                } else {
                    Log.d("DEBUG", "Không tìm thấy thông tin đặt phòng cho phòng: " + roomKey);
                    txtCustomerName.setText("Chưa có thông tin đặt phòng.");
                    txtCheckInDate.setText("");
                    txtCheckOutDate.setText("");
                    txtGuests.setText("");
                    txtPayment.setText("");
                    txtPhone.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DEBUG", "Lỗi truy vấn Firebase: " + error.getMessage());
                Toast.makeText(RoomDetailActivity.this, "Lỗi kết nối dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
