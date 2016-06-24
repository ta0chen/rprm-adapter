package com.polycom.sqa.xma.webservices;

import java.lang.reflect.Constructor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.polycom.sqa.utils.CommonUtils;

public class AsyncModeHandler {
    private static final String XMA_WEBSERVICES_PACKAGE_NAME = "com.polycom.sqa.xma.webservices";
    private final Logger        logger                       = Logger.getLogger("AsyncModeHandler");
    String                      cmd                          = "";
    public AsyncModeHandler(final String cmd) {
        this.cmd = cmd;
    }

    public String startRun() {
        String status = "Failed";
        final String delimeter = "\\s+";
        final String[] cmdArgs = cmd.split(delimeter);
        final String type = cmdArgs[2];

        Runnable handler = null;
        final Pattern p = Pattern.compile("([A-Za-z]+)(Manager|Service)");
        final Matcher m = p.matcher(type);
        String className = "";
        try {
            if (m.find()) {
                className = m.group(1) + "Handler";
            } else {
                className = type + "Handler";
            }
            logger.info("The class name need to initialize in the multithread class is: "
                    + className);

            // need to remove the cmdArgs[1] for the later command parse
            cmd = cmd.replaceAll("AsyncMode", "");
            final String packageName = XMA_WEBSERVICES_PACKAGE_NAME;
            final Class<?> clz = Class.forName(packageName + "." + className);
            final Class<?>[] paramTypes = { String.class };
            final Object[] params = { cmd };
            final Constructor<?> con = clz.getConstructor(paramTypes);
            handler = (Runnable) con.newInstance(params);
            final Thread t = new Thread(handler);
            t.start();
            status = "SUCCESS";
        } catch (final Exception e) {
            e.printStackTrace();
            status = CommonUtils.getExceptionStackTrace(e);
        }
        return status;
    }
}
