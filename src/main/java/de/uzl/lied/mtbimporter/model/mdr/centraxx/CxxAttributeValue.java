package de.uzl.lied.mtbimporter.model.mdr.centraxx;

import java.util.List;

public class CxxAttributeValue {
    
    private String domain;
    private String attribute;
    private String value;
    private List<CxxLinks> links;

    public String getDomain() {
        return domain;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getValue() {
        return value;
    }
    
    public List<CxxLinks> getLinks() {
        return links;
    }

}
