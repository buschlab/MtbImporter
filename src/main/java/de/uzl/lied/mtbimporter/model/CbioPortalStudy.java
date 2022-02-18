package de.uzl.lied.mtbimporter.model;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import de.uzl.lied.mtbimporter.jobs.EnsemblResolver;
import de.uzl.lied.mtbimporter.settings.Settings;
import de.uzl.lied.mtbimporter.tasks.AddClinicalData;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Pojo for a whole cBioPortal study.
 */
public class CbioPortalStudy {

    private List<Maf> maf = new ArrayList<Maf>();
    private Map<String, ClinicalPatient> patients = new HashMap<String, ClinicalPatient>();
    private Map<String, ClinicalHeader> patientAttributes = new HashMap<String, ClinicalHeader>();
    private Map<String, ClinicalSample> samples = new HashMap<String, ClinicalSample>();
    private Map<String, ClinicalHeader> sampleAttributes = new HashMap<String, ClinicalHeader>();
    private Map<String, List<Timeline>> timeline = new HashMap<String, List<Timeline>>();
    private Map<String, Cna> cna = new HashMap<String, Cna>();
    private Set<String> cnaSampleIds = new HashSet<String>();
    private List<ContinuousCna> seg = new ArrayList<ContinuousCna>();
    private Map<String, Map<String, String>> preparation = new HashMap<String, Map<String, String>>();
    private Map<String, GenePanelMatrix> genePanelMatrix = new HashMap<String, GenePanelMatrix>();
    private Map<String, SampleResource> sampleResources = new HashMap<String, SampleResource>();
    private Map<String, MutationalSignature> mutationalLimit = new HashMap<String, MutationalSignature>();
    private Map<String, MutationalSignature> mutationalContribution = new HashMap<String, MutationalSignature>();
    private String studyId = "";
    private Map<String, Meta> metaFiles = new HashMap<String, Meta>();
    private long state;

    public List<Maf> getMaf() {
        return maf;
    }

    /**
     * Filter mutations in study by a specific patient id.
     * @param sampleId patient id for filtering
     * @return all mutations for specified patient
     */
    public List<Maf> getMafBySampleId(String sampleId) {
        List<Maf> l = new ArrayList<Maf>();
        for (Maf m : maf) {
            if (m.getTumorSampleBarcode().equals(sampleId)) {
                l.add(m);
            }
        }
        return l;
    }

    public void addMaf(Maf newMaf) {
        this.maf = Maf.merge(this.maf, List.of(newMaf));
    }

    public void addMaf(List<Maf> newMaf) {
        this.maf = Maf.merge(this.maf, newMaf);
    }

    public List<ContinuousCna> getSeg() {
        return seg;
    }

    /**
     * Filter continuous cna in study by a specific patient id.
     * @param sampleId patient id for filtering
     * @return all continuous cna for specified patient
     */
    public List<ContinuousCna> getSegBySampleId(String sampleId) {
        List<ContinuousCna> l = new ArrayList<ContinuousCna>();
        for (ContinuousCna s : seg) {
            if (s.getId().equals(sampleId)) {
                l.add(s);
            }
        }
        return l;
    }

    public void addSeg(ContinuousCna newSeg) {
        this.seg = ContinuousCna.merge(this.seg, List.of(newSeg));
    }

    public void addSeg(List<ContinuousCna> newSeg) {
        this.seg = ContinuousCna.merge(this.seg, newSeg);
    }

    public ClinicalPatient getPatient(String patientId) {
        return patients.get(patientId);
    }

    public Collection<ClinicalPatient> getPatients() {
        return patients.values();
    }

    /**
     * Adds patient to study with potential merging.
     * @param patient patient to be added
     */
    public void addPatient(ClinicalPatient patient) {
        ClinicalPatient newPatient = patient;
        if (patients.containsKey(newPatient.getPatientId())) {
            newPatient = AddClinicalData.mergePatients(patients.get(newPatient.getPatientId()), newPatient);
        }
        patients.put(newPatient.getPatientId(), newPatient);
    }

    /**
     * Adds multiple patients to study.
     * @param newPatients list of patients to be added
     */
    public void addPatient(Collection<ClinicalPatient> newPatients) {
        for (ClinicalPatient patient : newPatients) {
            addPatient(patient);
        }
    }

    public Map<String, ClinicalHeader> getPatientAttributes() {
        return patientAttributes;
    }

    public void addPatientAttributes(Map<String, ClinicalHeader> newPatientAttributes) {
        this.patientAttributes.putAll(newPatientAttributes);
    }

