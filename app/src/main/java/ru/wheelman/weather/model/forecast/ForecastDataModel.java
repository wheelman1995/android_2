package ru.wheelman.weather.model.forecast;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ForecastDataModel implements Parcelable {

// "cod":"200",
//   "message":0.0049,
//   "cnt":40,
//   "list":[  ],
//   "city":{  }

//    code Internal parameter
//message Internal parameter
//    cnt Number of lines returned by this API call

    public static final Creator<ForecastDataModel> CREATOR = new Creator<ForecastDataModel>() {
        @Override
        public ForecastDataModel createFromParcel(Parcel in) {
            return new ForecastDataModel(in);
        }

        @Override
        public ForecastDataModel[] newArray(int size) {
            return new ForecastDataModel[size];
        }
    };
    @SerializedName("list")
    private ThreeHourData[] threeHourData;
    @SerializedName("city")
    private City city;

    private ForecastDataModel(Parcel in) {
        threeHourData = in.createTypedArray(ThreeHourData.CREATOR);
        city = in.readParcelable(City.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(threeHourData, flags);
        dest.writeParcelable(city, flags);
    }

    public ThreeHourData[] getThreeHourData() {
        return threeHourData;
    }

    public void setThreeHourData(ThreeHourData[] threeHourData) {
        this.threeHourData = threeHourData;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
