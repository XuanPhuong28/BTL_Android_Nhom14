package vn.tlu.edu.phungxuanpphuong.btl.cn2;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vn.tlu.edu.phungxuanpphuong.btl.R;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class RoomListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RoomBookingAdapter adapter;
    private List<RoomModel> roomList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        roomList = new ArrayList<>();
        adapter = new RoomBookingAdapter(roomList, this, room -> {
            // handle click
        });
        recyclerView.setAdapter(adapter);

        DatabaseReference ref = FirebaseDatabase.getInstance("https://btlon-941fd-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("rooms");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomList.clear();
                for (DataSnapshot roomSnap : snapshot.getChildren()) {
                    RoomModel room = roomSnap.getValue(RoomModel.class);
                    if (room != null) {
                        roomList.add(room);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RoomListActivity", "Database error: " + error.getMessage());
            }
        });
    }
}
