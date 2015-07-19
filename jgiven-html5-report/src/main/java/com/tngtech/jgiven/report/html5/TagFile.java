package com.tngtech.jgiven.report.html5;

import java.util.Map;

import com.google.common.collect.Maps;
import com.tngtech.jgiven.report.model.Tag;

public class TagFile {
    private Map<String, Tag> tagTypeMap = Maps.newLinkedHashMap();
    private Map<String, TagInstance> tags = Maps.newLinkedHashMap();

    private static class TagInstance {
        String tagType;
        String value;
    }

    public void fill( Map<String, Tag> tagIdMap ) {
        for( Map.Entry<String, Tag> entry : tagIdMap.entrySet() ) {

            // remove the value as it is not part of the type
            Tag tag = entry.getValue().copy();
            tag.setValue( (String) null );
            tagTypeMap.put( tag.getType(), tag );

            TagInstance instance = new TagInstance();
            instance.tagType = tag.getType();
            instance.value = entry.getValue().getValueString();
            tags.put( entry.getKey(), instance );

        }
    }

}
