package com.company.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ShipPositionStatus {

    public ShipPositionStatus(){

    }

    private int x;
    private int y;
    private boolean isHit;

    public ShipPositionStatus(int x, int y) {
        this.x = x;
        this.y = y;
        isHit = false;
    }

    public void markAsHit(){
        isHit = true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @JsonIgnore
    public boolean isHit() {
        return isHit;
    }
}
