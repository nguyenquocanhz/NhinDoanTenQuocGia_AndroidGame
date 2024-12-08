package com.notes.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private final Button[] answerButtons = new Button[4];
    private final List<Country> countries = new ArrayList<>();
    private String correctAnswer;
    private ImageView flagImageView;
    private int score = 0;
    int MaxScore = 500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        flagImageView = findViewById(R.id.imageView2);
        InitBtn();
        initDataFromJson();
        Button nextBtn = findViewById(R.id.nextbtn);
        nextBtn.setOnClickListener(v -> {
            InitBtn();
            initDataFromJson();
        });
    }
    // Cấu hình các button
    private void InitBtn() {
        answerButtons[0] = findViewById(R.id.button8);
        answerButtons[1] = findViewById(R.id.button9);
        answerButtons[2] = findViewById(R.id.button10);
        answerButtons[3] = findViewById(R.id.button11);
        // Thiết lập sự kiện cho các nút trả lời
        for (Button button : answerButtons) {
            button.setOnClickListener(v -> checkAnswer(button.getText().toString()));
        }
    }
    // Xử lý việc đọc dữ liệu sau khi load file tạo câu hỏi.
    private void initDataFromJson() {
        try {
            String json = loadJSONFromAsset(this);
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String countryName = jsonObject.getString("country");
                String flagUrl = jsonObject.getString("flag_url");
                countries.add(new Country(countryName, flagUrl));
            }

            // Random chọn một quốc gia từ danh sách
            Random random = new Random();
            Country randomCountry = countries.get(random.nextInt(countries.size()));
            correctAnswer = randomCountry.getName().toUpperCase();

            // Xóa quốc gia đã chọn khỏi danh sách để tránh trùng lặp
            countries.remove(randomCountry);

            // Tạo danh sách chứa 4 đáp án, bao gồm đáp án đúng và 3 đáp án sai
            List<String> answerOptions = new ArrayList<>();
            answerOptions.add(correctAnswer);
            for (int i = 0; i < 3; i++) {
                Country randomWrongCountry = generateRandomCountry();
                if (randomWrongCountry != null) {
                    answerOptions.add(randomWrongCountry.getName().toUpperCase());
                }
            }

            // Sắp xếp ngẫu nhiên các đáp án
            Collections.shuffle(answerOptions);

            // Hiển thị các đáp án trên các nút
            for (int i = 0; i < answerButtons.length; i++) {
                answerButtons[i].setText(answerOptions.get(i));
            }

            // Hiển thị cờ
            Picasso.get().load(randomCountry.getFlagUrl()).into(flagImageView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //  Load dữ liệu về lá cớ và tên các quốc gia từ file data.json
    // path : /assets/data.json
    private String loadJSONFromAsset(Context context) {
        String json;
        try {
            InputStream inputStream = context.getAssets().open("data.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    // Đoạn mã xử lý lấy ngẫu nhiên các quốc gia
    private Country generateRandomCountry() {
        if (!countries.isEmpty()) {
            Random random = new Random();
            return countries.get(random.nextInt(countries.size()));
        } else {
            return null;
        }
    }

    // reset màn trò chơi
    private void resetGame() {
        countries.clear();
        initDataFromJson();
    }

    // Cập nhật điểm số lên TextView
    @SuppressLint("SetTextI18n")
    private void updateScoreTextView() {
        TextView scoreTextView = findViewById(R.id.scoreTextView); // Thay R.id.scoreTextView bằng ID thực tế của TextView trong layout của bạn
        scoreTextView.setText("Điểm: " + score);
    }
    // Thông báo và hỏi người chơi tiếp hay không
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bạn đã chiến thắng Với sô điểm là " + score);
        builder.setMessage("Bạn có muốn chơi tiếp không?");
        builder.setPositiveButton("Chơi tiếp", (dialog, which) -> {
            score = 0;
            updateScoreTextView(); // Cập nhật điểm số trước khi thông báo trả lời sai
            resetGame();
            dialog.dismiss(); // Đóng dialog
        });
        builder.setNegativeButton("Thoát game", (dialog, which) -> {
            // Thoát game
            finish(); // Đóng activity (thoát game)
        });
        builder.setCancelable(false); // Không cho phép bấm ra ngoài để đóng dialog
        builder.show();
    }
   // Kiểm tra đáp án khi button được nhấn
    private void checkAnswer(String selectedAnswer) {
        // Kiểm tra đáp án
        MediaPlayer mediaPlayerWin = MediaPlayer.create(this, R.raw.win);
        MediaPlayer mediaPlayerLose = MediaPlayer.create(this, R.raw.lose);

        if (selectedAnswer.equalsIgnoreCase(correctAnswer)) {
            score += 100;
            mediaPlayerWin.start();
            updateScoreTextView(); // Cập nhật điểm số trước khi thông báo trả lời sai
            Toast.makeText(this, "Chính xác! Điểm của bạn: " + score, Toast.LENGTH_SHORT).show();
            if (score == MaxScore) {
                // Hiển thị dialog xác nhận
                mediaPlayerWin.start();
                showConfirmationDialog();
                return;
            }
        } else {
            // Kiểm tra điểm của người chơi trước khi trừ điểm
            if (score == 0) {
                mediaPlayerLose.start();
                Toast.makeText(this, "Sai rồi! Đáp án đúng là " + correctAnswer, Toast.LENGTH_SHORT).show();
                resetGame(); // Nếu điểm bằng 0, reset game
                return; // Kết thúc phương thức sau khi reset game
            }
            score -= 100;
            mediaPlayerLose.start();
            updateScoreTextView(); // Cập nhật điểm số trước khi thông báo trả lời sai
            Toast.makeText(this, "Sai rồi! Đáp án đúng là " + correctAnswer, Toast.LENGTH_SHORT).show();
        }
        updateScoreTextView(); // Cập nhật điểm số trên giao diện người dùng
        resetGame(); // Thực hiện reset game sau mỗi lần trả lời
    }

    // Xác đính cấu trúc của data
    private static class Country {
        private final String name;
        private final String flagUrl;

        public Country(String name, String flagUrl) {
            this.name = name;
            this.flagUrl = flagUrl;
        }

        public String getName() {
            return name;
        }

        public String getFlagUrl() {
            return flagUrl;
        }
    }
}