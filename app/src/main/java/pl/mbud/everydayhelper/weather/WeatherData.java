package pl.mbud.everydayhelper.weather;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import pl.mbud.everydayhelper.data.Coordinate;
import pl.mbud.everydayhelper.data.Location;
import pl.mbud.everydayhelper.data.WeatherIcon;

/**
 * Created by Maciek on 12.12.2016.
 */

public class WeatherData {

    private Coordinate coordinate;
    private Integer weatherId;
    private String weatherDescription;
    private String weatherIconName;
    private Double temperature;
    private Double pressure;
    private Double humidity;
    private Double windSpeed, windAngle;
    private Double cloudLevel;
    private Double rainLevel, snowLevel;
    private Date sunrise, sunset;
    private Location location;
    private Date forecastDate;
    private WeatherIcon icon;
    private int id;

    public WeatherData(Location location) {
        this.location = location;
    }

    public WeatherData(JSONObject weatherData) throws JSONException {
        this(null, weatherData);
    }

    public WeatherData(Location location, JSONObject weatherData) throws JSONException {

        try {
            forecastDate = new Date(weatherData.getLong("dt") * 1000l);
        } catch (JSONException e) {
        }
        JSONObject jsonWeather = weatherData.getJSONArray("weather").getJSONObject(0);
        weatherDescription = jsonWeather.getString("description");
        weatherIconName = jsonWeather.getString("icon");
        weatherId = jsonWeather.getInt("id");

        JSONObject jsonMain = weatherData.getJSONObject("main");
        temperature = jsonMain.getDouble("temp");
        pressure = jsonMain.getDouble("pressure");
        try {
            humidity = jsonMain.getDouble("humidity");
        } catch (JSONException e) {
        }

        try {
            JSONObject jsonWind = weatherData.getJSONObject("wind");
            windSpeed = jsonWind.getDouble("speed");
            windAngle = jsonWind.getDouble("deg");
        } catch (JSONException e) {
        }

        try {
            cloudLevel = weatherData.getJSONObject("clouds").getDouble("all");
        } catch (JSONException e) {
        }
        try {
            rainLevel = weatherData.getJSONObject("rain").getDouble("3h");
        } catch (JSONException e) {
        }
        try {
            snowLevel = weatherData.getJSONObject("snow").getDouble("3h");
        } catch (JSONException e) {
        }

        if (location != null) {
            JSONObject jsonCoord = weatherData.getJSONObject("coord");
            double lon = jsonCoord.getDouble("lon");
            double lat = jsonCoord.getDouble("lat");
            coordinate = new Coordinate(lon, lat);

            this.location = location;
            location.setLocationId(weatherData.getInt("id"));
            location.setLocationName(weatherData.getString("name"));

            JSONObject jsonSys = weatherData.getJSONObject("sys");
            location.setCountryCode(jsonSys.getString("country"));

            try {
                sunrise = new Date(jsonSys.getLong("sunrise") * 1000l);
                sunset = new Date(jsonSys.getLong("sunset") * 1000l);
            } catch (JSONException e) {
            }
        }
    }

    public String getLocationName() {
        return location.getLocationName();
    }

    public Integer getLocationId() {
        return location.getLocationId();
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public String getWeatherIconName() {
        return weatherIconName;
    }

    public Double getCloudLevel() {
        return cloudLevel;
    }

    public Double getHumidity() {
        return humidity;
    }

    public Double getPressure() {
        return pressure;
    }

    public Double getTemperature() {
        return getTemperature(TemperatureScale.KELVIN);
    }

    public Double getTemperature(TemperatureScale scale) {
        if (temperature == null) {
            return null;
        }
        switch (scale) {
            case KELVIN:
                return temperature;
            case CELCIUS:
                return temperature - 273.15;
            case FAHRENHEIT:
                return (temperature * 9.0 / 5.0) - 459.67;
        }
        return null;
    }

    public Double getWindAngle() {
        return windAngle;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public Date getSunrise() {
        return sunrise;
    }

    public Date getSunset() {
        return sunset;
    }

    public String getCountryCode() {
        return location.getCountryCode();
    }

    public Date getForecastDate() {
        return forecastDate;
    }

    public Double getRainLevel() {
        return rainLevel;
    }

    public Double getSnowLevel() {
        return snowLevel;
    }

    public Integer getWeatherId() {
        return weatherId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public WeatherIcon getIcon() {
        return icon;
    }

    public void setIcon(WeatherIcon icon) {
        this.icon = icon;
    }

    public void setCloudLevel(Double cloudLevel) {
        this.cloudLevel = cloudLevel;
    }

    public void setForecastDate(Date forecastDate) {
        this.forecastDate = forecastDate;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public void setRainLevel(Double rainLevel) {
        this.rainLevel = rainLevel;
    }

    public void setSnowLevel(Double snowLevel) {
        this.snowLevel = snowLevel;
    }

    public void setSunrise(Date sunrise) {
        this.sunrise = sunrise;
    }

    public void setSunset(Date sunset) {
        this.sunset = sunset;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public void setWeatherIconName(String weatherIconName) {
        this.weatherIconName = weatherIconName;
    }

    public void setWeatherId(Integer weatherId) {
        this.weatherId = weatherId;
    }

    public void setWindAngle(Double windAngle) {
        this.windAngle = windAngle;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
