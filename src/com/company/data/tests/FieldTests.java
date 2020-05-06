package com.company.data.tests;

import com.company.data.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FieldTests {
    @Test

    public void addShip_returns_SHIP_IS_NULL (){
        Field field = new Field(true);
//        var result = field.addShip(null);

//        Assertions.assertTrue(result == AddShipResultEnum.SHIP_IS_NULL);
//        Exception result = Assertions.assertThrows(IllegalArgumentException.class);
    }

    @Test

    public void addShip_returns_SHIPS_OF_THIS_TYPE_can_be_added (){
        Field field = new Field(true);
        Ship ship = new Ship(ShipTypeEnum.ONE, ShipOrientationEnum.NONE,1,1);

        var result = field.addShip(ship);

        Assertions.assertFalse(result == AddShipResultEnum.SHIPS_OF_THIS_TYPE_ALREADY_ADDED);

    }

    @Test

    public void addShip_does_not_return_no_positions (){
        Field field = new Field(true);
        Ship ship = new Ship(ShipTypeEnum.ONE, ShipOrientationEnum.NONE, 1,1);

        var result = field.addShip(ship);

        Assertions.assertFalse(result == AddShipResultEnum.NO_POSITIONS);

    }

    @Test

    public void addShip_can_add_one_position_ship (){
        Field field = new Field(true);
        Ship ship = new Ship(ShipTypeEnum.ONE, ShipOrientationEnum.NONE, 1,1);

        var result = field.addShip(ship);

        Assertions.assertTrue(result == AddShipResultEnum.OK);
        Assertions.assertTrue(field.getShipCount() == 1);

    }

    @Test

    public void addShip_positions_overlap (){
        Field field = new Field(true);
        Ship ship1 = new Ship(ShipTypeEnum.ONE, ShipOrientationEnum.NONE, 1,1);

        field.addShip(ship1);

        Ship ship2 = new Ship(ShipTypeEnum.ONE, ShipOrientationEnum.NONE, 1,1);

        var result = field.addShip(ship2);

        Assertions.assertTrue(result == AddShipResultEnum.POSITIONS_OVERLAP);

    }

    @Test

    public void addShip_positions_overlap_when_placed_near (){
        Field field = new Field(true);
        Ship ship1 = new Ship(ShipTypeEnum.ONE, ShipOrientationEnum.NONE, 1,1);

        field.addShip(ship1);

        Ship ship2 = new Ship(ShipTypeEnum.ONE, ShipOrientationEnum.NONE, 2,1);

        var result = field.addShip(ship2);

        Assertions.assertTrue(result == AddShipResultEnum.POSITIONS_OVERLAP);

    }

    @Test

    public void addShip_positions_do_not_overlap_then_space_is_used (){
        Field field = new Field(true);
        Ship ship1 = new Ship(ShipTypeEnum.ONE, ShipOrientationEnum.NONE, 1,1);

        field.addShip(ship1);

        Ship ship2 = new Ship(ShipTypeEnum.ONE, ShipOrientationEnum.NONE, 3,1);

        var result = field.addShip(ship2);

        Assertions.assertTrue(result == AddShipResultEnum.OK);

    }

    @Test

    public void addShip_returns_ships_of_this_type_already_exist (){
        Field field = new Field(true);
        Ship ship1 = new Ship(ShipTypeEnum.FOUR, ShipOrientationEnum.HORIZONTAL, 1,1);

        field.addShip(ship1);

        Ship ship2 = new Ship(ShipTypeEnum.FOUR, ShipOrientationEnum.HORIZONTAL, 3,1);

        var result = field.addShip(ship2);

        Assertions.assertTrue(result == AddShipResultEnum.SHIPS_OF_THIS_TYPE_ALREADY_ADDED);

    }

}
