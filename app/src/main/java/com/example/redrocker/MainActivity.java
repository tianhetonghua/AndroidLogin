package com.example.redrocker;

import android.content.Intent;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final String SERVER_URL = "http://192.168.130.211:5000/login"; // Flask服务器的URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText usernameInput = findViewById(R.id.username);
        EditText passwordInput = findViewById(R.id.password);
        Button onlineButton = findViewById(R.id.online_button);
        Button offlineButton = findViewById(R.id.offline_button);

        // 在线验证按钮逻辑
        onlineButton.setOnClickListener(v -> {
            String inputUsername = usernameInput.getText().toString(); // 获取输入的用户名
            String inputPassword = passwordInput.getText().toString(); // 获取输入的密码

            if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
                Toast.makeText(MainActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "Input Username: " + inputUsername + ", Input Password: " + inputPassword);

            // 显示加载对话框
            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("正在进行在线验证...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // 在线验证逻辑
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    // 在线验证
                    String verifiedUsername = NetworkUtils.postUsername(inputUsername, SERVER_URL);
                    if (verifiedUsername == null) {
                        // 在线验证失败
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "在线验证失败，请检查网络或用户名是否正确！", Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }
                    Log.d(TAG, "Verified Username from Server: " + verifiedUsername);

                    // 离线验证逻辑
                    JniBridge jniBridge = new JniBridge();
                    boolean isOfflineVerified = jniBridge.verifyLogin(verifiedUsername, inputPassword);

                    // 更新UI
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        if (isOfflineVerified) {
                            Toast.makeText(MainActivity.this, "欢迎你，freshman！", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "离线验证失败，请检查密码是否正确！", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "在线验证异常：" + e.getMessage());
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "验证出现异常，请稍后重试！", Toast.LENGTH_SHORT).show();
                    });
                } finally {
                    executor.shutdown();
                }
            });
        });

        // 离线验证按钮逻辑
        offlineButton.setOnClickListener(v -> {
            String inputUsername = usernameInput.getText().toString(); // 获取输入的用户名
            String inputPassword = passwordInput.getText().toString(); // 获取输入的密码

            if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
                Toast.makeText(MainActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "Input Username: " + inputUsername + ", Input Password: " + inputPassword);

            // 显示加载对话框
            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("正在进行离线验证...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // 离线验证逻辑
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    JniBridge jniBridge = new JniBridge();
                    boolean isOfflineVerified = jniBridge.verifyLogin(inputUsername, inputPassword);

                    // 更新UI
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        if (isOfflineVerified) {
                            Toast.makeText(MainActivity.this, "欢迎你，freshman！", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "离线验证失败，请检查用户名或密码是否正确！", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "离线验证异常：" + e.getMessage());
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "验证出现异常，请稍后重试！", Toast.LENGTH_SHORT).show();
                    });
                } finally {
                    executor.shutdown();
                }
            });
        });
    }
}
