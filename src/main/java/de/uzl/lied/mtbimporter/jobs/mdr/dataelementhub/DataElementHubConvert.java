package de.uzl.lied.mtbimporter.jobs.mdr.dataelementhub;

import de.dataelementhub.dal.jooq.enums.AccessLevelType;
import de.dataelementhub.model.dto.element.Namespace;
import de.dataelementhub.model.dto.element.section.ConceptAssociation;
import de.dataelementhub.model.dto.listviews.DataElementGroupMember;
import de.dataelementhub.model.dto.listviews.NamespaceMember;
import de.uzl.lied.mtbimporter.model.mdr.centraxx.RelationConvert;
import de.uzl.lied.mtbimporter.settings.DataElementHubSettings;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.codehaus.groovy.control.CompilationFailedException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.tinylog.Logger;

/**
 * Class for data conversion using Samply MDR.
 */
public final class DataElementHubConvert {

    private DataElementHubConvert() {
    }

    /**
     * Converts a relation from a source to a target using Samply MDR.
     *
     * @param oldMdr Configuration for MDR.
     * @param input  Relation with input data
     * @return Relation with output data
     * @throws CompilationFailedException
     * @throws IOException
     */
    public static RelationConvert convert(DataElementHubSettings oldMdr, RelationConvert input)
            throws CompilationFailedException, IOException {

        RelationConvert output = new RelationConvert();
        output.setTargetProfileVersion(input.getTargetProfileVersion());
        output.setTargetProfileCode(input.getTargetProfileCode());
        output.setValues(new HashMap<>());

        DataElementHubSettings mdr = oldMdr;

        String mdrNamespace = mdr.getSourceNamespace();

        if (mdr.isTokenExpired()) {
            mdr = DataElementHubLogin.login(mdr);
        }

        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(mdr.getUrl() + "/namespaces");
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString());
        headers.add("Authorization", "Bearer " + mdr.getToken());
        try {
            ParameterizedTypeReference<Map<AccessLevelType, List<Namespace>>> responseType =
                    new ParameterizedTypeReference<Map<AccessLevelType, List<Namespace>>>() {};
            ResponseEntity<Map<AccessLevelType, List<Namespace>>> response = rt.exchange(
                    builder.build().encode().toUri(), HttpMethod.GET,
                    new HttpEntity<>(headers), responseType);

            Map<AccessLevelType, List<Namespace>> ns = response.getBody();

            List<Namespace> namespaces = new ArrayList<>();
            ns.values().forEach(v -> namespaces.addAll(v));
            List<Namespace> l = new ArrayList<>();
            namespaces.forEach(n -> {
                n.getDefinitions().forEach(d -> {
                    if (d.getDesignation().equals(mdrNamespace)) {
                        l.add(n);
                    }
                });
            });
            if (l.size() != 1) {
                return null;
            }
            int namespaceId = l.get(0).getIdentification().getIdentifier();

            ParameterizedTypeReference<List<NamespaceMember>> namespaceMembersType =
                    new ParameterizedTypeReference<List<NamespaceMember>>() {};
            ParameterizedTypeReference<List<DataElementGroupMember>> membersType =
                    new ParameterizedTypeReference<List<DataElementGroupMember>>() {};
            UriComponentsBuilder builderMember = UriComponentsBuilder
                    .fromHttpUrl(mdr.getUrl() + "/namespaces/" + namespaceId + "/members?elementType=DATAELEMENTGROUP");
            HttpHeaders headersMembers = new HttpHeaders();
            headersMembers.add(HttpHeaders.ACCEPT, "application/vnd+de.dataelementhub.listview+json");
            headersMembers.add("Authorization", "Bearer " + mdr.getToken());
            ResponseEntity<List<NamespaceMember>> elementsE = rt.exchange(builderMember.build().toUri(), HttpMethod.GET,
                    new HttpEntity<>(headersMembers), namespaceMembersType);
            List<NamespaceMember> elements = elementsE.getBody();

            ns.values().forEach(v -> namespaces.addAll(v));
            List<NamespaceMember> l2 = new ArrayList<>();
            elements.forEach(n -> {
                n.getDefinitions().forEach(d -> {
                    if (d.getDesignation().equals(input.getSourceProfileCode())) {
                        l2.add(n);
                    }
                });
            });
            if (l2.size() != 1) {
                return null;
            }
            String dataElementGroupUri = "urn:" + namespaceId + ":dataelementgroup:" + l2.get(0).getIdentifier() + ":"
                    + l2.get(0).getRevision();

            builderMember = UriComponentsBuilder
                    .fromHttpUrl(mdr.getUrl() + "/element/" + dataElementGroupUri + "/members");
            ResponseEntity<List<DataElementGroupMember>> elementsM = rt.exchange(builderMember.build().toUri(),
                    HttpMethod.GET,
                    new HttpEntity<>(headersMembers), membersType);

            Map<String, List<ConceptAssociation>> dataelementMap = new HashMap<>();
            elementsM.getBody().forEach(e -> {

                UriComponentsBuilder builderSlots = UriComponentsBuilder
                        .fromHttpUrl(oldMdr.getUrl() + "/element/" + e.getUrn() + "/concepts");
                ParameterizedTypeReference<List<ConceptAssociation>> slotsType =
                        new ParameterizedTypeReference<List<ConceptAssociation>>() {};
                ResponseEntity<List<ConceptAssociation>> slotsE = rt.exchange(builderSlots.build().toUri(),
                        HttpMethod.GET,
                        new HttpEntity<>(headersMembers), slotsType);

                dataelementMap.put(e.getDefinitions().get(0).getDesignation(), slotsE.getBody());
            });

            Binding b = new Binding();
            for (Entry<String, Object> e : input.getValues().entrySet()) {
                if (dataelementMap.get(e.getKey()) == null) {
                    continue;
                }
                for (ConceptAssociation ca : dataelementMap.get(e.getKey())) {
                    if (ca.getSystem().equals("cbioportal")
                            && ca.getTerm().split("/")[0].equals(input.getTargetProfileCode())) {
                        if (ca.getLinktype().getLiteral().equals("equal")) {
                            output.getValues().put(ca.getText(), e.getValue());
                        } else {
                            GroovyShell s = new GroovyShell(b);
                            b.setVariable("src", e.getValue());
                            Object target = s.evaluate(new File(
                                    "mapper/" + input.getTargetProfileCode() + "/" + e.getKey().toLowerCase()
                                            + ".groovy"));
                            output.getValues().put(ca.getText(), target);
                        }
                    }
                }
            }

        } catch (

        final HttpClientErrorException e) {
            Logger.error("Object not found in MDR!");
            return null;
        }

        return output;

    }

}
