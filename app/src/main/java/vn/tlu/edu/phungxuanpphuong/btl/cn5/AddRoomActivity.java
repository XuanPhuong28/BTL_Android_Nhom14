package vn.tlu.edu.phungxuanpphuong.btl.cn5;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import vn.tlu.edu.phungxuanpphuong.btl.cn5.RoomManageActivity;
import vn.tlu.edu.phungxuanpphuong.btl.cn2.RoomModel;

public class AddRoomActivity extends AppCompatActivity {

    private EditText edtRoomCode, edtPrice, edtDescription, edtBeds;
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

        // Ánh xạ view
        edtRoomCode = findViewById(R.id.edtRoomCode);
        edtPrice = findViewById(R.id.edtPrice);
        edtDescription = findViewById(R.id.edtDescription);
        edtBeds = findViewById(R.id.edtBeds);
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

        // Thiết lập dữ liệu spinner loại phòng
        String[] roomTypes = {"Thường", "VIP", "Luxury"};
        ArrayAdapter<String> roomTypeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, roomTypes);
        spinnerRoomType.setAdapter(roomTypeAdapter);

        String[] statusList = {"Available", "Unavailable"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, statusList);
        spinnerStatus.setAdapter(statusAdapter);

        // Lấy thông tin sửa phòng nếu có
        isEditing = getIntent().getBooleanExtra("isEditing", false);
        roomModel = (RoomModel) getIntent().getSerializableExtra("roomModel");

        if (isEditing && roomModel != null) {
            // Hiển thị dữ liệu phòng lên UI
            edtRoomCode.setText(roomModel.getRoomNumber());
            edtRoomCode.setEnabled(false); // Không cho sửa mã phòng khi edit
            edtPrice.setText(String.valueOf(roomModel.getPrice()));
            edtBeds.setText(String.valueOf(roomModel.getBeds()));
            edtDescription.setText(roomModel.getDescription());

            imagePath = roomModel.getImageUrl();
            String imageUriStr = roomModel.getImageUrl();

            if (imageUriStr != null && imageUriStr.startsWith("http")) {
                Glide.with(this)
                        .load(imageUriStr)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(imgRoom);
            } else if (imageUriStr != null) {
                File imgFile = new File(imageUriStr);
                if (imgFile.exists()) {
                    imgRoom.setImageURI(Uri.fromFile(imgFile));
                }
            }

            // Chọn spinner loại phòng và trạng thái đúng
            ArrayAdapter<String> adapterType = (ArrayAdapter<String>) spinnerRoomType.getAdapter();
            spinnerRoomType.setSelection(adapterType.getPosition(roomModel.getType()));

            ArrayAdapter<String> adapterStatus = (ArrayAdapter<String>) spinnerStatus.getAdapter();
            spinnerStatus.setSelection(adapterStatus.getPosition(roomModel.getStatus()));

            btnAddRoom.setText("Sửa phòng");
        }

        btnAddRoom.setOnClickListener(view -> {
            String roomCode = edtRoomCode.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();
            String bedsStr = edtBeds.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();
            String roomType = spinnerRoomType.getSelectedItem().toString();
            String status = spinnerStatus.getSelectedItem().toString();

            // Kiểm tra dữ liệu nhập
            if (roomCode.isEmpty() || priceStr.isEmpty() || bedsStr.isEmpty() || description.isEmpty()) {
                Toast.makeText(AddRoomActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int price;
            int bedCount;
            try {
                price = Integer.parseInt(priceStr);
                bedCount = Integer.parseInt(bedsStr);
                if (bedCount <= 0) {
                    Toast.makeText(AddRoomActivity.this, "Số giường phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (price <= 0) {
                    Toast.makeText(AddRoomActivity.this, "Giá phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(AddRoomActivity.this, "Vui lòng nhập số hợp lệ cho giá và số giường", Toast.LENGTH_SHORT).show();
                return;
            }

            String roomId = roomCode;

            DatabaseReference ref = FirebaseDatabase
                    .getInstance("https://btlon-941fd-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("rooms");

            RoomModel room = new RoomModel(
                    roomId,
                    roomCode,
                    roomType,
                    price,
                    status,
                    description,
                    imagePath,
                    bedCount
            );

            if (isEditing) {
                ref.child(roomId).setValue(room).addOnCompleteListener(saveTask -> {
                    if (saveTask.isSuccessful()) {
                        Toast.makeText(AddRoomActivity.this, "Sửa phòng thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddRoomActivity.this, RoomManageActivity.class));
                        finish();
                    } else {
                        Toast.makeText(AddRoomActivity.this, "Lỗi khi sửa phòng", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                ref.child(roomId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            Toast.makeText(AddRoomActivity.this, "Phòng đã tồn tại", Toast.LENGTH_SHORT).show();
                        } else {
                            ref.child(roomId).setValue(room).addOnCompleteListener(saveTask -> {
                                if (saveTask.isSuccessful()) {
                                    Toast.makeText(AddRoomActivity.this, "Đã thêm phòng thành công!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(AddRoomActivity.this, "Lỗi khi thêm phòng", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(AddRoomActivity.this, "Lỗi khi kiểm tra phòng", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                // Tạo file trong bộ nhớ app
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
                Toast.makeText(this, "Lỗi khi sao lưu ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
