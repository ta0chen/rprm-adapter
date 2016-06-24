package com.polycom.sqa.xma.webservices.driver;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
// import org.apache.cxf.common.util.Base64Utility;
import org.apache.log4j.Logger;

import com.polycom.sqa.utils.CommonUtils;
import com.polycom.sqa.xma.webservices.driver.interceptor.JsonInvocationHandler;
import com.polycom.sqa.xma.webservices.driver.interceptor.SoapHeaderOutInterceptor;
import com.polycom.webservices.AddressBookManager.JAddressBook;
import com.polycom.webservices.AddressBookManager.JAddressBookManager;
import com.polycom.webservices.AddressBookManager.JAddressBookManager_Service;
import com.polycom.webservices.AddressBookManager.JAddressBookSpecifier;
import com.polycom.webservices.AddressBookManager.JCredentials;
import com.polycom.webservices.AddressBookManager.JGabEntry;
import com.polycom.webservices.AddressBookManager.JWebResult;

/**
 * AddressBook manager handler
 *
 * @author wbchao
 *
 */
public class AddressBookManagerHandler {
    protected static Logger    logger       = Logger
            .getLogger("AddressBookManagerHandler");
    private static final QName SERVICE_NAME = new QName(
            "http://polycom.com/WebServices", "JAddressBookManager");
    JAddressBookManager        port;

