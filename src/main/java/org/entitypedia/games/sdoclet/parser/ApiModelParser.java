package org.entitypedia.games.sdoclet.parser;

import com.sun.javadoc.*;
import org.entitypedia.games.sdoclet.DocletOptions;
import org.entitypedia.games.sdoclet.swagger.model.Model;
import org.entitypedia.games.sdoclet.swagger.model.ModelProperty;

import java.util.*;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 * @see <a href=https://github.com/ryankennedy/swagger-jaxrs-doclet">swagger-jaxrs-doclet</a>
 */
public class ApiModelParser {
    private final DocletOptions options;
    private final Translator translator;
    private final Type rootType;
    private final Set<Model> models;

    public ApiModelParser(DocletOptions options, Type rootType) {
        this.options = options;
        this.translator = options.getTranslator();
        this.rootType = rootType;
        this.models = new HashSet<>();
    }

    public Set<Model> parse() {
        parseModel(rootType);
        return models;
    }

    private void parseModel(Type type) {
        boolean isPrimitive = AnnotationHelper.isPrimitive(type);
        boolean isJavaxType = type.qualifiedTypeName().startsWith("javax.");
        boolean isBaseObject = type.qualifiedTypeName().equals("java.lang.Object") || type.qualifiedTypeName().equals("java.lang.Class");
        boolean isTypeToTreatAsOpaque = options.getTypesToTreatAsOpaque().contains(type.qualifiedTypeName());
        ClassDoc classDoc = type.asClassDoc();
        if (isPrimitive || isJavaxType || isBaseObject || isTypeToTreatAsOpaque || classDoc == null || alreadyStoredType(type)) {
            return;
        }

        Collection<Type> types = findReferencedTypes(type);
        Map<String, ModelProperty> properties = findProperties(type);
        if (!properties.isEmpty()) {
            Map<String, Type> resolvedTypeParameters = getResolvedTypeParameters(type);
            Model model = new Model(getGenericTypeName(translator, resolvedTypeParameters, type), properties);
            model.setDescription(type.asClassDoc().commentText());
            models.add(model);
            parseNestedModels(types);
        }
    }

    private Collection<Type> findReferencedTypes(Type type) {
        Map<String, Type> elements = new HashMap<>();

        // get resolved type parameters
        Map<String, Type> resolvedTypeParameters = getResolvedTypeParameters(type);

        ClassDoc classDoc = type.asClassDoc();
        FieldDoc[] fieldDocs = classDoc.fields(false);
        if (fieldDocs != null) {
            for (FieldDoc field : fieldDocs) {
                String key = getGenericTypeName(translator, resolvedTypeParameters, field.type());
                if (!elements.containsKey(key)) {
                    elements.put(key, field.type());
                }
            }
        }

        MethodDoc[] methodDocs = classDoc.methods();
        if (methodDocs != null) {
            for (MethodDoc method : methodDocs) {
                // ideally this should follow Jackson logic
                String key = getGenericTypeName(translator, resolvedTypeParameters, method.returnType());
                if (method.isPublic() && method.name().startsWith("get") && method.name().length() > 3 && !elements.containsKey(key)) {
                    elements.put(key, method.returnType());
                }
            }
        }

        if (null != type.asParameterizedType()) {
            ParameterizedType parameterizedType = type.asParameterizedType();
            for (Type t : parameterizedType.typeArguments()) {
                String key = getGenericTypeName(translator, resolvedTypeParameters, t);
                if (!elements.containsKey(key)) {
                    elements.put(key, t);
                }
            }
        }

        return elements.values();
    }

    public static Map<String, Type> getResolvedTypeParameters(Type type) {
        Map<String, Type> result = new HashMap<>();
        if (null != type.asParameterizedType() && null != type.asParameterizedType().asClassDoc()
                && null != type.asParameterizedType().asClassDoc().typeParameters()) {
            ClassDoc c = type.asParameterizedType().asClassDoc();
            for (int i = 0; i < c.typeParameters().length; i++) {
                result.put(c.typeParameters()[i].typeName(), type.asParameterizedType().typeArguments()[i]);
            }
        }
        return result;
    }

    public static String getGenericTypeName(Translator translator, Map<String, Type> resolvedTypeParameters, Type type) {
        String key = translator.typeName(type).value();
        if (null != type.asParameterizedType() && null != type.asParameterizedType().typeArguments()) {
            key = type.typeName() + "<";
            for (int i = 0; i < type.asParameterizedType().typeArguments().length; i++) {
                Type argType;
                if (null != type.asParameterizedType().typeArguments()[i].asTypeVariable()) {
                    argType = resolvedTypeParameters.get(type.asParameterizedType().typeArguments()[i].asTypeVariable().typeName());
                } else {
                    argType = type.asParameterizedType().typeArguments()[i];
                }

                key = key + argType.typeName();
                if (i < type.asParameterizedType().typeArguments().length - 1) {
                    key = key + ",";
                }
            }
            key = key + ">";
        }
        return key;
    }

