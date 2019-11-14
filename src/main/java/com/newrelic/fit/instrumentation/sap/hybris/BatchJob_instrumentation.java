
package com.newrelic.fit.instrumentation.sap.hybris;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import de.hybris.platform.cronjob.jalo.Step;
import java.util.logging.Level;

@Weave(originalName = "de.hybris.platform.cronjob.jalo.BatchJob", type = MatchType.BaseClass)
public class BatchJob_instrumentation
{
    @Trace(excludeFromTransactionTrace = true)
    protected static final Step getCurrentlyExecutingStep() {
        final Step step = Weaver.callOriginal();
        final String stepCode = step.getCode();
        NewRelic.getAgent().getLogger().log(Level.FINER, "Custom Instrumentation for SAP-Hybris - Step Code is " + stepCode);
        if (stepCode != null) {
            NewRelic.getAgent().getTracedMethod().setMetricName("Custom/StepCode",stepCode);
            NewRelic.addCustomParameter("StepCode", stepCode);
        }
        return step;
    }

}
