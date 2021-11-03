package de.uzl.lied.mtbimporter.jobs;

import java.util.List;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Specimen;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.UriType;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Parameters.ParametersParameterComponent;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import de.uzl.lied.mtbimporter.settings.Regex;
import de.uzl.lied.mtbimporter.settings.Settings;

public class FhirResolver {

    private static FhirContext ctx = FhirContext.forR4();
    private static IGenericClient cdrClient;
    private static IGenericClient terminologyClient;

    public static void initalize() {
        cdrClient = ctx.newRestfulGenericClient(Settings.getFhir().getClinicalDataServerUrl());
        if (Settings.getFhir().getTerminology() != null) {
            terminologyClient = ctx.newRestfulGenericClient(Settings.getFhir().getTerminology().getServerUrl());
        }
    }

    public static String resolvePatientFromSample(String sampleId) {

        for (Regex r : Settings.getRegex()) {
            sampleId = sampleId.replaceAll(r.getCbio(), r.getHis());
        }
        Bundle bSpecimenPatient = (Bundle) cdrClient.search().forResource(Specimen.class)
                .where(new TokenClientParam("identifier").exactly().code(sampleId))
                .include(new Include("Specimen:patient")).prettyPrint().execute();

        List<BundleEntryComponent> entries = bSpecimenPatient.getEntry();

        String pid = "";
        int pidCount = 0;

        for (BundleEntryComponent bec : entries) {
            if (bec.getResource() instanceof Patient) {
                Patient p = (Patient) bec.getResource();
                pidCount++;
                pid = p.getIdentifierFirstRep().getValue();
            }
        }

        if (pidCount == 1) {
            return pid;
        }

        return "";

    }

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
                if ((c.getValue() instanceof Coding)) {
                    coding = (Coding) c.getValue();
                }
                if ((c.getValue() instanceof StringType) && c.getName().equals("source")) {
                    str = ((StringType) c.getValue()).getValue();
                }
            }
            if (str.equals(Settings.getFhir().getTerminology().getIcdO3ToOncoTreeConceptMapUrl())) {
                return coding;
            }
        }
        
        return null;
    }

}
