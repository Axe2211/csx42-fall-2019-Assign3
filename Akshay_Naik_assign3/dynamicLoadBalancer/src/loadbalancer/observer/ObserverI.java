package loadbalancer.observer;

public interface ObserverI{

    public void setManagerName(String managerNameIn);
    public void printSomething();
    public void update(Object updateValues);
    public String getManagerName();
    
}