package com.caoyx.rpc.core.compress;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-22 11:13
 */
public enum CompressType {
    LZ4("lz4", (byte) 1),
    NO_COMPRESS("noCompress", (byte) 2),
    ;


    private String label;
    private byte type;

    CompressType(String label, byte type) {
        this.label = label;
        this.type = type;
    }

    public byte getType() {
        return this.type;
    }

    public String getLabel() {
        return this.label;
    }

    public static CompressType findByType(byte type) {
        CompressType[] compressTypes = values();
        for (int i = 0; i < compressTypes.length; i++) {
            if (compressTypes[i].type == type) {
                return compressTypes[i];
            }
        }
        return null;
    }

    public static CompressType findByLabel(String label) {
        CompressType[] compressTypes = values();
        for (int i = 0; i < compressTypes.length; i++) {
            if (compressTypes[i].label.equals(label)) {
                return compressTypes[i];
            }
        }
        return null;
    }
}