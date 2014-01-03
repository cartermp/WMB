package com.jmstudios.corvallistransit.models;

public class Tuple<E, V> {
    public final E lhs;
    public final V rhs;

    public Tuple(E e, V v) {
        this.lhs = e;
        this.rhs = v;
    }
}
