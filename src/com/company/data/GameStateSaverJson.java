package com.company.data;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class GameStateSaverJson implements GameStateSaver {
    @Override
    public void save(File file, ShipGame game) {
        var mapper = new ObjectMapper().writerWithDefaultPrettyPrinter();

        try {
            mapper.writeValue(file, game);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ShipGame load(File file) {
        var mapper = new ObjectMapper();
        try {
            var game =  mapper.readValue(file, ShipGame.class);
            return game;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ShipGame();
    }
}
