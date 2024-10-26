package org.buni.meldware.mail.abmounts;

import java.util.Map;

public interface ABMountFactory {

    String getType();

    ABMount create(Map<String, String> config, String name, String description);

}
