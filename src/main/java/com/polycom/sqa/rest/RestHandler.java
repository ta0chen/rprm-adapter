package com.polycom.sqa.rest;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.engine.ssl.DefaultSslContextFactory;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.util.Series;

public abstract class RestHandler {
    public static boolean      NEED_CERTIFICATE  = true;
    public static final String KEYSTORE_PATH     = "RprmCertification/rprm.p12";
    public static final String KEYSTORE_PASSWORD = "123456";
    protected static Logger    logger            = null;
    public static final String IP_ADDRESS_REGEXP = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}";
    protected RestServerType   restServerType    = null;
    protected ClientResource   resource          = null;
    protected Client           client            = null;
    protected String           ip                = "";
    protected String           port              = "";

    public RestHandler() {
        super();
        logger = Logger.getLogger(this.getClass());
    }

    public RestHandler(final RestServerType restServerType, final String cmd) {
        super();
        this.restServerType = restServerType;
        logger = Logger.getLogger(this.getClass());
        final Pattern p = Pattern.compile("(https?://)?(" + IP_ADDRESS_REGEXP
                + ")(:([0-9]+))?/");
        final Matcher m = p.matcher(cmd);
        if (m.find()) {
            ip = m.group(2);
            port = m.group(4);
        }
    }

    public abstract String build(String cmd);

    /**
     * Connect to the endpoiont using the givin protocol.
     *
     * @param protocol
     *            The {@link Protocol} to connect over.
     *
     * @return True if the system connects; otherwise fasle.
     *
     * @throws ResourceException
     * @throws IOException
     *             Throws this exception if we have trouble communicating with
     *             the system.
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws UnrecoverableKeyException
     */
    public boolean connect(final String address,
                           final Protocol protocol,
                           final String username,
                           final String password) throws ResourceException,
                                   IOException, NoSuchAlgorithmException,
                                   KeyStoreException, CertificateException,
                                   UnrecoverableKeyException {
        logger.info("Trying to connect over " + protocol.toString());
        final Context context = new Context();
        final Series<Parameter> params = context.getParameters();
        final ConcurrentMap<String, Object> attrs = context.getAttributes();
        params.set("maxConnectionsPerHost", "20");
        // Sometimes the REST API might hang when no proper response was
        // retrieved, including: a. The system crashed after we issued a
        // request. b. We try to retrieve some resource which does not exist.
        // So set the sockettimeout parameter to 60s so that we won't hang if no
        // proper response was retrieved after this period. Please note that
        // this timeout setting does not work for out of connection resource
        // problem
        params.set("socketTimeout", "30000");
        params.add("hostnameVerifier",
                   "org.apache.http.conn.ssl.AllowAllHostnameVerifier");
        if (protocol.equals(Protocol.HTTPS)) {
            KeyManager[] km = null;
            if (NEED_CERTIFICATE) {
                final KeyManagerFactory kmf = KeyManagerFactory
                        .getInstance(KeyManagerFactory.getDefaultAlgorithm());
                final KeyStore keystore = KeyStore.getInstance("PKCS12");
                keystore.load(new FileInputStream(KEYSTORE_PATH),
                              KEYSTORE_PASSWORD.toCharArray());
                kmf.init(keystore, KEYSTORE_PASSWORD.toCharArray());
                km = kmf.getKeyManagers();
            }
            final TrustManager tm = new X509TrustManager() {
                @Override
                public void
                        checkClientTrusted(final X509Certificate[] chain,
                                           final String authType)
                                                   throws CertificateException {
                    logger.info("X509TrustManager: checkClientTrusted");
                }

                @Override
                public void
                        checkServerTrusted(final X509Certificate[] chain,
                                           final String authType)
                                                   throws CertificateException {
                    logger.info("X509TrustManager: checkServerTrusted");
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    logger.info("X509TrustManager: getAcceptedIssuers");
                    return new X509Certificate[0];
                }
            };
            try {
                final SSLContext sslContext = SSLContext.getInstance("TLS");
                // context = client.getContext();
                // Get the connection to the system.
                params.set("persistingConnections", "false");
                logger.info("persistingConnections: false");
                final DefaultSslContextFactory sslContextFactory = new DefaultSslContextFactory() {
                    @Override
                    public SSLContext createSslContext() {
                        logger.info("sslContextFactory: createSslContext");
                        return sslContext;
                    }

                    @Override
                    public String[] getEnabledCipherSuites() {
                        logger.info("sslContextFactory: getEnabledCipherSuites");
                        return super.getEnabledCipherSuites();
                    }
                };
                // final String[] ciphers = new String[] {
                // "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
                // "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA" };
                // sslContextFactory
                // .setEnabledProtocols(new String[] { "TLS1.1" });
                // sslContextFactory.setEnabledCipherSuites(ciphers);
                attrs.put("sslContextFactory", sslContextFactory);
                // Also disable verifying the hostname. There are cases where
                // the final server certificate has final the wrong hostname
                // and/or wrong final IP address.
                attrs.put("hostnameVerifier", new HostnameVerifier() {
                    @Override
                    public boolean verify(final String hostname,
                                          final SSLSession session) {
                        System.out
                                .println("HostnameVerifier: don't check anything, just return");
                        return true;
                    }
                });
                sslContext.init(km, new TrustManager[] { tm }, null);
            } catch (KeyManagementException | NoSuchAlgorithmException e) {
                e.printStackTrace();
                return false;
            }
        }
        if (client == null) {
            logger.info("Create Client.");
            client = new Client(context, protocol);
        }
        logger.info("Create Client resource.");
        final String regex = "https?://";
        resource = new ClientResource(context,
                new Reference(protocol, address.replaceFirst(regex, "")));
        logger.info("Connect client to resource");
        resource.setNext(client);
        logger.info("Turn on buffering");
        resource.setEntityBuffering(true);
        logger.info("Connect...");
        login(username, password);
        return true;
    }

    public RestServerType getRestServerType() {
        return restServerType;
    }

    protected abstract void login(String username, String password)
            throws IOException;

    public void setRestServerType(final RestServerType restServerType) {
        this.restServerType = restServerType;
    }
}