    /**
     * Construction of UserManagerHandler class
     */
    public AddressBookManagerHandler(final String webServiceUrl) {
        if (webServiceUrl.contains("rest")) {
            final JsonInvocationHandler jsonInvocationHandler = JsonInvocationHandler
                    .getInstance(webServiceUrl);
            port = (JAddressBookManager) jsonInvocationHandler
                    .getProxy(JAddressBookManager.class);
        } else {
            final URL wsdlURL = AddressBookManagerHandler.class.getClassLoader()
                    .getResource("wsdl/current/JAddressBookManager.wsdl");
            final JAddressBookManager_Service ss = new JAddressBookManager_Service(
                    wsdlURL, SERVICE_NAME);
            port = ss.getJAddressBookManagerPort();
            final BindingProvider provider = (BindingProvider) port;
            provider.getRequestContext()
                    .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                         webServiceUrl + "/JAddressBookManager");
            // https support
            CommonUtils.httpsConnectionSupport(port);
            // Add custom SOAPAction header interceptor here
            final Client client = ClientProxy.getClient(port);
            final Endpoint cxfEndpoint = client.getEndpoint();
            cxfEndpoint.getOutInterceptors()
                    .add(new SoapHeaderOutInterceptor());
        }
    }

    /**
     * Add the addressbook to XMA
     *
     * @param addressBook
     * @return http response
     */
    public JWebResult addAddressBook(final String userToken,
                                     final JAddressBook addressBook) {
        System.out.println("Invoking addAddressBook...");
        logger.info("Invoking addAddressBook...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final JWebResult result = port.addAddressBook(credentials, addressBook);
        return result;
    }

    /**
     * Delete the specified address book
     *
     * @param userToken
     * @param addressBookList
     * @return
     */
    public JWebResult
            deleteAddressBooks(final String userToken,
                               final List<JAddressBook> addressBookList) {
        System.out.println("Invoking deleteAddressBooks...");
        final JCredentials _deleteAddressBooks_credentials = new JCredentials();
        _deleteAddressBooks_credentials.setUserToken(userToken);
        final List<JAddressBook> _deleteAddressBooks_addressBook = addressBookList;
        final JWebResult _deleteAddressBooks__return = port
                .deleteAddressBooks(_deleteAddressBooks_credentials,
                                    _deleteAddressBooks_addressBook);
        System.out.println("deleteAddressBooks.result="
                + _deleteAddressBooks__return);
        return _deleteAddressBooks__return;
    }

    public JAddressBook getAddressBookByName(final String userToken,
                                             final String searchStr) {
        final List<JAddressBook> addressBooks = getAddressBooks(userToken);
        if (searchStr != null && !searchStr.isEmpty()) {
            final Pattern pattern = Pattern.compile(searchStr);
            for (final JAddressBook addressBook : addressBooks) {
                final Matcher matcher = pattern.matcher(addressBook.getName());
                if (matcher.find()) {
                    return addressBook;
                }
            }
        }
        return null;
    }

    /**
     * Get the list of the available address books
     *
     * @param userToken
     * @return
     */
    public List<JAddressBook> getAddressBooks(final String userToken) {
        return getAddressBooks(userToken, null);
    }

    /**
     * Get the list of the available address books
     *
     * @param userToken
     * @return
     */
    public List<JAddressBook> getAddressBooks(final String userToken,
                                              final String searchStr) {
        System.out.println("Invoking getAddressBooks...");
        logger.info("Invoking getAddressBooks...");
        final JCredentials _getAddressBooks_credentials = new JCredentials();
        _getAddressBooks_credentials.setUserToken(userToken);
        final Holder<List<JAddressBook>> _getAddressBooks_addressBooks = new Holder<List<JAddressBook>>();
        final JWebResult _getAddressBooks__return = port
                .getAddressBooks(_getAddressBooks_credentials,
                                 _getAddressBooks_addressBooks);
        System.out
                .println("getAddressBooks.result=" + _getAddressBooks__return);
        System.out.println("getAddressBooks._getAddressBooks_addressBooks="
                + _getAddressBooks_addressBooks.value);
        if (searchStr != null && !searchStr.isEmpty()) {
            final Pattern pattern = Pattern.compile(searchStr);
            final List<JAddressBook> addressBooks = new ArrayList<JAddressBook>();
            for (final JAddressBook book : _getAddressBooks_addressBooks.value) {
                final Matcher matcher = pattern.matcher(book.getName());
                if (matcher.find()) {
                    addressBooks.add(book);
                }
            }
            return addressBooks;
        } else {
            return _getAddressBooks_addressBooks.value;
        }
    }

    /**
     * Get the default address book
     *
     * @param userToken
     * @return
     */
    public JAddressBookSpecifier getDefaultAddressBook(final String userToken) {
        System.out.println("Invoking getDefaultAddressBook...");
        logger.info("Invoking getDefaultAddressBook...");
        final JCredentials _getDefaultAddressBook_credentials = new JCredentials();
        _getDefaultAddressBook_credentials.setUserToken(userToken);
        final Holder<JAddressBookSpecifier> _getDefaultAddressBook_defaultAB = new Holder<JAddressBookSpecifier>();
        final JWebResult _getDefaultAddressBook__return = port
                .getDefaultAddressBook(_getDefaultAddressBook_credentials,
                                       _getDefaultAddressBook_defaultAB);
        System.out.println("getDefaultAddressBook.result="
                + _getDefaultAddressBook__return);
        System.out
                .println("getDefaultAddressBook._getDefaultAddressBook_defaultAB="
                        + _getDefaultAddressBook_defaultAB.value);
        return _getDefaultAddressBook_defaultAB.value;
    }

    /**
     * Get the GAB entries from XMA
     *
     * @param userToken
     * @return
     */
    public List<JGabEntry> getGlobalAddressBook(final String userToken) {
        logger.info("Invoking getGlobalAddressBook...");
        final JCredentials _getGlobalAddressBook_credentials = new JCredentials();
        _getGlobalAddressBook_credentials.setUserToken(userToken);
        final Holder<List<JGabEntry>> _getGlobalAddressBook_globalAddressBookEntries = new Holder<List<JGabEntry>>();
        final JWebResult _getGlobalAddressBook__return = port
                .getGlobalAddressBook(_getGlobalAddressBook_credentials,
                                      _getGlobalAddressBook_globalAddressBookEntries);
        logger.info("getGlobalAddressBook.result="
                + _getGlobalAddressBook__return.getStatus().toString());
        return _getGlobalAddressBook_globalAddressBookEntries.value;
    }

    /**
     * Get Lean address books
     *
     * @param userToken
     * @param leanAddressBooks
     * @return
     */
    public List<JAddressBook> getLeanAddressBooks(final String userToken) {
        System.out.println("Invoking getLeanAddressBooks...");
        logger.info("Invoking getLeanAddressBooks...");
        final JCredentials credentials = new JCredentials();
        credentials.setUserToken(userToken);
        final Holder<List<JAddressBook>> leanAddressBooks = new Holder<List<JAddressBook>>();
        port.getLeanAddressBooks(credentials, leanAddressBooks);
        return leanAddressBooks.value;
    }

    /**
     * Set the default address book
     *
     * @param userToken
     * @param defaultAB
     *            It can be parsed from the address book detail data
     * @return
     */
    public JWebResult
            setDefaultAddressBook(final String userToken,
                                  final JAddressBookSpecifier defaultAB) {
        System.out.println("Invoking setDefaultAddressBook...");
        logger.info("Invoking setDefaultAddressBook...");
        final JCredentials _setDefaultAddressBook_credentials = new JCredentials();
        _setDefaultAddressBook_credentials.setUserToken(userToken);
        final JAddressBookSpecifier _setDefaultAddressBook_defaultAB = defaultAB;
        final JWebResult _setDefaultAddressBook__return = port
                .setDefaultAddressBook(_setDefaultAddressBook_credentials,
                                       _setDefaultAddressBook_defaultAB);
        System.out.println("setDefaultAddressBook.result="
                + _setDefaultAddressBook__return);
        return _setDefaultAddressBook__return;
    }

    /**
     * Update the specified address book
     *
     * @param userToken
     * @param updatedAddressBook
     * @return
     */
    public JWebResult updateAddressBook(final String userToken,
                                        final JAddressBook updatedAddressBook) {
        System.out.println("Invoking updateAddressBook...");
        logger.info("Invoking updateAddressBook...");
        final JCredentials _updateAddressBook_credentials = new JCredentials();
        _updateAddressBook_credentials.setUserToken(userToken);
        final JAddressBook _updateAddressBook_addressBook = updatedAddressBook;
        final JWebResult _updateAddressBook__return = port
                .updateAddressBook(_updateAddressBook_credentials,
                                   _updateAddressBook_addressBook);
        System.out.println("updateAddressBook.result="
                + _updateAddressBook__return);
        return _updateAddressBook__return;
    }

    /**
     * Update the address book priorities
     *
     * @param userToken
     * @param orderedAddressBookIds
     * @return
     */
    public JWebResult
            updateAddressBookPriorities(final String userToken,
                                        final List<Integer> orderedAddressBookIds) {
        System.out.println("Invoking updateAddressBookPriorities...");
        logger.info("Invoking updateAddressBookPriorities...");
        final JCredentials _updateAddressBookPriorities_credentials = new JCredentials();
        _updateAddressBookPriorities_credentials.setUserToken(userToken);
        final List<Integer> _updateAddressBookPriorities_orderedAddressBookIds = orderedAddressBookIds;
        final JWebResult _updateAddressBookPriorities__return = port
                .updateAddressBookPriorities(_updateAddressBookPriorities_credentials,
                                             _updateAddressBookPriorities_orderedAddressBookIds);
        System.out.println("updateAddressBookPriorities.result="
                + _updateAddressBookPriorities__return);
        return _updateAddressBookPriorities__return;
    }
}
