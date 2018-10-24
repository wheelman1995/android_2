package ru.wheelman.weather.model.forecast;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ThreeHourData implements Parcelable {

//     "dt":1540198800,
//         "main":{  },
//         "weather":[  ],
//         "clouds":{  },
//         "wind":{  },
//         "rain":{  },
//         "sys":{  },
//         "dt_txt":"2018-10-22 09:00:00"

//    list.dt Time of data forecasted, unix, UTC
//    list.dt_txt Data/time of calculation, UTC

    public static final Creator<ThreeHourData> CREATOR = new Creator<ThreeHourData>() {
        @Override
        public ThreeHourData createFromParcel(Parcel in) {
            return new ThreeHourData(in);
        }

        @Override
        public ThreeHourData[] newArray(int size) {
            return new ThreeHourData[size];
        }
    };
    @SerializedName("weather")
    @Expose
    public Weather[] weather;
    @SerializedName("dt")
    @Expose
    private long dt;
    @SerializedName("main")
    @Expose
    private Main main;

    private ThreeHourData(Parcel in) {
        dt = in.readLong();
        main = in.readParcelable(Main.class.getClassLoader());
        weather = in.createTypedArray(Weather.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(dt);
        dest.writeParcelable(main, flags);
        dest.writeTypedArray(weather, flags);
    }

    public long getDt() {
        return dt;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }
}
