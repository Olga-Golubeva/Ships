package com.company.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Field {

    public Field(){

    }

    public static final int FIELD_COUNT = 10;

    public Field(boolean isMyField) {

        this.isMyField = isMyField;

        columnHeaders.put('K', 0);
        columnHeaders.put('A', 1);
        columnHeaders.put('R', 2);
        columnHeaders.put('T', 3);
        columnHeaders.put('U', 4);
        columnHeaders.put('P', 5);
        columnHeaders.put('E', 6);
        columnHeaders.put('L', 7);
        columnHeaders.put('I', 8);
        columnHeaders.put('S', 9);

        for (int i = 0; i < 10; i++) {
            rowHeaders.put(i + 1, i);
        }
        initialize();
    }

    private Map<Character, Integer> columnHeaders = new LinkedHashMap<>();
    @JsonIgnore
    private Map<Integer, Integer> rowHeaders = new LinkedHashMap<>();

    private List<Ship> ships = new ArrayList<>();
    private SquareEnum[][] squares = new SquareEnum[FIELD_COUNT][FIELD_COUNT];

    private boolean isMyField;

    @JsonIgnore
    public boolean isAllShipsInPlace() {
        return ships.size() == 10;
    }

    public Iterable<Ship> getShips(){
        return ships;
    }

    public AddShipResultEnum addShip(@NotNull Ship ship) {

        if (ship == null) {
            return AddShipResultEnum.SHIP_IS_NULL;
        }

        if (checkShips(ship.getType())) {
            return AddShipResultEnum.SHIPS_OF_THIS_TYPE_ALREADY_ADDED;
        }

        if (!checkShipPosition(ship)) {
            return AddShipResultEnum.NO_POSITIONS;
        }

        if(!checkShipPositionValidity(ship)){
            return AddShipResultEnum.POSITIONS_OVERLAP;
        }

        ships.add(ship);
        updateFields();

        return AddShipResultEnum.OK;
    }

    public boolean shoot(char column, int row) {
        var x = columnHeaders.get(column);
        var y = rowHeaders.get(row);

        return shoot(x, y);
    }

    public boolean shoot(int x, int y) {
        if (x < 0 || x > FIELD_COUNT - 1) {
            return false;
        }
        if (y < 0 || y > FIELD_COUNT - 1) {
            return false;
        }
        if (squares[y][x] != SquareEnum.EMPTY && squares[y][x] != SquareEnum.SHIP) {
            return false;
        }

        boolean isHit = false;
        for (var ship : ships) {
            isHit = ship.shoot(x, y);
            if (isHit) {
                break;
            }
        }

        if (!isHit) {
            squares[y][x] = SquareEnum.SHOOT;
        }

        updateFields();

        return isHit;

    }

    public void initialize() {
        ships.clear();
        for (int i = 0; i < FIELD_COUNT; i++) {
            for (int j = 0; j < FIELD_COUNT; j++) {
                squares[i][j] = SquareEnum.EMPTY;
            }
        }
    }
@JsonIgnore
    public Iterable<Character> getColumnHeaders() {
        return columnHeaders.keySet();
    }

    @JsonIgnore
    public int getShipCount(){
        return ships.size();
    }

    public SquareEnum getSquareValue(int y, int x){
        return squares[y][x];
    }

    @JsonIgnore
    public boolean isAllShipsSunk(){
     return  !ships.stream().filter(s -> !s.isSunk()).findFirst().isPresent();

    }

    private void updateFields() {
        for (var ship : ships) {
            for (var position : ship.getPositions()) {
                if (position.isHit()) {
                    if (ship.isSunk()) {
                        squares[position.getY()][position.getX()] = SquareEnum.SUNK;
                    }else {
                        squares[position.getY()][position.getX()] = SquareEnum.HIT;
                    }
                    } else {
                        squares[position.getY()][position.getX()] = SquareEnum.SHIP;
                }
            }
        }
    }

    private boolean checkShips(@NotNull ShipTypeEnum shipType) {
        var count = ships.stream().filter(s -> s.getType() == shipType).count();
        switch (shipType) {
            case ONE:
                return count == 4;
            case TWO:
                return count == 3;
            case THREE:
                return count == 2;
            case FOUR:
                return count == 1;
        }

        return false;
    }

    private boolean checkShipPosition(@NotNull Ship ship) {
        switch (ship.getType()) {
            case ONE:
                return ship.getPositionCount() == 1;
            case TWO:
                return ship.getPositionCount() == 2;
            case THREE:
                return ship.getPositionCount() == 3;
            case FOUR:
                return ship.getPositionCount() == 4;
        }
        return false;
    }

    private boolean checkShipPositionValidity(@NotNull Ship ship) {
        //check if locations are in the field
        var positions = ship.getPositions();

        for (var position : positions) {
            if (position.getX() < 0
                    || position.getX() > 9
                    || position.getY() < 0
                    || position.getY() > 9)
            {
                return false;
            }
        }

        // check if location items are near
        if (ship.getType() != ShipTypeEnum.ONE) {
            //check if fields are horizontal

            boolean isOrientationValid = false;

            switch (ship.getType()) {
                case TWO:
                    isOrientationValid = checkPositionDirection(ship, 2);
                    break;
                case THREE:
                    isOrientationValid = checkPositionDirection(ship, 3);
                    break;
                case FOUR:
                    isOrientationValid = checkPositionDirection(ship, 4);
                    break;
            }

            if (!isOrientationValid) {
                return false;
            }
        }
        // check if our location does not collide with other ships

       return checkShipBuffer(ship);

    }

    private boolean checkShipBuffer(Ship ship) {

        Set<String> existingShipPositions = new TreeSet<>();

        for (Ship existingShip : ships) {
            for (var shipPosition: existingShip.getPositions()) {
                existingShipPositions.add(shipPosition.getX() + "_" + shipPosition.getY());
            }
        }

        Set<String> currentShipPositionsAndBuffer = new TreeSet<>();

        switch (ship.getType()) {
            case ONE:
                createShipBuffer(ship, currentShipPositionsAndBuffer, 1);
                break;
            case TWO:
                createShipBuffer(ship, currentShipPositionsAndBuffer, 2);
                break;
            case THREE:
                createShipBuffer(ship, currentShipPositionsAndBuffer, 3);
                break;
            case FOUR:
                createShipBuffer(ship, currentShipPositionsAndBuffer, 4);
                break;
        }

        //does positions overlap
        int firstCount = existingShipPositions.size();
        int secondCount = currentShipPositionsAndBuffer.size();

        existingShipPositions.addAll(currentShipPositionsAndBuffer);

        return firstCount + secondCount == existingShipPositions.size();
    }

    private void createShipBuffer(Ship ship, Set<String> currentShipPositionsAndBuffer, int i) {
        int startX = 0;
        int startY = 0;
        boolean firstTime = true;

        for (var position : ship.getPositions()) {
            currentShipPositionsAndBuffer.add(getPositionString(position.getX(), position.getY()));

            if (firstTime) {
                startX = position.getX();
                startY = position.getY();
                firstTime = false;
            }
        }
        if (ship.getOrientation() == ShipOrientationEnum.HORIZONTAL) {
            currentShipPositionsAndBuffer.add(getPositionString(startX - 1, startY));
            currentShipPositionsAndBuffer.add(getPositionString(startX + i, startY));
            for (int j = 0; j < i + 2; j++) {
                currentShipPositionsAndBuffer.add(getPositionString(startX - 1 + j, startY - 1));
                currentShipPositionsAndBuffer.add(getPositionString(startX - 1 + j, startY + 1));
            }

        } else {
            currentShipPositionsAndBuffer.add(getPositionString(startX, startY - 1));
            currentShipPositionsAndBuffer.add(getPositionString(startX, startY + i));
            for (int j = 0; j < i+2; j++) {
                currentShipPositionsAndBuffer.add(getPositionString(startX - 1, startY - 1+j));
                currentShipPositionsAndBuffer.add(getPositionString(startX + 1, startY - 1+j));
            }
        }
    }

    private String getPositionString (int x, int y){
        return x + "_" + y;
    }

    private boolean checkPositionDirection(@NotNull Ship ship, int positionCount) {

        List<ShipPositionStatus> positions = new ArrayList<>();
        ship.getPositions().forEach(positions::add);

        for (int i = 0; i < positionCount - 1; i++) {
            if (ship.getOrientation() == ShipOrientationEnum.HORIZONTAL) {
                if (positions.get(i).getX() + 1 != positions.get(i + 1).getX()) {
                    return false;
                }
                if (positions.get(i).getY() != positions.get(i + 1).getY()) {
                    return false;
                }
            } else {
                if (positions.get(i).getY() + 1 != positions.get(i + 1).getY()) {
                    return false;
                }
                if (positions.get(i).getX() != positions.get(i + 1).getX()) {
                    return false;
                }
            }
        }
        return true;
    }

}
