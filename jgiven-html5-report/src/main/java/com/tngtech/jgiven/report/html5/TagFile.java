package com.tngtech.jgiven.report.html5;

import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.tngtech.jgiven.report.model.Tag;

public class TagFile {
    private Map<String, Tag> tagTypeMap = Maps.newLinkedHashMap();
    private Map<String, TagInstance> tags = Maps.newLinkedHashMap();

    private static class TagInstance {
        String tagType;
        String value;
        String description;
        String href;
    }

    public void fill( Map<String, Tag> tagIdMap ) {
        for( Map.Entry<String, Tag> entry : tagIdMap.entrySet() ) {

            // remove the value as it is not part of the type
            Tag tag = entry.getValue().copy();
            tag.setValue( (String) null );

            if( !tagTypeMap.containsKey( tag.getType() ) ) {
                tagTypeMap.put( tag.getType(), tag );
            }

            TagInstance instance = new TagInstance();
            instance.tagType = tag.getType();
            instance.value = entry.getValue().getValueString();

            // the description might be generated depending on the value, so it must be stored
            // for each tag instance separately
            if( !Objects.equal( entry.getValue().getDescription(), tagTypeMap.get( tag.getType() ).getDescription() ) ) {
                instance.description = entry.getValue().getDescription();
            }

            // the href might be generated depending on the value, so it must be stored
            // for each tag instance separately
            if( !Objects.equal( entry.getValue().getHref(), tagTypeMap.get( tag.getType() ).getHref() ) ) {
                instance.href = entry.getValue().getHref();
            }
            tags.put( entry.getKey(), instance );

        }
    }
}
