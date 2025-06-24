package vn.tlu.edu.phungxuanpphuong.btl.cn2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.*;

import vn.tlu.edu.phungxuanpphuong.btl.R;

public class AddEditRoomActivity extends AppCompatActivity {

    private EditText edtRoomCode, edtPrice, edtDescription, edtBeds;
    private Spinner spinnerRoomType, spinnerStatus;
    private Button btnSaveRoom;
    private ImageView imgRoom;
    private boolean isEditing = false;
    private RoomModel roomModel;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String imagePath;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        edtRoomCode = findViewById(R.id.edtRoomCode);
        edtPrice = findViewById(R.id.edtPrice);
        edtDescription = findViewById(R.id.edtDescription);
        edtBeds = findViewById(R.id.edtBeds);
        spinnerRoomType = findViewById(R.id.spinnerRoomType);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnSaveRoom = findViewById(R.id.btnAddRoom);
        imgRoom = findViewById(R.id.imgRoom);

        imgRoom.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Chọn ảnh phòng"), PICK_IMAGE_REQUEST);
        });

        // Spinner setup
        String[] roomTypes = {"Thường", "VIP", "Luxury"};
        ArrayAdapter<String> roomTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, roomTypes);
        spinnerRoomType.setAdapter(roomTypeAdapter);

        String[] statusList = {"Available", "Unavailable", "booked"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statusList);
        spinnerStatus.setAdapter(statusAdapter);

        // Kiểm tra xem đang sửa hay thêm
        isEditing = getIntent().getBooleanExtra("isEditing", false);
        roomModel = (RoomModel) getIntent().getSerializableExtra("roomModel");

        if (isEditing && roomModel != null) {
            edtRoomCode.setText(roomModel.getRoomNumber());
            edtRoomCode.setEnabled(false);
            edtPrice.setText(String.valueOf(roomModel.getPrice()));
            edtBeds.setText(String.valueOf(roomModel.getBeds()));
            edtDescription.setText(roomModel.getDescription());

            imagePath = roomModel.getImageUrl();
            if (imagePath != null && imagePath.startsWith("http")) {
                Glide.with(this).load(imagePath).placeholder(R.drawable.ic_launcher_background).into(imgRoom);
            } else if (imagePath != null) {
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    imgRoom.setImageURI(Uri.fromFile(imgFile));
                }
            }

            spinnerRoomType.setSelection(roomTypeAdapter.getPosition(roomModel.getType()));
            spinnerStatus.setSelection(statusAdapter.getPosition(roomModel.getStatus()));

            btnSaveRoom.setText("Sửa phòng");
        } else {
            btnSaveRoom.setText("Thêm phòng");
        }

        btnSaveRoom.setOnClickListener(view -> saveRoom());
    }

    private void saveRoom() {
        String roomCode = edtRoomCode.getText().toString().trim();
        String priceStr = edtPrice.getText().toString().trim();
        String bedsStr = edtBeds.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String roomType = spinnerRoomType.getSelectedItem().toString();
        String status = spinnerStatus.getSelectedItem().toString();

        if (roomCode.isEmpty() || priceStr.isEmpty() || bedsStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        int price, bedCount;
        try {
            price = Integer.parseInt(priceStr);
            bedCount = Integer.parseInt(bedsStr);
            if (price <= 0 || bedCount <= 0) {
                Toast.makeText(this, "Giá và số giường phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá và số giường phải là số", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance("https://btlon-941fd-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("rooms");

        if (isEditing && roomModel != null) {
            String roomId = roomModel.getRoomId();

            roomModel.setRoomNumber(roomCode);
            roomModel.setType(roomType);
            roomModel.setPrice(price);
            roomModel.setBeds(bedCount);
            roomModel.setDescription(description);
            roomModel.setStatus(status);
            roomModel.setImageUrl(imagePath);

            ref.child(roomId).setValue(roomModel)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Cập nhật phòng thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Lỗi khi cập nhật phòng", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            String roomId = ref.push().getKey();
            RoomModel newRoom = new RoomModel(roomId, roomCode, roomType, price, status, description, imagePath, bedCount);
            ref.child(roomId).setValue(newRoom)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Thêm phòng thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Lỗi khi thêm phòng", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                File directory = getFilesDir();
                String fileName = "room_" + System.currentTimeMillis() + ".jpg";
                File destFile = new File(directory, fileName);

                InputStream in = getContentResolver().openInputStream(imageUri);
                OutputStream out = new FileOutputStream(destFile);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }

                in.close();
                out.close();

                imageUri = Uri.fromFile(destFile);
                imgRoom.setImageURI(imageUri);
                this.imagePath = destFile.getAbsolutePath();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi khi lưu ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
