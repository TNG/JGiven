package com.tngtech.jgiven.report.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Represents the complete report model of all report files.
 */
public class CompleteReportModel {

    protected final List<ReportModelFile> models = Lists.newArrayList();
    protected final Map<Tag, List<ScenarioModel>> tagMap = Maps.newHashMap();
    protected final Map<ReportModelFile, ReportStatistics> statisticsMap = Maps.newHashMap();
    protected ReportStatistics totalStatistics = new ReportStatistics();
    protected final List<ScenarioModel> failedScenarios = Lists.newArrayList();
    protected final List<ScenarioModel> pendingScenarios = Lists.newArrayList();
    protected final List<ScenarioModel> allScenarios = Lists.newArrayList();
    protected final Map<String, Tag> tagIdMap = Maps.newLinkedHashMap();

    public void addModelFile( ReportModelFile modelFile ) {
        ReportModel model = modelFile.model;

        for( ScenarioModel scenario : model.getScenarios() ) {
            for( String tagId : scenario.getTagIds() ) {
                Tag tag = model.getTagWithId( tagId );
                addToMap( tag, scenario );
            }
        }

        tagIdMap.putAll( model.getTagMap() );
        ReportStatistics statistics = new StatisticsCalculator().getStatistics( model );

        statisticsMap.put( modelFile, statistics );

        totalStatistics = totalStatistics.add( statistics );

        models.add( modelFile );
        failedScenarios.addAll( model.getFailedScenarios() );
        pendingScenarios.addAll( model.getPendingScenarios() );
        allScenarios.addAll( model.getScenarios() );

    }

    private void addToMap( Tag tag, ScenarioModel scenario ) {
        List<ScenarioModel> list = tagMap.get( tag );
        if( list == null ) {
            list = Lists.newArrayList();
            tagMap.put( tag, list );
        }
        list.add( scenario );
    }

    public List<ScenarioModel> getFailedScenarios() {
        return failedScenarios;
    }

    public List<ScenarioModel> getPendingScenarios() {
        return pendingScenarios;
    }

    public List<ScenarioModel> getAllScenarios() {
        return allScenarios;
    }

    public ReportStatistics getTotalStatistics() {
        return totalStatistics;
    }

    public ReportStatistics getStatistics( ReportModelFile reportModelFile ) {
        return statisticsMap.get( reportModelFile );
    }

    public Set<Tag> getAllTags() {
        return tagMap.keySet();
    }

    public List<ScenarioModel> getScenariosByTag( Tag tag ) {
        return tagMap.get( tag );
    }

    public List<ReportModelFile> getAllReportModels() {
        return models;
    }

    public Map<String, Tag> getTagIdMap() {
        return tagIdMap;
    }
}
