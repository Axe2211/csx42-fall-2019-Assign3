package loadbalancer.observer;

import java.util.List;
import java.util.ArrayList;

import loadbalancer.subject.ClusterOp;
import loadbalancer.entities.Machine;
import loadbalancer.observer.ObserverI;


public class OperationConfig{
    ClusterOp operation; 
    ObserverI service;
    List<Machine> machines;

    public OperationConfig(){
        machines = new ArrayList<Machine>();
    }

    //get methods
    public ClusterOp getOperation(){
        return operation;
    }

    public ObserverI getService(){
        return service;
    }

    public List<Machine> getMachines(){
        return machines;
    }

    //set methods
    public void setOperation(ClusterOp clusterOpIn){
        operation = clusterOpIn;
    }

    public void setService(ObserverI serviceIn){
        service = serviceIn;
    }

    public void addHost(Machine machineIn){
        machines.add(machineIn);
    }
}