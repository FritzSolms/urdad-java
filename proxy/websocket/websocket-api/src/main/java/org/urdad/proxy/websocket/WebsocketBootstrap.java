package org.urdad.proxy.websocket;

import java.io.Serializable;

public class WebsocketBootstrap implements Serializable {

    private String bootstrap = "Bootstrap";

    public String getBootstrap() {
        return bootstrap;
    }

    public void setBootstrap(String bootstrap) {
        this.bootstrap = bootstrap;
    }
}
