package vn.tlu.edu.phungxuanpphuong.btl.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import vn.tlu.edu.phungxuanpphuong.btl.Model.RoomModel;
import vn.tlu.edu.phungxuanpphuong.btl.R;

public class RoomDetailActivity extends AppCompatActivity {

    private ImageView imgRoom;
    private TextView txtRoomNumber, txtType, txtPrice, txtStatus, txtDesc;
    private TextView txtCustomerName, txtCheckInDate, txtCheckOutDate, txtGuests, txtPayment, txtPhone;
    private LinearLayout bookingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

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

        bookingContainer = findViewById(R.id.bookingContainer);

        // Nhận dữ liệu phòng
        RoomModel room = (RoomModel) getIntent().getSerializableExtra("room");

        if (room != null) {
            txtRoomNumber.setText("Phòng " + room.getRoomNumber());
            txtType.setText("Loại: " + room.getType());
            txtPrice.setText("Giá: " + room.getPrice() + "đ/ngày");
            txtDesc.setText("Mô tả: " + room.getDescription());

            // Load ảnh
            if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(room.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(imgRoom);
            }

            // Trạng thái phòng
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

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        ImageView btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); // Nếu dùng Firebase Auth
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void getBookingInfoByRoomNumber(String roomNumber) {
        Log.d("DEBUG", "Đang truy vấn booking cho phòng: " + roomNumber);

        DatabaseReference bookingRef = FirebaseDatabase.getInstance("https://btlon-941fd-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("bookings")
                .child(roomNumber);

        bookingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingContainer.removeAllViews(); // clear cũ

                LayoutInflater inflater = LayoutInflater.from(RoomDetailActivity.this);
                boolean foundFirstBooking = false;

                for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                    String status = bookingSnapshot.child("status").getValue(String.class);
                    if ("Đã đặt".equals(status) || "Đã ở".equals(status)) {
                        String customerName = bookingSnapshot.child("customer_name").getValue(String.class);
                        String phone = bookingSnapshot.child("customer_phone").getValue(String.class);
                        String guests = bookingSnapshot.child("guests").getValue(String.class);
                        String payment = bookingSnapshot.child("payment").getValue(String.class);
                        String checkIn = bookingSnapshot.child("check_in").getValue(String.class);
                        String checkOut = bookingSnapshot.child("check_out").getValue(String.class);

                        // Nạp layout booking_item.xml
                        View bookingView = inflater.inflate(R.layout.item_booking, bookingContainer, false);

                        ((TextView) bookingView.findViewById(R.id.txtCustomerName)).setText("Khách hàng: " + customerName);
                        ((TextView) bookingView.findViewById(R.id.txtPhone)).setText("SĐT: " + phone);
                        ((TextView) bookingView.findViewById(R.id.txtGuests)).setText("Khách: " + guests);
                        ((TextView) bookingView.findViewById(R.id.txtPayment)).setText("Thanh toán: " + payment);
                        ((TextView) bookingView.findViewById(R.id.txtCheckIn)).setText("Nhận phòng: " + checkIn);
                        ((TextView) bookingView.findViewById(R.id.txtCheckOut)).setText("Trả phòng: " + checkOut);
                        ((TextView) bookingView.findViewById(R.id.txtStatus)).setText("Tình trạng: " + status);

                        bookingContainer.addView(bookingView);

                        if (!foundFirstBooking) {
                            txtCustomerName.setText("Khách hàng: " + customerName);
                            txtCheckInDate.setText("Ngày nhận phòng: " + checkIn);
                            txtCheckOutDate.setText("Ngày trả phòng: " + checkOut);
                            txtGuests.setText("Số lượng khách: " + guests);
                            txtPayment.setText("Thanh toán: " + payment);
                            txtPhone.setText("SĐT: " + phone);
                            foundFirstBooking = true;
                        }
                    }
                }

                if (!foundFirstBooking) {
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

