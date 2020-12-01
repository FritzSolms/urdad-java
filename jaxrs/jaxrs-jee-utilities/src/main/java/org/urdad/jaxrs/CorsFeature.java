package org.urdad.jaxrs;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class CorsFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {
        CorsFilter corsFilter = new CorsFilter();

        corsFilter.getAllowedOrigins().add("*");
        corsFilter.setAllowedMethods("GET, POST, PUT, DELETE, OPTIONS, HEAD");
        String headers="X-Requested-With, Content-Type, Content-Length, Origin, Accept, " +
                "Authorisation, Token, Access-Control-Request-Method, Access-Control-Request-Headers, " +
                "X-Request-Type, X-Response-Type, X-Throwable-Type, X-Service-Provider-Identifier, X-Websocket-Session-Identifier";
        corsFilter.setAllowedHeaders(headers);
        corsFilter.setExposedHeaders(headers);
        corsFilter.setAllowCredentials(true);

        context.register(corsFilter);

        return true;
    }

}