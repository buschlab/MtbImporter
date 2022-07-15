package de.uzl.lied.mtbimporter.jobs.mdr.dataelementhub;

import de.dataelementhub.model.dto.element.section.Slot;
import de.dataelementhub.model.dto.listviews.NamespaceMember;
import de.uzl.lied.mtbimporter.model.ClinicalHeader;
import de.uzl.lied.mtbimporter.model.mdr.MdrAttributes;
import de.uzl.lied.mtbimporter.settings.DataElementHubSettings;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.tinylog.Logger;

/**
 * Class to fetch attributes from DataElementHub.
 */
public final class DataElementHubAttributes {

    private DataElementHubAttributes() {
    }

    /**
     * Extracts clinical header information for cBioPortal using DataElementHub.
     *
     * @param oldMdr
     * @param key
     * @return
     */
    public static ClinicalHeader getAttributes(DataElementHubSettings oldMdr, String key) {

        DataElementHubSettings mdr = oldMdr;

        if (mdr.isTokenExpired()) {
            mdr = DataElementHubLogin.login(mdr);
        }

        try {

            RestTemplate rt = new RestTemplate();
            int id = DataElementHubNamespace.getNameSpaceId(mdr, mdr.getTargetNamespace());

            ParameterizedTypeReference<List<NamespaceMember>> namespaceMembersType =
                    new ParameterizedTypeReference<List<NamespaceMember>>() {
                };
            UriComponentsBuilder builderMember = UriComponentsBuilder
                    .fromHttpUrl(mdr.getUrl() + "/namespaces/" + id + "/members?elementType=DATAELEMENT");
            HttpHeaders headersMembers = new HttpHeaders();
            headersMembers.add(HttpHeaders.ACCEPT, "application/vnd+de.dataelementhub.listview+json");
            headersMembers.add("Authorization", "Bearer " + mdr.getToken());
            ResponseEntity<List<NamespaceMember>> elementsE = rt.exchange(builderMember.build().toUri(), HttpMethod.GET,
                    new HttpEntity<>(headersMembers), namespaceMembersType);
            List<NamespaceMember> elements = elementsE.getBody();
            List<NamespaceMember> el = new ArrayList<>();
            elements.forEach(e ->
                e.getDefinitions().forEach(d -> {
                    if (key.equals(d.getDesignation())) {
                        el.add(e);
                    }
                })
            );
            if (el.size() != 1) {
                return null;
            }

            String dataElement = "urn:" + id + ":dataelement:" + el.get(0).getIdentifier() + ":"
                    + el.get(0).getRevision();

            UriComponentsBuilder builderSlots = UriComponentsBuilder
                    .fromHttpUrl(mdr.getUrl() + "/element/" + dataElement + "/slots");
            ParameterizedTypeReference<List<Slot>> slotsType = new ParameterizedTypeReference<List<Slot>>() {
            };
            ResponseEntity<List<Slot>> slotsE = rt.exchange(builderSlots.build().toUri(), HttpMethod.GET,
                    new HttpEntity<>(headersMembers), slotsType);

            ClinicalHeader ch = new ClinicalHeader();
            for (Slot s : slotsE.getBody()) {
                switch (MdrAttributes.fromString(s.getName())) {
                    case DISPLAYNAME:
                        ch.setDisplayName(s.getValue());
                        break;
                    case DATATYPE:
                        ch.setDatatype(s.getValue());
                        break;
                    case PRIORITY:
                        ch.setPriority((int) Double.parseDouble(s.getValue()));
                        break;
                    case DESCRIPTION:
                        ch.setDescription(s.getValue());
                        break;
                    default:
                        break;
                }
            }

            return ch;

        } catch (

        final HttpClientErrorException e) {
            Logger.error("Object " + key + " not found in MDR!");
            return null;
        }
    }

}
