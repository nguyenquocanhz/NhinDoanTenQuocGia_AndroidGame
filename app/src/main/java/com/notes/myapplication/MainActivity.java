package com.notes.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private final Intent i = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitBtn();
    }
    private void InitBtn() {
        Button button1 ,button2 , button3 , button4;
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);

        button1.setOnClickListener(v -> {
            i.setAction(Intent.ACTION_VIEW);
            i.setClass(getApplicationContext(),GameActivity.class);
            startActivity(i);
        });

        button2.setOnClickListener(_view -> showConfirmationDialog());

        button3.setOnClickListener(_view -> {

        });

        button4.setOnClickListener(_view -> finish());
    }
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông tin nhà phát triển game");
        builder.setMessage("Trò chơi được thiết kế và lên ý tưởng bởi:\n" +
                "Họ và tên : Nguyễn Quốc Anh\n" +
                "Mã số sinh viên : 2330130013\n"+ "Chúc mọi người một ngày tốt lành ạ !"
        );
        builder.setPositiveButton("Đã hiêu", (dialog, which) -> {
            dialog.dismiss(); // Đóng dialog
        });

        builder.setCancelable(false); // Không cho phép bấm ra ngoài để đóng dialog
        builder.show();
    }

}