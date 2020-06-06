package io.github.vincemann.ezcompare.menu;

import io.github.vincemann.ezcompare.configurer.actor.ActorConfigurer;
import lombok.Getter;

@Getter
public abstract class ComparisonMenu<
        AB extends ActorBridge<ActorConfigurer<AM>>,AM,
        OptionsB extends OptionsBridge<OptionsM>,OptionsM,
        PB extends PropertyBridge<PM>,PM,
        OperationB extends OperationBridge<OperationM>,OperationM,
        RB extends ResultBridge<RM>,RM
        >
{
    AB actorBridge;
    OptionsB optionsBridge;
    PB propertyBridge;
    OperationB operationBridge;
    RB resultBridge;
}