    public Collection<ClinicalSample> getSamples() {
        return samples.values();
    }

    /**
     * Filter patients in study by a specific patient id.
     * @param patientId patient id for filtering
     * @return specified patient
     */
    public Collection<ClinicalSample> getSamplesByPatient(String patientId) {
        Collection<ClinicalSample> patientSamples = new ArrayList<ClinicalSample>();
        for (ClinicalSample cs : samples.values()) {
            if (cs.getPatientId().equals(patientId)) {
                patientSamples.add(cs);
            }
        }
        return patientSamples;
    }

    /**
     * Adds sample to study.
     * @param sample sample to be added
     */
    public void addSample(ClinicalSample sample) {
        if (sample == null) {
            return;
        }
        ClinicalSample newSample = sample;
        if (samples.containsKey(newSample.getSampleId())) {
            newSample = AddClinicalData.mergeSamples(samples.get(newSample.getSampleId()), newSample);
        }
        samples.put(newSample.getSampleId(), newSample);
    }

    /**
     * Adds multiple samples to study.
     * @param newSamples list of samples to be added
     */
    public void addSample(Collection<ClinicalSample> newSamples) {
        for (ClinicalSample sample : newSamples) {
            addSample(sample);
        }
    }

    public void setSamples(Map<String, ClinicalSample> newSamples) {
        this.samples = newSamples;
    }

    public Map<String, ClinicalHeader> getSampleAttributes() {
        return sampleAttributes;
    }

    public void addSampleAttributes(Map<String, ClinicalHeader> newSampleAttributes) {
        this.sampleAttributes.putAll(newSampleAttributes);
    }

    public Collection<GenePanelMatrix> getGenePanelMatrix() {
        return genePanelMatrix.values();
    }

    public GenePanelMatrix getGenePanelMatrixBySampleId(String sampleId) {
        return genePanelMatrix.get(sampleId);
    }

    /**
     * Set a list of gene panel matrix entries for study.
     * @param newGenePanelMatrix list of gene panel matrix entries to be set
     */
    public void setGenePanelMatrix(Collection<GenePanelMatrix> newGenePanelMatrix) {
        for (GenePanelMatrix gpm : newGenePanelMatrix) {
            this.genePanelMatrix.put(gpm.getSampleId(), gpm);
        }
    }

    /**
     * Adds gene panel matrix to study.
     * @param newGenePanelMatrix gene panel matrix entries to be added
     */
    public void addGenePanelMatrix(GenePanelMatrix newGenePanelMatrix) {
        if (newGenePanelMatrix != null) {
            this.genePanelMatrix.put(newGenePanelMatrix.getSampleId(), newGenePanelMatrix);
        }
    }

    /**
     * Adds multiple gene panel matrix entries to study.
     * @param newGenePanelMatrix gene panel matrix entries to be added
     */
    public void addGenePanelMatrix(Collection<GenePanelMatrix> newGenePanelMatrix) {
        for (GenePanelMatrix gpm : newGenePanelMatrix) {
            addGenePanelMatrix(gpm);
        }
    }

    public void addPreparation(String p, Map<String, String> m) {
        preparation.put(p, m);
    }

    public Map<String, String> getPreparation(String p) {
        return preparation.get(p);
    }

    /**
     * Set a list of sample resources for study.
     * @param sampleResources list of sample resources to be set
     */
    public void setSampleResources(List<SampleResource> sampleResources) {
        for (SampleResource sr : sampleResources) {
            this.sampleResources.put(sr.getSampleId(), sr);
        }
    }

    public Collection<SampleResource> getSampleResources() {
        return sampleResources.values();
    }

    public SampleResource getSampleResourcesBySampleId(String sampleId) {
        return sampleResources.get(sampleId);
    }

    /**
     * Adds sample resource to study.
     * @param sampleResource sample resource to be added
     */
    public void addSampleResource(SampleResource sampleResource) {
        if (sampleResource != null) {
            this.sampleResources.put(sampleResource.getSampleId(), sampleResource);
        }
    }

