package vn.tlu.edu.phungxuanpphuong.btl.cn2;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import vn.tlu.edu.phungxuanpphuong.btl.R;

public class RoomDetailActivity extends AppCompatActivity {
    private ImageView imgRoom;
    private TextView txtRoomNumber, txtType, txtPrice, txtStatus, txtDesc;

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

        String roomJson = getIntent().getStringExtra("room");
        RoomModel room = new Gson().fromJson(roomJson, RoomModel.class);

        txtRoomNumber.setText("Phòng " + room.getRoomNumber());
        txtType.setText("Loại: " + room.getType());
        txtPrice.setText("Giá: " + room.getPrice() + "đ/ngày");
        txtStatus.setText("Tình trạng: " + room.getStatus());
        txtDesc.setText("Mô tả: " + room.getDescription());

        Glide.with(this).load(room.getImageUrl()).into(imgRoom);
    }
}
