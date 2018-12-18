package fr.pinguet62.meetall;

import java.util.function.BiFunction;

public class PartialListUtils {

    public static <T> PartialList<T> partialEmpty() {
        return new PartialArrayList<>(true);
    }

    public static <T, E extends PartialList<T>> BiFunction<E, E, E> concatPartialList() {
        return (x, y) -> {
            boolean partial = x.isPartial() || y.isPartial();
            PartialList<T> merged = new PartialArrayList<>(partial);
            merged.addAll(x);
            merged.addAll(y);
            return (E) merged;
        };
    }

}
