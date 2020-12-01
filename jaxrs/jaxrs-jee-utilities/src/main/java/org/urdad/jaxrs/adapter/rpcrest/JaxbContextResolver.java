package org.urdad.jaxrs.adapter.rpcrest;

import org.urdad.xml.binding.ClassesToBeBound;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

@Provider
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class JaxbContextResolver implements ContextResolver<JAXBContext>
{
    @Override
    public JAXBContext getContext(Class<?> type)
    {
        try
        {
            return JAXBContext.newInstance(classesToBeBound.getClassesToBeBound());
        }
        catch (JAXBException e)
        {
            // System error.
            throw new RuntimeException(e);
        }
    }

    @Inject
    @ConfiguredClassesToBeBound
    private ClassesToBeBound classesToBeBound;
}