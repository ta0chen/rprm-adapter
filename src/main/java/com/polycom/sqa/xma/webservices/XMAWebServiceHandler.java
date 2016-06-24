package com.polycom.sqa.xma.webservices;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.UserManagerHandler;

public abstract class XMAWebServiceHandler implements Runnable {
    protected Logger              logger;
    protected String[]            cmdArgs        = null;
    protected String              userToken      = null;
    protected int                 loginUserUgpId = 0;
    protected String              webServiceUrl;
    protected String              rprmIp;
    protected UserManagerHandler  umh;
    @SuppressWarnings("serial")
    protected Map<String, String> inputCmd       = new HashMap<String, String>() {
        {
            put("keyword", "");
            put("field1", "");
            put("value1", "");
            put("field2", "");
            put("value2", "");
            put("field3", "");
            put("value3", "");
            put("field4", "");
            put("value4", "");
            put("field5", "");
            put("value5", "");
            put("field6", "");
            put("value6", "");
            put("field7", "");
            put("value7", "");
            put("field8", "");
            put("value8", "");
            put("field9", "");
            put("value9", "");
            put("field10", "");
            put("value10", "");
            put("keyword", "");
            put("searchStr", "");
        }
    };

    public XMAWebServiceHandler(final String cmd) throws IOException {
        logger = Logger.getLogger(this.getClass());
        logger.info("Begin to handle WS request: " + cmd);
        cmdArgs = cmd.split("\\s+");
        injectCmdArgs();
        CommonUtils.parseInputCmd(cmdArgs, inputCmd);
        final Pattern p = Pattern
                .compile("(https?://(.*?)(:[0-9]+)?/PlcmRmWeb(/rest)?)");
        final Matcher m = p.matcher(cmdArgs[0]);
        if (m.find()) {
            webServiceUrl = m.group(1);
            rprmIp = m.group(2);
        } else {
            throw new IOException("Could not find keyword PlcmRmWeb in url");
        }
        final String username = inputCmd.get("username");
        final String domain = inputCmd.get("domain");
        final String password = inputCmd.get("password");
        umh = new UserManagerHandler(webServiceUrl);
        userToken = umh.userLogin(username, domain, password);
        loginUserUgpId = umh.getLoginUserUgpId();
    }

    /**
     * The entrance of handler, this method will be called with TCL request
     *
     * @param cmd
     *            The cmd from TCL request
     *
     * @return The result of XMA webservice response
     */
    public String build() {
        // call the detail function according to the input commands
        final String methodName = cmdArgs[2];
        try {
            final Method method = CommonUtils.getDeclaredMethod(this,
                                                                methodName);
            return method.invoke(this) + "";
        } catch (SecurityException
                 | IllegalAccessException
                 | IllegalArgumentException
                 | InvocationTargetException e) {
            e.printStackTrace();
            return CommonUtils.getExceptionStackTrace(e);
        }
    }

    public Map<String, String> getInputCmd() {
        return inputCmd;
    }

    public String getUserToken() {
        return userToken;
    }

    /**
     * Inject the args from tcl
     */
    protected abstract void injectCmdArgs();

    protected void put(final String key, final String value) {
        inputCmd.put(key, value);
    }

    @Override
    public void run() {
        build();
    }
}
