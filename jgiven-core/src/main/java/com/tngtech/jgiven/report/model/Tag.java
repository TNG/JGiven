package com.tngtech.jgiven.report.model;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

/**
 * A tag represents a Java annotation of a scenario-test.
 */
public class Tag {
    /**
     * The type of the annotation of the tag
     */
    private final String type;

    /**
     * An optional name of the tag. If not set, the type is the name
     */
    private String name;

    /**
     * An optional value
     * Guaranteed to be either of type {@code String} or of type {@code List<String>}
     */
    private Object value;

    /**
     * An optional description.
     */
    private String description;

    /**
     * Whether the type should be prepended in the report.
     * <p>
     * Is either {@code true} or {@code null}
     */
    private Boolean prependType;

    /**
     * An optional color that is used in reports 
     */
    private String color;

    /**
     * An optional cssClass used in the HTML report.
     * Can be {@code null}.
     */
    private String cssClass;

    /**
     * An optional style used in the HTML report. 
     * Can be {@code null}.
     */
    private String style;

    /**
     * An optional (maybe null) list of tags that this tag is tagged with.
     * The tags are normalized as follows: <name>[-value].
     */
    private List<String> tags;

    /**
     * An optional href used in the HTML report.
     * Can be {@code null}.
     */
    private String href;

    /**
     * Whether the tag should appear in the navigation part of the report
     * <p>
     * Is either {@code true} or {@code null}
     */
    private Boolean hideInNav;

    public Tag( String type ) {
        this.type = type;
    }

    public Tag( String type, Object value ) {
        this( type );
        this.value = value;
    }

    public Tag( String type, String name, Object value ) {
        this( type, value );
        this.name = name;
    }

    public String getName() {
        if( name == null ) {
            return type;
        }

        return name;
    }

    public boolean isPrependType() {
        return prependType == null ? false : true;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public void setColor( String color ) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setCssClass( String cssClass ) {
        this.cssClass = cssClass;
    }

    public String getCssClass() {
        return cssClass;
    }

    public String getCssClassOrDefault() {
        return cssClass == null ? "tag-" + getName() : cssClass;
    }

    public void setStyle( String style ) {
        this.style = style;
    }

    public String getStyle() {
        return style;
    }

    public String getHref() {
        return href;
    }

    public void setHref( String href ) {
        this.href = href;
    }

    @SuppressWarnings( "unchecked" )
    public List<String> getValues() {
        if( value == null ) {
            return Collections.emptyList();
        }
        if( value instanceof String ) {
            return Lists.newArrayList( (String) value );
        }
        return (List<String>) value;
    }

    public void setValue( List<String> values ) {
        this.value = values;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public Tag setPrependType( boolean prependType ) {
        this.prependType = prependType ? true : null;
        return this;
    }

    public void setShowInNavigation( boolean show ) {
        this.hideInNav = show ? null : true;
    }

    @Override
    public String toString() {
        if( value != null ) {
            String valueString = getValueString();
            if( isPrependType() ) {
                return getName() + "-" + valueString;
            }
            return valueString;
        }
        return getName();
    }

    public String getValueString() {
        if( value == null ) {
            return null;
        }
        return Joiner.on( ", " ).join( getValues() );
    }

    public String toIdString() {
        if( value != null ) {
            return type + "-" + getValueString();
        }
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode( getType(), getName(), value );
    }

    @Override
    public boolean equals( Object obj ) {
        if( this == obj ) {
            return true;
        }
        if( obj == null ) {
            return false;
        }
        if( getClass() != obj.getClass() ) {
            return false;
        }
        Tag other = (Tag) obj;
        return Objects.equal( getType(), other.getType() )
                && Objects.equal( getName(), other.getName() )
                && Objects.equal( value, other.value );
    }

    /**
     * Returns a string representation where all non-alphanumeric characters are replaced with an underline (_).
     * In addition, the result is cut-off at a length of 255 characters.
     *
     * @return a string representation without special characters
     */
    public String toEscapedString() {
        List<String> parts = Lists.newArrayList( getName() );
        parts.addAll( getValues() );
        String escapedString = escape( Joiner.on( '-' ).join( parts ) );
        return escapedString.substring( 0, Math.min( escapedString.length(), 255 ) );
    }

    static String escape( String string ) {
        return string.replaceAll( "[^\\p{Alnum}-]", "_" );
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public boolean getShownInNavigation() {
        return hideInNav == null;
    }

    public List<String> getTags() {
        if( tags == null ) {
            return Collections.emptyList();
        }
        return tags;
    }

    public void setTags( List<String> tags ) {
        if( tags != null && !tags.isEmpty() ) {
            this.tags = tags;
        }
    }

    public Tag copy() {
        Tag tag = new Tag( type, name, value );
        tag.cssClass = this.cssClass;
        tag.color = this.color;
        tag.style = this.style;
        tag.description = this.description;
        tag.prependType = this.prependType;
        tag.tags = this.tags;
        tag.href = this.href;
        tag.hideInNav = this.hideInNav;
        return tag;
    }

}
