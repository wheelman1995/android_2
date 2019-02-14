package ru.wheelman.weather.data.data_sources.network.forecasted.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "weatherdata", strict = false)
public class ForecastedWeatherXML {

//    <location>
//<name>London</name>
//<type/>
//<country>US</country>
//<timezone/>
//<location altitude="0" latitude="39.8865" longitude="-83.4483" geobase="geonames" geobaseid="4517009"/>
//</location>
//<credit/>
//<meta>
//<lastupdate/>
//<calctime>0.0028</calctime>
//<nextupdate/>
//</meta>
//<sun rise="2017-03-03T12:03:03" set="2017-03-03T23:28:37"/>
//<forecast>...</forecast>

    @Element(name = "location")
    private Location location;

    @Element(name = "sun")
    private Sun sun;

    @ElementList(name = "forecast", entry = "time")
    private List<Time> forecast;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Sun getSun() {
        return sun;
    }

    public void setSun(Sun sun) {
        this.sun = sun;
    }

    public List<Time> getForecast() {
        return forecast;
    }

    public void setForecast(List<Time> forecast) {
        this.forecast = forecast;
    }

    @Root(strict = false)
    public static class Location {
// <location>
//<name>London</name>
//<type/>
//<country>US</country>
//<timezone/>
//<location altitude="0" latitude="39.8865" longitude="-83.4483" geobase="geonames" geobaseid="4517009"/>
//</location>

        @Element(name = "name")
        private String city;

        @Element(name = "country")
        private String country;

        @Element(name = "location")
        private InnerLocation location;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public InnerLocation getLocation() {
            return location;
        }

        public void setLocation(InnerLocation location) {
            this.location = location;
        }

        public static class InnerLocation {
//            <location altitude="0" latitude="39.8865" longitude="-83.4483" geobase="geonames" geobaseid="4517009"/>

            @Attribute(name = "altitude")
            private double altitude;

            @Attribute(name = "latitude")
            private double latitude;

            @Attribute(name = "longitude")
            private double longitude;

            @Attribute(name = "geobase")
            private String geobase;

            @Attribute(name = "geobaseid")
            private int geobaseid;

            public double getAltitude() {
                return altitude;
            }

            public void setAltitude(double altitude) {
                this.altitude = altitude;
            }

            public double getLatitude() {
                return latitude;
            }

            public void setLatitude(double latitude) {
                this.latitude = latitude;
            }

            public double getLongitude() {
                return longitude;
            }

            public void setLongitude(double longitude) {
                this.longitude = longitude;
            }

            public String getGeobase() {
                return geobase;
            }

            public void setGeobase(String geobase) {
                this.geobase = geobase;
            }

            public int getGeobaseid() {
                return geobaseid;
            }

            public void setGeobaseid(int geobaseid) {
                this.geobaseid = geobaseid;
            }
        }
    }

    public static class Sun {
        //<sun rise="2017-03-03T12:03:03" set="2017-03-03T23:28:37"/>
        @Attribute(name = "rise")
        private String rise;

        @Attribute(name = "set")
        private String set;

        public String getRise() {
            return rise;
        }

        public void setRise(String rise) {
            this.rise = rise;
        }

        public String getSet() {
            return set;
        }

        public void setSet(String set) {
            this.set = set;
        }
    }

    @Root(name = "time", strict = false)
    public static class Time {
        //<time from="2017-03-03T06:00:00" to="2017-03-03T09:00:00">

//<symbol number="600" name="light snow" var="13n"/>
//<precipitation unit="3h" value="0.03125" type="snow"/>
//<windDirection deg="303.004" code="WNW" name="West-northwest"/>
//<windSpeed mps="2.29" name="Light breeze"/>
//<temperature unit="kelvin" value="269.91" min="269.91" max="270.877"/>
//<pressure unit="hPa" value="1005.61"/>
//<humidity value="93" unit="%"/>
//<clouds value="scattered clouds" all="32" unit="%"/>

        //</time>
        @Attribute(name = "from")
        private String from;

        @Attribute(name = "to")
        private String to;

        @Element(name = "symbol")
        private Symbol symbol;

        @Element(name = "precipitation")
        private Precipitation precipitation;

        @Element(name = "windDirection")
        private WindDirection windDirection;

        @Element(name = "windSpeed")
        private WindSpeed windSpeed;

        @Element(name = "temperature")
        private Temperature temperature;

        @Element(name = "pressure")
        private Pressure pressure;

        @Element(name = "humidity")
        private Humidity humidity;

        @Element(name = "clouds")
        private Clouds clouds;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public Symbol getSymbol() {
            return symbol;
        }

        public void setSymbol(Symbol symbol) {
            this.symbol = symbol;
        }

        public Precipitation getPrecipitation() {
            return precipitation;
        }

        public void setPrecipitation(Precipitation precipitation) {
            this.precipitation = precipitation;
        }

        public WindDirection getWindDirection() {
            return windDirection;
        }

        public void setWindDirection(WindDirection windDirection) {
            this.windDirection = windDirection;
        }