    /**
     * Adds multimple sample resources to study.
     * @param sampleResource list of sample resources to be added
     */
    public void addSampleResource(Collection<SampleResource> sampleResource) {
        for (SampleResource sr : sampleResource) {
            addSampleResource(sr);
            try {
                AddClinicalData.addDummyPatient(this, sr.getSampleId());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public Collection<MutationalSignature> getMutationalContribution() {
        return mutationalContribution.values();
    }

    /**
     * Filter mutational contribution in study by a specific patient id.
     * @param sampleId patient id for filtering
     * @return all mutational contributions for specified patient
     */
    public Collection<MutationalSignature> getMutationalContributionBySampleId(String sampleId) {
        Collection<MutationalSignature> l = new ArrayList<MutationalSignature>();
        for (MutationalSignature c : mutationalContribution.values()) {
            if (c.getSamples().containsKey(sampleId)) {
                MutationalSignature n = new MutationalSignature();
                n.setDescription(c.getDescription());
                n.setEntityStableId(c.getEntityStableId());
                n.setName(c.getName());
                n.setSamples(sampleId, c.getSamples().get(sampleId));
                l.add(n);
            }
        }
        return l;
    }

    public Collection<MutationalSignature> getMutationalLimit() {
        return mutationalLimit.values();
    }

    /**
     * Filter mutational limits in study by a specific patient id.
     * @param sampleId patient id for filtering
     * @return all mutational limits for specified patient
     */
    public Collection<MutationalSignature> getMutationalLimitBySampleId(String sampleId) {
        Collection<MutationalSignature> l = new ArrayList<MutationalSignature>();
        for (MutationalSignature c : mutationalLimit.values()) {
            if (c.getSamples().containsKey(sampleId)) {
                MutationalSignature n = new MutationalSignature();
                n.setDescription(c.getDescription());
                n.setEntityStableId(c.getEntityStableId());
                n.setName(c.getName());
                n.setSamples(sampleId, c.getSamples().get(sampleId));
                l.add(n);
            }
        }
        return l;
    }

    public void addMutationalLimit(Collection<MutationalSignature> newMutationalLimit)
            throws JsonParseException, JsonMappingException, IOException {
        addMutationalSignature(newMutationalLimit, this.mutationalLimit, 0);

    }

    public void addMutationalContribution(Collection<MutationalSignature> newMutationalContribution)
            throws JsonParseException, JsonMappingException, IOException {
        addMutationalSignature(newMutationalContribution, this.mutationalContribution, 1);
    }

    private void addMutationalSignature(Collection<MutationalSignature> mutationalSignature,
            Map<String, MutationalSignature> map, int defaultValue)
            throws JsonParseException, JsonMappingException, IOException {
        Set<String> sampleIds = new HashSet<String>();
        for (MutationalSignature m : mutationalSignature) {
            MutationalSignature m2 = new MutationalSignature();
            m2.setEntityStableId(m.getEntityStableId());
            m2.setName(m.getName());
            m2.setDescription(m.getDescription());
            for (Entry<String, Number> e : m.getSamples().entrySet()) {
                m2.getSamples().put(e.getKey().replaceAll("_TD", ""), e.getValue());
            }
            if (map.containsKey(m.getEntityStableId())) {
                sampleIds.addAll(m2.getSamples().keySet());
                map.get(m2.getEntityStableId()).getSamples().putAll(m2.getSamples());
            } else {
                map.put(m2.getEntityStableId(), m2);
            }

        }
        for (MutationalSignature m : map.values()) {
            for (String i : sampleIds) {
                if (m.getSamples().get(i) == null) {
                    m.getSamples().put(i, defaultValue);
                }
            }
        }
        AddClinicalData.addDummyPatient(this, sampleIds);
    }

    public Collection<Cna> getCna() {
        return cna.values();
    }

    /**
     * Filter samples in study by a specific patient id.
     * @param sampleId patient id for filtering
     * @return all samples for specified patient
     */
    public Collection<Cna> getCnaBySampleId(String sampleId) {
        Collection<Cna> l = new ArrayList<Cna>();
        for (Cna c : cna.values()) {
            if (c.getSamples().containsKey(sampleId)) {
                Cna n = new Cna();
                n.setEntrezGeneId(c.getEntrezGeneId());
                n.setHugoSymbol(c.getHugoSymbol());
                n.setSamples(sampleId, c.getSamples().get(sampleId));
                l.add(n);
            }
        }
        return l;
    }

    /**
     * Add multiple discrete CNA entries.
     * @param newCna new discrete CNA entries
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public void addCna(Collection<Cna> newCna) throws JsonParseException, JsonMappingException, IOException {
        for (Cna c : newCna) {
            Cna c2 = new Cna();
            c2.setEntrezGeneId(c.getEntrezGeneId());
            c2.setHugoSymbol(c.getHugoSymbol());
            for (Entry<String, String> e : c.getSamples().entrySet()) {
                c2.getSamples().put(e.getKey().replaceAll("_TD", ""), e.getValue());
            }
            cnaSampleIds.addAll(c2.getSamples().keySet());
            if (this.cna.containsKey(c2.getHugoSymbol())) {
                this.cna.get(c2.getHugoSymbol()).getSamples().putAll(c2.getSamples());
            } else {
                this.cna.put(c2.getHugoSymbol(), c2);
            }
        }
        for (Cna c : this.cna.values()) {
            for (String i : this.cnaSampleIds) {
                if (c.getSamples().get(i) == null) {
                    c.getSamples().put(i, "0");
                }
            }
        }
        AddClinicalData.addDummyPatient(this, cnaSampleIds);
    }

    public Set<String> getCnaSampleIds() {
        return cnaSampleIds;
    }

    public <T> Collection<Timeline> getTimeline(Class<T> newTimeline) {
        return this.timeline.getOrDefault(newTimeline, new ArrayList<Timeline>());
    }

    /**
     * Filter timelines in study by a specific patient id.
     * @param patientId patient id for filtering
     * @return all timeline entries for specified patient
     */
    public Map<String, List<Timeline>> getTimelinesByPatient(String patientId) {
        Map<String, List<Timeline>> m = new HashMap<String, List<Timeline>>();

        for (Entry<String, List<Timeline>> e : timeline.entrySet()) {
            List<Timeline> l = new ArrayList<Timeline>();
            for (Timeline t : e.getValue()) {
                if (t.getPatientId().equals(patientId)) {
                    l.add(t);
                }
            }
            if (l.size() > 0) {
                m.put(e.getKey(), l);
            }
        }

        return m;
    }

    public void setTimelines(Map<String, List<Timeline>> newTimeline) {
        this.timeline = newTimeline;
    }

    /**
     * Adds a timeline entry of whatever timeline type.
     * @param type defines the timeline type for later serialization
     * @param entry timeilne entry
     */
    public void addTimeline(String type, Timeline entry) {
        if (entry == null) {
            return;
        }
        this.timeline.put(type,
                Timeline.merge(this.timeline.getOrDefault(type, new ArrayList<Timeline>()), List.of(entry)));
    }

    public <T> void addTimeline(String type, Collection<Timeline> entries) {
        this.timeline.put(type,
                Timeline.merge(this.timeline.getOrDefault(type, new ArrayList<Timeline>()), entries));
    }

    public Map<String, List<Timeline>> getTimelines() {
        return this.timeline;
    }

    /**
     * MDR mapped objects may have various types so there differntiation is here at a central place.
     * @param o study object of whatever type
     */
    public void add(Object o) {
        if (o instanceof ClinicalPatient) {
            addPatient((ClinicalPatient) o);
        }
        if (o instanceof ClinicalSample) {
            addSample((ClinicalSample) o);
        }
        if (o instanceof Timeline) {
            Timeline t = (Timeline) o;
            addTimeline(t.getEventType().replace("_", "").toLowerCase(), t);
        }
        if (o instanceof Maf) {
            Maf m = (Maf) o;
            if (m.getChromosome() == null || m.getStartPosition() == null || m.getEndPosition() == null) {
                m = EnsemblResolver.enrich(m);
            }
            addMaf(m);
        }
        if (o instanceof Cna) {
            Cna c = (Cna) o;
            if (c.getSamples().containsKey("SAMPLE_ID") && c.getSamples().containsKey("CNA")) {
                Cna n = new Cna();
                n.setEntrezGeneId(c.getEntrezGeneId());
                n.setHugoSymbol(c.getHugoSymbol());
                n.setSamples(c.getSamples().get("SAMPLE_ID"), c.getSamples().get("CNA"));
                c = n;
            }
            try {
                addCna(List.of(c));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public Meta getMetaFile(String meta) {
        return metaFiles.get(meta);
    }

    public Map<String, Meta> getMetaFiles() {
        return metaFiles;
    }

    public void addMeta(String type, Meta meta) {
        metaFiles.put(type, meta);
    }

    public long getState() throws IOException {
        return state;
    }

    /**
     * Set a new timestamp as last modficiation. Used as starting point after restart.
     * @param newState timestamp representing the last modification
     * @throws IOException
     */
    public void setState(Long newState) throws IOException {
        FileOutputStream fos = new FileOutputStream(Settings.getStudyFolder() + studyId + "/.state");
        fos.write(String.valueOf(newState).getBytes());
        fos.close();
        state = newState;
    }

}
