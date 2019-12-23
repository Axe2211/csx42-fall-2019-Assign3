package loadbalancer.observer;

import loadbalancer.entities.Machine;
import loadbalancer.subject.ClusterOp;

//import loadbalancer.entities.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class ServiceManager implements ObserverI{

    //key
    private String managerName;
    private String URL;
    private List<Machine> hostNames;
    private int hostListSize;
    private int nextHost;

    public ServiceManager(String managerNameIn, String URLIn){
        setManagerName(managerNameIn);
        setURL(URLIn);
        hostNames = new ArrayList<>();
        setHostListSize();
        setNextHost(-1);
    }

    // set methods
    public void setServiceMgrName(String managerNameIn){
        setManagerName(managerNameIn);
    }

    public void setURL(String URLIn){
        URL = URLIn;
    }

    public void setHostListSize(){
        hostListSize = getHostNames().size();
    }

    public void setNextHost(int nextHostIn){
        nextHost = nextHostIn;
    }

    //get method
    public String getServiceMgrName(){
        return managerName;
    }

    public String getURL(){
        return URL;
    }

    public List<Machine> getHostNames(){
        return hostNames;
    }

    public int getHostListSize(){
        return hostListSize;
    }

    public int getNextHost(){
        return nextHost;
    }

    //non interface methods
    public String getManagerName(){
        String result = getServiceMgrName();
        return result;
    }

    public void updateNextHost(){
        int recentlyUsedHost = getNextHost();
        int hostListSize = getHostListSize();
       // System.out.println("Recently used index: " + getNextHost());

        if(recentlyUsedHost < (hostListSize - 1) && hostListSize > 1){
            setNextHost(recentlyUsedHost + 1);
            return;
        }
        //if hostlist is empty
        else if(hostListSize == 0){
            nextHost = -1;
            return;
        }
        setNextHost(0);
    }

    public void addHost(Machine hostIn){
        // Service newServiceInstance = new Service(getServiceMgrName(), getURL());
        // hostIn.addService(newServiceInstance); 
        getHostNames().add(hostIn);
        setHostListSize();
        if(getNextHost() < 0){
            setNextHost(0);
        }
    }

    public Machine removeHost(String hostName){
        Machine hostItr = null, returnHost = null;
        Iterator<Machine> hostListItr =  getHostNames().listIterator();
        try{
            while(hostListItr.hasNext()){
                hostItr = hostListItr.next();
                if(hostItr.getHostName().equals(hostName)){
                    returnHost = hostItr;
                    break;
                }
                continue;
            }
            if(returnHost != null){
                getHostNames().remove(returnHost);
                setHostListSize();
                updateNextHost(); 
            }
        }
        catch(Exception ex){
            System.err.println("Encountered Error: ");
            ex.printStackTrace();
            System.exit(1);
        }
        return returnHost;
    }

    public Machine getHost(){

        Machine returnHost = null;
        int index = getNextHost();
       // System.out.println("Next Host Index: " + index);
        if(index < 0){
            return returnHost;
        }
        returnHost = getHostNames().get(getNextHost());
        updateNextHost();

        return returnHost;
    }

    //interface method implementation

    public void setManagerName(String managerNameIn){
        managerName = managerNameIn;
    }

    public void printSomething(){
        System.out.println("This is a service manager: " + getServiceMgrName());
    }

    public void update(Object updateValues){
        OperationConfig message = (OperationConfig) updateValues;
        try{
            if(message.getOperation() == ClusterOp.CLUSTER_OP__SCALE_DOWN){
                Machine targetHost = message.getMachines().get(0);
                if(getHostNames().contains(targetHost)){
                    getHostNames().remove(targetHost);
                    System.out.println("Removed Host: " + targetHost.getHostName() + " from " + getServiceMgrName());
                }
            }
            else if(message.getOperation() == ClusterOp.SERVICE_OP__ADD_INSTANCE){
                Machine targetHost = message.getMachines().get(0);
                if(message.getService() == this){
                    addHost(targetHost);
                }
            }
            else if(message.getOperation() == ClusterOp.SERVICE_OP__REMOVE_INSTANCE){
                Machine targetHost = message.getMachines().get(0);
                if(message.getService() == this){
                    removeHost(targetHost.getHostName());
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