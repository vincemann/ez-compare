package io.github.vincemann.ezcompare.menu;

public interface ComparisonMenu
        <
        ActorConfigurer,
        OptionsConfigurer,
        PropertyConfigurer,
        OperationConfigurer,
        ResultConfigurer,
        RestartConfigurer>
    extends
        ActorBridge<ActorConfigurer>,
        OptionsBridge<OptionsConfigurer>,
        PropertyBridge<PropertyConfigurer>,
        OperationBridge<OperationConfigurer>,
        ResultBridge<ResultConfigurer>,
        RestartBridge<RestartConfigurer>
{
}
