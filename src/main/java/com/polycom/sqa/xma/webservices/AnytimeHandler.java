package com.polycom.sqa.xma.webservices;

import java.io.IOException;
import java.util.List;

import com.polycom.sqa.xma.webservices.driver.AnytimeManagerHandler;
import com.polycom.webservices.AnytimeManager.JDmaConferenceTemplate;

/**
 * Conference Template handler. This class will handle the webservice request of
 * Conference Template module
 *
 * @author lixzhao
 *
 */
public class AnytimeHandler extends XMAWebServiceHandler {
    /**
     * @deprecated Not to use
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        final String method = "getTemplatePriority ";
        final String auth = "username=admin password=UG9seWNvbTEyMw== domain=LOCAL ";
        final String params = "templateName=template2 ";
        final String command = "http://localhost:8888/PlcmRmWeb/JAnytimeManager AnytimeManager "
                + method + auth + params;
        final AnytimeHandler handler = new AnytimeHandler(
                command);
        final String result = handler.build();
        System.out.println("result==" + result);
    }

    AnytimeManagerHandler amh;

    public AnytimeHandler(final String cmd) throws IOException {
        super(cmd);
        amh = new AnytimeManagerHandler(webServiceUrl);
    }

    /**
     * get dma templates
     *
     * @param templateName
     *            The template name
     * @return template priority
     */
    public String getTemplatePriority() {
    	String templateName = inputCmd.get("templateName");
        int priority = 0;
        List<JDmaConferenceTemplate> templates = amh.getDmaConferenceTemplates(userToken);
        for (JDmaConferenceTemplate template : templates) {
        	if (templateName.equals(template.getTemplateName())) {
        		priority = template.getPriority();
        		break;
        	}
        }
        return String.valueOf(priority);
    }

    @Override
    protected void injectCmdArgs() {
        put("templateName", "");
    }
}
