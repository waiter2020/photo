package com.upc.photo.component;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: waiter
 * @Date: 2019/4/19 20:51
 * @Version 1.0
 */

@Data
public class FastJsonWraper<T> implements Serializable {
    private T value;

    public FastJsonWraper() {
    }

    public FastJsonWraper(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}