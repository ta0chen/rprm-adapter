package com.polycom.webservices.driver.interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.xml.ws.BindingProvider;

public class SoapInvocationHandler implements InvocationHandler {
    private Object obj;

    public SoapInvocationHandler() {
        super();
    }

    public SoapInvocationHandler(final Object obj) {
        super();
        this.obj = obj;
    }

    public Object getProxy(final Class<?> cls) {
        return Proxy.newProxyInstance(
                                      Thread.currentThread()
                                              .getContextClassLoader(),
                                      new Class[] { cls },
                                      this);
    }

    @Override
    public Object invoke(final Object proxy,
                         final Method method,
                         final Object[] args) throws Throwable {
        final BindingProvider provider = (BindingProvider) obj;
        final String soapAction = "http://polycom.com/WebServices/ns2:"
                + method.getName();
        provider.getRequestContext()
                .put(BindingProvider.SOAPACTION_URI_PROPERTY, soapAction);
        return method.invoke(obj, args);
    }
}
