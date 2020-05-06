package com.company;

import com.company.data.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ShipGameForm extends JFrame {

    private static final int GAME_WIDTH = 1140;

    JPanel mainPanel;
    JPanel buttonPanel;
    JPanel playerBoard;
    JPanel shipSelectionPanel;

    JPanel myFieldPanel;
    JPanel opponentsFieldPanel;

    JButton btnNew;
    JButton btnLoad;
    JButton btnSave;
    JLabel lblActivePlayer;

    JButton[][] myFieldButtons = new JButton[Field.FIELD_COUNT][Field.FIELD_COUNT];
    JButton[][] opponentsFieldButtons = new JButton[Field.FIELD_COUNT][Field.FIELD_COUNT];

    JComboBox<String> cbOrientation;
    JComboBox<String> cbShipType;
    JButton btnReady;
    JButton btnSwitchPlayer;

    JFileChooser fileChooser;

    GameStateSaver gameStateSaver = new GameStateSaverJson(); //dependency injection

    private ShipGame shipGame = new ShipGame(gameStateSaver);
    public static final int ELEMENT_SIZE = 50;

    public ShipGameForm() {
        super("Kartupelis");
        this.setSize(GAME_WIDTH, 700);
        this.setResizable(false);

        fileChooser = new JFileChooser();

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        initializeButtonPanel();
        initializeFieldPanel();
        initializeShipPanel();

//        draw();

        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 1;
        c.weightx = 1;
        c.ipady = 50;

        mainPanel.add(buttonPanel, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.ipady = 600;

        mainPanel.add(playerBoard, c);

        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1;
        c.weighty = 1;
        c.ipady = 50;

        mainPanel.add(shipSelectionPanel, c);

        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }


    private void initializeShipPanel() {
        shipSelectionPanel = new JPanel();
//        shipSelectionPanel.setBackground(Color.GREEN);
        shipSelectionPanel.setLayout(new GridLayout(1, 4));

        String[] orientationValues = new String[]{"Horizontal", "Vertical"};

        cbOrientation = new JComboBox<>(orientationValues);
        cbOrientation.setName("cbOrientation");

        String[] shipValues = new String[]{"One", "Two", "Three", "Four"};

        cbShipType = new JComboBox<>(shipValues);
        cbShipType.setName("cbShipType");

        btnReady = new JButton();
        btnReady.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (shipGame.isPlayer1Active()) {
                    initializeCheckBoxStates();
                    shipGame.changeActivePlayer();

                } else {
                    setFieldButtonState(false);
                    updateCheckBoxStates();
                    shipGame.startGame();
                }

                updateReadyButtonState();
                redrawFields();

            }
        });

        btnSwitchPlayer = new JButton("Switch");
        btnSwitchPlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shipGame.changeActivePlayer();
                redrawFields();
                updateSwitchButtonState();
            }
        });

        shipSelectionPanel.add(cbOrientation);
        shipSelectionPanel.add(cbShipType);
        shipSelectionPanel.add(btnReady);
        shipSelectionPanel.add(btnSwitchPlayer);

        initializeShipHandlingButtonStates();

    }

    private void initializeShipHandlingButtonStates() {
        btnReady.setVisible(false);
        btnReady.setEnabled(false);
        btnReady.setText("Continue");

        cbShipType.setVisible(true);
        cbShipType.setEnabled(true);

        cbOrientation.setVisible(true);
        cbOrientation.setEnabled(true);

        btnSwitchPlayer.setVisible(false);
        btnSwitchPlayer.setEnabled(false);

        initializeCheckBoxStates();

    }

    private void updateSwitchButtonState() {
        btnSwitchPlayer.setEnabled(shipGame.isCanSwitchActivePlayer());
        btnSwitchPlayer.setVisible(shipGame.isCanSwitchActivePlayer());
    }

    private void redrawFields() {
        redrawMyField();
        redrawOpponentsField();
    }

    private void updateReadyButtonState() {
        if (shipGame.isPlayer1Active()) {
            btnReady.setEnabled(shipGame.hasPlayer1PlacedAllShips());
            btnReady.setVisible(shipGame.hasPlayer1PlacedAllShips());
        } else {
            btnReady.setText("Start game");
            btnReady.setEnabled(shipGame.hasPlayer2PlacedAllShips());
            btnReady.setVisible(shipGame.hasPlayer2PlacedAllShips());
        }
        if (shipGame.isGameStarted()) {
            btnReady.setVisible(false);
            btnReady.setEnabled(false);
        }
    }

    private void updateCheckBoxStates() {
        cbShipType.setVisible(false);
        cbShipType.setEnabled(false);

        cbOrientation.setVisible(false);
        cbOrientation.setEnabled(false);
    }

    private void setFieldButtonState(boolean myFieldsEnabled) {
        for (int i = 0; i < Field.FIELD_COUNT; i++) {
            for (int j = 0; j < Field.FIELD_COUNT; j++) {
                myFieldButtons[j][i].setEnabled(myFieldsEnabled);
                opponentsFieldButtons[j][i].setEnabled(!myFieldsEnabled);
            }
        }
    }

    private void initializeCheckBoxStates() {
        cbOrientation.setSelectedIndex(0);
        cbShipType.setSelectedIndex(0);
    }

    private void initializeFieldPanel() {
        playerBoard = new JPanel();
        playerBoard.setLayout(new GridLayout(1, 2));

        myFieldPanel = new JPanel();
        myFieldPanel.setBackground(Color.LIGHT_GRAY);

        opponentsFieldPanel = new JPanel();
        opponentsFieldPanel.setBackground(Color.LIGHT_GRAY);

        var board = shipGame.getActiveBoard();

        addHeaders(myFieldPanel, board);
        addHeaders(opponentsFieldPanel, board);

        addFields(myFieldPanel, myFieldButtons, true);
        addFields(opponentsFieldPanel, opponentsFieldButtons, false);

        playerBoard.add(opponentsFieldPanel);
        playerBoard.add(myFieldPanel);
    }

    private void addFields(JPanel myFieldPanel, JButton[][] myFieldButtons, boolean isMyField) {

        int yPos = ELEMENT_SIZE;
        for (int i = 0; i < Field.FIELD_COUNT; i++) {
            int xPos = ELEMENT_SIZE;
            for (int j = 0; j < Field.FIELD_COUNT; j++) {
                JButton btn = new JButton();
                btn.setName("btn_" + i + "_" + j);
                btn.setBounds(xPos, yPos, ELEMENT_SIZE, ELEMENT_SIZE);
                btn.setBackground(Color.WHITE);
                btn.setEnabled(isMyField);

                btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        if (shipGame.allShipsInPlaceGameStarted()) {
                            gameInProgressAction(btn);
                        } else {
                            gameNotStartedAction(btn);
                        }
                    }
                });

                myFieldButtons[i][j] = btn;
                myFieldPanel.add(btn);
                xPos += ELEMENT_SIZE;
            }
            yPos += ELEMENT_SIZE;
        }
    }

    private void gameInProgressAction(@NotNull JButton btn) {
        if (shipGame.isCanSwitchActivePlayer()) {
            return;
        }

        var position = getShootPosition(btn.getName());

        if(!shipGame.checkShootPosition(position.getX(), position.getY())){
        return;
        }

        shipGame.shoot(position.getX(), position.getY());

        redrawOpponentsField();
        updateSwitchButtonState();

        var gameResult = shipGame.checkWinner();
        switch (gameResult) {
            case PLAYER_1:
                JOptionPane.showMessageDialog(null, "Player 1 won!");
                gameHasFinished();
                break;
            case PLAYER_2:
                JOptionPane.showMessageDialog(null, "Player 2 won!");
                gameHasFinished();
                break;
        }
    }

    private void gameNotStartedAction(@NotNull JButton btn) {
        var shipType = ((String) cbShipType.getSelectedItem()).toUpperCase();
        var orientation = ((String) cbOrientation.getSelectedItem()).toUpperCase();

        var position = getShootPosition(btn.getName());

        //create ship
        Ship ship = new Ship(ShipTypeEnum.valueOf(shipType), ShipOrientationEnum.valueOf(orientation), position.getX(), position.getY());

        //add ship
        AddShipResultEnum addResult = shipGame.addShip(ship);

        if (addResult == AddShipResultEnum.OK) {
            redrawMyField();
        }
        updateReadyButtonState();
    }

    private void initializeButtonPanel() {
        buttonPanel = new JPanel();
//        buttonPanel.setBackground(Color.RED);
        buttonPanel.setLayout(new GridLayout(1, 4));
        buttonPanel.setSize(GAME_WIDTH, 50);

        btnNew = new JButton();
        btnNew.setText("New game");
        btnNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                var result = JOptionPane.showConfirmDialog(null, "Do you want to restart the gme?");
                if (result == 0) {
                    shipGame.restart();
                    redrawFields();
                    initializeShipHandlingButtonStates();
                    setFieldButtonState(true);
                }
            }
        });

        btnLoad = new JButton();
        btnLoad.setText("Load");
        btnLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogResult = fileChooser.showOpenDialog(mainPanel);
                if (dialogResult == JFileChooser.APPROVE_OPTION){
                    File file = fileChooser.getSelectedFile();
                    shipGame = shipGame.load(gameStateSaver, file);
                    redrawFields();
                    initializeButtonStateAfterLoad();
                    setFieldButtonState(!shipGame.isGameStarted());
                    setPlayerLabelText();
                }
            }
        });

        btnSave = new JButton();
        btnSave.setText("Save");
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogResult = fileChooser.showSaveDialog(mainPanel);
                if (dialogResult == JFileChooser.APPROVE_OPTION){
                    File file = fileChooser.getSelectedFile();
                    shipGame.save(file);

                }
            }
        });

        lblActivePlayer = new JLabel();
        setPlayerLabelText();

        buttonPanel.add(btnNew);
        buttonPanel.add(btnLoad);
        buttonPanel.add(btnSave);
        buttonPanel.add(lblActivePlayer);
    }

    private void initializeButtonStateAfterLoad() {
        cbShipType.setEnabled(!shipGame.isGameStarted());
        cbShipType.setVisible(!shipGame.isGameStarted());

        cbOrientation.setEnabled(!shipGame.isGameStarted());
        cbOrientation.setVisible(!shipGame.isGameStarted());

        if (shipGame.isPlayer1Active()&& !shipGame.isGameStarted()){
            btnReady.setText("Continue");
            if (shipGame.hasPlayer1PlacedAllShips()){
                btnReady.setVisible(true);
                btnReady.setEnabled(true);
            }

        }else if (!shipGame.isPlayer1Active() && !shipGame.isGameStarted()){
            btnReady.setText("Start game");
            if (shipGame.hasPlayer2PlacedAllShips()){
                btnReady.setVisible(true);
                btnReady.setEnabled(true);
            }
        }else {
            btnReady.setVisible(false);
            btnReady.setEnabled(false);
        }

        btnSwitchPlayer.setVisible(shipGame.isCanSwitchActivePlayer());
        btnSwitchPlayer.setEnabled(shipGame.isCanSwitchActivePlayer());


    }

    public void setPlayerLabelText() {
        if (shipGame.isPlayer1Active()) {
            lblActivePlayer.setText("Player 1");
        } else {
            lblActivePlayer.setText("Player 2");
        }
    }

    private void draw() {
        var board = shipGame.getActiveBoard();

        playerBoard = new JPanel();
        playerBoard.setLayout(new GridLayout(1, 2));

        JPanel myPanel = new JPanel();
        myPanel.setLayout(new GridLayout(11, 11));
        addHeaders(myPanel, board);

        JPanel opponentsPanel = new JPanel();
        opponentsPanel.setLayout(new GridLayout(11, 11));
        addHeaders(opponentsPanel, board);

        playerBoard.add(opponentsPanel);
        playerBoard.add(myPanel);

    }

    private void addHeaders(@NotNull JPanel panel, @NotNull PlayerBoard board) {
        panel.setLayout(null);

        int currentPos = 50;
        for (var header : board.getColumnHeaders()) {
            JLabel label = new JLabel(Character.toString(header), SwingConstants.CENTER);
            label.setBounds(currentPos, 0, ELEMENT_SIZE, ELEMENT_SIZE);
            panel.add(label);
            currentPos += ELEMENT_SIZE;
        }

        currentPos = 50;
        for (int i = 1; i <= Field.FIELD_COUNT; i++) {
            JLabel label = new JLabel(Integer.toString(i), SwingConstants.CENTER);
            label.setBounds(0, currentPos, ELEMENT_SIZE, ELEMENT_SIZE);
            panel.add(label);
            currentPos += ELEMENT_SIZE;
        }


//            for (var header : board.getColumnHeaders()) {
//                JLabel label = new JLabel();
//                label.setText(Character.toString(header));
//                panel.add(label);
//
//            }
//
//            for (int i = 1; i <= 10; i++) {
//                JLabel label = new JLabel();
//                label.setText(Integer.toString(i));
//                panel.add(label);
//            }
    }

    private void redrawMyField() {
        var board = shipGame.getActiveBoard();
        var myFields = board.getMyField();
        for (int i = 0; i < Field.FIELD_COUNT; i++) {
            for (int j = 0; j < Field.FIELD_COUNT; j++) {

                switch (myFields.getSquareValue(j, i)) {
                    case EMPTY:
                        myFieldButtons[j][i].setBackground(Color.WHITE);
                        break;
                    case SUNK:
                        myFieldButtons[j][i].setBackground(Color.RED);
                        break;
                    case SHIP:
                        myFieldButtons[j][i].setBackground(Color.GREEN);
                        break;
                    case HIT:
                        myFieldButtons[j][i].setBackground(Color.ORANGE);
                        break;
                    case SHOOT:
                        myFieldButtons[j][i].setBackground(Color.GRAY);
                        break;
                }
            }
        }

        btnReady.setEnabled(myFields.isAllShipsInPlace());
        setPlayerLabelText();
    }

    private void redrawOpponentsField() {
        var board = shipGame.getActiveBoard();
        var myOpponentsField = board.getOpponentsField();

        for (int i = 0; i < Field.FIELD_COUNT; i++) {
            for (int j = 0; j < Field.FIELD_COUNT; j++) {

                switch (myOpponentsField.getSquareValue(j, i)) {
                    case EMPTY:
                    case SHIP:
                        opponentsFieldButtons[j][i].setBackground(Color.WHITE);
                        break;
                    case SHOOT:
                        opponentsFieldButtons[j][i].setBackground(Color.GRAY);
                        break;
                    case HIT:
                        opponentsFieldButtons[j][i].setBackground(Color.ORANGE);
                        break;
                    case SUNK:
                        opponentsFieldButtons[j][i].setBackground(Color.RED);
                        break;
                }
            }
        }
    }

    private ShootPosition getShootPosition(String btnName) {
        var name = btnName.split("_");
        var y = Integer.parseInt(name[1]);
        var x = Integer.parseInt(name[2]);

        return new ShootPosition(x, y);
    }

    private void gameHasFinished() {
        btnSwitchPlayer.setVisible(false);

        for (int i = 0; i < Field.FIELD_COUNT; i++) {
            for (int j = 0; j < Field.FIELD_COUNT; j++) {
                opponentsFieldButtons[j][i].setEnabled(false);
            }
        }
    }
}


