package net.sunniwell.georgeweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 2017/10/10.
 */

public class Basic {
    public String city;
    @SerializedName("cnty")
    public String country;
    public String id;
    public String lat;
    public String lon;
    public Update update;
    public class Update {
        public String loc;
        public String utc;
    }

    @Override
    public String toString() {
        return "city:" + city + ",country:" + country + ",id:" + id + ",lat:" + lat + ",lon:"
                + lon + ",update-loc:" + update.loc + ",update-utc:" + update.utc;
    }
}
