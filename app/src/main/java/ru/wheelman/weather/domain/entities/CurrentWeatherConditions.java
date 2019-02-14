package ru.wheelman.weather.domain.entities;

public class CurrentWeatherConditions {
    private int cityId;
    private long dataReceivingTime;
    private long updateTime;
    private String cityName;
    private double latitude;
    private double longitude;
    private String country;
    private long sunrise;
    private long sunset;
    private float temperature;
    private int humidity;
    private int pressure;
    private int windSpeed;
    private int windDirection;
    private int windGust;
    private int cloudiness;
    private String weatherConditionGroup; //Rain, Snow, Extreme etc.
    private String weatherConditionDescription; //Weather condition within the group
    private String weatherIconURL;
    private int weatherConditionId;
    private Units units;

    public long getUpdateTime() {
        return updateTime;
    }

    public CurrentWeatherConditions setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public Units getUnits() {
        return units;
    }

    public CurrentWeatherConditions setUnits(Units units) {
        this.units = units;
        return this;
    }

    public int getWeatherConditionId() {
        return weatherConditionId;
    }

    public CurrentWeatherConditions setWeatherConditionId(int weatherConditionId) {
        this.weatherConditionId = weatherConditionId;
        return this;
    }

    public int getCityId() {
        return cityId;
    }

    public CurrentWeatherConditions setCityId(int cityId) {
        this.cityId = cityId;
        return this;
    }

    public long getDataReceivingTime() {
        return dataReceivingTime;
    }

    public CurrentWeatherConditions setDataReceivingTime(long dataReceivingTime) {
        this.dataReceivingTime = dataReceivingTime;
        return this;
    }

    public String getCityName() {
        return cityName;
    }

    public CurrentWeatherConditions setCityName(String cityName) {
        this.cityName = cityName;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public CurrentWeatherConditions setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public CurrentWeatherConditions setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public CurrentWeatherConditions setCountry(String country) {
        this.country = country;
        return this;
    }

    public long getSunrise() {
        return sunrise;
    }

    public CurrentWeatherConditions setSunrise(long sunrise) {
        this.sunrise = sunrise;
        return this;
    }

    public long getSunset() {
        return sunset;
    }

    public CurrentWeatherConditions setSunset(long sunset) {
        this.sunset = sunset;
        return this;
    }

    public float getTemperature() {
        return temperature;
    }

    public CurrentWeatherConditions setTemperature(float temperature) {
        this.temperature = temperature;
        return this;
    }

    public int getHumidity() {
        return humidity;
    }

    public CurrentWeatherConditions setHumidity(int humidity) {
        this.humidity = humidity;
        return this;
    }

    public int getPressure() {
        return pressure;
    }

    public CurrentWeatherConditions setPressure(int pressure) {
        this.pressure = pressure;
        return this;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public CurrentWeatherConditions setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
        return this;
    }

    public int getWindDirection() {
        return windDirection;
    }

    public CurrentWeatherConditions setWindDirection(int windDirection) {
        this.windDirection = windDirection;
        return this;
    }

    public int getWindGust() {
        return windGust;
    }

    public CurrentWeatherConditions setWindGust(int windGust) {
        this.windGust = windGust;
        return this;
    }

    public int getCloudiness() {
        return cloudiness;
    }

    public CurrentWeatherConditions setCloudiness(int cloudiness) {
        this.cloudiness = cloudiness;
        return this;
    }

    public String getWeatherConditionGroup() {
        return weatherConditionGroup;
    }

    public CurrentWeatherConditions setWeatherConditionGroup(String weatherConditionGroup) {
        this.weatherConditionGroup = weatherConditionGroup;
        return this;
    }

    public String getWeatherConditionDescription() {
        return weatherConditionDescription;
    }

    public CurrentWeatherConditions setWeatherConditionDescription(String weatherConditionDescription) {
        this.weatherConditionDescription = weatherConditionDescription;
        return this;
    }

    public String getWeatherIconURL() {
        return weatherIconURL;
    }

    public CurrentWeatherConditions setWeatherIconURL(String weatherIconURL) {
        this.weatherIconURL = weatherIconURL;
        return this;
    }
}
