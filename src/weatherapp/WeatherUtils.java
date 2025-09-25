package weatherapp;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherUtils {

    public static void updateBackground(JPanel panel, String weatherMain, Color[] colors){
        switch(weatherMain.toLowerCase()){
            case "clear": colors[0]=new Color(255,214,102); colors[1]=new Color(255,179,71); break;
            case "clouds": colors[0]=new Color(176,196,222); colors[1]=new Color(119,136,153); break;
            case "rain": colors[0]=new Color(74,144,226); colors[1]=new Color(0,31,77); break;
            case "snow": colors[0]=new Color(224,247,250); colors[1]=new Color(179,229,252); break;
            default: colors[0]=new Color(30,144,255); colors[1]=new Color(0,102,204);
        }
        panel.repaint();
    }

    public static void updateDateTime(JLabel label){
        String[] thu = {"Chủ nhật","Thứ 2","Thứ 3","Thứ 4","Thứ 5","Thứ 6","Thứ 7"};
        LocalDateTime now = LocalDateTime.now();
        int dayOfWeek = now.getDayOfWeek().getValue();
        String dayName = dayOfWeek==7 ? thu[0] : thu[dayOfWeek];
        String dateText = dayName + ", " + now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        label.setText(dateText);
    }
}
