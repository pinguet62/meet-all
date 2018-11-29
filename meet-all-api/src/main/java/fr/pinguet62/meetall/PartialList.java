package fr.pinguet62.meetall;

import lombok.Getter;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Getter
public class PartialList<T> {

    private final List<T> data;

    private final boolean partial;

    public PartialList(List<T> data, boolean partial) {
        this.data = requireNonNull(data);
        this.partial = partial;
    }

}
