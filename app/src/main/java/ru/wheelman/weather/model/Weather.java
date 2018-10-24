package ru.wheelman.weather.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Weather implements Parcelable {
//      id	Weather condition id	-	-	-
//    main	Group of weather parameters (Rain, Snow, Extreme etc.)	-	-	-
//    description	Weather condition within the group	-	-	-
//    icon	Weather icon id

    public static final Creator<Weather> CREATOR = new Creator<Weather>() {
        @Override
        public Weather createFromParcel(Parcel in) {
            return new Weather(in);
        }

        @Override
        public Weather[] newArray(int size) {
            return new Weather[size];
        }
    };
    @SerializedName("id")
    private String id;
    @SerializedName("main")
    private String main;
    @SerializedName("description")
    private String description;
    @SerializedName("icon")
    private String icon;

    protected Weather(Parcel in) {
        id = in.readString();
        main = in.readString();
        description = in.readString();
        icon = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(main);
        dest.writeString(description);
        dest.writeString(icon);
    }
}
