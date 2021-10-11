package de.uzl.lied.mtbimporter.model.serializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import de.uzl.lied.mtbimporter.model.Timeline;
import de.uzl.lied.mtbimporter.model.TimelineSpecimen;
import de.uzl.lied.mtbimporter.model.TimelineTreatment;

public class TimelineSerializer extends StdSerializer<Timeline> {
    
    public TimelineSerializer() {
        this(null);
    }

    public TimelineSerializer(Class<Timeline> t) {
        super(t);
    }

    @Override
    public void serialize(Timeline value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if(value.getTmpBaseDate() != null) {
            LocalDate base = value.getTmpBaseDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate start = value.getTmpStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate stop = value.getTmpStopDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    
            value.setStartDate(ChronoUnit.DAYS.between(base, start));
            value.setStopDate(ChronoUnit.DAYS.between(base, stop));
        }
        if(value.getStartDate() == null || value.getStopDate() == null) {
            return;
        }

        gen.writeStartObject();

        gen.writeStringField("EVENT_TYPE", value.getEventType());
        gen.writeStringField("PATIENT_ID", value.getPatientId());
        gen.writeStringField("START_DATE", value.getStartDate() + "");
        gen.writeStringField("STOP_DATE", value.getStopDate() + "");
        gen.writeStringField("NOTE", value.getNote());

        if(value instanceof TimelineSpecimen) {
            TimelineSpecimen tls = (TimelineSpecimen) value;
            gen.writeStringField("SPECIMEN_SITE", tls.getSpecimenSite());
            gen.writeStringField("SPECIMEN_TYPE", tls.getSpecimenType());
            gen.writeStringField("SOURCE", tls.getSource());
            gen.writeStringField("SPECIMEN_REFERENCE_NUMBER", tls.getSpecimenReferenceNumber());
        }

        if(value instanceof TimelineTreatment) {
            TimelineTreatment tlt = (TimelineTreatment) value;
            gen.writeStringField("AGENT", tlt.getAgent());
        }

        gen.writeEndObject();
    }
    

}
