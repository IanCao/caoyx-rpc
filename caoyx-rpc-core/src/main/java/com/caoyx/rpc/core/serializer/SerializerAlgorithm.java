package com.caoyx.rpc.core.serializer;

/**
 * @author caoyixiong
 */
public enum SerializerAlgorithm {

    JDK("JDK", (byte) 1);

    private String label;
    private byte algorithmId;

    SerializerAlgorithm(String label, byte algorithmId) {
        this.label = label;
        this.algorithmId = algorithmId;
    }

    public byte getAlgorithmId() {
        return this.algorithmId;
    }
}