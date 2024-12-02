package com.tngtech.jgiven.gradle.internal;

import com.tngtech.jgiven.gradle.JGivenReport;
import com.tngtech.jgiven.gradle.JGivenReportsContainer;
import groovy.lang.Closure;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.NamedDomainObjectSet;
import org.gradle.api.NonNullApi;
import org.gradle.api.Task;
import org.gradle.api.internal.DefaultNamedDomainObjectSet;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.reporting.Report;
import org.gradle.api.reporting.ReportContainer;
import org.gradle.internal.instantiation.InstanceGenerator;
import org.gradle.internal.instantiation.InstantiatorFactory;
import org.gradle.internal.service.ServiceRegistry;
import org.gradle.util.internal.ConfigureUtil;

import javax.inject.Inject;
import java.util.Map;

import static org.gradle.api.internal.CollectionCallbackActionDecorator.NOOP;

@NonNullApi
public class JGivenReportsContainerImpl extends DefaultNamedDomainObjectSet<JGivenReport> implements JGivenReportsContainer {

    private final NamedDomainObjectSet<JGivenReport> enabled;
    @Inject
    public JGivenReportsContainerImpl( Task task ) {
        super( JGivenReport.class, locateInstantiator(task), Report::getName, NOOP);
        add(JGivenHtmlReportImpl.class, task);
        add(JGivenTextReportImpl.class, task);
        add(JGivenAsciiDocReportImpl.class, task);
        enabled = this.matching(element -> element.getRequired().get());
    }

    @Override public JGivenHtmlReportImpl getHtml() {
        return (JGivenHtmlReportImpl) getByName( JGivenHtmlReportImpl.NAME );
    }

    @Override public JGivenTextReportImpl getText() {
        return (JGivenTextReportImpl) getByName( JGivenTextReportImpl.NAME );
    }

    @Override
    public JGivenAsciiDocReportImpl getAsciiDoc() {
      return (JGivenAsciiDocReportImpl) getByName( JGivenAsciiDocReportImpl.NAME );
    }

    private static InstanceGenerator locateInstantiator(Task task) {
        ServiceRegistry projectServices = ((ProjectInternal)task.getProject()).getServices();
        return ((InstantiatorFactory)projectServices.get(InstantiatorFactory.class)).decorateLenient(projectServices);
    }


    protected void assertMutableCollectionContents() {
        throw new ReportContainer.ImmutableViolationException();
    }

    public NamedDomainObjectSet<JGivenReport> getEnabled() {
        return this.enabled;
    }

    public ReportContainer<JGivenReport> configure(Closure cl) {
        ConfigureUtil.configureSelf(cl, this);
        return this;
    }


    protected <N extends JGivenReport> N add(Class<N> clazz, Object... constructionArgs) {
        N report = (N) this.getInstantiator().newInstance(clazz, constructionArgs);
        String name = report.getName();
        if (name.equals("enabled")) {
            throw new InvalidUserDataException("Reports that are part of a ReportContainer cannot be named 'enabled'");
        } else {
            this.getStore().add(report);
            this.index();
            return report;
        }
    }

    @NotNull
    public Map<String, JGivenReport> getEnabledReports() {
        return this.getEnabled().getAsMap();
    }
}
