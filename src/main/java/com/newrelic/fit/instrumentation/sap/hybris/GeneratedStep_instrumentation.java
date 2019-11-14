package com.newrelic.fit.instrumentation.sap.hybris;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import de.hybris.platform.jalo.GenericItem;
import java.util.logging.Level;


@Weave(originalName = "de.hybris.platform.cronjob.jalo.GeneratedStep", type = MatchType.BaseClass)
public abstract class GeneratedStep_instrumentation extends GenericItem
{
    @Trace(excludeFromTransactionTrace = true)
    public String getCode() {
        String generatedStepCode = Weaver.callOriginal();
        NewRelic.getAgent().getLogger().log(Level.FINER, "Custom Instrumentation for SAP-Hybris - StepCode is " + generatedStepCode);
        if (generatedStepCode != null) {
            NewRelic.getAgent().getTracedMethod().setMetricName("Custom/StepCode",generatedStepCode);
            NewRelic.addCustomParameter("StepCode Code", generatedStepCode);
        }
        return generatedStepCode;
    }
}
