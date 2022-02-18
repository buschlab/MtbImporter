package de.uzl.lied.mtbimporter.jobs.mdr.centraxx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.uzl.lied.mtbimporter.model.mdr.centraxx.RelationConvert;
import de.uzl.lied.mtbimporter.settings.CxxMdrSettings;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 * Class for data conversion using Kairos CentraXX MDR.
 */
public final class CxxMdrConvert {

    private CxxMdrConvert() {
    }

    /**
     * Converts data from a source profile to a target profile using Kairos CentraXX MDR.
     * @param mdr Configuration for MDR.
     * @param input Relation with input data
     * @return Relation with output data
     * @throws JsonProcessingException
     */
    public static RelationConvert convert(CxxMdrSettings mdr, RelationConvert input) throws JsonProcessingException {

        if (mdr.isTokenExpired()) {
            CxxMdrLogin.login(mdr);
        }

        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString());
        headers.add("Authorization", "Bearer " + mdr.getToken());
        ObjectMapper om = new ObjectMapper();
        om.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
                .writeValueAsString(input);
        return rt.postForObject(mdr.getUrl() + "/rest/v1/relations/convert",
                new HttpEntity<>(om.writeValueAsString(input), headers),
                RelationConvert.class);

    }

}
