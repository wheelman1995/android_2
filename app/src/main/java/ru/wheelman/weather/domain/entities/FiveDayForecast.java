package ru.wheelman.weather.domain.entities;

import java.util.List;

public class FiveDayForecast {
    private int cityId;
    private String cityName;
    private double latitude;
    private double longitude;
    private String country;
    private Units units;
    private List<OneDay> days;

    public Units getUnits() {
        return units;
    }

    public FiveDayForecast setUnits(Units units) {
        this.units = units;
        return this;
    }

    public int getCityId() {
        return cityId;
    }

    public FiveDayForecast setCityId(int cityId) {
        this.cityId = cityId;
        return this;
    }

    public String getCityName() {
        return cityName;
    }

    public FiveDayForecast setCityName(String cityName) {
        this.cityName = cityName;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public FiveDayForecast setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public FiveDayForecast setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public FiveDayForecast setCountry(String country) {
        this.country = country;
        return this;
    }

    public List<OneDay> getDays() {
        return days;
    }

    public FiveDayForecast setDays(List<OneDay> days) {
        this.days = days;
        return this;
    }


    //    private long[] dates;
//
//    private String[] weatherConditionDescriptions;
//
//    private float[][] dayTemperatures;
//
//    private float[][] nightTemperatures;
//
//    private String[] icons;
//
//    public FiveDayForecast() {
//
//    }
//
//    public FiveDayForecast(int cityId, long[] dates, String[] weatherConditionDescriptions, float[][] dayTemperatures, float[][] nightTemperatures, String[] icons) {
//        this.cityId = cityId;
//        this.dates = dates;
//        this.weatherConditionDescriptions = weatherConditionDescriptions;
//        this.dayTemperatures = dayTemperatures;
//        this.nightTemperatures = nightTemperatures;
//        this.icons = icons;
//    }
//
//    public int getCityId() {
//        return cityId;
//    }
//
//    public void setCityId(int cityId) {
//        this.cityId = cityId;
//    }
//
//    public long[] getDates() {
//        return dates;
//    }
//
//    public void setDates(long[] dates) {
//        this.dates = dates;
//    }
//
//    public String[] getWeatherConditionDescriptions() {
//        return weatherConditionDescriptions;
//    }
//
//    public void setWeatherConditionDescriptions(String[] weatherConditionDescriptions) {
//        this.weatherConditionDescriptions = weatherConditionDescriptions;
//    }
//
//    public float[][] getDayTemperatures() {
//        return dayTemperatures;
//    }
//
//    public void setDayTemperatures(float[][] dayTemperatures) {
//        this.dayTemperatures = dayTemperatures;
//    }
//
//    public float[][] getNightTemperatures() {
//        return nightTemperatures;
//    }
//
//    public void setNightTemperatures(float[][] nightTemperatures) {
//        this.nightTemperatures = nightTemperatures;
//    }
//
//    public String[] getIcons() {
//        return icons;
//    }
//
//    public void setIcons(String[] icons) {
//        this.icons = icons;
//    }

    public static class OneDay {

        private long sunset;
        private long sunrise;
        private String worstWeatherIconURL;
        private String worstWeatherConditionDescription;
        private List<DataPiece> dataPieces;

        public String getWorstWeatherIconURL() {
            return worstWeatherIconURL;
        }

        public OneDay setWorstWeatherIconURL(String worstWeatherIconURL) {
            this.worstWeatherIconURL = worstWeatherIconURL;
            return this;
        }

        public String getWorstWeatherConditionDescription() {
            return worstWeatherConditionDescription;
        }

        public OneDay setWorstWeatherConditionDescription(String worstWeatherConditionDescription) {
            this.worstWeatherConditionDescription = worstWeatherConditionDescription;
            return this;
        }

        public long getSunset() {
            return sunset;
        }

        public OneDay setSunset(long sunset) {
            this.sunset = sunset;
            return this;
        }

        public long getSunrise() {
            return sunrise;
        }

        public OneDay setSunrise(long sunrise) {
            this.sunrise = sunrise;
            return this;
        }

        public List<DataPiece> getDataPieces() {
            return dataPieces;
        }

        public OneDay setDataPieces(List<DataPiece> dataPieces) {
            this.dataPieces = dataPieces;
            return this;
        }

        public static class DataPiece {
            private long forecastTime;
            private float temperature;
            private int humidity;
            private float pressure;
            private double windSpeed;
            private double windDirection;
            private int cloudiness;
            private int weatherConditionId;
            private String weatherConditionGroup; //Rain, Snow, Extreme etc.
            private String weatherConditionDescription; //Weather condition within the group
            private String weatherIconURL;

            public long getForecastTime() {
                return forecastTime;
            }

            public DataPiece setForecastTime(long forecastTime) {
                this.forecastTime = forecastTime;
                return this;
            }

            public float getTemperature() {
                return temperature;
            }

            public DataPiece setTemperature(float temperature) {
                this.temperature = temperature;
                return this;
            }

            public int getHumidity() {
                return humidity;
            }

            public DataPiece setHumidity(int humidity) {
                this.humidity = humidity;
                return this;
            }

            public float getPressure() {
                return pressure;
            }

            public DataPiece setPressure(float pressure) {
                this.pressure = pressure;
                return this;
            }

            public double getWindSpeed() {
                return windSpeed;
            }

            public DataPiece setWindSpeed(double windSpeed) {
                this.windSpeed = windSpeed;
                return this;
            }

            public double getWindDirection() {
                return windDirection;
            }

            public DataPiece setWindDirection(double windDirection) {
                this.windDirection = windDirection;
                return this;
            }

            public int getCloudiness() {
                return cloudiness;
            }

            public DataPiece setCloudiness(int cloudiness) {
                this.cloudiness = cloudiness;
                return this;
            }

            public int getWeatherConditionId() {
                return weatherConditionId;
            }

            public DataPiece setWeatherConditionId(int weatherConditionId) {
                this.weatherConditionId = weatherConditionId;
                return this;
            }

            public String getWeatherConditionGroup() {
                return weatherConditionGroup;
            }

            public DataPiece setWeatherConditionGroup(String weatherConditionGroup) {
                this.weatherConditionGroup = weatherConditionGroup;
                return this;
            }

            public String getWeatherConditionDescription() {
                return weatherConditionDescription;
            }

            public DataPiece setWeatherConditionDescription(String weatherConditionDescription) {
                this.weatherConditionDescription = weatherConditionDescription;
                return this;
            }

            public String getWeatherIconURL() {
                return weatherIconURL;
            }

            public DataPiece setWeatherIconURL(String weatherIconURL) {
                this.weatherIconURL = weatherIconURL;
                return this;
            }
        }
    }
}
