package weatherapp;

public class WeatherData {
    private double temp;
    private double feelsLike;
    private int humidity;
    private int pressure;
    private double wind;
    private String description;
    private String mainWeather;

    public double getTemp() { return temp; }
    public void setTemp(double temp) { this.temp = temp; }

    public double getFeelsLike() { return feelsLike; }
    public void setFeelsLike(double feelsLike) { this.feelsLike = feelsLike; }

    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }

    public int getPressure() { return pressure; }
    public void setPressure(int pressure) { this.pressure = pressure; }

    public double getWind() { return wind; }
    public void setWind(double wind) { this.wind = wind; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMainWeather() { return mainWeather; }
    public void setMainWeather(String mainWeather) { this.mainWeather = mainWeather; }
}
