package com.polycom.sqa.xma.webservices.driver.interceptor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlType;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.log4j.Logger;

public class SoapHeaderOutInterceptor
        extends AbstractPhaseInterceptor<Message> {
    Logger logger = Logger.getLogger(this.getClass());

    public SoapHeaderOutInterceptor() {
        super(Phase.PRE_PROTOCOL);
    }

    @Override
    public void handleMessage(final Message message) throws Fault {
        final List<?> contentMessage = message.getContent(List.class);
        final Object o = contentMessage.get(0);
        final XmlType xmlTypeAnno = o.getClass().getAnnotation(XmlType.class);
        final String soapAction = "http://polycom.com/WebServices/ns2:"
                + xmlTypeAnno.name();
        final Map<String, List<String>> headers = new HashMap<String, List<String>>();
        headers.put("SOAPAction", Arrays.asList(soapAction));
        message.put(Message.PROTOCOL_HEADERS, headers);
    }
}
