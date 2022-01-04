package de.uzl.lied.mtbimporter.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.uzl.lied.mtbimporter.jobs.EnsemblResolver;
import de.uzl.lied.mtbimporter.settings.Settings;
import de.uzl.lied.mtbimporter.tasks.AddClinicalData;

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

    public List<Maf> getMafBySampleId(String sampleId) {
        List<Maf> l = new ArrayList<Maf>();
        for (Maf m : maf) {
            if (m.getTumorSampleBarcode().equals(sampleId)) {
                l.add(m);
            }
        }
        return l;
    }

    public void addMaf(Maf maf) {
        this.maf = Maf.merge(this.maf, List.of(maf));
    }

    public void addMaf(List<Maf> maf) {
        this.maf = Maf.merge(this.maf, maf);
    }

    public List<ContinuousCna> getSeg() {
        return seg;
    }

    public List<ContinuousCna> getSegBySampleId(String sampleId) {
        List<ContinuousCna> l = new ArrayList<ContinuousCna>();
        for (ContinuousCna s : seg) {
            if (s.getId().equals(sampleId)) {
                l.add(s);
            }
        }
        return l;
    }

    public void addSeg(ContinuousCna seg) {
        this.seg = ContinuousCna.merge(this.seg, List.of(seg));
    }

    public void addSeg(List<ContinuousCna> seg) {
        this.seg = ContinuousCna.merge(this.seg, seg);
    }

    public ClinicalPatient getPatient(String patientId) {
        return patients.get(patientId);
    }

    public Collection<ClinicalPatient> getPatients() {
        return patients.values();
    }

    public void setPatients(Map<String, ClinicalPatient> patients) {
        this.patients = patients;
    }

    public void addPatient(ClinicalPatient patient) {
        if (patients.containsKey(patient.getPatientId())) {
            patient = AddClinicalData.mergePatients(patients.get(patient.getPatientId()), patient);
        }
        patients.put(patient.getPatientId(), patient);
    }

    public void addPatient(Collection<ClinicalPatient> patients) {
        for (ClinicalPatient patient : patients) {
            addPatient(patient);
        }
    }

    public Map<String, ClinicalHeader> getPatientAttributes() {
        return patientAttributes;
    }

    public void addPatientAttributes(Map<String, ClinicalHeader> patientAttributes) {
        this.patientAttributes.putAll(patientAttributes);
    }

    public Collection<ClinicalSample> getSamples() {
        return samples.values();
    }

    public Collection<ClinicalSample> getSamplesByPatient(String patientId) {
        Collection<ClinicalSample> patientSamples = new ArrayList<ClinicalSample>();
        for (ClinicalSample cs : samples.values()) {
            if (cs.getPatientId().equals(patientId)) {
                patientSamples.add(cs);
            }
        }
        return patientSamples;
    }

    public void addSample(ClinicalSample sample) {
        if (sample == null) {
            return;
        }
        if (samples.containsKey(sample.getSampleId())) {
            sample = AddClinicalData.mergeSamples(samples.get(sample.getSampleId()), sample);
        }
        samples.put(sample.getSampleId(), sample);
    }

    public void addSample(Collection<ClinicalSample> samples) {
        for (ClinicalSample sample : samples) {
            addSample(sample);
        }
    }

    public void setSamples(Map<String, ClinicalSample> samples) {
        this.samples = samples;
    }

    public Map<String, ClinicalHeader> getSampleAttributes() {
        return sampleAttributes;
    }

    public void addSampleAttributes(Map<String, ClinicalHeader> sampleAttributes) {
        this.sampleAttributes.putAll(sampleAttributes);
    }

    public Collection<GenePanelMatrix> getGenePanelMatrix() {
        return genePanelMatrix.values();
    }

    public GenePanelMatrix getGenePanelMatrixBySampleId(String sampleId) {
        return genePanelMatrix.get(sampleId);
    }

    public void setGenePanelMatrix(Collection<GenePanelMatrix> genePanelMatrix) {
        for (GenePanelMatrix gpm : genePanelMatrix) {
            this.genePanelMatrix.put(gpm.getSampleId(), gpm);
        }
    }

    public void addGenePanelMatrix(GenePanelMatrix genePanelMatrix) {
        if (genePanelMatrix != null) {
            this.genePanelMatrix.put(genePanelMatrix.getSampleId(), genePanelMatrix);
        }
    }

    public void addGenePanelMatrix(Collection<GenePanelMatrix> genePanelMatrix) {
        for (GenePanelMatrix gpm : genePanelMatrix) {
            addGenePanelMatrix(gpm);
        }
    }

    public void addPreparation(String p, Map<String, String> m) {
        preparation.put(p, m);
    }

    public Map<String, String> getPreparation(String p) {
        return preparation.get(p);
    }

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

    public void addSampleResource(SampleResource sampleResource) {
        if(sampleResource != null) {
            this.sampleResources.put(sampleResource.getSampleId(), sampleResource);
        }
    }

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

    public Collection<MutationalSignature> getMutationalContributionBySampleId(String sampleId) {
        Collection<MutationalSignature> l = new ArrayList<MutationalSignature>();
        for(MutationalSignature c : mutationalContribution.values()) {
            if(c.getSamples().containsKey(sampleId)) {
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

    public void setMutationalContribution(Collection<MutationalSignature> mutationalContribution) {
        for (MutationalSignature mutationalSignature : mutationalContribution) {
            this.mutationalContribution.put(mutationalSignature.getEntityStableId(), mutationalSignature);
        }
    }

    public Collection<MutationalSignature> getMutationalLimit() {
        return mutationalLimit.values();
    }

    public Collection<MutationalSignature> getMutationalLimitBySampleId(String sampleId) {
        Collection<MutationalSignature> l = new ArrayList<MutationalSignature>();
        for(MutationalSignature c : mutationalLimit.values()) {
            if(c.getSamples().containsKey(sampleId)) {
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

    public void setMutationalLimit(Collection<MutationalSignature> mutationalLimit) {
        for (MutationalSignature mutationalSignature : mutationalLimit) {
            this.mutationalLimit.put(mutationalSignature.getEntityStableId(), mutationalSignature);
        }
    }

    public void addMutationalLimit(Collection<MutationalSignature> mutationalLimit)
            throws JsonParseException, JsonMappingException, IOException {
        addMutationalSignature(mutationalLimit, this.mutationalLimit, 0);

    }

    public void addMutationalContribution(Collection<MutationalSignature> mutationalContribution)
            throws JsonParseException, JsonMappingException, IOException {
        addMutationalSignature(mutationalContribution, this.mutationalContribution, 1);
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

    public Collection<Cna> getCnaBySampleId(String sampleId) {
        Collection<Cna> l = new ArrayList<Cna>();
        for(Cna c : cna.values()) {
            if(c.getSamples().containsKey(sampleId)) {
                Cna n = new Cna();
                n.setEntrezGeneId(c.getEntrezGeneId());
                n.setHugoSymbol(c.getHugoSymbol());
                n.setSamples(sampleId, c.getSamples().get(sampleId));
                l.add(n);
            }
        }
        return l;
    }

    public void setCna(Collection<Cna> cna) {
        for (Cna c : cna) {
            this.cna.put(c.getHugoSymbol(), c);
            cnaSampleIds.addAll(c.getSamples().keySet());
        }
    }

    public void addCna(Collection<Cna> cna) throws JsonParseException, JsonMappingException, IOException {
        for (Cna c : cna) {
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

    public <T> Collection<Timeline> getTimeline(Class<T> timeline) {
        return this.timeline.getOrDefault(timeline, new ArrayList<Timeline>());
    }

    public Map<String, List<Timeline>> getTimelinesByPatient(String patientId) {
        Map<String, List<Timeline>> m = new HashMap<String, List<Timeline>>();

        for(Entry<String, List<Timeline>> e : timeline.entrySet()) {
            List<Timeline> l = new ArrayList<Timeline>();
            for(Timeline t : e.getValue()) {
                if(t.getPatientId().equals(patientId)) {
                    l.add(t);
                }
            }
            if (l.size() > 0) {
                m.put(e.getKey(), l);
            }
        }

        return m;
    }

    public void setTimelines(Map<String, List<Timeline>> timeline) {
        this.timeline = timeline;
    }

    public <T> void addTimeline(String type, Timeline entry) {
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
            if(m.getChromosome() == null || m.getStartPosition() == null || m.getEndPosition() == null) {
                m = EnsemblResolver.enrich(m);
            }
            addMaf(m);
        }
        if (o instanceof Cna) {
            Cna c = (Cna) o;
            if(c.getSamples().containsKey("SAMPLE_ID") && c.getSamples().containsKey("CNA")) {
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

    public void setState(Long newState) throws IOException {
        FileOutputStream fos = new FileOutputStream(Settings.getStudyFolder() + studyId + "/.state");
        fos.write(String.valueOf(newState).getBytes());
        fos.close();
        state = newState;
    }

}
