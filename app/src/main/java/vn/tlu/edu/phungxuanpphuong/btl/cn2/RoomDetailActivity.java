package vn.tlu.edu.phungxuanpphuong.btl.cn2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.*;
import vn.tlu.edu.phungxuanpphuong.btl.R;

public class RoomDetailActivity extends AppCompatActivity {

    private ImageView imgRoom;
    private TextView txtRoomNumber, txtType, txtPrice, txtStatus, txtDesc;
    private TextView txtCustomerName, txtCheckInDate, txtCheckOutDate, txtGuests, txtPayment, txtPhone;
    private LinearLayout bookingContainer;
    private String roomNumber;

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
        bookingContainer = findViewById(R.id.bookingContainer);

        // Nhận dữ liệu phòng
        RoomModel room = (RoomModel) getIntent().getSerializableExtra("room");

        if (room != null) {
            roomNumber = room.getRoomNumber();
            txtRoomNumber.setText("Phòng " + roomNumber);
            txtType.setText("Loại: " + room.getType());
            txtPrice.setText("Giá: " + room.getPrice() + "đ/ngày");
            txtDesc.setText("Mô tả: " + room.getDescription());

            if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
                Glide.with(this).load(room.getImageUrl()).into(imgRoom);
            }

            switch (room.getStatus()) {
                case "booked": txtStatus.setText("Tình trạng: Đã đặt"); break;
                case "in-use": txtStatus.setText("Tình trạng: Đã ở"); break;
                default: txtStatus.setText("Tình trạng: Trống"); break;
            }

            loadBookings();
        } else {
            Toast.makeText(this, "Không có dữ liệu phòng", Toast.LENGTH_SHORT).show();
            finish();
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadBookings() {
        bookingContainer.removeAllViews();

        DatabaseReference ref = FirebaseDatabase.getInstance("https://btlon-941fd-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("bookings").child(roomNumber);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean firstShown = false;

                for (DataSnapshot snap : snapshot.getChildren()) {
                    BookingModel booking = snap.getValue(BookingModel.class);
                    if (booking == null) continue;

                    booking.setBookingId(snap.getKey());
                    booking.setRoomNumber(roomNumber);

                    if (!firstShown && ("Đã đặt".equals(booking.getStatus()) || "Đã ở".equals(booking.getStatus()))) {
                        txtCustomerName.setText("Khách hàng: " + booking.getCustomer_name());
                        txtCheckInDate.setText("Ngày nhận phòng: " + booking.getCheck_in());
                        txtCheckOutDate.setText("Ngày trả phòng: " + booking.getCheck_out());
                        txtGuests.setText("Số lượng khách: " + booking.getGuests());
                        txtPayment.setText("Thanh toán: " + booking.getPayment());
                        txtPhone.setText("SĐT: " + booking.getCustomer_phone());
                        firstShown = true;
                    }

                    View item = LayoutInflater.from(RoomDetailActivity.this).inflate(R.layout.item_booking, bookingContainer, false);
                    fillBookingView(item, booking);
                    bookingContainer.addView(item);
                }

                if (!firstShown) {
                    txtCustomerName.setText("Chưa có thông tin đặt phòng.");
                    txtCheckInDate.setText(""); txtCheckOutDate.setText("");
                    txtGuests.setText(""); txtPayment.setText(""); txtPhone.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RoomDetailActivity.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillBookingView(View view, BookingModel booking) {
        TextView txtCustomerName = view.findViewById(R.id.txtCustomerName);
        TextView txtPhone = view.findViewById(R.id.txtPhone);
        TextView txtGuests = view.findViewById(R.id.txtGuests);
        TextView txtPayment = view.findViewById(R.id.txtPayment);
        TextView txtCheckIn = view.findViewById(R.id.txtCheckIn);
        TextView txtCheckOut = view.findViewById(R.id.txtCheckOut);
        TextView txtStatus = view.findViewById(R.id.txtStatus);
        LinearLayout layoutButtons = view.findViewById(R.id.layoutButtons);
        Button btnCheckIn = view.findViewById(R.id.btnCheckIn);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnCheckOut = view.findViewById(R.id.btnCheckOut);

        txtCustomerName.setText("Khách hàng: " + booking.getCustomer_name());
        txtPhone.setText("SĐT: " + booking.getCustomer_phone());
        txtGuests.setText("Khách: " + booking.getGuests());
        txtPayment.setText("Thanh toán: " + booking.getPayment());
        txtCheckIn.setText("Nhận phòng: " + booking.getCheck_in());
        txtCheckOut.setText("Trả phòng: " + booking.getCheck_out());
        txtStatus.setText("Tình trạng: " + booking.getStatus());

        layoutButtons.setVisibility(View.GONE);
        if ("Đã đặt".equals(booking.getStatus())) {
            layoutButtons.setVisibility(View.VISIBLE);
            btnCheckIn.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            btnCheckOut.setVisibility(View.GONE);

            btnCheckIn.setOnClickListener(v -> updateStatus(booking, "Đã ở"));
            btnCancel.setOnClickListener(v -> updateStatus(booking, "Đã huỷ đặt"));
        } else if ("Đã ở".equals(booking.getStatus())) {
            layoutButtons.setVisibility(View.VISIBLE);
            btnCheckIn.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            btnCheckOut.setVisibility(View.VISIBLE);

            btnCheckOut.setOnClickListener(v -> updateStatus(booking, "Đã trả phòng"));
        }
    }

    private void updateStatus(BookingModel booking, String newStatus) {
        String bookingId = booking.getBookingId();
        String roomKey = booking.getRoomNumber();
        if (roomKey == null || bookingId == null) {
            Toast.makeText(this, "Thiếu thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase db = FirebaseDatabase.getInstance("https://btlon-941fd-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference bookingRef = db.getReference("bookings").child(roomKey).child(bookingId).child("status");
        DatabaseReference roomRef = db.getReference("rooms").child(roomKey).child("status");

        bookingRef.setValue(newStatus);
        String roomStatus;
        switch (newStatus) {
            case "Đã ở":
                roomStatus = "in-use";
                break;
            case "Đã huỷ đặt":
            case "Đã trả phòng":
                roomStatus = "available";
                break;
            default:
                roomStatus = "booked";
                break;
        }


        roomRef.setValue(roomStatus).addOnSuccessListener(unused -> {
            Toast.makeText(this, "Đã cập nhật trạng thái", Toast.LENGTH_SHORT).show();
            loadBookings(); // reload
        });
    }
}
