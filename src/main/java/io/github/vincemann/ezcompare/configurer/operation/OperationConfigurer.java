package io.github.vincemann.ezcompare.configurer.operation;


public interface OperationConfigurer {
    //menu options
    public SelectedOperationConfigurer go();
    public SelectedOperationConfigurer assertEqual();
    public SelectedOperationConfigurer assertNotEqual();
}
