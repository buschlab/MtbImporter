package de.uzl.lied.mtbimporter.jobs;

import de.uzl.lied.mtbimporter.model.Maf;
import de.uzl.lied.mtbimporter.settings.Settings;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.tinylog.Logger;

/**
 * Class for calling VEP variant recorder.
 */
public final class EnsemblResolver {

    private static final int CHRX = 23;
    private static final int CHRY = 24;

    private EnsemblResolver() {
    }

    /**
     * Method to invoke Ensembl variant recorder to enrich the annotaiton of a given
     * mutation.
     * @param m Maf entry lacking information about start/end, chr...
     * @return Maf entry for cBioPortal
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Maf enrich(Maf m) {

        String mutation = m.getTxChange() != null ? m.getTxChange() : m.getHgvspShort();
        if (mutation == null || mutation.isEmpty() || "MUTATED".equals(mutation)) {
            return m;
        }

        RestTemplate rt = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(Settings.getEnsemblUrl() + "/variant_recoder/human/" + m.getHugoSymbol() + ":"
                        + mutation + "?content-type=application/json");
        try {
            List l = rt.getForEntity(builder.build().encode().toUri(), List.class).getBody();

            if (l == null || l.isEmpty()) {
                return m;
            }
            Map<String, Object> map = (Map<String, Object>) l.get(0);
            if (map.size() != 2) {
                return m;
            }
            for (Entry<String, Object> o : map.entrySet()) {
                if (!o.getKey().equals("warnings")) {
                    Map<String, List<String>> content = (Map<String, List<String>>) o.getValue();
                    String s = content.get("hgvsg").get(0);
                    String s2 = s.split("g.")[1].replaceAll("[a-zA-Z>]", "");
                    if (s2.matches("_")) {
                        m.setStartPosition(Integer.parseInt(s2.split("-")[0]));
                        m.setEndPosition(Integer.parseInt(s2.split("-")[1]));
                    } else {
                        m.setStartPosition(Integer.parseInt(s2));
                        m.setEndPosition(Integer.parseInt(s2));
                    }
                    Integer chr = Integer.parseInt(s.replace("NC_", "").split("\\.")[0]);
                    switch (chr) {
                        case CHRX:
                            m.setChromosome("chrX");
                            break;
                        case CHRY:
                            m.setChromosome("chrY");
                            break;
                        default:
                            m.setChromosome("chr" + chr);
                            break;
                    }
                }
            }
        } catch (HttpClientErrorException e) {
            Logger.error("Could not enrich variant " + m.getHugoSymbol() + " " + m.getHgvspShort()
                    + "using Ensembl variant recorder.", e);
        }

        return m;
    }

}
