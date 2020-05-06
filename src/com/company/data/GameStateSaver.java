package com.company.data;

import java.io.File;

public interface GameStateSaver {

    void save (File file, ShipGame game);
    ShipGame load (File file);
}
