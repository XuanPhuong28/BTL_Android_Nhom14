package vn.tlu.edu.phungxuanpphuong.btl.cn2;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import vn.tlu.edu.phungxuanpphuong.btl.R;

public class BookingFormActivity extends AppCompatActivity {

    private ImageView imgRoomBanner;
    private TextView txtRoomTitle, txtRoomPrice, txtRoomBeds, tvTotal;
    private EditText etFullName, etBirthDate, etFromDate, etToDate;
    private Spinner genderSpinner;

    private static final int PRICE_PER_DAY = 450000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_form);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        // Ánh xạ View
        imgRoomBanner = findViewById(R.id.imgRoomBanner);
        txtRoomTitle = findViewById(R.id.txtRoomTitle);
        txtRoomPrice = findViewById(R.id.txtRoomPrice);
        txtRoomBeds = findViewById(R.id.txtRoomBeds);
        tvTotal = findViewById(R.id.tvTotalPrice);
        etFullName = findViewById(R.id.etFullName);
        etBirthDate = findViewById(R.id.etBirthDate);
        etFromDate = findViewById(R.id.etFromDate);
        etToDate = findViewById(R.id.etToDate);
        genderSpinner = findViewById(R.id.spinnerGender);

        // Đổ dữ liệu Spinner Giới tính
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                new String[]{"Nam", "Nữ", "Khác"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        // Nhận thông tin phòng
        RoomModel room = (RoomModel) getIntent().getSerializableExtra("room");
        if (room != null) {
            txtRoomTitle.setText("PHÒNG " + room.getRoomNumber());
            txtRoomPrice.setText("Giá: " + formatCurrency(room.getPrice()) + " / ngày");
            txtRoomBeds.setText("Giường: " + extractBeds(room.getDescription()));

            Glide.with(this)
                    .load(room.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imgRoomBanner);
        }

        // Tính tổng tiền khi chọn ngày
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Runnable calculateTotal = () -> {
            try {
                String from = etFromDate.getText().toString();
                String to = etToDate.getText().toString();

                if (!from.isEmpty() && !to.isEmpty()) {
                    Date fromDate = sdf.parse(from);
                    Date toDate = sdf.parse(to);
                    long diff = toDate.getTime() - fromDate.getTime();
                    long days = TimeUnit.MILLISECONDS.toDays(diff);
                    if (days > 0) {
                        long total = days * PRICE_PER_DAY;
                        tvTotal.setText("Tổng tiền: " + formatCurrency((int) total));
                    } else {
                        tvTotal.setText("Tổng tiền: 0đ");
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        };

        etFromDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) calculateTotal.run();
        });
        etToDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) calculateTotal.run();
        });
    }

    private String extractBeds(String desc) {
        if (desc == null) return "-";
        if (desc.contains("1 giường")) return "1";
        else if (desc.contains("2 giường")) return "2";
        else return "-";
    }

    private String formatCurrency(int price) {
        return String.format("%,d", price).replace(",", ".") + "đ";
    }
}
