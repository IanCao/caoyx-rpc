package com.caoyx.rpc.core.serialization.api;

/**
 * @author caoyixiong
 */
public enum SerializerAlgorithm {

    JDK("jdk", (byte) 1),
    HESSIAN2("hessian2", (byte) 2),
    PROTOSTUFF("protostuff", (byte) 3);

    private String label;
    private byte algorithmId;

    SerializerAlgorithm(String label, byte algorithmId) {
        this.label = label;
        this.algorithmId = algorithmId;
    }

    public byte getAlgorithmId() {
        return this.algorithmId;
    }

    public String getLabel() {
        return this.label;
    }

    public static SerializerAlgorithm findByAlgorithmId(byte algorithmId) {
        SerializerAlgorithm[] algorithms = values();
        for (int i = 0; i < algorithms.length; i++) {
            if (algorithms[i].algorithmId == algorithmId) {
                return algorithms[i];
            }
        }
        return null;
    }

    public static SerializerAlgorithm findByLabel(String label) {
        SerializerAlgorithm[] algorithms = values();
        for (int i = 0; i < algorithms.length; i++) {
            if (algorithms[i].label.equals(label)) {
                return algorithms[i];
            }
        }
        return null;
    }
}