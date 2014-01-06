package com.jmstudios.corvallistransit.models;

public class Tuple<E, V> {
    public final E lhs;
    public final V rhs;

    public Tuple(E lhs, V rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public String toString() {
        return "(" + lhs.toString() + ", " + rhs.toString() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o instanceof Tuple) {
            return false;
        }

        Tuple<E, V> other = getClass().cast(o);

        return (lhs == null ? other.lhs == null : lhs.equals(other.lhs))
                && (rhs == null ? other.rhs == null : rhs.equals(other.rhs));
    }
}
