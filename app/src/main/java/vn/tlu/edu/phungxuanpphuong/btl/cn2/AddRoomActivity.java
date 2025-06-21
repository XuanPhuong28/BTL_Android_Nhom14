package vn.tlu.edu.phungxuanpphuong.btl.cn2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import vn.tlu.edu.phungxuanpphuong.btl.R;


public class AddRoomActivity extends AppCompatActivity {

    private EditText edtRoomCode, edtPrice, edtDescription;
    private Spinner spinnerRoomType, spinnerStatus;
    private Button btnAddRoom;
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

        // Ánh xạ các View trong layout
        edtRoomCode = findViewById(R.id.edtRoomCode);
        edtPrice = findViewById(R.id.edtPrice);
        edtDescription = findViewById(R.id.edtDescription);
        spinnerRoomType = findViewById(R.id.spinnerRoomType);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnAddRoom = findViewById(R.id.btnAddRoom);
        imgRoom = findViewById(R.id.imgRoom);


        imgRoom.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Chọn ảnh phòng"), PICK_IMAGE_REQUEST);
        });


        String[] roomTypes = {"Thường", "VIP", "Luxury"};
        ArrayAdapter<String> roomTypeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, roomTypes);
        spinnerRoomType.setAdapter(roomTypeAdapter);


        String[] statusList = {"Available", "Unavailable"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, statusList);
        spinnerStatus.setAdapter(statusAdapter);

        isEditing = getIntent().getBooleanExtra("isEditing", false);


        if (isEditing) {
            roomModel = (RoomModel) getIntent().getSerializableExtra("roomModel");

            if (roomModel != null) {
                edtRoomCode.setText(roomModel.getRoomNumber());
                edtPrice.setText(String.valueOf(roomModel.getPrice()));
                edtDescription.setText(roomModel.getDescription());
                imagePath = roomModel.getImageUrl();

                if (imagePath.startsWith("http")) {
                    Glide.with(this)
                            .load(imagePath)
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(imgRoom);
                } else {
                    File imgFile = new File(imagePath);
                    if (imgFile.exists()) {
                        imgRoom.setImageURI(Uri.fromFile(imgFile));
                    }
                }

                spinnerRoomType.setSelection(((ArrayAdapter) spinnerRoomType.getAdapter())
                        .getPosition(roomModel.getType()));
                spinnerStatus.setSelection(((ArrayAdapter) spinnerStatus.getAdapter())
                        .getPosition(roomModel.getStatus()));

                btnAddRoom.setText("Sửa phòng");
                edtRoomCode.setEnabled(false); // Không cho sửa mã phòng
            }
        }

        // Xử lý khi bấm nút thêm hoặc sửa
        btnAddRoom.setOnClickListener(view -> {
            String roomCode = edtRoomCode.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();
            String roomType = spinnerRoomType.getSelectedItem().toString();
            String status = spinnerStatus.getSelectedItem().toString();

            if (roomCode.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            String roomId = "room" + roomCode;

            DatabaseReference ref = FirebaseDatabase
                    .getInstance("https://btlon-941fd-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("rooms");

            RoomModel room = new RoomModel(
                    roomId, roomCode, roomType, (int) price, status, description, imagePath
            );

            if (isEditing) {
                ref.child(roomId).setValue(room).addOnCompleteListener(saveTask -> {
                    if (saveTask.isSuccessful()) {
                        Toast.makeText(this, "Sửa phòng thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, RoomManageActivity.class));
                    } else {
                        Toast.makeText(this, "Lỗi khi sửa phòng", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                ref.child(roomId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            Toast.makeText(this, "Phòng đã tồn tại", Toast.LENGTH_SHORT).show();
                        } else {
                            ref.child(roomId).setValue(room).addOnCompleteListener(saveTask -> {
                                if (saveTask.isSuccessful()) {
                                    Toast.makeText(this, "Đã thêm phòng thành công!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(this, "Lỗi khi thêm phòng", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(this, "Lỗi khi kiểm tra phòng", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // 📌 Xử lý ảnh đã chọn
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
                imagePath = destFile.getAbsolutePath();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi khi sao lưu ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


