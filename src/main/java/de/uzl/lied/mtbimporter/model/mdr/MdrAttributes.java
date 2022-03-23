package de.uzl.lied.mtbimporter.model.mdr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum for Attributes queried from MDR.
 */
public enum MdrAttributes {
    /**
     * Display name for attribute in cBioPortal.
     */
    DISPLAYNAME("display-name"),
    /**
     * Description for attribute in cBioPortal.
     */
    DESCRIPTION("description"),
    /**
     * Priority for attribute in cBioPortal.
     */
    PRIORITY("priority"),
    /**
     * Datatype for attribute in cBioPortal.
     */
    DATATYPE("datatype"),
    /**
     * Mandatory for attribute in cBioPortal.
     */
    MANDATORY("mandatory");

    private final String label;

    MdrAttributes(String label) {
        this.label = label;
    }

    @Override
    @JsonValue
    public String toString() {
        return this.label;
    }

    /**
     * Look Enum up from String.
     * @param s string to be looked up
     * @return corresponding Enum
     */
    @JsonCreator
    public static MdrAttributes fromString(String s) {
        for (MdrAttributes a : MdrAttributes.values()) {
            if (a.label.equalsIgnoreCase(s)) {
                return a;
            }
        }
        return null;
    }

}
