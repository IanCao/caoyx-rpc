package com.caoyx.rpc.core.data;

/**
 * @author caoyixiong
 */

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class Packet implements Serializable {
    Byte version = 1;
}