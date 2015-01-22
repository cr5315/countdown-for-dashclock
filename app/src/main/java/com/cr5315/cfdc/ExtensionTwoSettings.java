package com.cr5315.cfdc;

public class ExtensionTwoSettings extends BaseExtensionSettings {

    @Override
    public String getCountdownNumber() {
        return "two";
    }

    @Override
    public int getXmlResource() {
        return R.xml.settings_two;
    }

}