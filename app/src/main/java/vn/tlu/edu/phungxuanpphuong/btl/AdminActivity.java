package vn.tlu.edu.phungxuanpphuong.btl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import vn.tlu.edu.phungxuanpphuong.btl.cn2.RoomListActivity;
import vn.tlu.edu.phungxuanpphuong.btl.cn2.RoomManageActivity;

public class AdminActivity extends AppCompatActivity {

    private Button btnPhong;
    private Button btnQly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btnPhong = findViewById(R.id.btnPhong);
        btnQly = findViewById(R.id.btnQly);
        btnPhong.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, RoomListActivity.class));
        });
        btnQly.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, RoomManageActivity.class));
        });
    }
}
