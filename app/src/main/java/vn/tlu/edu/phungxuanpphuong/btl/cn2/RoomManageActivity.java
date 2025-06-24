
package vn.tlu.edu.phungxuanpphuong.btl.cn2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import vn.tlu.edu.phungxuanpphuong.btl.R;
import com.google.firebase.database.*;

import java.util.*;

public class RoomManageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RoomBookingAdapter adapter;
    private List<RoomModel> roomList;
    private Spinner spinnerType, spinnerStatus;
    private Button btnApply, buttonAddRoom;
    private List<RoomModel> originalRoomList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_room);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        roomList = new ArrayList<>();
        adapter = new RoomBookingAdapter(roomList, this, room -> {
            Intent intent = new Intent(RoomManageActivity.this, AddEditRoomActivity.class);
            intent.putExtra("isEditing", true);
            intent.putExtra("roomModel", room);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        spinnerType = findViewById(R.id.spinnerType);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnApply = findViewById(R.id.btnApply);
        buttonAddRoom = findViewById(R.id.btnAddManageRoom);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                this, R.array.room_types, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                this, R.array.room_statuses, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        btnApply.setOnClickListener(v -> applyFilter());

        buttonAddRoom.setOnClickListener(v -> {
            Intent intent = new Intent(RoomManageActivity.this, AddEditRoomActivity.class);
            intent.putExtra("isEditing", false);
            startActivity(intent);
        });

        fetchRoomsFromFirebase();
    }

    private void fetchRoomsFromFirebase() {
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://btlon-941fd-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("rooms");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                originalRoomList.clear();
                for (DataSnapshot roomSnap : snapshot.getChildren()) {
                    RoomModel room = roomSnap.getValue(RoomModel.class);
                    if (room != null) {
                        originalRoomList.add(room);
                    }
                }

                Collections.sort(originalRoomList, (o1, o2) -> {
                    try {
                        int num1 = Integer.parseInt(o1.getRoomNumber());
                        int num2 = Integer.parseInt(o2.getRoomNumber());
                        return Integer.compare(num1, num2);
                    } catch (NumberFormatException e) {
                        return o1.getRoomNumber().compareTo(o2.getRoomNumber());
                    }
                });

                applyFilter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RoomManageActivity", "Firebase error: " + error.getMessage());
            }
        });
    }

    private void applyFilter() {
        String selectedType = spinnerType.getSelectedItem().toString();
        String selectedStatus = spinnerStatus.getSelectedItem().toString();

        List<RoomModel> filtered = new ArrayList<>();
        for (RoomModel room : originalRoomList) {
            boolean matchType = selectedType.equals("Loại phòng") || room.getType().equalsIgnoreCase(selectedType);
            boolean matchStatus = selectedStatus.equals("Tình trạng") || room.getStatus().equalsIgnoreCase(selectedStatus);
            if (matchType && matchStatus) {
                filtered.add(room);
            }
        }

        roomList.clear();
        roomList.addAll(filtered);
        adapter.notifyDataSetChanged();
    }
}
