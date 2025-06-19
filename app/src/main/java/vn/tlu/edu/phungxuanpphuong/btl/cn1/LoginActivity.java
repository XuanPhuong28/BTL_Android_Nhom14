package vn.tlu.edu.phungxuanpphuong.btl.cn1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import vn.tlu.edu.phungxuanpphuong.btl.AdminActivity;
import vn.tlu.edu.phungxuanpphuong.btl.GuestActivity;
import vn.tlu.edu.phungxuanpphuong.btl.R;

public class LoginActivity extends AppCompatActivity {

    EditText usernameEdt, passwordEdt;
    Button loginBtn;

    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEdt = findViewById(R.id.usernameEdt); // Nhập email
        passwordEdt = findViewById(R.id.passwordEdt);
        loginBtn = findViewById(R.id.loginBtn);

        usersRef = FirebaseDatabase.getInstance("https://btlon-941fd-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users");

        loginBtn.setOnClickListener(v -> login());
    }

    private void login() {
        String inputEmail = usernameEdt.getText().toString().trim();
        String inputPassword = passwordEdt.getText().toString().trim();

        if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String email = userSnap.child("email").getValue(String.class);
                    String password = userSnap.child("password").getValue(String.class);
                    String role = userSnap.child("role").getValue(String.class);

                    if (email != null && password != null &&
                            email.equalsIgnoreCase(inputEmail) && password.equals(inputPassword)) {
                        found = true;
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                        if (role.equalsIgnoreCase("admin")) {
                            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                            intent.putExtra("users", email);
                            startActivity(intent);
                        } else if (role.equalsIgnoreCase("guest")) {
                            Intent intent = new Intent(LoginActivity.this, GuestActivity.class);
                            intent.putExtra("users", email);
                            startActivity(intent);
                        }

                        finish();
                        break;
                    }
                }

                if (!found) {
                    Toast.makeText(LoginActivity.this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

