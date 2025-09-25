package weatherapp;

import org.json.*;
import java.io.*;
import java.net.*;

public class WeatherService {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    public WeatherData getWeather(String city) {
        try(Socket socket = new Socket(HOST, PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(city);
            String response = in.readLine();
            JSONObject json = new JSONObject(response);

            if(json.has("error")){
                System.out.println(json.getString("error"));
                return null;
            }

            JSONObject main = json.getJSONObject("main");
            JSONObject weather0 = json.getJSONArray("weather").getJSONObject(0);
            JSONObject wind = json.has("wind") ? json.getJSONObject("wind") : new JSONObject();

            WeatherData data = new WeatherData();
            data.setTemp(main.getDouble("temp"));
            data.setFeelsLike(main.getDouble("feels_like"));
            data.setHumidity(main.getInt("humidity"));
            data.setPressure(main.getInt("pressure"));
            data.setWind(wind.optDouble("speed",0));
            data.setDescription(weather0.getString("description"));
            data.setMainWeather(weather0.getString("main"));

            return data;

        } catch (Exception e) {
            System.out.println("⚠ Lỗi WeatherService: " + e.getMessage());
            return null;
        }
    }
}
