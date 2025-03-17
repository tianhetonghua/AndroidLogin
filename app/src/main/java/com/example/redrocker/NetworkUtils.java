package com.example.redrocker;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

    /**
     * 向指定URL发送用户名，返回服务器的响应
     *
     * @param username 用户名
     * @param urlString 服务器URL
     * @return 验证后的用户名，若失败返回 null
     */
    public static String postUsername(String username, String urlString) {
        HttpURLConnection connection = null;
        try {
            // 创建连接
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            // 构建JSON请求体
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", username);

            // 写入请求体
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonBody.toString().getBytes());
            outputStream.flush();
            outputStream.close();

            // 获取响应
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // 解析JSON响应
                JSONObject jsonResponse = new JSONObject(response.toString());
                if ("success".equals(jsonResponse.getString("status"))) {
                    return jsonResponse.getString("username"); // 返回验证成功的用户名
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null; // 验证失败返回 null
    }
}
