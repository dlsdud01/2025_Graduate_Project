package com.example.mypet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityActivity extends AppCompatActivity {

    // 체크박스 선언
    CheckBox checkEat, checkWater, checkMove, checkRest;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity);

        // SharedPreferences 초기화 (MODE_PRIVATE: 앱 내에서만 사용 가능)
        prefs = getSharedPreferences("checkboxPrefs", MODE_PRIVATE);

        // 체크박스 연결
        checkEat = findViewById(R.id.eat_check);
        checkWater = findViewById(R.id.water_check);
        checkMove = findViewById(R.id.move_check);
        checkRest = findViewById(R.id.rest_check);

        // 체크박스 상태 복원
        checkEat.setChecked(prefs.getBoolean("eat", false));
        checkWater.setChecked(prefs.getBoolean("water", false));
        checkMove.setChecked(prefs.getBoolean("move", false));
        checkRest.setChecked(prefs.getBoolean("rest", false));

        // 체크할 때마다 값 저장
        checkEat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("eat", isChecked).apply();
        });

        checkWater.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("water", isChecked).apply();
        });

        checkMove.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("move", isChecked).apply();
        });

        checkRest.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("rest", isChecked).apply();
        });
    }
}
