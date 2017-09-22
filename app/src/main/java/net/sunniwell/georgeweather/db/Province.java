package net.sunniwell.georgeweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by admin on 17/9/21.
 */

public class Province extends DataSupport {
    private int id;
    private String name;

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
