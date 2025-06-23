package vn.tlu.edu.phungxuanpphuong.btl.cn2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import vn.tlu.edu.phungxuanpphuong.btl.R;

public class RoomDetailActivity extends AppCompatActivity {
    private ImageView imgRoom;
    private TextView txtRoomNumber, txtType, txtPrice, txtStatus, txtDesc;
    private LinearLayout manageButtons;
    private Button btnEdit, btnDelete;

    private RoomModel room;

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
        manageButtons = findViewById(R.id.manageButtons);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        // Nhận dữ liệu phòng
        room = (RoomModel) getIntent().getSerializableExtra("room");

        if (room != null) {
            txtRoomNumber.setText("Phòng " + room.getRoomNumber());
            txtType.setText("Loại: " + room.getType());
            txtPrice.setText("Giá: " + room.getPrice() + "đ/ngày");
            txtStatus.setText("Tình trạng: " + room.getStatus());
            txtDesc.setText("Mô tả: " + room.getDescription());

            Glide.with(this).load(room.getImageUrl()).into(imgRoom);
        } else {
            Toast.makeText(this, "Không có dữ liệu phòng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Quản lý mới được phép sửa/xoá
        boolean fromManager = getIntent().getBooleanExtra("fromManager", false);
        manageButtons.setVisibility(fromManager ? View.VISIBLE : View.GONE);

        btnDelete.setOnClickListener(v -> showDeleteDialog());
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(RoomDetailActivity.this, AddRoomActivity.class);
            intent.putExtra("room", room); // Truyền room để sửa
            intent.putExtra("isEditing", true);
            startActivity(intent);
        });
    }

    private void showDeleteDialog() {
        if (room.getRoomId() == null || room.getRoomId().isEmpty()) {
            Toast.makeText(this, "Không thể xoá phòng không có ID!", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xoá")
                .setMessage("Bạn có chắc chắn muốn xoá phòng này?")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    DatabaseReference ref = FirebaseDatabase
                            .getInstance("https://btlon-941fd-default-rtdb.asia-southeast1.firebasedatabase.app/")
                            .getReference("rooms");

                    ref.child(room.getRoomId()).removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Đã xoá phòng", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Xoá thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }
}
