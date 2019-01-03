package fr.pinguet62.meetall.provider.happn;

import org.junit.Test;

import java.util.List;

import static fr.pinguet62.meetall.provider.happn.GraphQLUtils.parseGraph;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class GraphQLUtilsTest {

    static class BaseType {
        @GraphQLField
        private SubType first;
    }

    static class SubType {
        @GraphQLField
        private SubSubType second;
    }

    static class SubSubType {
        private String value;
    }

    static class BaseListType {
        @GraphQLField
        private List<SubSubType> elements;
    }

    static class BaseAdditionalType {
        @GraphQLField(additional = ".first(11).second(22)")
        private SubSubType full;
    }

    /**
     * @see GraphQLField
     */
    @Test
    public void test_parseGraph_simple() {
        assertThat(parseGraph(SubSubType.class), is("value"));
    }

    /**
     * @see GraphQLField
     */
    @Test
    public void test_parseGraph_subTypes() {
        assertThat(parseGraph(BaseType.class), is("first.fields(second.fields(value))"));
    }

    /**
     * @see GraphQLField
     */
    @Test
    public void test_parseGraph_subTypes_list() {
        assertThat(parseGraph(BaseListType.class), is("elements.fields(value)"));
    }

    /**
     * @see GraphQLField#additional()
     */
    @Test
    public void test_parseGraph_additional() {
        assertThat(parseGraph(BaseAdditionalType.class), is("full.first(11).second(22).fields(value)"));
    }

}
