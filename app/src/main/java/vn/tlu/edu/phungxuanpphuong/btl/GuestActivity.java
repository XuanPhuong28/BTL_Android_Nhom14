package vn.tlu.edu.phungxuanpphuong.btl;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GuestActivity extends AppCompatActivity {

    LinearLayout roomContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest); // Đảm bảo bạn có file này

        roomContainer = findViewById(R.id.roomContainer); // ID này nằm trong activity_guest.xml

    }


}

