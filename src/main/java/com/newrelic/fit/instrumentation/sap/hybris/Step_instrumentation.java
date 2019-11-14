package com.newrelic.fit.instrumentation.sap.hybris;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import de.hybris.platform.cronjob.jalo.AbortCronJobException;
import de.hybris.platform.cronjob.jalo.CronJob;
import de.hybris.platform.cronjob.jalo.GeneratedStep;

@Weave(originalName = "de.hybris.platform.cronjob.jalo.Step", type = MatchType.BaseClass)
public abstract class Step_instrumentation extends GeneratedStep
{
    @Trace(dispatcher = false)
    protected abstract void performStep(final CronJob p0) throws AbortCronJobException;

}
