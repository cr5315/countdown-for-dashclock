package com.cr5315.cfdc;

public class ExtensionThreeSettings extends BaseExtensionSettings {

    @Override
    public String getCountdownNumber() {
        return "three";
    }

    @Override
    public int getXmlResource() {
        return R.xml.settings_three;
    }

}