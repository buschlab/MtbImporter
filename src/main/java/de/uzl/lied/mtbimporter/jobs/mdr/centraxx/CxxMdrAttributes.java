package de.uzl.lied.mtbimporter.jobs.mdr.centraxx;

import de.uzl.lied.mtbimporter.model.ClinicalHeader;
import de.uzl.lied.mtbimporter.model.mdr.centraxx.CxxAttributeValue;
import de.uzl.lied.mtbimporter.model.mdr.centraxx.CxxList;
import de.uzl.lied.mtbimporter.settings.CxxMdrSettings;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Class to fetch attributes from a Kairos CentraXX MDR.
 */
public final class CxxMdrAttributes {

    private CxxMdrAttributes() {
    }

    /**
     * Retrieves a cBioPortal ClinicalHeader object for a specified attribute.
     * @param mdr Configuration for MDR.
     * @param mdrProfile profile / form / ItemSet where the item is defined
     * @param key key of the requested attribute
     * @return ClinicalHeader object for cBioPortal
     */
    public static ClinicalHeader getAttributes(CxxMdrSettings mdr, String mdrProfile, String key) {

        if (mdr.isTokenExpired()) {
            CxxMdrLogin.login(mdr);
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        form.set("code", mdrProfile);
        form.set("domainCode", "cbioportal");
        form.set("itemCode", key);

        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(mdr.getUrl() + "/rest/v1/itemsets/attributes/item");
        builder.queryParams(form);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString());
        headers.add("Authorization", "Bearer " + mdr.getToken());
        try {
            ResponseEntity<CxxList> response = rt.exchange(builder.build().encode().toUri(), HttpMethod.GET,
                    new HttpEntity<>(headers), CxxList.class);
            CxxList l = response.getBody();
            if (l.getContent() != null) {
                ClinicalHeader ch = new ClinicalHeader();
                for (CxxAttributeValue av : l.getContent()) {
                    switch (av.getAttribute()) {
                        case "display-name":
                            ch.setDisplayName(av.getValue());
                            break;
                        case "datatype":
                            ch.setDatatype(av.getValue());
                            break;
                        case "priority":
                            ch.setPriority((int) Double.parseDouble(av.getValue()));
                            break;
                        case "description":
                            ch.setDescription(av.getValue());
                            break;
                        default:
                            break;
                    }
                }
                return ch;
            }
            return null;

        } catch (final HttpClientErrorException e) {
            System.err.println("Object " + form.get("itemCode") + " not found in MDR!");
            return null;
        }
    }

}
