package org.entitypedia.games.sdoclet.parser;

import com.sun.javadoc.*;
import org.entitypedia.games.sdoclet.DocletOptions;
import org.entitypedia.games.sdoclet.SwaggerSerializer;
import org.entitypedia.games.sdoclet.swagger.model.*;
import org.entitypedia.games.sdoclet.swagger.model.Parameter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class SpringMCVParser {

    public static final String REQUEST_MAPPING = "org.springframework.web.bind.annotation.RequestMapping";
    public static final String DEPRECATED = "java.lang.Deprecated";
    public static final String RESPONSE_BODY = "org.springframework.web.bind.annotation.ResponseBody";
    public static final String REQUEST_BODY = "org.springframework.web.bind.annotation.RequestBody";
    public static final String REQUEST_PARAM = "org.springframework.web.bind.annotation.RequestParam";
    public static final String PATH_VARIABLE = "org.springframework.web.bind.annotation.PathVariable";
    public static final String REQUEST_PARAM_REQUIRED = "org.springframework.web.bind.annotation.RequestParam.required";
    public static final String REQUEST_MAPPING_VALUE = "org.springframework.web.bind.annotation.RequestMapping.value";
    public static final String REQUEST_MAPPING_METHOD = "org.springframework.web.bind.annotation.RequestMapping.method";

    public static final String TAG_PREFIX = "spring-mvc-doclet.";
    public static final String PATH_TAG = TAG_PREFIX + "path";
    public static final String TOS_TAG = TAG_PREFIX + "termsOfServiceUrl";
    public static final String CONTACT_TAG = TAG_PREFIX + "contact";
    public static final String LICENSE_TAG = TAG_PREFIX + "license";
    public static final String LICENSE_URL_TAG = TAG_PREFIX + "licenseUrl";

    private final DocletOptions options;
    private final RootDoc rootDoc;
    private boolean hasRequestBody;

    private Set<Type> types;

    public SpringMCVParser(DocletOptions options, RootDoc rootDoc) {
        this.options = options;
        this.rootDoc = rootDoc;
    }

    public boolean run() throws IOException {
        writeApis();
        return true;
    }

    private void writeApis() throws IOException {
        List<ApiListingReference> apiReferences = new ArrayList<>();
        List<ApiListing> apis = new ArrayList<>();

        getApis(apiReferences, apis);

        File outputDirectory = options.getDestinationDirectory();
        SwaggerSerializer serializer = options.getSwaggerSerializer();
        for (ApiListing api : apis) {
            String resourcePath = api.getResourcePath();
            String resourceName = resourcePath.replaceFirst("/", "").replaceAll("/", "_").replaceAll("[\\{\\}]", "");
            File apiFile = new File(outputDirectory, resourceName);
            serializer.serialize(apiFile, api);
        }

        ApiInfo apiInfo = getAPIInfo(rootDoc.packageNamed(options.getApiPackage()));

        ResourceListing listing = new ResourceListing(
                options.getApiVersion(), options.getDocBasePath(),
                apiReferences, new ArrayList<AuthorizationType>(), apiInfo);
        File file = new File(outputDirectory, "api-docs");
        serializer.serialize(file, listing);
    }

    private void getApis(List<ApiListingReference> apiReferences, List<ApiListing> apis) {
        PackageDoc apiPackage = rootDoc.packageNamed(options.getApiPackage());

        for (ClassDoc classDoc : apiPackage.interfaces()) {
            Tag[] paths = classDoc.tags(PATH_TAG);

            if (1 == paths.length) {
                String path = paths[0].text();
                ApiListingReference reference = new ApiListingReference(path);
                reference.setDescription(getTitle(classDoc));
                apiReferences.add(reference);

                ApiListing listing = new ApiListing();
                listing.setApiVersion(options.getApiVersion());
                listing.setSwaggerVersion(DocletOptions.SWAGGER_VERSION);
                listing.setBasePath(options.getApiBasePath());
                listing.setResourcePath(path);
                listing.setProduces(DocletOptions.PRODUCES);
                listing.setConsumes(DocletOptions.CONSUMES);
                listing.setProtocols(DocletOptions.PROTOCOLS);
                listing.setDescription(classDoc.commentText());
                // there is no nice way to do it without extra annotations
                //listing.setAuthorizations();

                types = new HashSet<>();

                List<ApiDescription> apiDescriptions = getApiDescriptions(classDoc);
                listing.setApis(apiDescriptions);

                Map<String, Model> models = new HashMap<>();
                for (Type type : types) {
                    Set<Model> newModels = new ApiModelParser(options, type).parse();
                    for (Model model : newModels) {
                        if (!models.containsKey(model.getName())) {
                            models.put(model.getName(), model);
                        }
                    }
                }
                listing.setModels(models);
                types.clear();

                apis.add(listing);
            }
        }
    }

    private List<ApiDescription> getApiDescriptions(ClassDoc apiInterface) {
        List<ApiDescription> result = new ArrayList<>();

        // find controller by interface
        ClassDoc controller = findController(apiInterface);

        if (null != controller) {
            for (MethodDoc cMethod : controller.methods()) {
                MethodDoc iMethod = getInterfaceMethod(apiInterface, cMethod);
                if (null != iMethod) {
                    AnnotationDesc requestMapping = findAnnotationDesc(cMethod, REQUEST_MAPPING);

                    if (null != requestMapping) {
                        AnnotationDesc.ElementValuePair value = findElementValuePair(requestMapping, REQUEST_MAPPING_VALUE);

                        if (null != value) {
                            String path = value.value().toString();
                            path = path.substring(1, path.length() - 1);
                            ApiDescription apiDescription = new ApiDescription(path);
                            apiDescription.setDescription(iMethod.commentText());

                            Operation operation = new Operation(cMethod.name());
                            AnnotationDesc.ElementValuePair method = findElementValuePair(requestMapping, REQUEST_MAPPING_METHOD);
                            if (null != method) {
                                String methodName = method.value().toString();
                                methodName = methodName.substring(methodName.lastIndexOf('.') + 1, methodName.length());
                                operation.setMethod(methodName);
                            }

                            String summary = getTitle(cMethod);
                            if (null == summary || "".equals(summary.trim())) {
                                summary = getTitle(iMethod);
                            }
                            if (null != summary && !"".equals(summary.trim())) {
                                operation.setSummary(summary);
                            }

                            String notes = getDescription(cMethod);
                            if (null == notes || "".equals(notes.trim())) {
                                notes = getDescription(iMethod);
                            }
                            if (null != notes && !"".equals(notes.trim())) {
                                operation.setNotes(notes);
                            }

                            types.add(cMethod.returnType());
                            Map<String, Type> resolvedTypeParameters = ApiModelParser.getResolvedTypeParameters(controller);
                            operation.setResponseClass(getSwaggerDataType(options.getTranslator(), resolvedTypeParameters, cMethod.returnType()));

                            // can get from protocols apiChannelProcessingFilter
                            //operation.setProtocols(DocletOptions.PROTOCOLS);

                            if (null != findAnnotationDesc(cMethod, RESPONSE_BODY)) {
                                operation.setProduces(DocletOptions.PRODUCES);
                            }

                            if (null != findAnnotationDesc(cMethod, DEPRECATED)) {
                                operation.setDeprecated("true");
                            }

                            // there is no nice way to do it without extra annotations
                            //operation.setAuthorizations();

                            hasRequestBody = false;
                            operation.setParameters(getParameters(resolvedTypeParameters, iMethod, cMethod));
                            if (hasRequestBody) {
                                operation.setConsumes(DocletOptions.CONSUMES);
                            }

                            //operation.setResponseMessages();

                            apiDescription.setOperations(Arrays.asList(operation));
                            result.add(apiDescription);
                        }
                    }
                }
            }
        }

        return result;
    }

    private List<Parameter> getParameters(Map<String, Type> resolvedTypeParameters, MethodDoc iMethod, MethodDoc cMethod) {
        List<Parameter> result = new ArrayList<>();
        for (com.sun.javadoc.Parameter iParam : iMethod.parameters()) {
            com.sun.javadoc.Parameter cParam = findParam(iParam, cMethod);
            Parameter p = new Parameter(iParam.name(), getSwaggerDataType(options.getTranslator(), resolvedTypeParameters, iParam.type()));
            types.add(iParam.type());

            ParamTag paramTag = findParamTag(cMethod, iParam);
            if (null == paramTag) {
                paramTag = findParamTag(iMethod, iParam);
            }

            if (null != paramTag) {
                p.setDescription(paramTag.parameterComment());
            }

            p.setAllowMultiple(false);

            // @RequestParam(required = false)
            p.setRequired(true);
            AnnotationDesc requestParam = findAnnotationDesc(cParam, REQUEST_PARAM);
            if (null != requestParam) {
                AnnotationDesc.ElementValuePair required = findElementValuePair(requestParam, REQUEST_PARAM_REQUIRED);
                if (null != required) {
                    if ("false".equals(required.value().toString())) {
                        p.setRequired(false);
                    }
                }
            }

            // path, query, body, header, form
            // @RequestParam (or missing), @RequestBody, @PathVariable
            p.setParamType("query");

            AnnotationDesc requestBody = findAnnotationDesc(cParam, REQUEST_BODY);
            if (null != requestBody) {
                hasRequestBody = true;
                p.setParamType("body");
            } else {
                AnnotationDesc pathVariable = findAnnotationDesc(cParam, PATH_VARIABLE);
                if (null != pathVariable) {
                    p.setParamType("path");
                }
            }

            result.add(p);
        }
        return result;
    }

    private com.sun.javadoc.Parameter findParam(com.sun.javadoc.Parameter iParam, MethodDoc cMethod) {
        com.sun.javadoc.Parameter result = null;
        for (com.sun.javadoc.Parameter parameter : cMethod.parameters()) {
            if (iParam.name().equals(parameter.name()) && iParam.typeName().equals(parameter.typeName())) {
                result = parameter;
                break;
            }
        }
        return result;
    }

    private ParamTag findParamTag(MethodDoc cMethod, com.sun.javadoc.Parameter parameter) {
        ParamTag result = null;
        for (ParamTag paramTag : cMethod.paramTags()) {
            if (parameter.name().equals(paramTag.parameterName())) {
                result = paramTag;
                break;
            }
        }
        return result;
    }

    private AnnotationDesc findAnnotationDesc(com.sun.javadoc.Parameter parameter, String annotation) {
        AnnotationDesc result = null;
        for (AnnotationDesc annotationDesc : parameter.annotations()) {
            if (annotation.equals(annotationDesc.annotationType().qualifiedName())) {
                result = annotationDesc;
                break;
            }
        }
        return result;
    }

    private AnnotationDesc.ElementValuePair findElementValuePair(AnnotationDesc annotationDesc, String annotation) {
        AnnotationDesc.ElementValuePair result = null;
        for (AnnotationDesc.ElementValuePair pair : annotationDesc.elementValues()) {
            if (annotation.equals(pair.element().qualifiedName())) {
                result = pair;
                break;
            }
        }
        return result;
    }

    private AnnotationDesc findAnnotationDesc(MethodDoc cMethod, String annotation) {
        AnnotationDesc result = null;
        for (AnnotationDesc annotationDesc : cMethod.annotations()) {
            if (annotation.equals(annotationDesc.annotationType().qualifiedName())) {
                result = annotationDesc;
                break;
            }
        }
        return result;
    }

    private MethodDoc getInterfaceMethod(ClassDoc apiInterface, MethodDoc cMethod) {
        MethodDoc result = null;
        for (MethodDoc iMethod : apiInterface.methods()) {
            if (cMethod.overrides(iMethod)) {
                result = iMethod;
                break;
            }
        }
        return result;
    }

    private ClassDoc findController(ClassDoc apiInterface) {
        ClassDoc result = null;
        PackageDoc controllers = rootDoc.packageNamed(options.getControllerPackage());
        out:
        for (ClassDoc classDoc : controllers.ordinaryClasses()) {
            for (ClassDoc iface : classDoc.interfaces()) {
                if (iface.qualifiedName().equals(apiInterface.qualifiedName())) {
                    result = classDoc;
                    break out;
                }
            }
        }
        return result;
    }

    public static String getSwaggerDataType(Translator translator, Map<String, Type> resolvedTypeParameters, Type type) {
        switch (type.qualifiedTypeName()) {
            case "byte": {
                return "byte";
            }
            case "short": {
                return "byte";
            }
            case "int": {
                return "int32";
            }
            case "long": {
                return "int64";
            }
            case "float": {
                return "float";
            }
            case "double": {
                return "double";
            }
            case "boolean": {
                return "boolean";
            }
            case "char": {
                return "string";
            }
            case "java.lang.Byte": {
                return "byte";
            }
            case "java.lang.Short": {
                return "byte";
            }
            case "java.lang.Integer": {
                return "int32";
            }
            case "java.lang.Long": {
                return "int64";
            }
            case "java.lang.Float": {
                return "float";
            }
            case "java.lang.Double": {
                return "double";
            }
            case "java.lang.Boolean": {
                return "boolean";
            }
            case "java.lang.Character": {
                return "string";
            }
            case "java.lang.String": {
                return "string";
            }
            case "java.util.Date": {
                return "date-time";
            }
            default: {
                return ApiModelParser.getGenericTypeName(translator, resolvedTypeParameters, type);
            }
        }
    }

    private ApiInfo getAPIInfo(PackageDoc apiPackage) {
        ApiInfo result = new ApiInfo();

        result.setTitle(getTitle(apiPackage));
        result.setDescription(getDescription(apiPackage));

        for (Field field : ApiInfo.class.getDeclaredFields()) {
            if (field.getType().equals(String.class)) {
                Tag[] tags = apiPackage.tags(TAG_PREFIX + field.getName());
                if (1 == tags.length) {
                    boolean accessible = field.isAccessible();
                    field.setAccessible(true);
                    try {
                        field.set(result, tags[0].text());
                    } catch (IllegalAccessException e) {
                        // ignore
                    }
                    field.setAccessible(accessible);
                }
            }
        }
        return result;
    }

    private static String getDescription(Doc doc) {
        String result = null;
        for (Tag tag : doc.firstSentenceTags()) {
            if ("Text".equals(tag.name())) {
                String title = tag.text();

                result = doc.commentText();
                // remove title
                result = result.substring(title.length(), result.length()).trim();
                break;
            }
        }
        return result;
    }

    private static String getTitle(Doc doc) {
        String result = null;
        for (Tag tag : doc.firstSentenceTags()) {
            if ("Text".equals(tag.name())) {
                result = tag.text();

                int fullStopIndex = result.indexOf('.');
                if (-1 < fullStopIndex) {
                    result = result.substring(0, fullStopIndex);
                }
            }
        }
        return result;
    }
}
