package io.github.vincemann.ezcompare.configurer;

import io.github.vincemann.ezcompare.RapidEqualsBuilder;
import io.github.vincemann.ezcompare.configurer.actor.SelectedActorConfigurer;
import io.github.vincemann.ezcompare.bridges.ContinueBridge;


public interface ResultConfigurer extends
        //menu options
        ContinueBridge<SelectedActorConfigurer> {
        public RapidEqualsBuilder.Diff getDiff();
        public boolean isEqual();
}
