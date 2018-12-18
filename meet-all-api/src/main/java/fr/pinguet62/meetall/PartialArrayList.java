package fr.pinguet62.meetall;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * By default is full (not partial).
 */
public class PartialArrayList<E> extends ArrayList<E> implements PartialList<E> {

    @Getter
    private final boolean partial;

    public PartialArrayList() {
        this(false);
    }

    public PartialArrayList(boolean partial) {
        this.partial = partial;
    }

    public PartialArrayList(Collection<? extends E> initial) {
        this(initial, false);
    }

    public PartialArrayList(Collection<? extends E> initial, boolean partial) {
        super(initial);
        this.partial = partial;
    }

    @Override
    public String toString() {
        String suffix = partial ? "partial" : "full";
        return super.toString() + " " + suffix;
    }
}
