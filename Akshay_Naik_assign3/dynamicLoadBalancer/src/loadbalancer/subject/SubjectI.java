package loadbalancer.subject;

import loadbalancer.observer.ObserverI;
import loadbalancer.observer.OperationConfig;

public interface SubjectI{

    public void registerObserver(ObserverI observerIn);
    public void removeObserver(String observerNameIn);
    public void notifyObservers(OperationConfig message);
}