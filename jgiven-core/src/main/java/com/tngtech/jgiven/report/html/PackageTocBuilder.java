package com.tngtech.jgiven.report.html;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.tngtech.jgiven.report.html.StaticHtmlReportGenerator.ModelFile;

public class PackageTocBuilder {

    private final List<ModelFile> sortedModels;
    private final Map<String, PackageToc> tocs = Maps.newLinkedHashMap();

    public PackageTocBuilder( List<ModelFile> models ) {
        this.sortedModels = models;
    }

    static class PackageToc {
        String name;
        List<PackageToc> packages = Lists.newArrayList();
        List<ModelFile> files = Lists.newArrayList();

        public String getParentName() {
            int lastIndexOf = name.lastIndexOf( '.' );
            if( lastIndexOf == -1 ) {
                return "";
            }
            return name.substring( 0, lastIndexOf );
        }

        public String getLastName() {
            int lastIndexOf = name.lastIndexOf( '.' );
            if( lastIndexOf == -1 ) {
                return name;
            }
            return name.substring( lastIndexOf + 1 );
        }

        void sortFiles() {
            Comparator<ModelFile> comparator = new Comparator<ModelFile>() {
                @Override
                public int compare( ModelFile o1, ModelFile o2 ) {
                    return o1.model.getClassName().compareTo( o2.model.getClassName() );
                }
            };
            Collections.sort( files, comparator );
        }

        void sortPackages() {
            Comparator<PackageToc> comparator = new Comparator<PackageToc>() {
                @Override
                public int compare( PackageToc o1, PackageToc o2 ) {
                    return o1.name.compareTo( o2.name );
                }
            };
            Collections.sort( packages, comparator );
        }

        void sort() {
            sortFiles();
            sortPackages();
        }

    }

    public PackageToc getRootPackageToc() {
        calculatePackageTocs();
        Queue<PackageToc> tocCopy = Queues.newArrayDeque( tocs.values() );
        Map<String, PackageToc> handledTocs = Maps.newHashMap();

        while( !tocCopy.isEmpty() ) {
            PackageToc toc = tocCopy.remove();
            if( !toc.name.equals( "" ) ) {
                PackageToc parentToc = getOrCreate( toc.getParentName() );
                parentToc.packages.add( toc );
                handledTocs.put( toc.name, toc );
                if( !handledTocs.containsKey( parentToc.name ) ) {
                    tocCopy.add( parentToc );
                    handledTocs.put( parentToc.name, parentToc );
                }
            }

        }

        for( PackageToc toc : tocs.values() ) {
            toc.sort();
        }

        return tocs.get( "" );
    }

    private void calculatePackageTocs() {
        for( ModelFile file : sortedModels ) {
            String packageName = file.model.getPackageName();
            PackageToc packageToc = getOrCreate( packageName );
            packageToc.files.add( file );
        }
    }

    private PackageToc getOrCreate( String packageName ) {
        PackageToc packageToc = tocs.get( packageName );
        if( packageToc == null ) {
            packageToc = new PackageToc();
            packageToc.name = packageName;
            tocs.put( packageName, packageToc );
        }
        return packageToc;
    }

}
