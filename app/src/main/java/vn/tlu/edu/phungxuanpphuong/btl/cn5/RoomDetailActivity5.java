package vn.tlu.edu.phungxuanpphuong.btl.cn5;

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
import com.google.gson.Gson;

import vn.tlu.edu.phungxuanpphuong.btl.R;
import vn.tlu.edu.phungxuanpphuong.btl.cn2.RoomModel;

public class RoomDetailActivity5 extends AppCompatActivity {
    private ImageView imgRoom;
    private TextView txtRoomNumber, txtType, txtPrice, txtStatus, txtDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail_5);

        imgRoom = findViewById(R.id.imgRoom);
        txtRoomNumber = findViewById(R.id.txtRoomNumber);
        txtType = findViewById(R.id.txtType);
        txtPrice = findViewById(R.id.txtPrice);
        txtStatus = findViewById(R.id.txtStatus);
        txtDesc = findViewById(R.id.txtDesc);

        // Nhận dữ liệu từ Intent
        RoomModel room = (RoomModel) getIntent().getSerializableExtra("room");

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
        }

        boolean fromManager = getIntent().getBooleanExtra("fromManager", false);
        LinearLayout manageButtons = findViewById(R.id.manageButtons);
        Button btnEdit = findViewById(R.id.btnEdit);
        Button btnDelete = findViewById(R.id.btnDelete);

        if (fromManager) {
            manageButtons.setVisibility(View.VISIBLE);
        }

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(vn.tlu.edu.phungxuanpphuong.btl.cn5.RoomDetailActivity5.this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc muốn xóa phòng này?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        // Thực hiện xóa nếu người dùng đồng ý
                        String roomId = room.getRoomId();

                        DatabaseReference ref = FirebaseDatabase
                                .getInstance("https://btlon-941fd-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("rooms");

                        ref.child(roomId).removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(vn.tlu.edu.phungxuanpphuong.btl.cn5.RoomDetailActivity5.this, "Đã xóa phòng", Toast.LENGTH_SHORT).show();
                                finish(); // Quay lại màn trước
                            } else {
                                Toast.makeText(vn.tlu.edu.phungxuanpphuong.btl.cn5.RoomDetailActivity5.this, "Lỗi khi xóa", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                    .show();
        });
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(vn.tlu.edu.phungxuanpphuong.btl.cn5.RoomDetailActivity5.this, AddRoomActivity.class);
            intent.putExtra("roomModel", room); // Serializable
            intent.putExtra("isEditing", true);
            startActivity(intent);
        });
    }
}

