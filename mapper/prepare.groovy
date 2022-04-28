/**
* Preparation. Loads the diagnoses file for calculation fo dates relative to first diagnose.
* @param csv File with diagnoses
* @param study study that will cache the diagnoses data
* @throws IOException if file is not properly formatted csv or non existent
*/
final String pid = 'PID'
final String jahrText = 'JAHR_TEXT'
final String monatText = 'MONAT_TEXT'

if (csv.name.contains('Diagnosen_Vorst')) {
    Map<String, Map<String, String>> pMap = [:]

    // Create Jackson mapper and reader
    com.fasterxml.jackson.dataformat.csv.CsvMapper om = new com.fasterxml.jackson.dataformat.csv.CsvMapper()
            .enable(com.fasterxml.jackson.dataformat.csv.CsvParser.Feature.ALLOW_COMMENTS)
    com.fasterxml.jackson.databind.ObjectReader or = om.readerFor(
            new com.fasterxml.jackson.core.type.TypeReference<java.util.HashMap<String, String>>() { })
                    .with(com.fasterxml.jackson.dataformat.csv.CsvSchema.emptySchema()
                            .withHeader().withComments().withColumnSeparator(';' as char))

    // Read csv to list
    List<Map<String, String>> l = or.
            readValues(org.mozilla.universalchardet.ReaderFactory.createBufferedReader(csv)).readAll()

    // Determine first diagnosed
    for (m in l) {
        if (pMap.containsKey(m.get(pid))) {
            Map<String, String> n = pMap.get(m.get(pid))
            if (!m.containsKey(jahrText) || !m.containsKey(monatText)
                || m.get(jahrText) == '' || m.get(monatText) == '') {
                continue
            }
            if (Integer.parseInt(m.get(jahrText)) < Integer.parseInt(n.get(jahrText))
                    && Integer.parseInt(m.get(monatText)) < Integer.parseInt(n.get(monatText))) {
                pMap.put(m.get(pid), m)
                    }
        } else {
            if (m.containsKey(jahrText) && m.containsKey(monatText)
                && m.get(jahrText) != '' && m.get(monatText) != '') {
                pMap.put(m.get(pid), m)
            }
        }
    }
    pMap.each { k, v -> study.addPreparation(k, v) }
}
