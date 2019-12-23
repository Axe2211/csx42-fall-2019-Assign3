package loadbalancer.observer;

import loadbalancer.entities.Machine;
import loadbalancer.observer.Trie;
import loadbalancer.subject.ClusterOp;

public class LoadBalancer implements ObserverI{

    private String balancerName;
    private Trie ServiceURLAndHostnameFetcher;

    public LoadBalancer(String balancerNameIn){
        setBalancerName(balancerNameIn);
        ServiceURLAndHostnameFetcher = new Trie();
    }

    //set methods
    public void setBalancerName(String balancerNameIn){
        setManagerName(balancerNameIn);
    }

    //get methods
    public String getBalancerName(){
        return balancerName;
    }

    public Trie getSUHF(){
        return ServiceURLAndHostnameFetcher;
    }

    // non interface methods
    public String addManager(String managerNameIn, ObserverI managerIn){
        String result;
        if(getSUHF().addManager(managerNameIn, managerIn) == null){
            result = new String("Service: " + managerNameIn + " added to load balancer");
        }
        else{
            result = new String("Service: " + managerNameIn + " already exists in the load balancer");
        }
        return result;
    }

    public ObserverI getManager(String managerNameIn){
        ObserverI result = null;

        result = getSUHF().getManager(managerNameIn);

        return result;
    }

    public Boolean removeManager(String managerNameIn){
        Boolean result;
        result = getSUHF().removeManager(managerNameIn);

        return result;
    }

    public String executeRequest(String serviceNameIn){
        String result = null;

        try{
            ServiceManager serviceMgr = (ServiceManager) getManager(serviceNameIn);
            if(serviceMgr != null){
                Machine host = serviceMgr.getHost();
                if(host != null){
                    result = host.executeService(serviceNameIn);
                }
                else{
                    result = new String("Service Inactive - Service::" + serviceMgr.getServiceMgrName());
                }
            }
            else{
                result = new String("Invalid Service");
            }
        }
        catch(Exception ex){
            System.err.println("Encountered Error: ");
            ex.printStackTrace();
            System.exit(1);
        }
        return result;
    }

    // interface method implementation
    public String getManagerName(){
        String result = getBalancerName();
        return result;
    }

    public void printSomething(){
        System.out.println("This is the Load Balancer: " + getBalancerName());
    }

    public void setManagerName(String managerNameIn){
        balancerName = managerNameIn;
    } 
    
    public void update(Object updateValues){
        OperationConfig message = (OperationConfig) updateValues;
        try{
            if(message.getOperation() == ClusterOp.SERVICE_OP__ADD_SERVICE){
                ServiceManager newManager = (ServiceManager)message.getService();
                addManager(newManager.getServiceMgrName(), newManager);
            }
            else if(message.getOperation() == ClusterOp.SERVICE_OP__REMOVE_SERVICE){
                ServiceManager newManager = (ServiceManager)message.getService();
                String serviceName = new String(newManager.getServiceMgrName());
                if(removeManager(serviceName)){
                    return;
                }
            }
        }
        catch(Exception ex){
            System.err.println("Encountered Error: ");
            ex.printStackTrace();
            System.exit(1);
        }
    }
}