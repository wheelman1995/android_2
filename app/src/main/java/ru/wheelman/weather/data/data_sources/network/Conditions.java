package ru.wheelman.weather.data.data_sources.network;

import java.util.ArrayList;

final class Conditions {
    private static final ArrayList<String[][]> conditions = new ArrayList<String[][]>() {{
//Snow
        add(new String[][]{{"rain and snow", "Snow"}, {"13d", "13n"}});
        add(new String[][]{{"light rain and snow", "Snow"}, {"13d", "13n"}});

        add(new String[][]{{"shower sleet", "Snow"}, {"13d", "13n"}});
        add(new String[][]{{"sleet", "Snow"}, {"13d", "13n"}});

        add(new String[][]{{"heavy shower snow", "Snow"}, {"13d", "13n"}});
        add(new String[][]{{"shower snow", "Snow"}, {"13d", "13n"}});
        add(new String[][]{{"light shower snow", "Snow"}, {"13d", "13n"}});

        add(new String[][]{{"heavy snow", "Snow"}, {"13d", "13n"}});
        add(new String[][]{{"snow", "Snow"}, {"13d", "13n"}});
        add(new String[][]{{"light snow", "Snow"}, {"13d", "13n"}});


        //thunderstorm
        add(new String[][]{{"thunderstorm with heavy rain", "Thunderstorm"}, {"11d", "11n"}});
        add(new String[][]{{"thunderstorm with rain", "Thunderstorm"}, {"11d", "11n"}});
        add(new String[][]{{"thunderstorm with light rain", "Thunderstorm"}, {"11d", "11n"}});

        add(new String[][]{{"thunderstorm with heavy drizzle", "Thunderstorm"}, {"11d", "11n"}});
        add(new String[][]{{"thunderstorm with drizzle", "Thunderstorm"}, {"11d", "11n"}});
        add(new String[][]{{"thunderstorm with light drizzle", "Thunderstorm"}, {"11d", "11n"}});

        add(new String[][]{{"heavy thunderstorm", "Thunderstorm"}, {"11d", "11n"}});
        add(new String[][]{{"ragged thunderstorm", "Thunderstorm"}, {"11d", "11n"}});
        add(new String[][]{{"thunderstorm", "Thunderstorm"}, {"11d", "11n"}});
        add(new String[][]{{"light thunderstorm", "Thunderstorm"}, {"11d", "11n"}});


        //rain
        add(new String[][]{{"freezing rain", "Rain"}, {"13d", "13n"}});

        add(new String[][]{{"extreme rain", "Rain"}, {"10d", "10n"}});

        add(new String[][]{{"heavy intensity shower rain", "Rain"}, {"09d", "09n"}});

        add(new String[][]{{"very heavy rain", "Rain"}, {"10d", "10n"}});
        add(new String[][]{{"heavy intensity rain", "Rain"}, {"10d", "10n"}});

        add(new String[][]{{"shower rain", "Rain"}, {"09d", "09n"}});
        add(new String[][]{{"ragged shower rain", "Rain"}, {"09d", "09n"}});

        add(new String[][]{{"moderate rain", "Rain"}, {"10d", "10n"}});

        add(new String[][]{{"light intensity shower rain", "Rain"}, {"09d", "09n"}});
        add(new String[][]{{"light rain", "Rain"}, {"10d", "10n"}});


        //drizzle
        add(new String[][]{{"heavy shower rain and drizzle", "Drizzle"}, {"09d", "09n"}});
        add(new String[][]{{"shower rain and drizzle", "Drizzle"}, {"09d", "09n"}});

        add(new String[][]{{"heavy intensity drizzle rain", "Drizzle"}, {"09d", "09n"}});
        add(new String[][]{{"drizzle rain", "Drizzle"}, {"09d", "09n"}});
        add(new String[][]{{"light intensity drizzle rain", "Drizzle"}, {"09d", "09n"}});

        add(new String[][]{{"shower drizzle", "Drizzle"}, {"09d", "09n"}});
        add(new String[][]{{"heavy intensity drizzle", "Drizzle"}, {"09d", "09n"}});
        add(new String[][]{{"drizzle", "Drizzle"}, {"09d", "09n"}});
        add(new String[][]{{"light intensity drizzle", "Drizzle"}, {"09d", "09n"}});


        //Atmosphere
        add(new String[][]{{"tornado", "Atmosphere"}, {"50d", "50n"}});
        add(new String[][]{{"squalls", "Atmosphere"}, {"50d", "50n"}});
        add(new String[][]{{"volcanic ash", "Atmosphere"}, {"50d", "50n"}});
        add(new String[][]{{"sand, dust whirls", "Atmosphere"}, {"50d", "50n"}});
        add(new String[][]{{"sand", "Atmosphere"}, {"50d", "50n"}});
        add(new String[][]{{"dust", "Atmosphere"}, {"50d", "50n"}});

        add(new String[][]{{"smoke", "Atmosphere"}, {"50d", "50n"}});
        add(new String[][]{{"fog", "Atmosphere"}, {"50d", "50n"}});
        add(new String[][]{{"mist", "Atmosphere"}, {"50d", "50n"}});
        add(new String[][]{{"haze", "Atmosphere"}, {"50d", "50n"}});


        //clouds
        add(new String[][]{{"overcast clouds", "Clouds"}, {"04d", "04n"}});
        add(new String[][]{{"scattered clouds", "Clouds"}, {"03d", "03n"}});
        add(new String[][]{{"broken clouds", "Clouds"}, {"04d", "04n"}});
        add(new String[][]{{"few clouds", "Clouds"}, {"02d", "02n"}});

        //clear
        add(new String[][]{{"clear sky", "Clear"}, {"01d", "01n"}});

    }};

    private Conditions() {
    }

    //    SNOW("Snow", new String[]{"13d", "13n"}),
//    THUNDERSTORM("Thunderstorm", new String[]{"11d", "11n"}),
//    RAIN("Rain", new String[]{"10d", "10n", "13d", "13n", "09d", "09n"}),
//    DRIZZLE("Drizzle", new String[]{"09d", "09n"}),
//    MIST("Atmosphere", new String[]{"50d", "50n"}),
//    CLOUDS("Clouds", new String[]{"02d", "02n", "03d", "03n", "04d", "04n"}),
//    CLEAR("Clear", new String[]{"01d", "01n"});
//
//    String group;
//    String description;
//    String[] icons;
//
//    Conditions(String group, String[] icons) {
//        this.group = group;
//        this.icons = icons;
//    }

    public static ArrayList<String[][]> getConditions() {
        return conditions;
    }

}
