package vn.tlu.edu.phungxuanpphuong.btl.cn2;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import vn.tlu.edu.phungxuanpphuong.btl.R;
import vn.tlu.edu.phungxuanpphuong.btl.cn1.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.*;

public class BookingFormActivity extends AppCompatActivity {

    private ImageView imgRoomBanner, btnBack;
    private TextView txtRoomTitle, txtRoomBeds, txtRoomPrice, tvTotal;
    private EditText etFullName, etPhone, etGuests, etCheckIn, etCheckOut;
    private Spinner spinnerPayment;
    private Button btnSubmit;

    private RoomModel room;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private Calendar checkInCal, checkOutCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_form);

        ImageView btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); // Nếu dùng Firebase Auth
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Ánh xạ View
        imgRoomBanner = findViewById(R.id.imgRoomBanner);
        btnBack = findViewById(R.id.btnBack);
        txtRoomTitle = findViewById(R.id.txtRoomTitle);
        txtRoomBeds = findViewById(R.id.txtRoomBeds);
        txtRoomPrice = findViewById(R.id.txtRoomPrice);
        tvTotal = findViewById(R.id.tvTotal);
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etGuests = findViewById(R.id.etGuests);
        etCheckIn = findViewById(R.id.etCheckIn);
        etCheckOut = findViewById(R.id.etCheckOut);
        spinnerPayment = findViewById(R.id.spinnerPayment);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Nút quay lại
        btnBack.setOnClickListener(v -> onBackPressed());


        // Nhận thông tin phòng
        room = (RoomModel) getIntent().getSerializableExtra("room");
        if (room != null) {
            txtRoomTitle.setText("PHÒNG " + room.getRoomNumber());
            txtRoomBeds.setText("Giường: " + room.getBeds());
            txtRoomPrice.setText("Giá: " + formatCurrency(room.getPrice()) + " / ngày");

            Glide.with(this)
                    .load(room.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imgRoomBanner);
        }

        // Spinner thanh toán
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Thanh toán tại chỗ nghỉ", "Chuyển khoản"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPayment.setAdapter(adapter);

        checkInCal = Calendar.getInstance();
        checkOutCal = Calendar.getInstance();

        // Chọn ngày
        etCheckIn.setOnClickListener(v -> showDate(etCheckIn, checkInCal));
        etCheckOut.setOnClickListener(v -> showDate(etCheckOut, checkOutCal));

        // Nút đặt phòng
        btnSubmit.setOnClickListener(v -> submitBooking());
    }

    private void showDate(EditText field, Calendar cal) {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            cal.set(year, month, dayOfMonth);
            field.setText(sdf.format(cal.getTime()));
            updateTotal();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateTotal() {
        try {
            Date from = sdf.parse(etCheckIn.getText().toString());
            Date to = sdf.parse(etCheckOut.getText().toString());
            long diff = to.getTime() - from.getTime();
            if (diff >= 0) {
                long days = Math.max(diff / (1000 * 60 * 60 * 24), 1); // Luôn >= 1 ngày
                int total = (int) (days * room.getPrice());
                tvTotal.setText("Tổng tiền: " + formatCurrency(total));
            } else {
                tvTotal.setText("Tổng tiền: 0đ");
            }
        } catch (Exception e) {
            tvTotal.setText("Tổng tiền: 0đ");
        }
    }

    private void submitBooking() {
        String name = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String guests = etGuests.getText().toString().trim();
        String checkIn = etCheckIn.getText().toString().trim();
        String checkOut = etCheckOut.getText().toString().trim();
        String payment = spinnerPayment.getSelectedItem().toString();

        if (name.isEmpty() || phone.isEmpty() || guests.isEmpty() || checkIn.isEmpty() || checkOut.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance("https://btlon-941fd-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("bookings").child(room.getRoomNumber());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Date newCheckIn = sdf.parse(checkIn);
                    Date newCheckOut = sdf.parse(checkOut);
                    boolean conflict = false;

                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String ciStr = String.valueOf(snap.child("check_in").getValue());
                        String coStr = String.valueOf(snap.child("check_out").getValue());

                        if (ciStr == null || coStr == null || ciStr.isEmpty() || coStr.isEmpty()) continue;

                        Date ci = sdf.parse(ciStr);
                        Date co = sdf.parse(coStr);

                        // Kiểm tra trùng thời gian
                        if (!(newCheckOut.before(ci) || newCheckIn.after(co))) {
                            conflict = true;
                            break;
                        }
                    }

                    if (conflict) {
                        Toast.makeText(BookingFormActivity.this, "Phòng đã được đặt trong khoảng thời gian này!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Nếu không trùng lịch
                    Map<String, Object> booking = new HashMap<>();
                    booking.put("customer_name", name);
                    booking.put("customer_phone", phone);
                    booking.put("guests", guests);
                    booking.put("check_in", checkIn);
                    booking.put("check_out", checkOut);
                    booking.put("payment", payment);
                    booking.put("room_type", room.getType());
                    booking.put("beds", room.getBeds());
                    booking.put("price_per_day", room.getPrice());
                    booking.put("status", "Đã đặt");

                    String key = ref.push().getKey();
                    ref.child(key).setValue(booking).addOnSuccessListener(unused -> {
                        startActivity(new Intent(BookingFormActivity.this, BookingSuccessActivity.class));
                        finish();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(BookingFormActivity.this, "Lỗi đặt phòng", Toast.LENGTH_SHORT).show();
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(BookingFormActivity.this, "Lỗi xử lý ngày tháng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookingFormActivity.this, "Không thể kiểm tra lịch phòng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatCurrency(int price) {
        return String.format(Locale.getDefault(), "%,d", price).replace(",", ".") + "đ";
    }
}
