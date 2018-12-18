package fr.pinguet62.meetall;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
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
                just(new PartialArrayList<>(singletonList(1), false), new PartialArrayList<>(singletonList(2), false), new PartialArrayList<>(singletonList(3), false))
                        .reduce(PartialListUtils.concatPartialList())
                        .block(),
                isPartialList(false, 1, 2, 3));
        assertThat(
                just(new PartialArrayList<>(singletonList(1), false), new PartialArrayList<>(singletonList(2), true), new PartialArrayList<>(singletonList(3), false))
                        .reduce(PartialListUtils.concatPartialList())
                        .block(),
                isPartialList(true, 1, 2, 3));
        assertThat(
                just(new PartialArrayList<>(singletonList(1), true), new PartialArrayList<>(singletonList(2), true), new PartialArrayList<>(singletonList(3), true))
                        .reduce(PartialListUtils.concatPartialList())
                        .block(),
                isPartialList(true, 1, 2, 3));
    }

    @Test
    public void concatPartialList_shouldMergeValues() {
        assertThat(
                just(new PartialArrayList<>(asList(1, 2)), new PartialArrayList<>(asList(3, 4)), new PartialArrayList<>(asList(5, 6)))
                        .reduce(PartialListUtils.concatPartialList())
                        .block(),
                isPartialList(false, 1, 2, 3, 4, 5, 6));
    }

    @Test
    public void concatPartialList_single() {
        assertThat(
                just(new PartialArrayList<>(asList(1, 2), false))
                        .reduce(PartialListUtils.concatPartialList())
                        .block(),
                isPartialList(false, 1, 2));
        assertThat(
                just(new PartialArrayList<>(asList(1, 2), true))
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

    public static <T> Matcher<PartialList<T>> isPartialList(boolean partial, T... items) {
        return new TypeSafeMatcher<PartialList<T>>() {
            @Override
            public void describeTo(Description description) {
                description.appendValue(new PartialArrayList<>(asList(items), partial));
            }

            @Override
            protected boolean matchesSafely(PartialList<T> partialList) {
                return partialList.isPartial() == partial
                        && new ArrayList<>(partialList).equals(new ArrayList<>(asList(items)));
            }
        };
    }

}