    private Map<String, ModelProperty> findProperties(Type type) {
        Map<String, ModelProperty> elements = new HashMap<>();

        ClassDoc classDoc = type.asClassDoc();
        FieldDoc[] fieldDocs = classDoc.fields(false);
        if (fieldDocs != null) {
            for (FieldDoc field : fieldDocs) {
                String name = translator.fieldName(field).value();
                if (name != null && !elements.containsKey(name)) {
                    String description = null;
                    if (null != field.commentText()) {
                        description = field.commentText();
                    }

                    ModelProperty property;
                    ClassDoc typeClassDoc = field.type().asClassDoc();
                    if (typeClassDoc != null && typeClassDoc.isEnum()) {
                        property = new ModelProperty(typeClassDoc.enumConstants(), description);
                    } else {
                        Map<String, Type> resolvedTypeParameters = getResolvedTypeParameters(type);
                        List<String> containerTypeOf = getContainerTypeOf(resolvedTypeParameters, field.type());
                        String dataTypeName = getGenericTypeName(translator, resolvedTypeParameters, field.type());
                        property = new ModelProperty(dataTypeName, description, containerTypeOf);
                    }
                    elements.put(name, property);
                }
            }
        }

        MethodDoc[] methodDocs = classDoc.methods();
        if (methodDocs != null) {
            for (MethodDoc method : methodDocs) {
                if (method.isPublic()) {
                    String name = translator.methodName(method).value();
                    // check fields to respect @JsonIgnore
                    if (name != null && !elements.containsKey(name)) {
                        FieldDoc field = findField(fieldDocs, name);
                        if (null != field) {
                            name = null;
                        }
                    }
                    if (name != null && !elements.containsKey(name)) {
                        String description = null;
                        if (null != method.commentText()) {
                            description = method.commentText();
                        }

                        ModelProperty property;
                        ClassDoc typeClassDoc = method.returnType().asClassDoc();
                        if (typeClassDoc != null && typeClassDoc.isEnum()) {
                            property = new ModelProperty(typeClassDoc.enumConstants(), description);
                        } else {
                            Map<String, Type> resolvedTypeParameters = getResolvedTypeParameters(type);
                            List<String> containerTypeOf = getContainerTypeOf(resolvedTypeParameters, method.returnType());
                            String dataTypeName = getGenericTypeName(translator, resolvedTypeParameters, method.returnType());
                            property = new ModelProperty(dataTypeName, description, containerTypeOf);
                        }
                        elements.put(name, property);
                    }
                }
            }
        }

        return elements;
    }

    private FieldDoc findField(FieldDoc[] fieldDocs, String name) {
        FieldDoc result = null;
        for (FieldDoc fieldDoc : fieldDocs) {
            if (fieldDoc.name().equals(name)) {
                result = fieldDoc;
                break;
            }
        }
        return result;
    }

    private List<String> getContainerTypeOf(Map<String, Type> resolvedTypeParameters, Type type) {
        List<Type> containerOf = parseParameterisedTypeOf(resolvedTypeParameters, type);
        List<String> result = containerOf == null ? null : new ArrayList<String>();
        if (null != result) {
            for (Type t : containerOf) {
                result.add(getGenericTypeName(translator, resolvedTypeParameters, t));
            }
        }
        return result;
    }

    private void parseNestedModels(Collection<Type> types) {
        for (Type type : types) {
            parseModel(type);
        }
    }

    private List<Type> parseParameterisedTypeOf(Map<String, Type> resolvedTypeParameters, Type type) {
        List<Type> result = null;
        ParameterizedType pt = type.asParameterizedType();
        if (pt != null) {
            Type[] typeArgs = pt.typeArguments();
            if (null != typeArgs && 0 < typeArgs.length) {
                result = new ArrayList<>();
                for (Type t : typeArgs) {
                    if (null != t.asTypeVariable()) {
                        result.add(resolvedTypeParameters.get(t.typeName()));
                    } else {
                        result.add(t);
                    }
                }
            }
        }
        return result;
    }

    private boolean alreadyStoredType(final Type type) {
        boolean result = false;
        for (Model model : models) {
            if (model.getName().equals(translator.typeName(type).value())) {
                result = true;
                break;
            }
        }
        return result;
    }
}
