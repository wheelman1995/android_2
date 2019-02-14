package ru.wheelman.weather.data.data_sources.network.forecasted.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class City implements Parcelable {
//     "id":504341,
//      "name":"Pskov",
//      "coord":{
//         "lat":57.8174,
//         "lon":28.3344
//      },
//      "country":"RU",
//      "population":201990

//    city.id City$$ ID
//city.name City$$ name
//city.coord
//city.coord.lat City$$ geo location, latitude
//city.coord.lon City$$ geo location, longitude
//city.country Country code (GB, JP etc.)

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("population")
    @Expose
    private int population;

    private City(Parcel in) {
        id = in.readInt();
        name = in.readString();
        country = in.readString();
        population = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(country);
        dest.writeInt(population);
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }
}
