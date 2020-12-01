package org.urdad.proxy.services.websocket;


import org.urdad.proxy.websocket.PayloadCompressor;
import org.urdad.proxy.websocket.PayloadDecompressor;
import org.urdad.proxy.websocket.PayloadTransformer;

public class WebsocketEndpointConfiguration {

    private PayloadDecompressor payloadDecompressor;
    private PayloadCompressor payloadCompressor;
    private PayloadTransformer inboundPayloadTransformer;
    private PayloadTransformer outboundPayloadTransformer;

    public PayloadDecompressor getPayloadDecompressor() {
        return payloadDecompressor;
    }

    public void setPayloadDecompressor(PayloadDecompressor payloadDecompressor) {
        this.payloadDecompressor = payloadDecompressor;
    }

    public PayloadCompressor getPayloadCompressor() {
        return payloadCompressor;
    }

    public void setPayloadCompressor(PayloadCompressor payloadCompressor) {
        this.payloadCompressor = payloadCompressor;
    }

    public PayloadTransformer getInboundPayloadTransformer() {
        return inboundPayloadTransformer;
    }

    public void setInboundPayloadTransformer(PayloadTransformer inboundPayloadTransformer) {
        this.inboundPayloadTransformer = inboundPayloadTransformer;
    }

    public PayloadTransformer getOutboundPayloadTransformer() {
        return outboundPayloadTransformer;
    }

    public void setOutboundPayloadTransformer(PayloadTransformer outboundPayloadTransformer) {
        this.outboundPayloadTransformer = outboundPayloadTransformer;
    }
}
