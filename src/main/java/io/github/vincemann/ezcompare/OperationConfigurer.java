package io.github.vincemann.ezcompare;



public interface OperationConfigurer {
    public ResultProvider go();
    public ResultProvider assertEqual();
    public ResultProvider assertNotEqual();
}
