package org.urdad.proxy.websocket;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Properties;

@XmlRootElement
@XmlType
@XmlAccessorType(XmlAccessType.PROPERTY)
public class WebsocketMessage implements Serializable {

    private Properties headers = new Properties();

    private Object payload;

    public Properties getHeaders() {
        return headers;
    }

    public void setHeaders(Properties headers) {
        this.headers = headers;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
