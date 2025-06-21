package vn.tlu.edu.phungxuanpphuong.btl;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import vn.tlu.edu.phungxuanpphuong.btl.cn2.RoomListActivity;
import vn.tlu.edu.phungxuanpphuong.btl.cn2.RoomManageActivity;

public class AdminActivity extends AppCompatActivity {

    private Button btnPhong;
    private Button btnManageRoom;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main1);

        btnPhong = findViewById(R.id.btnPhong);
        btnManageRoom = findViewById(R.id.btnManageRoom);
        btnPhong.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, RoomListActivity.class));
        });

        btnManageRoom.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, RoomManageActivity.class));
        });
    }
}