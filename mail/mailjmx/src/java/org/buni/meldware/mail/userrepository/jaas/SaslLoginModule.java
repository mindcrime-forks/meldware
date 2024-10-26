package org.buni.meldware.mail.userrepository.jaas;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

import org.jboss.security.auth.spi.DatabaseServerLoginModule;
import org.jboss.util.Base64;

/**
 * An implementation (Hack) that let users login using a SASL authentication
 * mechanism.
 * 
 * @author Michael Barker
 *
 */
public class SaslLoginModule extends DatabaseServerLoginModule {

    private String hostname;
    private String protocol;
    private String mechanism;
    private Principal principal;
    private Codec codec;

    @Override
    public void initialize(Subject subject, CallbackHandler h, Map sharedState, 
            Map options) {
        
        super.initialize(subject, h, sharedState, options);
        hostname = (String) options.get("sasl.hostname");
        codec = getCodec((String) options.get("sasl.encoding"));
        mechanism = getString(options, "sasl.mechanism", "CRAM-MD5");
        protocol = getString(options, "sasl.protocol", "imap");
    }
    
    private Codec getCodec(String encoding) {
        if (encoding == null) {
            return new Base64Codec();
        } else if (encoding.equals("base64")) {
            return new Base64Codec();
        } else {
            return new Base64Codec();
        }        
    }
    
    private String getString(Map options, String key, String defValue) {
        String value = (String) options.get(key);
        if (value == null) {
            return defValue;
        }
        return value;
    }

    /**
     * @see org.jboss.security.auth.spi.UsernamePasswordLoginModule#login()
     */
    @Override
    public boolean login() throws LoginException {
        super.loginOk = false;
        
        CallbackHandler lcbh = new CallbackHandler() {
            public void handle(Callback[] callbacks) throws IOException, 
                    UnsupportedCallbackException {
                
                for (Callback cb : callbacks) {
                    if (cb instanceof NameCallback) {
                        NameCallback nc = (NameCallback) cb;
                        try {
                            principal = createIdentity(nc.getDefaultName());
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to set principal", e);
                        }
                    } else if (cb instanceof PasswordCallback) {
                        PasswordCallback pc = (PasswordCallback) cb;
                        try {
                            String pass = getUsersPassword();
                            pc.setPassword(pass.toCharArray());
                        } catch (LoginException e) {
                            throw new RuntimeException("Failed to set password", e);
                        }
                    } else if (cb instanceof AuthorizeCallback) {
                        AuthorizeCallback ac = (AuthorizeCallback) cb;
                        ac.setAuthorized(true);
                    }
                }
            }
        };
        
        try {
            SaslServer ss = Sasl.createSaslServer(mechanism, protocol, hostname, null, lcbh);
            byte[] response = new byte[0];
            while (!ss.isComplete()) {
                byte[] challenge = ss.evaluateResponse(response);
                if (ss.isComplete()) {
                    List<Object> l = new ArrayList<Object>();
                    if (challenge != null) {
                        l.add(new TextOutputCallback(TextOutputCallback.INFORMATION, 
                                codec.encode(challenge)));
                    }
                    if (principal != null) {
                        NameCallback nc = new NameCallback("SASL");
                        nc.setName(principal.getName());
                        l.add(nc);
                    }
                    
                    Callback[] cbs = (Callback[]) l.toArray(new Callback[0]);
                    callbackHandler.handle(cbs);
                } else {
                    TextOutputCallback toc = new TextOutputCallback(
                            TextOutputCallback.INFORMATION, 
                            codec.encode(challenge));
                    TextInputCallback tic = new TextInputCallback("response");
                    callbackHandler.handle(new Callback[] { toc, tic });
                    response = codec.decode(tic.getText());
                }
            }
            super.loginOk = true;
        } catch (SaslException e) {
            e.printStackTrace();
            super.loginOk = false;
        } catch (IOException e) {
            e.printStackTrace();
            throw new LoginException(e.getMessage());
        } catch (UnsupportedCallbackException e) {
            e.printStackTrace();
            throw new LoginException(e.getMessage());
        }
        
        return super.loginOk;
    }
    
    protected Principal getIdentity() {
        return principal;
    }
    
    private interface Codec {
        String encode(byte[] data);
        byte[] decode(String data);
    }
    
    private static class Base64Codec implements Codec {

        public byte[] decode(String data) {
            return Base64.decode(data.trim());
        }

        public String encode(byte[] data) {
            return Base64.encodeBytes(data);
        }
        
    }
}
