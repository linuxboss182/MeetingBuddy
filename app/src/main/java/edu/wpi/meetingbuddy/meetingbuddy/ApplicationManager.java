package edu.wpi.meetingbuddy.meetingbuddy;

import android.app.Application;

/**
 * Created by jtgaulin on 2/21/18.
 */

public class ApplicationManager extends Application {

    private NetworkManager networkManager = new NetworkManager(this);

    public NetworkManager getNetworkManager(){
        return networkManager;
    }

}
