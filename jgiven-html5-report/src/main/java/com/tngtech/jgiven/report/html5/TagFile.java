package com.tngtech.jgiven.report.html5;

import java.util.LinkedHashMap;
import java.util.Map;

import com.tngtech.jgiven.impl.util.ObjectUtil;
import com.tngtech.jgiven.report.model.Tag;

public class TagFile {
    private Map<String, Tag> tagTypeMap = new LinkedHashMap<String, Tag>();
    private Map<String, TagInstance> tags = new LinkedHashMap<String, TagInstance>();

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
            if( !ObjectUtil.equals( entry.getValue().getDescription(), tagTypeMap.get( tag.getType() ).getDescription() ) ) {
                instance.description = entry.getValue().getDescription();
            }

            // the href might be generated depending on the value, so it must be stored
            // for each tag instance separately
            if( !ObjectUtil.equals( entry.getValue().getHref(), tagTypeMap.get( tag.getType() ).getHref() ) ) {
                instance.href = entry.getValue().getHref();
            }
            tags.put( entry.getKey(), instance );

        }
    }
}
