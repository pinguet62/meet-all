package fr.pinguet62.meetall;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static reactor.core.publisher.Flux.just;

public class PartialListUtilsTest {

    @Test
    public void partialEmpty() {
        PartialList<Object> value = PartialListUtils.partialEmpty();

        assertThat(value, isPartialList(true));
    }

    @Test
    public void concatPartialList_shouldApplyMaskOnPartialFlag() {
        assertThat(
                just(new PartialArrayList<>(List.of(1), false), new PartialArrayList<>(List.of(2), false), new PartialArrayList<>(List.of(3), false))
                        .reduce(PartialListUtils.concatPartialList())
                        .block(),
                isPartialList(false, 1, 2, 3));
        assertThat(
                just(new PartialArrayList<>(List.of(1), false), new PartialArrayList<>(List.of(2), true), new PartialArrayList<>(List.of(3), false))
                        .reduce(PartialListUtils.concatPartialList())
                        .block(),
                isPartialList(true, 1, 2, 3));
        assertThat(
                just(new PartialArrayList<>(List.of(1), true), new PartialArrayList<>(List.of(2), true), new PartialArrayList<>(List.of(3), true))
                        .reduce(PartialListUtils.concatPartialList())
                        .block(),
                isPartialList(true, 1, 2, 3));
    }

    @Test
    public void concatPartialList_shouldMergeValues() {
        assertThat(
                just(new PartialArrayList<>(List.of(1, 2)), new PartialArrayList<>(List.of(3, 4)), new PartialArrayList<>(List.of(5, 6)))
                        .reduce(PartialListUtils.concatPartialList())
                        .block(),
                isPartialList(false, 1, 2, 3, 4, 5, 6));
    }

    @Test
    public void concatPartialList_single() {
        assertThat(
                just(new PartialArrayList<>(List.of(1, 2), false))
                        .reduce(PartialListUtils.concatPartialList())
                        .block(),
                isPartialList(false, 1, 2));
        assertThat(
                just(new PartialArrayList<>(List.of(1, 2), true))
                        .reduce(PartialListUtils.concatPartialList())
                        .block(),
                isPartialList(true, 1, 2));
    }

    @Test
    public void concatPartialList_empty() {
        assertThat(
                Flux.<PartialList<Void>>empty()
                        .reduce(PartialListUtils.concatPartialList())
                        .block(),
                nullValue());
        assertThat(
                Flux.<PartialList<Void>>empty()
                        .reduce(new PartialArrayList<>(false), PartialListUtils.concatPartialList())
                        .block(),
                isPartialList(false));
        assertThat(
                Flux.<PartialList<Void>>empty()
                        .reduce(new PartialArrayList<>(true), PartialListUtils.concatPartialList())
                        .block(),
                isPartialList(true));
    }

    @SafeVarargs
    public static <T> Matcher<PartialList<T>> isPartialList(boolean partial, T... items) {
        return new TypeSafeMatcher<>() {
            @Override
            public void describeTo(Description description) {
                description.appendValue(new PartialArrayList<>(List.of(items), partial));
            }

            @Override
            protected boolean matchesSafely(PartialList<T> partialList) {
                return partialList.isPartial() == partial
                        && new ArrayList<>(partialList).equals(new ArrayList<>(List.of(items)));
            }
        };
    }

}
