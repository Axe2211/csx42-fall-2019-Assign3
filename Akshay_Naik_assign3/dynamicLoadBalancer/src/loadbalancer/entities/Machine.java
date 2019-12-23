package loadbalancer.entities;

import java.util.Map;
import java.util.HashMap;

//custom classes
import loadbalancer.entities.Service;

public class Machine{
    private String hostName;
    private Map<String, Service> hostedServices;

    public Machine(String hostNameIn){
        hostedServices = new HashMap<String, Service>(); 
        setHostName(hostNameIn);  
    }

    // set methods
    public void setHostName(String hostNameIn){
        hostName = hostNameIn;
    }

    // get method
    public String getHostName(){
        return hostName;
    }

    public Map<String, Service> getHostedServices(){
        return hostedServices;
    }

    public boolean addService(Service serviceIn){
        Service ret = null;
        ret = getHostedServices().putIfAbsent(serviceIn.getServiceName(), serviceIn);

        if(ret == null){
            return true;
        }
        return false;
    }

    public boolean isServicePresent(String serviceNameIn){
        Service service = null;

        service = getHostedServices().get(serviceNameIn);
        if(service == null){
            //System.out.println("Service not present..");
            return false;
        }
        return true;
    }

    public String executeService(String serviceNameIn){
        Service service = null;
        String result = null;

        if(!isServicePresent(serviceNameIn)){
            result = new String("Error in executing service: Service Not Found..");
            return result;
        }
        service = getHostedServices().get(serviceNameIn);
        result = new String(service.processRequest() + "Host::" + getHostName());
        return result;
    }

    public void removeService(String serviceNameIn){
        try{
            if(getHostedServices().containsKey(serviceNameIn)){
               getHostedServices().remove(serviceNameIn);
            }
        }
        catch(Exception ex){
            System.out.println("Encountered Error: " + ex + " in Host.removeService method");
        }
    }
}