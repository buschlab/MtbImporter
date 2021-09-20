package de.uzl.lied.mtbimporter.jobs;

import java.util.List;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Specimen;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import de.uzl.lied.mtbimporter.settings.Regex;
import de.uzl.lied.mtbimporter.settings.Settings;

public class FhirResolver {

    private static FhirContext ctx = FhirContext.forR4();
    private static IGenericClient client;


    public static void initalize() {
        client = ctx.newRestfulGenericClient(Settings.getFhirServer());
    }

    public static String resolvePatientFromSample(String sampleId) {

        for(Regex r : Settings.getRegex()) {
            sampleId = sampleId.replaceAll(r.getCbio(), r.getHis());
        }
        Bundle bSpecimenPatient = (Bundle) client.search().forResource(Specimen.class)
        .where(new TokenClientParam("identifier").exactly().code(sampleId))
        .include(new Include("Specimen:patient"))
        .prettyPrint()
        .execute();

        List<BundleEntryComponent> entries = bSpecimenPatient.getEntry();

        String pid = "";
        int pidCount = 0;

        for(BundleEntryComponent bec : entries) {
            if(bec.getResource() instanceof Patient) {
                Patient p = (Patient) bec.getResource();
                pidCount++;
                pid = p.getIdentifierFirstRep().getValue();
            }
        }

        if(pidCount == 1) {
            return pid;
        }

        return "";

    }

}
