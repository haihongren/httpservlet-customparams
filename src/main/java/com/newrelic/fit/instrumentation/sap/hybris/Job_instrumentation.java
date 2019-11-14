package com.newrelic.fit.instrumentation.sap.hybris;

import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import de.hybris.platform.cronjob.jalo.CronJob;
import de.hybris.platform.cronjob.jalo.GeneratedJob;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.servicelayer.event.events.AfterCronJobFinishedEvent;
import de.hybris.platform.servicelayer.event.events.BeforeCronJobStartEvent;

import java.util.logging.Level;

@Weave(originalName = "de.hybris.platform.cronjob.jalo.Job", type = MatchType.BaseClass)
public abstract class Job_instrumentation {

    @Trace(dispatcher = true)
    public final void perform(final CronJob cronJob) {
        String cronJobCode = cronJob.getCode();
        NewRelic.getAgent().getLogger().log(Level.FINER, "Custom Instrumentation for SAP-Hybris - CronJob Code is " + cronJobCode);
        if (cronJobCode != null) {
            NewRelic.getAgent().getTracedMethod().setMetricName("Custom/CronJobCode",cronJobCode);
            NewRelic.addCustomParameter("CronJob Code", cronJobCode);
            NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, false, "cronJobCode", new String[]{cronJobCode});
        }
        Weaver.callOriginal();
    }

    @Trace(dispatcher = true)
    public final void perform(final CronJob cronJob, final boolean synchronous) {
        String cronJobCode = cronJob.getCode();
        NewRelic.getAgent().getLogger().log(Level.FINER, "Custom Instrumentation for SAP-Hybris - CronJob Code is " + cronJobCode);
        if (cronJobCode != null) {
            NewRelic.getAgent().getTracedMethod().setMetricName("Custom/CronJobCode",cronJobCode);
            NewRelic.addCustomParameter("CronJob Code", cronJobCode);
            NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, false, "cronJobCode", new String[]{cronJobCode});
        }
        Weaver.callOriginal();
    }

    @Trace(dispatcher = true)
    public final void perform(final CronJob cronJob, final de.hybris.platform.cronjob.jalo.Job.Synchronicity synchronousType) {
        String cronJobCode = cronJob.getCode();
        NewRelic.getAgent().getLogger().log(Level.FINER, "Custom Instrumentation for SAP-Hybris - CronJob Code is " + cronJobCode);
        if (cronJobCode != null) {
            NewRelic.getAgent().getTracedMethod().setMetricName("Custom/CronJobCode",cronJobCode);
            NewRelic.addCustomParameter("CronJob Code", cronJobCode);
            NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, false, "cronJobCode", new String[]{cronJobCode});
        }
        Weaver.callOriginal();
    }


    @Trace(dispatcher = true)
    private void execute(CronJob cronJob, BeforeCronJobStartEvent startEventToSend, AfterCronJobFinishedEvent endEventToSend) {
        Weaver.callOriginal();
        EnumerationValue cronJobResult = cronJob.getResult();
        NewRelic.getAgent().getLogger().log(Level.FINER, "Custom Instrumentation for SAP-Hybris - Job.execute, cronJobResult is " + cronJobResult.getCode());
        NewRelic.addCustomParameter("CronJob Result Code", cronJobResult.getCode());
    }
}
