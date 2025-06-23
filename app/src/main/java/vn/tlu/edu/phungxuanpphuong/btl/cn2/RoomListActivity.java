package vn.tlu.edu.phungxuanpphuong.btl.cn2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

import vn.tlu.edu.phungxuanpphuong.btl.R;

public class RoomListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RoomBookingAdapter adapter;
    private List<RoomModel> roomList = new ArrayList<>();
    private List<RoomModel> originalRoomList = new ArrayList<>();
    private Spinner spinnerType, spinnerStatus;
    private Button btnApply;

    private ActivityResultLauncher<Intent> roomDetailLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        // Ánh xạ view
        recyclerView = findViewById(R.id.recyclerView);
        spinnerType = findViewById(R.id.spinnerType);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnApply = findViewById(R.id.btnApply);
        ImageView btnBack = findViewById(R.id.btnBack);

        // Quay lại
        btnBack.setOnClickListener(v -> finish());

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RoomBookingAdapter(roomList, this, room -> {
            Intent intent = new Intent(RoomListActivity.this, RoomDetailActivity.class);
            intent.putExtra("room", room);
            roomDetailLauncher.launch(intent); // sử dụng launcher
        });
        recyclerView.setAdapter(adapter);

        // Khởi tạo launcher để nhận kết quả
        roomDetailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        fetchRoomsFromFirebase(); // cập nhật khi quay về
                    }
                });

        // Spinner loại phòng
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                this, R.array.room_types, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        // Spinner trạng thái phòng
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                this, R.array.room_statuses, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        btnApply.setOnClickListener(v -> applyFilter());

        // Load danh sách phòng
        fetchRoomsFromFirebase();
    }

    private void fetchRoomsFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://btlon-941fd-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("rooms");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                originalRoomList.clear();
                for (DataSnapshot roomSnap : snapshot.getChildren()) {
                    RoomModel room = roomSnap.getValue(RoomModel.class);
                    if (room != null) {
                        originalRoomList.add(room);
                    }
                }
                applyFilter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RoomListActivity", "Firebase error: " + error.getMessage());
            }
        });
    }

    private void applyFilter() {
        String selectedType = spinnerType.getSelectedItem().toString();
        String selectedStatus = spinnerStatus.getSelectedItem().toString();

        List<RoomModel> filtered = new ArrayList<>();
        for (RoomModel room : originalRoomList) {
            boolean matchType = selectedType.equals("Tất cả") || room.getType().equals(selectedType);
            boolean matchStatus = selectedStatus.equals("Tất cả") || room.getStatus().equals(selectedStatus);
            if (matchType && matchStatus) {
                filtered.add(room);
            }
        }

        roomList.clear();
        roomList.addAll(filtered);
        adapter.notifyDataSetChanged();
    }
}
