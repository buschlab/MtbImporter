package de.uzl.lied.mtbimporter.settings;

/**
 * MDR settings.
 */
public class Mdr {

    private CxxMdrSettings cxx;
    private SamplyMdrSettings samply;
    private DataElementHubSettings dataelementhub;

    public CxxMdrSettings getCxx() {
        return cxx;
    }

    public SamplyMdrSettings getSamply() {
        return samply;
    }

    public DataElementHubSettings getDataelementhub() {
        return dataelementhub;
    }

}
