package com.polycom.sqa.utils;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class WebClientDevWrapper {

    public static AbstractHttpClient wrapClient(final AbstractHttpClient base,
            final int port) {
        try {
            final SSLContext ctx = SSLContext.getInstance("TLS");
            final X509TrustManager tm = new X509TrustManager() {

                @Override
                public void checkClientTrusted(final X509Certificate[] xcs,
                        final String string) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(final X509Certificate[] xcs,
                        final String string) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[] { tm }, null);
            final SSLSocketFactory ssf = new SSLSocketFactory(ctx,
                    SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            final ClientConnectionManager ccm = base.getConnectionManager();
            final SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", port, ssf));
            return new DefaultHttpClient(ccm, base.getParams());
        } catch (final Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}