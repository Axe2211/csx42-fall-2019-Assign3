package loadbalancer.subject;

import java.util.HashMap;
import java.util.Map;

import loadbalancer.subject.SubjectI;
import loadbalancer.observer.LoadBalancer;
import loadbalancer.observer.ServiceManager;
import loadbalancer.observer.ObserverI;
import loadbalancer.observer.OperationConfig;
import loadbalancer.entities.Machine;
import loadbalancer.entities.Service;
import loadbalancer.subject.ObsType;


public class Cluster implements SubjectI{

    private Map<String, Machine> machines;
    private Map<String, Filter> observerMap;

    public Cluster(){
        machines = new HashMap<String, Machine>();
        observerMap = new HashMap<String, Filter>();
    }

    //get method
    public Map<String, Machine> getMachines(){
        return machines;
    }

    public Map<String, Filter> getObserverMap(){
        return observerMap;
    }

    public void registerObserver(ObserverI observerIn){
        Filter filter = new Filter();
        try{
            filter.setObserver(observerIn);
            if(observerIn instanceof ServiceManager){
                filter.addClusterOp(ClusterOp.CLUSTER_OP__SCALE_DOWN);
                filter.addClusterOp(ClusterOp.SERVICE_OP__ADD_INSTANCE);
                filter.addClusterOp(ClusterOp.SERVICE_OP__REMOVE_INSTANCE);
            }
            if(observerIn instanceof LoadBalancer){
                filter.addClusterOp(ClusterOp.SERVICE_OP__ADD_SERVICE);
                filter.addClusterOp(ClusterOp.SERVICE_OP__REMOVE_SERVICE);
            }
            getObserverMap().put(observerIn.getManagerName(), filter);
        }
        catch(Exception ex){
            System.err.println("Encountered Error: " );
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public void removeObserver(String observerNameIn){
        try{
            getObserverMap().remove(observerNameIn);
        }
        catch(Exception ex){
            System.out.println("Encountered Error: ");
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public void notifyObservers(OperationConfig message){
        getObserverMap().forEach((observerName, filter) -> {
            if(filter.check(message.getOperation())){
                filter.getObserver().update(message);
            }
        });
    }

    // non interface methods
    public void addHost(Machine hostIn){
        getMachines().put(hostIn.getHostName(), hostIn);
    }

    public ObserverI createObserver(ObsType obsTypeIn, String observerNameIn, String URLIn){
        ObserverI observer = null;
        try{
            if(obsTypeIn == ObsType.LoadBalancer){
                observer = new LoadBalancer(observerNameIn);
            }
            else if(obsTypeIn ==  ObsType.ServiceManager){
                observer = new ServiceManager(observerNameIn, URLIn);
            }
        }
        catch(Exception ex){
            System.err.println("Encountered Error: ");
            ex.printStackTrace();
            System.exit(1);
        }
        return observer;
    }

    public String handleServiceRequest(ServiceConfig servConfigIn){
        String result = null;
        try{
            switch(servConfigIn.getOperation()){
                case CLUSTER_OP__SCALE_UP:
                    result = scaleUp(servConfigIn.getHostNames().get(0));
                    break;
                case SERVICE_OP__ADD_SERVICE:
                    result = addService(servConfigIn);
                    break;
                case SERVICE_OP__REMOVE_SERVICE:
                    result = removeService(servConfigIn);
                    break;
                case CLUSTER_OP__SCALE_DOWN:
                    result = scaleDown(servConfigIn.getHostNames().get(0));
                    break;
                case SERVICE_OP__ADD_INSTANCE:
                    result = addInstance(servConfigIn);
                    break;
                case SERVICE_OP__REMOVE_INSTANCE:
                    result = removeInstance(servConfigIn);
                    break;
                default:
                    result = new String("Unknown Operation: " + servConfigIn.getOperation());
            }
        }
        catch(Exception ex){
            System.err.println("Encountered Error: ");
            ex.printStackTrace();
            System.exit(1);
        }

        return result;
    }

    public String scaleUp(String hostNameIn){
        String result = null;
        try{
            if(!getMachines().containsKey(hostNameIn)){
                Machine machine = new Machine(hostNameIn);
                addHost(machine);
                result = new String("Cluster Scaled Up");
                return result;
            }
            result = new String("Machine with name " + hostNameIn + " already exists");
        }
        catch(Exception ex){
            System.err.println("Encountered Error: ");
            ex.printStackTrace();
            System.exit(1);
        }
        
        return result;
    }

    public String scaleDown(String hostNameIn){
        String result = null;
        try{
            // checking to see if target host is present
            if(getMachines().containsKey(hostNameIn)){
                //removing host from cluster
                Machine targetHost = getMachines().get(hostNameIn);
                getMachines().remove(hostNameIn);

                //notify all Service Managers
                OperationConfig message = new OperationConfig();    
                message.setOperation(ClusterOp.CLUSTER_OP__SCALE_DOWN);
                message.addHost(targetHost);
                notifyObservers(message);

                result = new String("Cluster Scaled Down");

                return result;

            }
            result = new String("Machine with name " + hostNameIn + " does not exist");
        }
        catch(Exception ex){
            System.err.println("Encountered Error: ");
            ex.printStackTrace();
            System.exit(1);
        }
        return result;
    }

    public String addService(ServiceConfig servConfigIn){
        String result = null, serviceName, URL;
        Machine targetHost;
        try{
            // checking to see if all input hosts exist
            for (String host: servConfigIn.getHostNames()) {
                if(!getMachines().containsKey(host)){
                    result = new String("Machine named: " + host + " not present in Cluster");
                    return result;
                }
                else{
                    continue;
                }
            }
            serviceName = servConfigIn.getServiceName();
            URL = servConfigIn.getServiceURL();

            //checking to see if service already exists to avoid duplication
            if(getObserverMap().containsKey(serviceName)){
                result = new String("Service named: " + serviceName + " already exists");
                return result;
            }

            // creating service manager and registering it as an observer
            ServiceManager servManager = (ServiceManager) createObserver(ObsType.ServiceManager, serviceName, URL);
            registerObserver((ObserverI)servManager);

            // adding an instance of the service to each host and
            // adding the host to the service manager
            for (String host: servConfigIn.getHostNames()){
                targetHost = getMachines().get(host);
                if(servManager.getHostNames().contains(targetHost)){
                    continue;
                }
                targetHost.addService(new Service(serviceName, URL));
                servManager.addHost(targetHost);
            } 
            
            //notifying loadbalancer
            OperationConfig message = new OperationConfig();
            message.setOperation(ClusterOp.SERVICE_OP__ADD_SERVICE);
            message.setService(servManager);
            notifyObservers(message);
            result = new String("Service Added");
        }
        catch(Exception ex){
            System.err.println("Encountered Error: ");
            ex.printStackTrace();
            System.exit(1);
        }
        
        return result;
    }

    public String removeService(ServiceConfig servConfigIn){
        String result = null, serviceName;

        serviceName = servConfigIn.getServiceName();
        try{
            //checking to see if service exists for invalid service
            if(!getObserverMap().containsKey(serviceName)){
                result = new String("Service named: " + serviceName + " is invalid");
                return result;
            }

            // fetching the service manager for the target service
            ServiceManager targetService = (ServiceManager)getObserverMap().get(serviceName).getObserver();

            // remove the instance of the service from each of the hosts 
            // present in the list of hosts maintained in the service manager
            for(Machine host: targetService.getHostNames()){
                host.removeService(serviceName);
            }

            // removing the service from the observer map
            getObserverMap().remove(serviceName);

            //notifying loadbalancer
            OperationConfig message = new OperationConfig();
            message.setOperation(ClusterOp.SERVICE_OP__REMOVE_SERVICE);
            message.setService((ObserverI)targetService);
            notifyObservers(message);
            result = new String("Service Removed");

        }
        catch(Exception ex){
            System.err.println("Encountered Error: ");
            ex.printStackTrace();
            System.exit(1);
        }

        return result;
    }

    public String addInstance(ServiceConfig servConfigIn){
        String result = null, serviceName, URL;
        Machine targetHost;
        ServiceManager targetService;

        try{
            serviceName = servConfigIn.getServiceName();

            targetHost = getMachines().get(servConfigIn.getHostNames().get(0));
            
            //checking if target host exists
            if(targetHost == null){
                result = new String("Host Name: " + servConfigIn.getHostNames().get(0) + " does not exist");
                return result;
            }
            targetService = (ServiceManager) getObserverMap().get(serviceName).getObserver();

            //checking if target service exists
            if(targetService == null){
                result = new String("Service Name: " + serviceName + " does not exist");
                return result;
            }

            URL = new String(targetService.getURL());

            //checking if target service exists in target host
            if(targetHost.isServicePresent(serviceName)){
                result = new String("Host Name: " + targetHost.getHostName() + " already contains Host Name: " + serviceName);
                return result;
            }

            //adding instance of the service to targetHost
            targetHost.addService(new Service(serviceName, URL));

            //notify all Service Managers
            OperationConfig message = new OperationConfig();    
            message.setOperation(ClusterOp.SERVICE_OP__ADD_INSTANCE);
            message.addHost(targetHost);
            message.setService(targetService);
            notifyObservers(message);

            result = new String("Instance Added");

        }
        catch(Exception ex){
            System.err.println("Encountered Error: ");
            ex.printStackTrace();
            System.exit(1);
        }

        return result;
    }
    
    public String removeInstance(ServiceConfig servConfigIn){
        String result = null, serviceName, hostName;
        Machine targetHost;
        ServiceManager targetService;

        try{
            serviceName = servConfigIn.getServiceName();
            hostName = servConfigIn.getHostNames().get(0);

            targetService = (ServiceManager)getObserverMap().get(serviceName).getObserver();
            //checking if target Service exists
            if(targetService == null){
                result = new String("Service Name: " + serviceName + " does not exist");
                return result;
            }

            targetHost = getMachines().get(hostName);

            //checking if target host exists
            if(targetHost == null){
                result = new String("Host Name: " + hostName + " does not exist");
                return result;
            }

            //checking if service exists in the machine
            if(!targetHost.isServicePresent(serviceName)){
                result = new String("Service Name: " + serviceName + " not present in  Host Name: " + hostName);
                return result;
            }

            //removing service from host
            targetHost.removeService(serviceName);

            //notify Service Manager
            OperationConfig message = new OperationConfig();
            message.setOperation(ClusterOp.SERVICE_OP__REMOVE_INSTANCE);
            message.setService(targetService);
            message.addHost(targetHost);
            notifyObservers(message);
            result = new String("Instance Removed");
        }
        catch(Exception ex){
            System.err.println("Encountered Error: ");
            ex.printStackTrace();
            System.exit(1);
        }
        return result;
    }
}