package com.tngtech.jgiven.format.table;

import com.tngtech.jgiven.config.FormatterConfiguration;
import com.tngtech.jgiven.format.ObjectFormatter;

/**
 * Factory to create a custom {@link com.tngtech.jgiven.format.table.TableFormatter}
 * <p>
 * Implementing classes must have a default constructor!    
 * </p>
 * 
 * @see com.tngtech.jgiven.annotation.Table
 */
public interface TableFormatterFactory {
    /**
     * Creates a {@link com.tngtech.jgiven.format.table.TableFormatter}
     * 
     * @param formatterConfiguration the formatter configuration
     * @param objectFormatter the default object formatter that would be used by JGiven
     * @return a {@link com.tngtech.jgiven.format.table.TableFormatter}
     */
    TableFormatter create( FormatterConfiguration formatterConfiguration, ObjectFormatter<?> objectFormatter );
}
