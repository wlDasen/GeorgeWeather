package net.sunniwell.georgeweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by admin on 17/9/21.
 */

public class County extends DataSupport {
    private int countyId;
    private String countyName;
    private String countyWeatherCode;

    public int getCountyId() {
        return countyId;
    }

    public void setCountyId(int countyId) {
        this.countyId = countyId;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getCountyWeatherCode() {
        return countyWeatherCode;
    }

    public void setCountyWeatherCode(String countyWeatherCode) {
        this.countyWeatherCode = countyWeatherCode;
    }
}
