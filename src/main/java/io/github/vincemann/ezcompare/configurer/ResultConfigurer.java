package io.github.vincemann.ezcompare.configurer;

import io.github.vincemann.ezcompare.RapidEqualsBuilder;
import io.github.vincemann.ezcompare.configurer.actor.ActorConfigurer;
import io.github.vincemann.ezcompare.menu.RestartBridge;


public interface ResultConfigurer extends
        //menu options
        RestartBridge<ActorConfigurer> {
        public RapidEqualsBuilder.Diff getDiff();
        public boolean isEqual();
}
