package org.entitypedia.games.sdoclet.parser;

import com.sun.javadoc.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="http://autayeu.com/">Aliaksandr Autayeu</a>
 * @see <a href="https://github.com/ryankennedy/swagger-jaxrs-doclet">swagger-jaxrs-doclet</a>
 */
public class AnnotationHelper {

    private static final List<String> PRIMITIVES = new ArrayList<String>() {{
        add("byte");
        add("boolean");
        add("int");
        add("long");
        add("float");
        add("double");
        add("string");
        add("Date");
    }};

    public static boolean isPrimitive(Type type) {
        return PRIMITIVES.contains(typeOf(type));
    }

    /**
     * Determines the String representation of the object Type.
     *
     * @param type type
     * @return String representation of the object Type
     */
    public static String typeOf(Type type) {
        String javaType = type.qualifiedTypeName();
        String result;
        if (javaType.startsWith("java.lang.")) {
            int i = javaType.lastIndexOf(".");
            result = javaType.substring(i + 1).toLowerCase();
        } else if (PRIMITIVES.contains(javaType.toLowerCase())) {
            result = javaType.toLowerCase();
        } else if (javaType.equals("java.util.Date")) {
            result = "Date";
        } else {
            int i = javaType.lastIndexOf(".");
            if (i >= 0) {
                result = javaType.substring(i + 1);
            } else {
                result = javaType;
            }
        }
        if (result.equalsIgnoreCase("integer")) {
            result = "int";
        } else if (result.equalsIgnoreCase("arraylist") || result.equalsIgnoreCase("linkedlist")) {
            result = "List";
        }
        return result;
    }
}