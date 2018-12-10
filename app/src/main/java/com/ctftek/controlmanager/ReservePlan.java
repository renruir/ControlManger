package com.ctftek.controlmanager;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by renrui on 2017/10/25/0025.
 */

@Entity
public class ReservePlan {
    @Id(autoincrement = true)
    private long id;
    private int index;
    private String name;
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getIndex() {
        return this.index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    @Generated(hash = 773766435)
    public ReservePlan(long id, int index, String name) {
        this.id = id;
        this.index = index;
        this.name = name;
    }
    @Generated(hash = 2143201070)
    public ReservePlan() {
    }

}
