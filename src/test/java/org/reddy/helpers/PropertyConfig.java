package org.reddy.helpers;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Properties;

public class PropertyConfig extends Properties {
    public PropertyConfig() {
        super();
        try {
            super.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
        }
        catch (IOException ex) {
            System.out.println("unable to load file.");
        }
    }

    public static org.reddy.datacontracts.Properties getTestProperties(String testDataLoc) throws Exception {
        org.reddy.datacontracts.Properties properties;
        try {
            Gson gson = new GsonBuilder().create();
            properties = gson.fromJson(File.readFile(testDataLoc), org.reddy.datacontracts.Properties.class);
            return properties;
        }catch (IOException ex) {
            System.out.println("unable to load file.");
            throw ex;
        }
    }

    public String getProperty(String name) {
        String isFlagged = System.getProperty(name);
        if ( Strings.isNullOrEmpty(isFlagged) )
            return super.getProperty(name);

        return isFlagged;
    }

    public String[] getPropertyList(String name) {
        return getProperty(name).split(",");
    }
}