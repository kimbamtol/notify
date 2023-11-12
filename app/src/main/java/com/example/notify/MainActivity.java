package com.example.notify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.app.PendingIntent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.graphics.Color;
import android.os.Build;
import android.app.Notification;
import android.app.NotificationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText ssidEditText;
    private BroadcastReceiver wifiReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ssidEditText = findViewById(R.id.ssidEditText);
        Button addButton = findViewById(R.id.addButton);
        Button checkButton = findViewById(R.id.checkButton);

        wifiReceiver = new WifiReceiver();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));

        addButton.setOnClickListener(v -> {
            String ssid = ssidEditText.getText().toString();
            if (!ssid.isEmpty()) {
                // 여기서 ssid를 저장하고 UI에 표시
                // 저장 및 UI 업데이트 코드는 나중에 추가
                Toast.makeText(this, "SSID 등록 완료", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SSID를 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        checkButton.setOnClickListener(v -> checkWifiStatus());
    }

    private void checkWifiStatus() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String connectedSSID = wifiInfo.getSSID().replace("\"", "");

        if (!connectedSSID.isEmpty()) {
            Toast.makeText(this, "현재 Wi-Fi SSID: " + connectedSSID, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "현재 Wi-Fi에 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtils.createNotificationChannel(this);
            builder = new Notification.Builder(this, NotificationUtils.CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        builder.setContentTitle("WiFi 연결 알림")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setLights(Color.RED, 3000, 3000);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiReceiver);
    }
}
