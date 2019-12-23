package loadbalancer.subject;

import java.util.List;
import java.util.ArrayList;

import loadbalancer.observer.ObserverI;

public class Filter{
    List<ClusterOp> clusterOp;
    ObserverI observer;

    public Filter(){
        clusterOp = new ArrayList<ClusterOp>();
    }

    //set methods
    public void setObserver(ObserverI observerIn){
        observer = observerIn;
    }

    //get methods
    public List<ClusterOp> getClusterOp(){
        return clusterOp;
    }

    public ObserverI getObserver(){
        return observer;
    }

    public boolean check(ClusterOp operationIn){
        if(getClusterOp().contains(operationIn)){
            return true;
        }
        return false;
    }

    public boolean containsOp(ClusterOp clusterOpIn){
        if(clusterOp.contains(clusterOpIn)){
            return true;
        }
        return false;
    }

    public void addClusterOp(ClusterOp clusterOpIn){
        if(!clusterOp.contains(clusterOpIn)){
            clusterOp.add(clusterOpIn);
        }
    }

    public void removeClusterOp(ClusterOp clusterOpIn){
        if(clusterOp.contains(clusterOpIn)){
            int index = clusterOp.indexOf(clusterOpIn);
            clusterOp.remove(index);
        }
    }

}