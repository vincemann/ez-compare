package io.github.vincemann.ezcompare.configurer.operation;

import io.github.vincemann.ezcompare.configurer.ResultConfigurer;
import io.github.vincemann.ezcompare.configurer.actor.SelectedActorConfigurer;
import io.github.vincemann.ezcompare.bridges.ContinueBridge;
import io.github.vincemann.ezcompare.bridges.ResultBridge;

public interface SelectedOperationConfigurer extends
        //menu options
        ResultBridge<ResultConfigurer>,
        ContinueBridge<SelectedActorConfigurer>
{
}
