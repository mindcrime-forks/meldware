/**
 * 
 */
package org.buni.meldware.mail.message;

import java.util.ArrayList;
import java.util.List;

import org.buni.meldware.mail.MailException;
import org.columba.ristretto.parser.AddressParser;
import org.columba.ristretto.parser.ParserException;

/**
 * @author Michael.Barker
 *
 */
public class MailAddressFactory {

    /**
     * Parses a string address line into an array of address 
     * objects.
     * 
     * @param list
     * @return
     */
    public static List<MailAddress> parseAddressList(CharSequence list) {
        
        List<MailAddress> mas = new ArrayList<MailAddress>();
        if (list != null) {
            try {
                org.columba.ristretto.message.Address[] as = AddressParser.parseMailboxList(list);
                for (int i = 0; i < as.length; i++) {
                    MailAddress ma = MailAddress.parseSMTPStyle(as[i].getDisplayName() 
                            + " " + as[i].getCanonicalMailAddress());
                    mas.add(ma);
                }
                return mas;
            } catch (ParserException e) {
                throw new MailException(e);
            }            
        }
        return mas;
    }

}
