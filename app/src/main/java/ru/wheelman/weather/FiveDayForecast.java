package ru.wheelman.weather;

public class FiveDayForecast {

    private int cityId;

    private long[] dates;

    private String[] weatherConditionDescriptions;

    private float[] maxDayTemperatures;

    private float[] minNightTemperatures;

    private String[] icons;

    public FiveDayForecast() {

    }

    public FiveDayForecast(int cityId, long[] dates, String[] weatherConditionDescriptions, float[] maxDayTemperatures, float[] minNightTemperatures, String[] icons) {
        this.cityId = cityId;
        this.dates = dates;
        this.weatherConditionDescriptions = weatherConditionDescriptions;
        this.maxDayTemperatures = maxDayTemperatures;
        this.minNightTemperatures = minNightTemperatures;
        this.icons = icons;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public long[] getDates() {
        return dates;
    }

    public void setDates(long[] dates) {
        this.dates = dates;
    }

    public String[] getWeatherConditionDescriptions() {
        return weatherConditionDescriptions;
    }

    public void setWeatherConditionDescriptions(String[] weatherConditionDescriptions) {
        this.weatherConditionDescriptions = weatherConditionDescriptions;
    }

    public float[] getMaxDayTemperatures() {
        return maxDayTemperatures;
    }

    public void setMaxDayTemperatures(float[] maxDayTemperatures) {
        this.maxDayTemperatures = maxDayTemperatures;
    }

    public float[] getMinNightTemperatures() {
        return minNightTemperatures;
    }

    public void setMinNightTemperatures(float[] minNightTemperatures) {
        this.minNightTemperatures = minNightTemperatures;
    }

    public String[] getIcons() {
        return icons;
    }

    public void setIcons(String[] icons) {
        this.icons = icons;
    }
}
