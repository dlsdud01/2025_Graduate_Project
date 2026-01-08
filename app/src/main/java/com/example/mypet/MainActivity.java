package com.example.mypet;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private TextView textTemperature, textHumidity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Android 12+ 정확한 알람 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }

        // Android 13+ 알림 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        setContentView(R.layout.activity_main);

        // 푸시 알림 테스트 버튼
        Button testButton = findViewById(R.id.testButton);
        testButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                            == PackageManager.PERMISSION_GRANTED) {
                NotificationHelper.sendNotification(MainActivity.this, "🐹 긴급 알림! 햄스터가 탈출했어요!");
            } else {
                Toast.makeText(this, "알림 권한이 필요합니다!", Toast.LENGTH_SHORT).show();
            }
        });

        // 텍스트뷰 연결
        textTemperature = findViewById(R.id.textTemperature);
        textHumidity = findViewById(R.id.textHumidity);

        // 캠 카드뷰 클릭 → CamActivity 이동
        CardView camCard = findViewById(R.id.cam);
        if (camCard != null) {
            camCard.setOnClickListener(v -> {
                Toast.makeText(MainActivity.this, "카메라 이동!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, CamActivity.class));
            });
        } else {
            Log.e("MainActivity", "camCard가 null! XML ID 확인 필요");
        }

        // 활동알림 카드뷰 클릭 → ActivityActivity 이동
        CardView activityCard = findViewById(R.id.activity);
        if (activityCard != null) {
            activityCard.setOnClickListener(v -> {
                Toast.makeText(MainActivity.this, "활동알림 이동!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, ActivityActivity.class));
            });
        } else {
            Log.e("MainActivity", "activityCard가 null! XML ID 확인 필요");
        }

        // MQTT 연결 + 콜백 처리
        MqttHandler mqttHandler = new MqttHandler(
                getApplicationContext(),

                // 센서 데이터 수신 시 UI 갱신
                (temp, humid) -> runOnUiThread(() -> {
                    textTemperature.setText(String.format("%.1f°C", temp));
                    textHumidity.setText(String.format("습도: %.1f%%", humid));

                    TextView textMessage = findViewById(R.id.textView9); // 안내 메시지 텍스트

                    if (temp < 20) {
                        textMessage.setText(String.format("%.1f° 더 올려주세요 🥶", 20 - temp));
                    } else if (temp > 24) {
                        textMessage.setText(String.format("%.1f° 더 내려주세요 🥵", temp - 24));
                    } else {
                        textMessage.setText("적정 온도 😊");
                    }
                }),

                // 움직임 감지 알림 처리
                zone -> {
                    SharedPreferences prefs = getSharedPreferences("checkboxPrefs", MODE_PRIVATE);
                    boolean send = false;
                    String msg = "";

                    switch (zone.trim()) {
                        case "A":
                            send = prefs.getBoolean("water", false);
                            msg = "🐹 햄스터가 물을 마시고 있어요!";
                            break;
                        case "B":
                            send = prefs.getBoolean("eat", false);
                            msg = "🐹 햄스터가 밥을 먹고 있어요!";
                            break;
                        case "C":
                            send = prefs.getBoolean("move", false);
                            msg = "🐹 햄스터가 쳇바퀴를 돌리고 있어요!";
                            break;
                        case "D":
                            send = prefs.getBoolean("rest", false);
                            msg = "🐹 햄스터가 휴식 중이에요!";
                            break;
                    }

                    if (send) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                                        == PackageManager.PERMISSION_GRANTED) {
                            NotificationHelper.sendNotification(MainActivity.this, msg);
                        }
                    }
                }
        );
    }
}
