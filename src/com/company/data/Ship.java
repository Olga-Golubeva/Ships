package com.company.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Ship {

    public Ship(){

    }

    private ShipOrientationEnum orientation;

    public ShipTypeEnum getType() {
        return type;
    }

    private ShipTypeEnum type;

    private boolean isSunk;
    private List<ShipPositionStatus> positions = new ArrayList<>();

    public Ship(ShipTypeEnum typeEnum, ShipOrientationEnum orientation, int x, int y) {
        this.type = typeEnum;
        this.orientation = orientation;

        generatePositions(x, y);
    }

@JsonIgnore
    public int getPositionCount() {
        return positions.size();
    }

    public Iterable<ShipPositionStatus> getPositions() {
        return positions;
    }

    public boolean shoot(int x, int y) {
        var result = positions.stream()
                .filter(p -> p.getX() == x && p.getY() == y)
                .findFirst();
        if (result.isPresent()) {
            var position = result.get();
            if (!position.isHit()) {
                position.markAsHit();

                var notHitResult = positions.stream().filter(p -> p.isHit() == false).findAny();

                if (!notHitResult.isPresent()) {
                    isSunk = true;
                }
            }
            return true;
        }
        return false;
    }

    public ShipOrientationEnum getOrientation() {
        return orientation;
    }

    @JsonIgnore
    public ShipPositionStatus getStartPosition(){
        return new ShipPositionStatus(positions.get(0).getX(), positions.get(0).getY());
    }

    private void generatePositions(int x, int y) {
        switch (type) {
            case ONE:
                generatePositions(1, x, y);
                break;
            case TWO:
                generatePositions(2, x, y);
                break;
            case THREE:
                generatePositions(3, x, y);
                break;
            case FOUR:
                generatePositions(4, x, y);
                break;
        }
    }

    private void generatePositions(int shipLength, int x, int y) {

        positions.clear();
        for (int i = 1; i <= shipLength; i++) {
            positions.add(new ShipPositionStatus(x, y));
            if (orientation == ShipOrientationEnum.HORIZONTAL) {
                ++x;
            } else {
                ++y;
            }
        }
    }
    @JsonIgnore
    public boolean isSunk() {
        return isSunk;
    }
}
