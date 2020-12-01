package org.urdad.services.mask;

import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.Response;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.concurrent.Future;

public interface ServiceMaskProvider {

    /** TODO Javadoc */
    public provideServiceMaskResponse provideServiceMask(provideServiceMaskRequest provideServiceMaskRequest) throws RequestNotValidException;

    public Future<provideServiceMaskResponse> provideServiceMaskAsync(provideServiceMaskRequest provideServiceMaskRequest) throws RequestNotValidException;

    class provideServiceMaskRequest extends Request {
        @NotNull(message = "The service Mask Identifier must be specified.")
        @Size(min=1,message = "The service Mask Identifier must be specified.")
        private String serviceMaskIdentifier;

        public String getServiceMaskIdentifier() {
            return serviceMaskIdentifier;
        }

        public void setServiceMaskIdentifier(String serviceMaskIdentifier) {
            this.serviceMaskIdentifier = serviceMaskIdentifier;
        }

        public provideServiceMaskRequest() {
        }

        public provideServiceMaskRequest(String serviceMaskIdentifier) {
            this.serviceMaskIdentifier = serviceMaskIdentifier;
        }
    }
    class provideServiceMaskResponse extends Response {
        private ServiceMask serviceMask;

        public ServiceMask getServiceMask() {
            return serviceMask;
        }

        public void setServiceMask(ServiceMask serviceMask) {
            this.serviceMask = serviceMask;
        }

        public provideServiceMaskResponse(ServiceMask serviceMask) {
            this.serviceMask = serviceMask;
        }

        public provideServiceMaskResponse() {
        }
    }

    interface ServiceMaskProviderLocal extends ServiceMaskProvider{}
    interface ServiceMaskProviderRemote extends ServiceMaskProvider{}
}
