package ru.wheelman.weather.data.data_sources.network.current.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class CurrentWeather implements Parcelable {

    public static final Creator<CurrentWeather> CREATOR = new Creator<CurrentWeather>() {
        @Override
        public CurrentWeather createFromParcel(Parcel in) {
            return new CurrentWeather(in);
        }

        @Override
        public CurrentWeather[] newArray(int size) {
            return new CurrentWeather[size];
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
    @SerializedName("weather")
    private Weather[] weather;

    protected CurrentWeather(Parcel in) {
        dt = in.readLong();
        id = in.readInt();
        main = in.readParcelable(ClassLoader.getSystemClassLoader());
        name = in.readString();
        sys = in.readParcelable(ClassLoader.getSystemClassLoader());
        weather = (Weather[]) in.readParcelableArray(ClassLoader.getSystemClassLoader());
    }

    public CurrentWeather() {
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
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
        dest.writeInt(id);
        dest.writeParcelable(main, flags);
        dest.writeString(name);
        dest.writeParcelable(sys, flags);
        dest.writeParcelableArray(weather, flags);

    }
}
