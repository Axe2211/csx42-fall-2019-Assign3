package loadbalancer.subject;

import java.util.List;
import java.util.ArrayList;

import loadbalancer.subject.ClusterOp;

public class ServiceConfig{
    ClusterOp operation;
    String serviceName;
    String serviceURL;
    List<String> hostNames;

    public ServiceConfig(String[] requestFieldsIn){
        hostNames = new ArrayList<String>();

        try{
            if(requestFieldsIn[0].equals("CLUSTER_OP__SCALE_UP")){
                if(requestFieldsIn.length != 2){
                    throw new Exception(" Scale Up accepts 1 argument. No of arguments: " + requestFieldsIn.length);
                }
                setOperation(ClusterOp.CLUSTER_OP__SCALE_UP);
                //requestFields[1] is the host name
                addHost(requestFieldsIn[1]);
                return;
            }
            else if(requestFieldsIn[0].equals("CLUSTER_OP__SCALE_DOWN")){
                if(requestFieldsIn.length != 2){
                    throw new Exception(" Scale Down accepts 1 argument. No of arguments: " + requestFieldsIn.length);
                }
                setOperation(ClusterOp.CLUSTER_OP__SCALE_DOWN);
                //requestFields[1] is the host name
                addHost(requestFieldsIn[1]);
                return;
            }
            else if(requestFieldsIn[0].equals("SERVICE_OP__ADD_SERVICE")){
                if(requestFieldsIn.length != 4){
                    throw new Exception(" Add Service accepts 3 arguments. No of arguments: " + requestFieldsIn.length);
                }
                setOperation(ClusterOp.SERVICE_OP__ADD_SERVICE);
                //requestFieldsIn[1] is the service name
                setServiceName(requestFieldsIn[1]);
                //requestFieldsIn[2] is the service URL
                setServiceURL(requestFieldsIn[2]);
                //requestFieldsIn[3] is the list of hosts
                for(String host: requestFieldsIn[3].split(",")){
                    addHost(host);
                }
                return;
            }
            else if(requestFieldsIn[0].equals("SERVICE_OP__ADD_INSTANCE")){
                if(requestFieldsIn.length != 3){
                    throw new Exception(" Add Instance accepts 2 arguments. No of arguments: " + requestFieldsIn.length);
                }
                setOperation(ClusterOp.SERVICE_OP__ADD_INSTANCE);
                //requestFieldsIn[1] is the service name
                setServiceName(requestFieldsIn[1]);
                //requestFieldsIn[1] is the host name
                addHost(requestFieldsIn[2]);
                return;
            }
            else if(requestFieldsIn[0].equals("SERVICE_OP__REMOVE_INSTANCE")){
                if(requestFieldsIn.length != 3){
                    throw new Exception(" Remove Instance accepts 2 arguments. No of arguments: " + requestFieldsIn.length);
                }
                setOperation(ClusterOp.SERVICE_OP__REMOVE_INSTANCE);
                //requestFields[1] is the service name
                setServiceName(requestFieldsIn[1]);
                //requestFieldsIn[1] is the host name
                addHost(requestFieldsIn[2]);
                return;
            }
            else if(requestFieldsIn[0].equals("SERVICE_OP__REMOVE_SERVICE")){
                if(requestFieldsIn.length != 2){
                    throw new Exception(" Remove Service accepts 1 argument. No of arguments: " + requestFieldsIn.length);
                }
                setOperation(ClusterOp.SERVICE_OP__REMOVE_SERVICE);
                //requestFields[1] is the service name
                setServiceName(requestFieldsIn[1]);
                return;
            }
            setOperation(ClusterOp.SERVICE_UNKNOWN);
        }
        catch(Exception ex){
            System.err.println("Exception Occurred: ");
            ex.printStackTrace();
            System.exit(1);
        }

    }

    //get methods
    public ClusterOp getOperation(){
        return operation;
    }

    public String getServiceName(){
        return serviceName;
    }

    public String getServiceURL(){
        return serviceURL;
    }

    public List<String> getHostNames(){
        return hostNames;
    }

    //set methods
    public void setOperation(ClusterOp clusterOpIn){
        operation = clusterOpIn;
    }

    public void setServiceName(String serviceNameIn){
        serviceName = serviceNameIn;
    }

    public void setServiceURL(String serviceURLIn){
        serviceURL = serviceURLIn;
    }

    public void addHost(String hostIn){
        hostNames.add(hostIn);
    }
}