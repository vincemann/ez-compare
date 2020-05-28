package io.github.vincemann.ezcompare.template;



public interface OperationConfigurer {
    public ResultProvider go();
    public ResultProvider assertEqual();
    public ResultProvider assertNotEqual();
}
