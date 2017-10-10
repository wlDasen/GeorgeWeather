package net.sunniwell.georgeweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by admin on 17/9/21.
 */

public class City extends DataSupport {
    private int id;
    private String name;
    private int provinceId;
    private int cityCode;

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
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
}
