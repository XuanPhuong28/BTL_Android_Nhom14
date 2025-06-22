package vn.tlu.edu.phungxuanpphuong.btl.cn2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import vn.tlu.edu.phungxuanpphuong.btl.R;
import vn.tlu.edu.phungxuanpphuong.btl.cn1.LoginActivity;

public class GuestRoomDetailActivity extends AppCompatActivity {

    private ImageView imgRoom;
    private TextView txtRoomNumber, txtType, txtPrice, txtBeds, txtDesc;
    private Button btnBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_room_detail);

        ImageView btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); // Nếu dùng Firebase Auth
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        imgRoom = findViewById(R.id.imgRoom);
        txtRoomNumber = findViewById(R.id.txtRoomNumber);
        txtType = findViewById(R.id.txtType);
        txtPrice = findViewById(R.id.txtPrice);
        txtBeds = findViewById(R.id.txtBeds);
        txtDesc = findViewById(R.id.txtDesc);
        btnBook = findViewById(R.id.btnBook);

        RoomModel room = (RoomModel) getIntent().getSerializableExtra("room");

        if (room != null) {
            txtRoomNumber.setText("PHÒNG " + room.getRoomNumber());
            txtType.setText("Loại: " + room.getType());
            txtPrice.setText("Giá: " + formatCurrency(room.getPrice()) + " / ngày");
            txtBeds.setText("Giường: " + room.getBeds());
            txtDesc.setText("Mô tả: " + room.getDescription());

            Glide.with(this)
                    .load(room.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imgRoom);

            btnBook.setOnClickListener(v -> {
                Intent intent = new Intent(GuestRoomDetailActivity.this, BookingFormActivity.class);
                intent.putExtra("room", room);
                startActivity(intent);
            });
        } else {
            Toast.makeText(this, "Không có dữ liệu phòng", Toast.LENGTH_SHORT).show();
            finish();
        }

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }
    }

    private String formatCurrency(int price) {
        return String.format("%,d", price).replace(",", ".") + "đ";
    }
}
