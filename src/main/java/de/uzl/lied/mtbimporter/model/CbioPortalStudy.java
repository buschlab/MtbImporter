package de.uzl.lied.mtbimporter.model;

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

import de.uzl.lied.mtbimporter.tasks.AddClinicalData;

@SuppressWarnings("rawtypes")
public class CbioPortalStudy {
    
    private List<Maf> maf = new ArrayList<Maf>();
    private Map<String, ClinicalPatient> patients = new HashMap<String, ClinicalPatient>();
    private Map<String, ClinicalHeader> patientAttributes = new HashMap<String, ClinicalHeader>();
    private Map<String, ClinicalSample> samples = new HashMap<String, ClinicalSample>();
    private Map<String, ClinicalHeader> sampleAttributes = new HashMap<String, ClinicalHeader>();
    private Map<Class, List<Timeline>> timeline = new HashMap<Class, List<Timeline>>();
    private Map<String, Cna> cna = new HashMap<String, Cna>();
    private Set<String> cnaSampleIds = new HashSet<String>();
    private List<ContinuousCna> seg = new ArrayList<ContinuousCna>();
    private Map<String, Map<String, String>> preparation = new HashMap<String, Map<String, String>>();
    private Map<String, GenePanelMatrix> genePanelMatrix = new HashMap<String, GenePanelMatrix>();
    private Map<String, SampleResource> sampleResources = new HashMap<String, SampleResource>();
    private Map<String, MutationalSignature> mutationalLimit = new HashMap<String, MutationalSignature>();
    private Map<String, MutationalSignature> mutationalContribution = new HashMap<String, MutationalSignature>();

    public List<Maf> getMaf() {
        return maf;
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

    public void addSeg(ContinuousCna seg) {
        this.seg = ContinuousCna.merge(this.seg, List.of(seg));
    }

    public void addSeg(List<ContinuousCna> seg) {
        this.seg = ContinuousCna.merge(this.seg, seg);
    }

    public Collection<ClinicalPatient> getPatients() {
        return patients.values();
    }

    public void setPatients(Map<String, ClinicalPatient> patients) {
        this.patients = patients;
    }

    public void addPatient(ClinicalPatient patient) {
        if(patients.containsKey(patient.getPatientId())) {
            patient = AddClinicalData.mergePatients(patients.get(patient.getPatientId()), patient);
        }
        patients.put(patient.getPatientId(), patient);
    }

    public void addPatient(Collection<ClinicalPatient> patients) {
        for(ClinicalPatient patient : patients) {
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

    public void addSample(ClinicalSample sample) {
        if(sample == null) {
            return;
        }
        if(samples.containsKey(sample.getSampleId())) {
            sample = AddClinicalData.mergeSamples(samples.get(sample.getSampleId()), sample);
        }
        samples.put(sample.getSampleId(), sample);
    }

    public void addSample(Collection<ClinicalSample> samples) {
        for(ClinicalSample sample : samples) {
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

    public void setGenePanelMatrix(Collection<GenePanelMatrix> genePanelMatrix) {
        for(GenePanelMatrix gpm : genePanelMatrix) {
            this.genePanelMatrix.put(gpm.getSampleId(), gpm);
        }
    }

    public void addGenePanelMatrix(GenePanelMatrix genePanelMatrix) {
        this.genePanelMatrix.put(genePanelMatrix.getSampleId(), genePanelMatrix);
    }

    public void addGenePanelMatrix(Collection<GenePanelMatrix> genePanelMatrix) {
        for(GenePanelMatrix gpm : genePanelMatrix) {
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
        for(SampleResource sr : sampleResources) {
            this.sampleResources.put(sr.getSampleId(), sr);
        }
    }

    public Collection<SampleResource> getSampleResources() {
        return sampleResources.values();
    }

    public void addSampleResource(SampleResource sampleResource) {
        this.sampleResources.put(sampleResource.getSampleId(), sampleResource);
    }

    public void addSampleResource(Collection<SampleResource> sampleResource) {
        for(SampleResource sr : sampleResource) {
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

    public void setMutationalContribution(Collection<MutationalSignature> mutationalContribution) {
        for(MutationalSignature mutationalSignature : mutationalContribution) {
            this.mutationalContribution.put(mutationalSignature.getEntityStableId(), mutationalSignature);
        }
    }

    public Collection<MutationalSignature> getMutationalLimit() {
        return mutationalLimit.values();
    }

    public void setMutationalLimit(Collection<MutationalSignature> mutationalLimit) {
        for(MutationalSignature mutationalSignature : mutationalLimit) {
            this.mutationalLimit.put(mutationalSignature.getEntityStableId(), mutationalSignature);
        }
    }

    public void addMutationalLimit(Collection<MutationalSignature> mutationalLimit) throws JsonParseException, JsonMappingException, IOException {
        addMutationalSignature(mutationalLimit, this.mutationalLimit, 0);

    }

    public void addMutationalContribution(Collection<MutationalSignature> mutationalContribution) throws JsonParseException, JsonMappingException, IOException {
        addMutationalSignature(mutationalContribution, this.mutationalContribution, 1);
    }

    private void addMutationalSignature(Collection<MutationalSignature> mutationalSignature, Map<String, MutationalSignature> map, int defaultValue) throws JsonParseException, JsonMappingException, IOException {
        Set<String> sampleIds = new HashSet<String>();
        for(MutationalSignature m : mutationalSignature) {
            MutationalSignature m2 = new MutationalSignature();
            m2.setEntityStableId(m.getEntityStableId());
            m2.setName(m.getName());
            m2.setDescription(m.getDescription());
            for (Entry<String, Number> e : m.getSamples().entrySet()) {
                m2.getSamples().put(e.getKey().replaceAll("_TD", ""), e.getValue());
            }
            if(map.containsKey(m.getEntityStableId())) {
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

    public void setCna(Collection<Cna> cna) {
        for(Cna c : cna) {
            this.cna.put(c.getHugoSymbol(), c);
            cnaSampleIds.addAll(c.getSamples().keySet());
        }
    }

    public void addCna(Collection<Cna> cna) throws JsonParseException, JsonMappingException, IOException {
        for(Cna c : cna) {
            if (c.getEntrezGeneId().equals("NA")) {
                continue;
            }
            Cna  c2 = new Cna();
            c2.setEntrezGeneId(c.getEntrezGeneId());
            c2.setHugoSymbol(c.getHugoSymbol());
            for (Entry<String, Integer> e : c.getSamples().entrySet()) {
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
                    c.getSamples().put(i, 0);
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

    public <T> void addTimeline(Class<T> timeline, Timeline entry) {
        this.timeline.put(timeline.getClass(), Timeline.merge(this.timeline.getOrDefault(timeline, new ArrayList<Timeline>()), List.of(entry)));
    }

    public <T> void addTimeline(Class<T> timeline, Collection<Timeline> entries) {
        this.timeline.put(timeline.getClass(), Timeline.merge(this.timeline.getOrDefault(timeline, new ArrayList<Timeline>()), entries));
    }

    public Map<Class, List<Timeline>> getTimelines() {
        return this.timeline;
    }


}
