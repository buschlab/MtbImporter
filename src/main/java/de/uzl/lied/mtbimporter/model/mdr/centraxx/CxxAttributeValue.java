package de.uzl.lied.mtbimporter.model.mdr.centraxx;

import de.uzl.lied.mtbimporter.model.mdr.MdrAttributes;
import java.util.List;

/**
 * Model for AttributeValue in Kairos CentraXX MDR.
 */
public class CxxAttributeValue {

    private String domain;
    private MdrAttributes attribute;
    private String value;
    private List<CxxLinks> links;

    public String getDomain() {
        return domain;
    }

    public MdrAttributes getAttribute() {
        return attribute;
    }

    public String getValue() {
        return value;
    }

    public List<CxxLinks> getLinks() {
        return links;
    }

}
