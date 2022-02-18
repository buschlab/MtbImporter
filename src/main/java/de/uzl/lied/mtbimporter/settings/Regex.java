package de.uzl.lied.mtbimporter.settings;

/**
 * Regular expression to overcome limited charset in cBioPortal.
 */
public class Regex {

    private String cbio;
    private String his;

    public String getCbio() {
        return cbio;
    }

    public void setCbio(String cbio) {
        this.cbio = cbio;
    }

    public String getHis() {
        return his;
    }

    public void setHis(String his) {
        this.his = his;
    }

}
