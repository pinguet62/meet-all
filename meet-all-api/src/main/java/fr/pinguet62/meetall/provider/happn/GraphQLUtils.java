package fr.pinguet62.meetall.provider.happn;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GraphQLUtils {

    public static String parseGraph(Class<?> baseType) {
        String result = "";
        for (Field field : baseType.getDeclaredFields()) {
            result += "," + field.getName();

            // support for sub-types
            GraphQLField graphQLField = field.getDeclaredAnnotation(GraphQLField.class);
            if (graphQLField != null) {
                Class<?> subType = field.getType();

                // support for List<>
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericType;
                    subType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                }

                // support for additional info
                result += graphQLField.additional();

                result += ".fields(" + parseGraph(subType) + ")";
            }
        }
        return result.isEmpty() ? "" : result.substring(1);
    }

}
