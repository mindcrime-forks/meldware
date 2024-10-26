package org.buni.meldware.mail.abmounts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.buni.meldware.address.AddressBook;
import org.buni.meldware.address.AddressBookEntry;
import org.buni.meldware.address.AddressBookEntryFactory;
import org.buni.meldware.address.MutableAddressBookEntry;
import org.buni.meldware.mail.util.MMJMXUtil;

public class LocalABMountFactory implements ABMountFactory {
    private final static String TYPE = "local";
    public ABMount create(Map<String,String> config, String name, String description) {
        ABMount localABMount = new LocalABMount(config, name, description);
        return localABMount;
    }

    public String getType() {
        return TYPE;
    }
    
    public class LocalABMount implements ABMount {
        private static final String OBJECT_NAME = "ObjectName";
        private String oname;
        private String name;
        private String description;
        public LocalABMount(Map<String,String> config, String name, String description) {
            this.oname=config.get(OBJECT_NAME);
            this.name=name;
            this.description=description;
        }

        public List<MutableAddressBookEntry> getMatchingAddresses(String typed, int pattern, int number) {
            AddressBook ab = MMJMXUtil.getMBean(oname, AddressBook.class);
            List<MutableAddressBookEntry> list = new ArrayList<MutableAddressBookEntry>();
            switch (pattern) {
            case 0:
                list.addAll(ab.searchEitherNameOrEmail(typed));
                break;
            case 1:
                list.addAll(ab.searchEmail(typed));
                break;
            case 2:
            	list.addAll(ab.searchEitherName(typed));
            	break;
            }
            return list;
        }

        public String getDescription() {
            return this.description;
        }

        public String getName() {
            return this.name;
        }

        public String getType() {
            return TYPE;
        }

        public MutableAddressBookEntry createAddress(String uid, String givenName,String sn, String cn, List<String> mail) {
            AddressBook ab = MMJMXUtil.getMBean(oname, AddressBook.class);
            AddressBookEntryFactory factory = AddressBookEntryFactory.getInstance();
            AddressBookEntry entry = factory.createNewTransientEntry(cn,
                                            description, 
                                            null, 
                                            givenName, 
                                            sn,
                                            null, 
                                            null,
                                            null, 
                                            mail, 
                                            null, 
                                            null, 
                                            null, 
                                            null, 
                                            null, 
                                            null, 
                                            null, 
                                            uid);
            return ab.addAddress(entry);
        }

        public void updateAddress(MutableAddressBookEntry entry) {
            AddressBook ab = MMJMXUtil.getMBean(oname, AddressBook.class);
            ab.modifyAddress(entry);
        }

    }

}
