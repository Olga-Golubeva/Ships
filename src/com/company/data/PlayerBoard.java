package com.company.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PlayerBoard {

    public PlayerBoard(boolean isVisible){
        myField = new Field(true);
        opponentsField = new Field(false);
        this.isVisible = isVisible;
    }

    public PlayerBoard(){

    }

    public Field getMyField() {
        return myField;
    }


    public Field getOpponentsField() {
        return opponentsField;
    }

    private Field myField;
    private Field opponentsField;
    private boolean isVisible;

   @JsonGetter("isVisible")
    public boolean isVisible() {
        return isVisible;
    }

    @JsonSetter("isVisible")
    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    @JsonIgnore
    public Iterable<Character> getColumnHeaders(){
        return myField.getColumnHeaders();
    }

    public void copyMyShipInfoToOpponentsField(Field opField){
        for (var ship : myField.getShips()){
            ShipPositionStatus position = ship.getStartPosition();
            Ship newShip = new Ship(ship.getType(), ship.getOrientation(), position.getX(), position.getY());
            opField.addShip(newShip);
        }
    }
}