        public WindSpeed getWindSpeed() {
            return windSpeed;
        }

        public void setWindSpeed(WindSpeed windSpeed) {
            this.windSpeed = windSpeed;
        }

        public Temperature getTemperature() {
            return temperature;
        }

        public void setTemperature(Temperature temperature) {
            this.temperature = temperature;
        }

        public Pressure getPressure() {
            return pressure;
        }

        public void setPressure(Pressure pressure) {
            this.pressure = pressure;
        }

        public Humidity getHumidity() {
            return humidity;
        }

        public void setHumidity(Humidity humidity) {
            this.humidity = humidity;
        }

        public Clouds getClouds() {
            return clouds;
        }

        public void setClouds(Clouds clouds) {
            this.clouds = clouds;
        }

        public static class Symbol {
            //<symbol number="600" name="light snow" var="13n"/>

            @Attribute(name = "number")
            private int weatherConditionId;

            @Attribute(name = "name")
            private String weatherConditionDescription;

            @Attribute(name = "var")
            private String weatherConditionIcon;

            public int getWeatherConditionId() {
                return weatherConditionId;
            }

            public void setWeatherConditionId(int weatherConditionId) {
                this.weatherConditionId = weatherConditionId;
            }

            public String getWeatherConditionDescription() {
                return weatherConditionDescription;
            }

            public void setWeatherConditionDescription(String weatherConditionDescription) {
                this.weatherConditionDescription = weatherConditionDescription;
            }

            public String getWeatherConditionIcon() {
                return weatherConditionIcon;
            }

            public void setWeatherConditionIcon(String weatherConditionIcon) {
                this.weatherConditionIcon = weatherConditionIcon;
            }
        }

        public static class Precipitation {
            //<precipitation unit="3h" value="0.03125" type="snow"/>

            @Attribute(name = "unit", required = false)
            private String unit;

            @Attribute(name = "value", required = false)
            private double value;

            @Attribute(name = "type", required = false)
            private String weatherConditionGroup;

            public String getUnit() {
                return unit;
            }

            public void setUnit(String unit) {
                this.unit = unit;
            }

            public double getValue() {
                return value;
            }

            public void setValue(double value) {
                this.value = value;
            }

            public String getWeatherConditionGroup() {
                return weatherConditionGroup;
            }

            public void setWeatherConditionGroup(String weatherConditionGroup) {
                this.weatherConditionGroup = weatherConditionGroup;
            }
        }

        public static class WindDirection {
            //<windDirection deg="303.004" code="WNW" name="West-northwest"/>

            @Attribute(name = "deg")
            private double deg;

            @Attribute(name = "code")
            private String code;

            @Attribute(name = "name")
            private String name;

            public double getDeg() {
                return deg;
            }

            public void setDeg(double deg) {
                this.deg = deg;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class WindSpeed {
            //<windSpeed mps="2.29" name="Light breeze"/>

            @Attribute(name = "mps")
            private double mps;

            @Attribute(name = "name")
            private String name;

            public double getMps() {
                return mps;
            }

            public void setMps(double mps) {
                this.mps = mps;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class Temperature {
            //<temperature unit="kelvin" value="269.91" min="269.91" max="270.877"/>

            @Attribute(name = "unit")
            private String unit;

            @Attribute(name = "value")
            private float value;

            @Attribute(name = "min")
            private float min;

            @Attribute(name = "max")
            private float max;

            public String getUnit() {
                return unit;
            }

            public void setUnit(String unit) {
                this.unit = unit;
            }

            public float getValue() {
                return value;
            }

            public void setValue(float value) {
                this.value = value;
            }

            public float getMin() {
                return min;
            }

            public void setMin(float min) {
                this.min = min;
            }

            public float getMax() {
                return max;
            }

            public void setMax(float max) {
                this.max = max;
            }
        }

        public static class Pressure {
            //<pressure unit="hPa" value="1005.61"/>

            @Attribute(name = "unit")
            private String unit;

            @Attribute(name = "value")
            private float value;

            public String getUnit() {
                return unit;
            }

            public void setUnit(String unit) {
                this.unit = unit;
            }

            public float getValue() {
                return value;
            }

            public void setValue(float value) {
                this.value = value;
            }
        }

        public static class Humidity {
            //<humidity value="93" unit="%"/>

            @Attribute(name = "value")
            private int value;

            @Attribute(name = "unit")
            private String unit;

            public int getValue() {
                return value;
            }

            public void setValue(int value) {
                this.value = value;
            }

            public String getUnit() {
                return unit;
            }

            public void setUnit(String unit) {
                this.unit = unit;
            }
        }

        public static class Clouds {
            //<clouds value="scattered clouds" all="32" unit="%"/>

            @Attribute(name = "value")
            private String value;

            @Attribute(name = "all")
            private int all;

            @Attribute(name = "unit")
            private String unit;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public int getAll() {
                return all;
            }

            public void setAll(int all) {
                this.all = all;
            }

            public String getUnit() {
                return unit;
            }

            public void setUnit(String unit) {
                this.unit = unit;
            }
        }
    }
}
