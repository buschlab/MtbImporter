package de.uzl.lied.mtbimporter.model.mdr.centraxx;

import java.util.List;

/**
 * Model for List in Kairos CentraXX MDR.
 */
public class CxxList {

    private List<CxxAttributeValue> content;
    private List<CxxLinks> links;

    public List<CxxAttributeValue> getContent() {
        return content;
    }

    public List<CxxLinks> getLinks() {
        return links;
    }

}
