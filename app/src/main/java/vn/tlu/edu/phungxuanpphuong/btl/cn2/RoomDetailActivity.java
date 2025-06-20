package vn.tlu.edu.phungxuanpphuong.btl.cn2;

import android.os.Bundle;
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

        // Nhận dữ liệu từ Intent
        RoomModel room = (RoomModel) getIntent().getSerializableExtra("room");

        if (room != null) {
            txtRoomNumber.setText("Phòng " + room.getRoomNumber());
            txtType.setText("Loại: " + room.getType());
            txtPrice.setText("Giá: " + room.getPrice() + "đ/ngày");
            txtStatus.setText("Tình trạng: " + room.getStatus());
            txtDesc.setText("Mô tả: " + room.getDescription());
            Glide.with(this).load(room.getImageUrl()).into(imgRoom);
            if (room.getStatus().equals("Đã đặt") || room.getStatus().equals("Đã ở")) {
                getBookingInfoByRoomNumber(room.getRoomNumber());
            }
        } else {
            Toast.makeText(this, "Không có dữ liệu phòng", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void getBookingInfoByRoomNumber(String roomNumber) {
        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("bookings");

        bookingRef.child(roomNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String customerName = snapshot.child("customer_name").getValue(String.class);
                            String checkIn = snapshot.child("check_in").getValue(String.class);
                            String checkOut = snapshot.child("check_out").getValue(String.class);
                            String guests = snapshot.child("guests").getValue(String.class);
                            String payment = snapshot.child("payment").getValue(String.class);
                            String phone = snapshot.child("customer_phone").getValue(String.class);

                            txtCustomerName.setText("Tên khách: " + customerName);
                            txtCheckInDate.setText("Nhận phòng: " + checkIn);
                            txtCheckOutDate.setText("Trả phòng: " + checkOut);
                            txtGuests.setText("Số khách: " + guests);
                            txtPayment.setText("Thanh toán: " + payment);
                            txtPhone.setText("SĐT: " + phone);
                        } else {
                            txtCustomerName.setText("Chưa có thông tin đặt phòng.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RoomDetailActivity.this, "Lỗi kết nối dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
