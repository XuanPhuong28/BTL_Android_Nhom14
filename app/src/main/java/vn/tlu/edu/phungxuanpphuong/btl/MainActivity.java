package vn.tlu.edu.phungxuanpphuong.btl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import vn.tlu.edu.phungxuanpphuong.btl.cn2.RoomListActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnPhong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main1);

        btnPhong = findViewById(R.id.btnPhong);

        btnPhong.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RoomListActivity.class));
        });

    }
}
