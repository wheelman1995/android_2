package ru.wheelman.weather.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class WeatherDataModel implements Parcelable {

    public static final Creator<WeatherDataModel> CREATOR = new Creator<WeatherDataModel>() {
        @Override
        public WeatherDataModel createFromParcel(Parcel in) {
            return new WeatherDataModel(in);
        }

        @Override
        public WeatherDataModel[] newArray(int size) {
            return new WeatherDataModel[size];
        }
    };
    @SerializedName("name")
    private String name;
    @SerializedName("sys")
    private Sys sys;
    @SerializedName("dt")
    private long dt;
    @SerializedName("id")
    private int id;
    @SerializedName("main")
    private Main main;

    public WeatherDataModel() {
    }

    protected WeatherDataModel(Parcel in) {
        dt = in.readLong();
        main = in.readParcelable(ClassLoader.getSystemClassLoader());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(dt);
        dest.writeParcelable(main, flags);
    }
}
