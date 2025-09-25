module WeatherApp {
    requires java.desktop;
    requires java.net.http;
    requires org.json;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.swing;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.ikonli.weathericons;
    requires org.kordamp.ikonli.material;

    exports weatherapp;
}
