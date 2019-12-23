package loadbalancer.entities;

public class Service{
    private String url;
    private String serviceName;

    // Constructor
    public Service(String serviceNameIn, String urlIn){
        setUrl(urlIn);
        setServiceName(serviceNameIn);
    }

    //set methods
    public void setUrl(String urlIn){
        url = urlIn;
    }

    public void setServiceName(String serviceNameIn){
        serviceName = serviceNameIn;
    }

    //get methods
    public String getUrl(){
        return url;
    }

    public String getServiceName(){
        return serviceName;
    }

    // service procedure
    public String processRequest(){
        String result;
        result = new String("Processed Request - Service_URL::" + getUrl() + " ");
        return result;
    }
    
}