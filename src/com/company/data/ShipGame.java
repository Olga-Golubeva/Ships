package com.company.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.io.DataInput;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@JsonAutoDetect (fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ShipGame {

    private GameStateSaver gameStateSaver;

    public ShipGame (GameStateSaver gameStateSaver){
        this.gameStateSaver = gameStateSaver;
        restart();
    }

    public ShipGame() {
        restart();
    }

    private boolean isPlayer1Active;
    private boolean canSwitchActivePlayer;
    private boolean isGameStarted;


    private PlayerBoard playerBoard1;
    private PlayerBoard playerBoard2;

    @JsonIgnore
    public boolean isPlayer1Active() {
        return isPlayer1Active;
    }

    public static ShipGame load (GameStateSaver gameStateSaver, File file){
        return gameStateSaver.load(file);
    }

    public void save(File file){
        gameStateSaver.save(file, this);
    }
    public static ShipGame getObjectData (ShipGame game){
        game.checkWinner();
        game.startGame();
        game.isGameStarted();

        return game;
    }

//            FileWriter fileWriter = new FileWriter(file);
//            fileWriter.write(String.valueOf(ShipGame.class));
//            fileWriter.close();

    public void restart() {
        playerBoard1 = new PlayerBoard(true);
        playerBoard2 = new PlayerBoard(false);
        isPlayer1Active = true;
        isGameStarted = false;
        canSwitchActivePlayer = false;
    }

    public void changeActivePlayer (){
        isPlayer1Active = !isPlayer1Active;
        canSwitchActivePlayer = false;

        playerBoard1.setVisible(isPlayer1Active);
        playerBoard2.setVisible(!isPlayer1Active);
    }

   @JsonIgnore
    public PlayerBoard getActiveBoard(){
        return isPlayer1Active ? playerBoard1 : playerBoard2;
    }

    @JsonIgnore
    public boolean allShipsInPlaceGameStarted (){
      return playerBoard1.getMyField().isAllShipsInPlace() && playerBoard2.getMyField().isAllShipsInPlace();
    }

    @JsonIgnore
    public boolean isGameStarted() {
        return isGameStarted;
    }

    public void startGame(){
        isGameStarted = true;
        changeActivePlayer();

        playerBoard1.copyMyShipInfoToOpponentsField(playerBoard2.getOpponentsField());
        playerBoard2.copyMyShipInfoToOpponentsField(playerBoard1.getOpponentsField());
    }

    @JsonIgnore
    public boolean isCanSwitchActivePlayer() {
        return canSwitchActivePlayer;
    }
    @JsonIgnore
    public boolean hasPlayer1PlacedAllShips(){
        return playerBoard1.getMyField().isAllShipsInPlace();
    }
    @JsonIgnore
    public boolean hasPlayer2PlacedAllShips(){
        return playerBoard2.getMyField().isAllShipsInPlace();
    }

    public AddShipResultEnum addShip(Ship ship){
        var activeBoard = getActiveBoard();
        return activeBoard.getMyField().addShip(ship);
    }

    public boolean shoot(int x, int y){
        var activeBoard = getActiveBoard();
        var result = activeBoard.getOpponentsField().shoot(x,y);

        Field otherPlayersMyField;
        if(isPlayer1Active){
            otherPlayersMyField = playerBoard2.getMyField();

        }else {
            otherPlayersMyField = playerBoard1.getMyField();
        }

        //notify
        otherPlayersMyField.shoot(x,y);

        if(!result){
            canSwitchActivePlayer = true;
        }
        return result;

    }
    @JsonIgnore
    public boolean isGameFinished(){
        return playerBoard1.getMyField().isAllShipsSunk()
                ||playerBoard2.getMyField().isAllShipsSunk();
    }

    public WinnerEnum checkWinner(){
        if(playerBoard1.getMyField().isAllShipsSunk()){
            return WinnerEnum.PLAYER_2;
        }
        if(playerBoard2.getMyField().isAllShipsSunk()){
            return WinnerEnum.PLAYER_1;
        }
        return WinnerEnum.NONE;
    }

    public boolean checkShootPosition(int x, int y) {
        var activeBoard = getActiveBoard();
        var posInfo = activeBoard.getOpponentsField().getSquareValue(y,x);
        return posInfo == SquareEnum.EMPTY || posInfo == SquareEnum.SHIP;
    }
}
