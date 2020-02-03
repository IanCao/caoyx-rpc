package com.caoyx.rpc.core.serialization;

/**
 * @author caoyixiong
 */
public enum SerializerType {

    JDK("jdk", (byte) 1),
    HESSIAN2("hessian2", (byte) 2),
    PROTOSTUFF("protoStuff", (byte) 3);

    private String label;
    private byte type;

    SerializerType(String label, byte type) {
        this.label = label;
        this.type = type;
    }

    public byte getType() {
        return this.type;
    }

    public String getLabel() {
        return this.label;
    }

    public static SerializerType findByAlgorithmId(byte algorithmId) {
        SerializerType[] types = values();
        for (int i = 0; i < types.length; i++) {
            if (types[i].type == algorithmId) {
                return types[i];
            }
        }
        return null;
    }

    public static SerializerType findByLabel(String label) {
        SerializerType[] types = values();
        for (int i = 0; i < types.length; i++) {
            if (types[i].label.equals(label)) {
                return types[i];
            }
        }
        return null;
    }
}