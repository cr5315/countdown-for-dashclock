package com.cr5315.cfdc;

public class ExtensionOneSettings extends BaseExtensionSettings {

    @Override
    public String getCountdownNumber() {
        return "one";
    }

    @Override
    public int getXmlResource() {
        return R.xml.settings_one;
    }

}