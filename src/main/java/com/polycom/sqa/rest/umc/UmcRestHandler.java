package com.polycom.sqa.rest.umc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Header;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.util.Series;

import com.polycom.sqa.rest.RestHandler;
import com.polycom.sqa.rest.RestServerType;
import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.utils.JaxbUtils;
import com.polycom.sqa.utils.JsonUtils;
import com.polycom.sqa.xma.webservices.driver.UserManagerHandler;

public class UmcRestHandler extends RestHandler {
    private static UmcRestHandler instance  = null;
    private static long           startTime = 0;

    public static UmcRestHandler getInstance(final String cmd) {
        final long currentTime = System.currentTimeMillis();
        if (instance == null || (currentTime - startTime) > 1000 * 60 * 3) {
            instance = new UmcRestHandler(cmd);
            logger.info("Create the UmcRestHandler instance");
            startTime = currentTime;
        }
        return instance;
    }

    public final Map<String, String> packageMap = new HashMap<String, String>();
    protected String                 username;
    protected String                 password;
    protected String                 domain;
    private String                   userToken  = "";

    private UmcRestHandler(final String cmd) {
        super(RestServerType.UMC, cmd);
        username = "admin";
        password = "UG9seWNvbTEyMw==";
        domain = "local";
        initPackageMap();
    }

    public void addHeader() {
        final Series<Header> headers = resource.getRequest().getHeaders();
        headers.add(new Header("X-CA-Domain", domain));
        headers.add(new Header("X-CA-UserName", username));
        headers.add(new Header("X-CA-Token", userToken));
        // headers.add(new Header("Authorization", "Basic YWRtaW4="));
    }

    @Override
    public String build(final String cmd) {
        String rtn = "Failed";
        String rootElement = "";
        String returnKeyword = "";
        final String delimeter = "\\s+";
        final String[] cmdArgs = cmd.split(delimeter);
        final String url = cmdArgs[1];
        final String restMethod = cmdArgs[2];
        if (cmdArgs.length >= 4) {
            rootElement = cmdArgs[3];
        }
        if (cmdArgs.length >= 5) {
            returnKeyword = cmdArgs[4];
        }
        Object jaxbObject = null;
        try {
            connect(url, Protocol.HTTPS, username, password);
            addHeader();
            final Class<?> c = getPdPojoClass(url, rootElement);
            final MediaType mediaType = getContentType(rootElement);
            Representation result = null;
            if (restMethod.equalsIgnoreCase("GET")) {
                result = resource.get(mediaType);
                if (result == null) {
                    return "Failed, did not get response with cmd " + cmd;
                }
                if (rootElement.isEmpty()
                        || returnKeyword.equalsIgnoreCase("null")) {
                    return resource.getResponse().getStatus().toString();
                }
                rtn = result.getText();
                logger.info(rtn);
                jaxbObject = JsonUtils.convertToJavaBean(rtn, c);
                CommonUtils.addObjectCache(jaxbObject);
                if (returnKeyword.isEmpty()) {
                    return resource.getResponse().getStatus().toString();
                }
                return CommonUtils.invokeGetMethod(jaxbObject, returnKeyword);
            }
            if (restMethod.equalsIgnoreCase("POST")) {
                jaxbObject = JaxbUtils.createJavaBean(c, cmd);
                final String jsonRequestBody = JsonUtils
                        .convertToJson(jaxbObject);
                logger.info(jsonRequestBody);
                result = new StringRepresentation(jsonRequestBody);
                result.setMediaType(mediaType);
                resource.post(result);
                rtn = resource.getResponse().getStatus().toString();
                logger.info(rtn);
                return rtn;
            }
            if (restMethod.equalsIgnoreCase("PUT")) {
                final String bodyText = resource.get(mediaType).getText();
                jaxbObject = JsonUtils.convertToJavaBean(bodyText, c);
                JaxbUtils.updateJavaBean(jaxbObject, cmd);
                final String jsonRequestBody = JsonUtils
                        .convertToJson(jaxbObject);
                logger.info(jsonRequestBody);
                result = new StringRepresentation(jsonRequestBody);
                result.setMediaType(mediaType);
                resource.put(result);
                rtn = resource.getResponse().getStatus().toString();
                logger.info(rtn);
                return rtn;
            }
            if (restMethod.equalsIgnoreCase("DELETE")) {
                result = resource.delete(mediaType);
                rtn = resource.getResponse().getStatus().toString();
                logger.info(rtn);
                return rtn;
            }
        } catch (final Exception e) {
            e.printStackTrace();
            return CommonUtils.getExceptionStackTrace(e);
        }
        return rtn;
    }

    public MediaType getContentType(final String rootElement) {
        if (rootElement.contains("ServiceGroup")) {
            final String contentType = "application/plcm.vnd." + rootElement
                    + "+json";
            MediaType.register(contentType, "polycom content-type");
            return MediaType.valueOf(contentType);
        } else {
            return MediaType.APPLICATION_JSON;
        }
    }

    private Class<?> getPdPojoClass(final String url, final String rootElement)
            throws ClassNotFoundException {
        if (rootElement == null) {
            return null;
        }
        String packageName = "";
        if (rootElement.contains("VO")) {
            packageName = packageMap.get("ndm");
        } else if (rootElement.contains("ServiceGroup")) {
            packageName = packageMap.get("serviceGroup");
        } else if (rootElement.equals("NTPConfig")) {
            packageName = packageMap.get("ntp");
        } else if (rootElement.equals("SnmpAgentConfigurationRepresentation")) {
            packageName = packageMap.get("snmp");
        } else if (rootElement
                .contains("LicenseFeatureAllocationCollectionRepresentation")) {
            packageName = packageMap.get("license");
        } else {
            packageName = packageMap.get("data");
        }
        return Class.forName(packageName + "." + rootElement);
    }

    private void initPackageMap() {
        packageMap.put("ndm", "com.polycom.rpum.ndmservice.viewobject");
        packageMap.put("data", "com.polycom.rpum.ndmservice.dataobject");
        packageMap.put("serviceGroup",
                       "com.polycom.umc.core.domain.service_group");
        packageMap.put("ntp", "com.polycom.umc.core.domain.NTP");
        packageMap.put("snmp",
                       "com.polycom.broker.client.config.cloudaxis.model");
        packageMap.put("license", "com.polycom.rpum.shared.rest.model");
    }

    @Override
    protected void login(final String username, final String password)
            throws IOException {
        final String webServiceUrl = "https://" + ip + ":" + "8443"
                + "/PlcmRmWeb/rest";
        final UserManagerHandler umh = new UserManagerHandler(webServiceUrl);
        userToken = umh.userLogin(username, domain, password);
        logger.info("UserToken is: " + userToken);
        final ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
        final ChallengeResponse authentication = new ChallengeResponse(scheme,
                username, password);
        resource.setChallengeResponse(authentication);
    }
}
