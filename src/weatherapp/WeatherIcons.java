package weatherapp;

import org.kordamp.ikonli.swing.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import java.awt.*;

public class WeatherIcons {
    public static FontIcon getIcon(String weatherMain,int size){
        weatherMain = weatherMain.toLowerCase();
        switch(weatherMain){
            case "cloud": return FontIcon.of(FontAwesomeSolid.CLOUD,size,Color.LIGHT_GRAY);
            case "rain": return FontIcon.of(FontAwesomeSolid.CLOUD_RAIN,size,new Color(135,206,250));
            case "snow": return FontIcon.of(FontAwesomeSolid.SNOWFLAKE,size,Color.CYAN);
            case "sun":
            case "clear": return FontIcon.of(FontAwesomeSolid.SUN,size,Color.ORANGE);
            case "wind": return FontIcon.of(FontAwesomeSolid.WIND,size,Color.WHITE);
            case "pressure": return FontIcon.of(FontAwesomeSolid.TACHOMETER_ALT,size,Color.WHITE);
            default: return FontIcon.of(FontAwesomeSolid.CLOUD,size,Color.GRAY);
        }
    }
}
