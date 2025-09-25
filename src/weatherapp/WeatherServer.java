package weatherapp;

import java.io.*;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import org.json.*;

public class WeatherServer {
    private static final String API_KEY = "db1caf75a5d6aced0988cb51e71468c1"; // Thay API key của bạn
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("🌤 Weather Server đang chạy tại cổng " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("✅ Client đã kết nối: " + clientSocket.getInetAddress());
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String city = in.readLine();
            System.out.println("📩 Yêu cầu thời tiết: " + city);

            String weatherData = getWeather(city);
            out.println(weatherData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getWeather(String city) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String url = "https://api.openweathermap.org/data/2.5/forecast?q="
                    + city + "&appid=" + API_KEY + "&units=metric&lang=vi";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());

            if (json.has("cod") && !json.getString("cod").equals("200")) {
                return new JSONObject().put("error", json.getString("message")).toString();
            }

            JSONArray list = json.getJSONArray("list");
            JSONObject current = list.getJSONObject(0);
            JSONObject main = current.getJSONObject("main");
            JSONArray weatherArr = current.getJSONArray("weather");
            JSONObject wind = current.has("wind") ? current.getJSONObject("wind") : new JSONObject();

            JSONArray forecast6 = new JSONArray();
            for (int i = 0; i < Math.min(6, list.length()); i++) {
                forecast6.put(list.getJSONObject(i));
            }

            JSONObject result = new JSONObject();
            result.put("main", main);
            result.put("weather", weatherArr);
            result.put("wind", wind);
            result.put("list", forecast6);

            return result.toString();

        } catch (Exception e) {
            System.out.println("⚠ Lỗi lấy dữ liệu: " + e.getMessage());
            return new JSONObject().put("error", "Không thể lấy dữ liệu thời tiết").toString();
        }
    }
}
