package de.uzl.lied.mtbimporter.jobs;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import de.uzl.lied.mtbimporter.settings.Regex;
import de.uzl.lied.mtbimporter.settings.Settings;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Specimen;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.UriType;

/**
 * Class to query a FHIR server for data.
 */
public final class FhirResolver {

    private static FhirContext ctx = FhirContext.forR4();
    private static IGenericClient cdrClient;
    private static IGenericClient terminologyClient;

    private FhirResolver() {
    }

    /**
     * Initializes the class by creating clients for clinical data and terminology.
     */
    public static void initalize() {
        cdrClient = ctx.newRestfulGenericClient(Settings.getFhir().getClinicalDataServerUrl());
        if (Settings.getFhir().getTerminology() != null) {
            terminologyClient = ctx.newRestfulGenericClient(Settings.getFhir().getTerminology().getServerUrl());
        }
    }

    /**
     * Searchs on the FHIR server for the patient the sample belongs to and returns its id.
     * @param sampleId Given sample
     * @return Patient, the sample belongs to
     */
    public static String resolvePatientFromSample(String sampleId) {
        String newSampleId = sampleId;
        for (Regex r : Settings.getRegex()) {
            newSampleId = newSampleId.replaceAll(r.getCbio(), r.getHis());
        }
        Bundle bSpecimenPatient = (Bundle) cdrClient.search().forResource(Specimen.class)
                .where(new TokenClientParam("identifier").exactly().code(newSampleId))
                .include(new Include("Specimen:patient")).prettyPrint().execute();

        if (!bSpecimenPatient.hasEntry() || bSpecimenPatient.hasTotal() && bSpecimenPatient.getTotal() != 1) {
            return null;
        }

        BundleEntryComponent bec = bSpecimenPatient.getEntryFirstRep();
        if (bec.getResource() instanceof Patient) {
            Patient p = (Patient) bec.getResource();
            return p.getIdentifierFirstRep().getValue();
        }

        return null;

    }

    /**
     * Converts an ICD-O-3 classification into an OncoTree coding.
     * @param topography ICD-O-3 topography code (first part)
     * @param morphology ICD-O-3 morpholoy code (second part)
     * @return FHIR coding for the corresponding OncoTree-Code.
     */
    public static Coding resolveOncoTree(String topography, String morphology) {
        Parameters params = new Parameters();
        params.addParameter("system", new UriType(Settings.getFhir().getTerminology().getIcdO3Url()));
        params.addParameter("target", new UriType(Settings.getFhir().getTerminology().getOncoTreeUrl()));
        params.addParameter("conceptMap",
                new UriType(Settings.getFhir().getTerminology().getIcdO3ToOncoTreeConceptMapUrl()));
        params.addParameter("code", topography.split(" ")[0] + " " + morphology.split(" ")[0]);
        Parameters result = terminologyClient.operation()
                .onInstance("ConceptMap/" + Settings.getFhir().getTerminology().getIcdO3ToOncoTreeConceptMapId())
                .named("translate").withParameters(params).execute();

        for (ParametersParameterComponent p : result.getParameter()) {
            if (!p.getName().equals("match")) {
                continue;
            }
            Coding coding = null;
            String str = null;
            for (ParametersParameterComponent c : p.getPart()) {
                if (c.getValue() instanceof Coding) {
                    coding = (Coding) c.getValue();
                }
                if (c.getValue() instanceof StringType && c.getName().equals("source")) {
                    str = ((StringType) c.getValue()).getValue();
                }
            }
            if (str.equals(Settings.getFhir().getTerminology().getIcdO3ToOncoTreeConceptMapUrl())) {
                return coding;
            }
        }

        return new Coding();
    }

}
