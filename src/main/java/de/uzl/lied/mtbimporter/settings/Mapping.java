package de.uzl.lied.mtbimporter.settings;

/**
 * Defines mappings between source and target profiles used for MDR mapping.
 */
public class Mapping {

    private String source;
    private String target;
    private Class<?> modelClass;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Class<?> getModelClass() {
        return modelClass;
    }

    public void setModelClass(Class<?> modelClass) {
        this.modelClass = modelClass;
    }

}
