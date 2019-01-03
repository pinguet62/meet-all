package fr.pinguet62.meetall.provider.happn;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that the field is a complex type and his sub-fields must be extracted in {@code ".fields(...)"} tag.
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface GraphQLField {

    String additional() default "";

}
