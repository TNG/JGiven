package com.tngtech.jgiven.report.model;

import java.util.List;

import com.google.common.collect.Lists;

public class ReportModel {
    public String className;
    public List<ScenarioModel> scenarios = Lists.newArrayList();

    public void accept( ReportModelVisitor visitor ) {
        visitor.visit( this );
        for( ScenarioModel m : scenarios ) {
            m.accept( visitor );
        }
        visitor.visitEnd( this );

    }

    public ScenarioModel getLastScenarioModel() {
        return scenarios.get( scenarios.size() - 1 );
    }

    public void addScenarioModel( ScenarioModel currentScenarioModel ) {
        scenarios.add( currentScenarioModel );
    }

}
