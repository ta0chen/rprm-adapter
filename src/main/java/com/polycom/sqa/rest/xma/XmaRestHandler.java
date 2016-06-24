package com.polycom.sqa.rest.xma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.polycom.sqa.rest.RestServerType;
import com.polycom.sqa.rest.apishared.ApiSharedRestHandler;

public class XmaRestHandler extends ApiSharedRestHandler {
    private static XmaRestHandler instance          = null;
    private static String         lastLoginUsername = "";
    private static long           startTime         = 0;

    public static XmaRestHandler getInstance(final String cmd) {
        final long currentTime = System.currentTimeMillis();
        final String delimeter = "\\s+";
        final String[] cmdArgs = cmd.split(delimeter);
        final String systemType = cmdArgs[0];
        final Pattern p = Pattern.compile("([A-Za-z]+)\\((.*)/(.*)\\)");
        final Matcher m = p.matcher(systemType);
        String username = "";
        String password = "";
        if (m.find()) {
            username = m.group(2);
            password = m.group(3);
        } else {
            username = "admin";
            password = "Polycom123";
        }
        if (instance == null || (currentTime - startTime) > 1000 * 60 * 5
                || !lastLoginUsername.equals(username)) {
            instance = new XmaRestHandler(cmd, username, password);
            logger.info("Create the XmaRestHandler instance");
            startTime = currentTime;
            lastLoginUsername = username;
        }
        return instance;
    }

    protected String domain;

    private XmaRestHandler(final String cmd,
            final String username,
            final String password) {
        super(RestServerType.XMA, cmd);
        this.username = username;
        this.password = password;
        domain = "local";
    }
}