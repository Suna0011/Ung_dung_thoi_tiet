package weatherapp;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.*;
import org.json.*;
import org.kordamp.ikonli.swing.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import java.util.Map;
import java.util.LinkedHashMap;

public class WeatherClientUI extends JFrame {

    private JComboBox<String> cityCombo;
    private JLabel tempLabel, descLabel, iconLabel, dateTimeLabel;
    private JPanel forecastPanel, detailPanel;
    private JLabel weatherStatusLabel, humidityLabelText, windLabelText, pressureLabelText;
    private Timer timer;

    private Color topColor = new Color(30,144,255);
    private Color bottomColor = new Color(0,102,204);

    // Map tên Việt → tên API
    private Map<String,String> cityMap = new LinkedHashMap<>(){{
        put("Hà Nội", "Hanoi,VN");
        put("TP. Hồ Chí Minh", "Ho Chi Minh,VN");
        put("Đà Nẵng", "Da Nang,VN");
        put("Hải Phòng", "Haiphong,VN");
        put("Cần Thơ", "Can Tho,VN");
        put("Huế", "Hue,VN");
    }};

    public WeatherClientUI() {
        setTitle("🌤 Weather App");
        setSize(480,680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();

        fetchWeather(); // fetch mặc định lần đầu
        timer = new Timer(5000, e -> fetchWeather());
        timer.start();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0,0,topColor,0,getHeight(),bottomColor);
                g2d.setPaint(gp);
                g2d.fillRect(0,0,getWidth(),getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);

        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,10));
        topPanel.setOpaque(false);

        cityCombo = new JComboBox<>(cityMap.keySet().toArray(new String[0]));
        cityCombo.setPreferredSize(new Dimension(250,35));

        JButton searchButton = new JButton("🔍");
        searchButton.setPreferredSize(new Dimension(50,35));
        searchButton.setBackground(new Color(0,123,255));
        searchButton.setOpaque(true);
        searchButton.setBorderPainted(false);
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> fetchWeather());

        topPanel.add(cityCombo);
        topPanel.add(searchButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        tempLabel = new JLabel("--°");
        tempLabel.setFont(new Font("Arial",Font.BOLD,60));
        tempLabel.setForeground(Color.WHITE);
        tempLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        iconLabel = new JLabel();
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        descLabel = new JLabel("Đang tải...");
        descLabel.setFont(new Font("Arial",Font.PLAIN,18));
        descLabel.setForeground(Color.WHITE);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        detailPanel = new JPanel(new GridLayout(2,2,20,10));
        detailPanel.setOpaque(false);
        detailPanel.setMaximumSize(new Dimension(420,120));
        detailPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Thêm info panel: Trạng thái thời tiết, Độ ẩm, Gió, Áp suất
        detailPanel.add(createInfoPanel("Thời tiết","--","weatherStatus"));
        detailPanel.add(createInfoPanel("Độ ẩm","0%","cloud"));
        detailPanel.add(createInfoPanel("Gió","0 m/s","wind"));
        detailPanel.add(createInfoPanel("Áp suất","0 hPa","pressure"));

        dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Arial",Font.BOLD,16));
        dateTimeLabel.setForeground(Color.WHITE);
        dateTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createRigidArea(new Dimension(0,20)));
        centerPanel.add(tempLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0,10)));
        centerPanel.add(iconLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0,10)));
        centerPanel.add(descLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0,20)));
        centerPanel.add(detailPanel);
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(dateTimeLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0,10)));

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Forecast panel
        forecastPanel = new JPanel(new GridLayout(1,6,5,5));
        forecastPanel.setOpaque(false);
        JPanel forecastWrapper = new JPanel(new BorderLayout());
        forecastWrapper.setOpaque(false);
        forecastWrapper.setBorder(BorderFactory.createEmptyBorder(20,5,20,5));
        forecastWrapper.add(forecastPanel, BorderLayout.CENTER);
        mainPanel.add(forecastWrapper, BorderLayout.SOUTH);
    }

    private JPanel createInfoPanel(String labelText, String valueText, String iconName){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel icon = getWeatherIconLabel(iconName,32);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(valueText);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Arial",Font.BOLD,14));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial",Font.PLAIN,12));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(icon);
        panel.add(Box.createRigidArea(new Dimension(0,5)));
        panel.add(valueLabel);
        panel.add(label);

        switch(iconName){
            case "weatherStatus": weatherStatusLabel = valueLabel; break;
            case "cloud": humidityLabelText = valueLabel; break;
            case "wind": windLabelText = valueLabel; break;
            case "pressure": pressureLabelText = valueLabel; break;
        }

        return panel;
    }

    private JLabel getWeatherIconLabel(String weatherMain,int size){
        weatherMain = weatherMain.toLowerCase();
        FontIcon icon;
        switch(weatherMain){
            case "cloud": icon = FontIcon.of(FontAwesomeSolid.CLOUD,size,Color.LIGHT_GRAY); break;
            case "rain": icon = FontIcon.of(FontAwesomeSolid.CLOUD_RAIN,size,new Color(135,206,250)); break;
            case "snow": icon = FontIcon.of(FontAwesomeSolid.SNOWFLAKE,size,Color.CYAN); break;
            case "sun": case "clear": icon = FontIcon.of(FontAwesomeSolid.SUN,size,Color.ORANGE); break;
            case "wind": icon = FontIcon.of(FontAwesomeSolid.WIND,size,Color.WHITE); break;
            case "pressure": icon = FontIcon.of(FontAwesomeSolid.TACHOMETER_ALT,size,Color.WHITE); break;
            case "thunderstorm": icon = FontIcon.of(FontAwesomeSolid.BOLT,size,Color.YELLOW); break;
            default: icon = FontIcon.of(FontAwesomeSolid.CLOUD,size,Color.GRAY);
        }
        JLabel label = new JLabel(icon);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void updateBackground(String weatherMain){
        switch(weatherMain.toLowerCase()){
            case "clear": topColor=new Color(255,214,102); bottomColor=new Color(255,179,71); break;
            case "clouds": topColor=new Color(176,196,222); bottomColor=new Color(119,136,153); break;
            case "rain": topColor=new Color(74,144,226); bottomColor=new Color(0,31,77); break;
            case "snow": topColor=new Color(224,247,250); bottomColor=new Color(179,229,252); break;
            case "thunderstorm": topColor=new Color(93,109,126); bottomColor=new Color(44,62,80); break;
            case "mist": topColor=new Color(211,211,211); bottomColor=new Color(169,169,169); break;
            default: topColor=new Color(30,144,255); bottomColor=new Color(0,102,204);
        }
        repaint();
    }

    private String getMainWeather(JSONArray weatherArr){
        String[] priority = {"Thunderstorm","Rain","Snow","Clouds","Clear","Mist"};
        for(String w : priority){
            for(int i=0;i<weatherArr.length();i++){
                if(weatherArr.getJSONObject(i).getString("main").equalsIgnoreCase(w)){
                    return w;
                }
            }
        }
        return "Clear";
    }

    private void fetchWeather(){
        String cityVN = (String) cityCombo.getSelectedItem();
        String cityAPI = cityMap.getOrDefault(cityVN,"Hanoi,VN");

        try(Socket socket = new Socket("localhost",12345);
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){

            out.println(cityAPI);
            String response = in.readLine();
            JSONObject json = new JSONObject(response);

            if(json.has("error")){
                JOptionPane.showMessageDialog(this,json.getString("error"));
                return;
            }

            JSONObject main = json.getJSONObject("main");
            JSONArray weatherArr = json.getJSONArray("weather");
            JSONObject weather0 = weatherArr.getJSONObject(0);
            JSONObject windObj = json.has("wind")?json.getJSONObject("wind"):new JSONObject();

            double temp = main.getDouble("temp");
            double humidity = main.getDouble("humidity");
            double pressure = main.getDouble("pressure");
            double wind = windObj.optDouble("speed",0);

            String desc = weather0.getString("description");
            String weatherMain = getMainWeather(weatherArr);

            tempLabel.setText(Math.round(temp)+"°");
            descLabel.setText(desc);
            iconLabel.setIcon(getWeatherIconLabel(weatherMain,80).getIcon());
            weatherStatusLabel.setText(weatherMain); // ✅ Trạng thái thời tiết
            humidityLabelText.setText(humidity+"%");
            windLabelText.setText(String.format("%.1f m/s",wind));
            pressureLabelText.setText(Math.round(pressure)+" hPa");

            // Ngày + Thứ
            String[] thu = {"Chủ nhật","Thứ 2","Thứ 3","Thứ 4","Thứ 5","Thứ 6","Thứ 7"};
            LocalDateTime now = LocalDateTime.now();
            int dayOfWeek = now.getDayOfWeek().getValue(); // 1=Monday
            String dayName = dayOfWeek==7?thu[0]:thu[dayOfWeek];
            String dateText = dayName+", "+now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            dateTimeLabel.setText(dateText);

            updateBackground(weatherMain);

            // Forecast 6 giờ demo
            forecastPanel.removeAll();
            JSONArray list = json.getJSONArray("list");
            for(int i=0;i<6;i++){
                JSONObject hourObj = list.getJSONObject(i);
                double tempF = hourObj.getJSONObject("main").getDouble("temp");
                JSONArray wArr = hourObj.getJSONArray("weather");
                String wMain = getMainWeather(wArr);

                JPanel card = new JPanel();
                card.setOpaque(false);
                card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));
                card.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
                card.setAlignmentX(Component.CENTER_ALIGNMENT);

                JLabel t = new JLabel(LocalDateTime.now().plusHours(i+1).getHour()+"h");
                t.setForeground(Color.WHITE);
                JLabel ic = getWeatherIconLabel(wMain,32);
                JLabel tp = new JLabel(Math.round(tempF)+"°");
                tp.setForeground(Color.WHITE);

                t.setAlignmentX(Component.CENTER_ALIGNMENT);
                ic.setAlignmentX(Component.CENTER_ALIGNMENT);
                tp.setAlignmentX(Component.CENTER_ALIGNMENT);

                card.add(t); card.add(ic); card.add(tp);
                forecastPanel.add(card);
            }
            forecastPanel.revalidate();
            forecastPanel.repaint();

        }catch(Exception e){
            System.out.println("⚠ Lỗi lấy thời tiết: "+e.getMessage());
        }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new WeatherClientUI().setVisible(true));
    }
}
