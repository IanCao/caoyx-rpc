package com.caoyx.rpc.benchmark.base;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2020-02-19 23:27
 */
@Data
public class Page<T> implements Serializable {
    private int pageNo;
    private int total;
    private List<T> result;

    @Override
    public String toString() {
        return "Page{" +
                "pageNo=" + pageNo +
                ", total=" + total +
                ", result=" + result +
                '}';
    }
}