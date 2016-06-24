package com.polycom.sqa.rest.apishared;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import com.polycom.sqa.rest.RestHandler;
import com.polycom.sqa.rest.RestServerType;
import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.utils.JaxbUtils;
import com.polycom.sqa.utils.JsonUtils;

public abstract class ApiSharedRestHandler extends RestHandler {
    protected String username;
    protected String password;

    protected ApiSharedRestHandler(final RestServerType type,
            final String cmd) {
        super(type, cmd);
    }

    public void addHeader() {
    }

    @Override
    public String build(final String cmd) {
        String rtn = "Failed";
        String returnKeyword = "";
        final String delimeter = "\\s+";
        final String[] cmdArgs = cmd.split(delimeter);
        final String url = cmdArgs[1];
        final String restMethod = cmdArgs[2];
        final String rootElement = cmdArgs[3];
        final String xmlOrJson = cmdArgs[4];
        if (cmdArgs.length >= 6) {
            for (int i = 5; i < cmdArgs.length; i++) {
                returnKeyword = returnKeyword + cmdArgs[i] + " ";
            }
            returnKeyword = returnKeyword.trim();
        }
        final MediaType mediaType = getContentType(rootElement, xmlOrJson);
        Object jaxbObject = null;
        try {
            connect(url, Protocol.HTTPS, username, password);
            addHeader();
            Representation result = null;
            final Class<?> c = getApiSharedPojoClass(rootElement);
            if (restMethod.equalsIgnoreCase("GET")) {
                result = resource.get(mediaType);
                if (result == null) {
                    return "Failed, did not get response with cmd " + cmd;
                }
                rtn = result.getText();
                logger.info(rtn);
                // convert the return xml/json to java so that the return
                // message
                // can be retrieved by java
                if (xmlOrJson.equalsIgnoreCase("xml")) {
                    jaxbObject = JaxbUtils.convertToJavaBean(rtn, c);
                } else if (xmlOrJson.equalsIgnoreCase("json")) {
                    jaxbObject = JsonUtils.convertToJavaBean(rtn, c);
                }
                // retrieve the data from the object if the returnKeyword is
                // not null
                if (returnKeyword.isEmpty()
                        || returnKeyword.equalsIgnoreCase("null")) {
                    return rtn;
                } else {
                    return CommonUtils.invokeGetMethod(jaxbObject,
                                                       returnKeyword);
                }
            } else if (restMethod.equalsIgnoreCase("POST")) {
                jaxbObject = JaxbUtils.createJavaBean(c, cmd);
                if (xmlOrJson.equalsIgnoreCase("xml")) {
                    final String xmlRequestBody = JaxbUtils
                            .convertToXml(jaxbObject);
                    logger.info(xmlRequestBody);
                    result = new StringRepresentation(xmlRequestBody);
                    result.setMediaType(mediaType);
                } else if (xmlOrJson.equalsIgnoreCase("json")) {
                    final String jsonRequestBody = JsonUtils
                            .convertToJson(jaxbObject);
                    logger.info(jsonRequestBody);
                    result = new StringRepresentation(jsonRequestBody);
                    result.setMediaType(mediaType);
                }
                resource.post(result);
                rtn = resource.getResponse().getStatus().toString();
                logger.info(rtn);
                return rtn;
            } else if (restMethod.equalsIgnoreCase("PUT")) {
                String responseBodyText;
                if (mediaType.getName().contains("plcm-group-member")) {
                    responseBodyText = resource.get().getText();
                } else {
                    responseBodyText = resource.get(mediaType).getText();
                }
                if (xmlOrJson.equalsIgnoreCase("xml")) {
                    jaxbObject = JaxbUtils.convertToJavaBean(responseBodyText,
                                                             c);
                } else if (xmlOrJson.equalsIgnoreCase("json")) {
                    jaxbObject = JsonUtils.convertToJavaBean(responseBodyText,
                                                             c);
                }
                if (jaxbObject == null) {
                    jaxbObject = c.newInstance();
                }
                final String etag = resource.getResponse().getHeaders()
                        .getValues("ETag");
                JaxbUtils.updateJavaBean(jaxbObject, returnKeyword);
                if (xmlOrJson.equalsIgnoreCase("xml")) {
                    final String xmlRequestBody = JaxbUtils
                            .convertToXml(jaxbObject);
                    logger.info(xmlRequestBody);
                    result = new StringRepresentation(xmlRequestBody);
                } else if (xmlOrJson.equalsIgnoreCase("json")) {
                    final String jsonRequestBody = JsonUtils
                            .convertToJson(jaxbObject);
                    logger.info(jsonRequestBody);
                    result = new StringRepresentation(jsonRequestBody);
                }
                result.setMediaType(mediaType);
                resource.getRequest().getHeaders().set("If-Match", etag);
                resource.put(result);
                rtn = resource.getResponse().getStatus().toString();
                logger.info(rtn);
                return rtn;
            } else if (restMethod.equalsIgnoreCase("DELETE")) {
                result = resource.delete(mediaType);
                rtn = resource.getResponse().getStatus().toString();
                logger.info(rtn);
                return rtn;
            }
        } catch (final Exception e) {
            return "Failed, got exception with cmd " + cmd + " error msg is:\n "
                    + CommonUtils.getExceptionStackTrace(e);
        }
        return rtn;
    }

    public Class<?> getApiSharedPojoClass(final String rootElement)
            throws ClassNotFoundException {
        final String[] tmp = rootElement.split("-");
        final StringBuffer className = new StringBuffer();
        for (final String str : tmp) {
            className.append(Character.toUpperCase(str.charAt(0))
                    + str.substring(1));
        }
        String prefix = "com.polycom.api.rest";
        String suffix = rootElement.replaceAll("-", "_");
        if (rootElement.equalsIgnoreCase("plcm-string-list")
                || rootElement.equalsIgnoreCase("plcm-map-item-list")) {
            suffix = "plcm_objects";
        }
        if (rootElement.contains("plcm-baseline")
                || rootElement.equals("plcm-common-setting-profile")) {
            prefix = "com.polycom.rpum.api";
        }
        return Class.forName(prefix + "." + suffix + "." + className);
    }

    public MediaType getContentType(final String rootElement,
                                    final String xmlOrJson) {
        String contentType = "";
        if (rootElement.equals("plcm-map-item-list")) {
            contentType = "application/vnd.plcm.plcm-common-setting-current-setting-list+"
                    + xmlOrJson.toLowerCase();
        } else {
            contentType = "application/vnd.plcm." + rootElement + "+"
                    + xmlOrJson.toLowerCase();
        }
        MediaType.register(contentType, "polycom content-type");
        return MediaType.valueOf(contentType);
    }

    @Override
    protected void login(final String username, final String password) {
        final ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
        final ChallengeResponse authentication = new ChallengeResponse(scheme,
                username, password);
        resource.setChallengeResponse(authentication);
    }
}