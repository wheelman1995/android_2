package ru.wheelman.weather.data.data_sources.network.forecasted.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ForecastedWeather implements Parcelable {

// "cod":"200",
//   "message":0.0049,
//   "cnt":40,
//   "list":[  ],
//   "city":{  }

//    code Internal parameter
//message Internal parameter
//    cnt Number of lines returned by this API call

    public static final Creator<ForecastedWeather> CREATOR = new Creator<ForecastedWeather>() {
        @Override
        public ForecastedWeather createFromParcel(Parcel in) {
            return new ForecastedWeather(in);
        }

        @Override
        public ForecastedWeather[] newArray(int size) {
            return new ForecastedWeather[size];
        }
    };
    @SerializedName("list")
    private OneDataPiece[] oneDatumPieces;
    @SerializedName("city")
    private City city;

    private ForecastedWeather(Parcel in) {
        oneDatumPieces = in.createTypedArray(OneDataPiece.CREATOR);
        city = in.readParcelable(City.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(oneDatumPieces, flags);
        dest.writeParcelable(city, flags);
    }

    public OneDataPiece[] getOneDatumPieces() {
        return oneDatumPieces;
    }

    public void setOneDatumPieces(OneDataPiece[] oneDatumPieces) {
        this.oneDatumPieces = oneDatumPieces;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
