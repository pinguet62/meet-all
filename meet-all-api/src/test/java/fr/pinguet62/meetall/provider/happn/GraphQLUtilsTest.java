package fr.pinguet62.meetall.provider.happn;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.pinguet62.meetall.provider.happn.GraphQLUtils.parseGraph;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class GraphQLUtilsTest {

    static class BaseType {
        @GraphQLField
        SubType first;
    }

    static class SubType {
        @GraphQLField
        SubSubType second;
    }

    static class SubSubType {
        String value;
    }

    static class BaseListType {
        @GraphQLField
        List<SubSubType> elements;
    }

    static class BaseAdditionalType {
        @GraphQLField(additional = ".first(11).second(22)")
        SubSubType full;
    }

    /**
     * @see GraphQLField
     */
    @Test
    void simple() {
        assertThat(parseGraph(SubSubType.class), is("value"));
    }

    @Nested
    class subTypes {
        /**
         * @see GraphQLField
         */
        @Test
        void simple() {
            assertThat(parseGraph(BaseType.class), is("first.fields(second.fields(value))"));
        }

        /**
         * @see GraphQLField
         */
        @Test
        void list() {
            assertThat(parseGraph(BaseListType.class), is("elements.fields(value)"));
        }
    }

    /**
     * @see GraphQLField#additional()
     */
    @Test
    void additional() {
        assertThat(parseGraph(BaseAdditionalType.class), is("full.first(11).second(22).fields(value)"));
    }
}
