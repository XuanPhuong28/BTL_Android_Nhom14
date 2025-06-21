package vn.tlu.edu.phungxuanpphuong.btl.cn2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import vn.tlu.edu.phungxuanpphuong.btl.R;

public class GuestRoomDetailActivity extends AppCompatActivity {

    private ImageView imgRoom;
    private TextView txtRoomNumber, txtType, txtPrice, txtBeds;
    private Button btnBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_room_detail); // Tên layout XML

        imgRoom = findViewById(R.id.imgRoom);
        txtRoomNumber = findViewById(R.id.txtRoomNumber);
        txtType = findViewById(R.id.txtType);
        txtPrice = findViewById(R.id.txtPrice);
        txtBeds = findViewById(R.id.txtBeds);
        btnBook = findViewById(R.id.btnBook);

        // Nhận dữ liệu phòng
        RoomModel room = (RoomModel) getIntent().getSerializableExtra("room");

        if (room != null) {
            txtRoomNumber.setText("PHÒNG " + room.getRoomNumber());
            txtType.setText("Loại: " + room.getType());
            txtPrice.setText("Giá: " + formatCurrency(room.getPrice()) + " / ngày");
            txtBeds.setText("Giường: " + extractBeds(room.getDescription()));

            Glide.with(this)
                    .load(room.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imgRoom);

        } else {
            Toast.makeText(this, "Không có dữ liệu phòng", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnBook.setOnClickListener(v -> {
            Toast.makeText(this, "Chuyển sang đặt phòng...", Toast.LENGTH_SHORT).show();
            // TODO: Chuyển đến màn đặt phòng
        });
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private String extractBeds(String desc) {
        if (desc.contains("1 giường")) return "1";
        else if (desc.contains("2 giường")) return "2";
        else return "-";
    }

    private String formatCurrency(int price) {
        return String.format("%,d", price).replace(",", ".") + "đ";
    }
}
