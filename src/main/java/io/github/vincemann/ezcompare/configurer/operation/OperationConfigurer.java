package io.github.vincemann.ezcompare.configurer.operation;


public interface OperationConfigurer {
    //menu options
    public OperationDoneConfigurer go();
    public OperationDoneConfigurer assertEqual();
    public OperationDoneConfigurer assertNotEqual();
}
