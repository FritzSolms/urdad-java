package org.urdad.jaxrs;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;

/** This filter is used to enable Cross Origin Resource Sharing (CORS). */
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter
{

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException
    {
        String origin = requestContext.getHeaderString(CorsHttpHeaders.ORIGIN);

        if (origin == null)
        {
            return;
        }
        if (requestContext.getMethod().equalsIgnoreCase("OPTIONS"))
        {
            preflight(origin, requestContext);
        }
        else
        {
            checkOrigin(requestContext, origin);
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
    {
        String origin = requestContext.getHeaderString(CorsHttpHeaders.ORIGIN);

        if ((origin == null) || (requestContext.getMethod().equalsIgnoreCase("OPTIONS")) || (requestContext
            .getProperty("cors.failure") != null))
        {

            // Don't do anything if origin is null, its an OPTIONS request, or cors.failure is set.
            return;
        }

        responseContext.getHeaders().putSingle(CorsHttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);

        if (allowCredentials)
        {
            responseContext.getHeaders().putSingle(CorsHttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        }

        if (exposedHeaders != null)
        {
            responseContext.getHeaders().putSingle(CorsHttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, exposedHeaders);
        }
    }

    private void preflight(String origin, ContainerRequestContext requestContext) throws IOException
    {
        checkOrigin(requestContext, origin);

        Response.ResponseBuilder builder = Response.ok();

        builder.header(CorsHttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);

        if (allowCredentials)
        {
            builder.header(CorsHttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        }

        String requestMethods = requestContext.getHeaderString(CorsHttpHeaders.ACCESS_CONTROL_REQUEST_METHOD);

        if (requestMethods != null)
        {
            if (allowedMethods != null)
            {
                requestMethods = this.allowedMethods;
            }
            builder.header(CorsHttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, requestMethods);
        }

        String allowHeaders = requestContext.getHeaderString(CorsHttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);

        if (allowHeaders != null)
        {
            if (allowedHeaders != null)
            {
                allowHeaders = this.allowedHeaders;
            }
            builder.header(CorsHttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, allowHeaders);
        }

        if (corsMaxAge > -1)
        {
            builder.header(CorsHttpHeaders.ACCESS_CONTROL_MAX_AGE, corsMaxAge);
        }

        requestContext.abortWith(builder.build());
    }

    private void checkOrigin(ContainerRequestContext requestContext, String origin)
    {

        if ((!allowedOrigins.contains("*")) && (!allowedOrigins.contains(origin)))
        {
            requestContext.setProperty("cors.failure", true);

            throw new ForbiddenException("Origin not allowed: " + origin);
        }
    }

    /**
     * Put "*" if you want to accept all origins
     *
     * @return
     */
    public Set<String> getAllowedOrigins()
    {
        return allowedOrigins;
    }

    /**
     * Defaults to true
     *
     * @return
     */
    public boolean isAllowCredentials()
    {
        return allowCredentials;
    }

    public void setAllowCredentials(boolean allowCredentials)
    {
        this.allowCredentials = allowCredentials;
    }

    /**
     * Will allow all by default
     *
     * @return
     */
    public String getAllowedMethods()
    {
        return allowedMethods;
    }

    /**
     * Will allow all by default
     * comma delimited string for Access-Control-Allow-Methods
     *
     * @param allowedMethods
     */
    public void setAllowedMethods(String allowedMethods)
    {
        this.allowedMethods = allowedMethods;
    }

    public String getAllowedHeaders()
    {
        return allowedHeaders;
    }

    /**
     * Will allow all by default
     * comma delimited string for Access-Control-Allow-Headers
     *
     * @param allowedHeaders
     */
    public void setAllowedHeaders(String allowedHeaders)
    {
        this.allowedHeaders = allowedHeaders;
    }

    public int getCorsMaxAge()
    {
        return corsMaxAge;
    }

    public void setCorsMaxAge(int corsMaxAge)
    {
        this.corsMaxAge = corsMaxAge;
    }

    public String getExposedHeaders()
    {
        return exposedHeaders;
    }

    /**
     * comma delimited list
     *
     * @param exposedHeaders
     */
    public void setExposedHeaders(String exposedHeaders)
    {
        this.exposedHeaders = exposedHeaders;
    }

    private boolean allowCredentials = true;
    private String allowedMethods;
    private String allowedHeaders;
    private String exposedHeaders;
    private int corsMaxAge = -1;
    private Set<String> allowedOrigins = new HashSet<>();

}