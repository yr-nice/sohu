/*
 * Sohu_uiApp.java
 */

package com.mu.tv.ui;

import com.mu.util.log.Log;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class Sohu_uiApp extends SingleFrameApplication {

    static{
        Log.loadCfg("log4j.properties", "hellomkt");
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new Sohu_uiView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of Sohu_uiApp
     */
    public static Sohu_uiApp getApplication() {
        return Application.getInstance(Sohu_uiApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(Sohu_uiApp.class, args);
    }

    @Override
    protected void shutdown()
    {
        // The default shutdown saves session window state.
        super.shutdown();
        // Now perform any other shutdown tasks you need.
        // ...
    }
}
