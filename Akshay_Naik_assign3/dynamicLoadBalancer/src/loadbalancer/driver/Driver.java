package loadbalancer.driver;

import loadbalancer.observer.LoadBalancer;
import loadbalancer.subject.Cluster;
import loadbalancer.subject.ObsType;
import loadbalancer.subject.ServiceConfig;
import loadbalancer.util.FileProcessor;
import loadbalancer.util.Results;

/**
 * @author Akshay Naik
 *
 */
public class Driver{
	public static void main(String[] args) throws Exception{

		/*
		 * As the build.xml specifies the arguments as argX, in case the
		 * argument value is not given java takes the default value specified in
		 * build.xml. To avoid that, below condition is used
		 */
		if (args.length != 2 || args[0].equals("${arg0}") || args[1].equals("${arg1}")) {

			System.err.println("Error: Incorrect number of arguments. Program accepts 2 arguments.");
			System.exit(0);
		}

		FileProcessor ioProcessor = new FileProcessor(args[0], args[1]);
		Results executionResult = new Results();
		//creating cluster and Load Balancer
		Cluster cluster = new Cluster();
		LoadBalancer loadBalancer =	(LoadBalancer)cluster.createObserver(ObsType.LoadBalancer, "LoadBalancer", null);
		cluster.registerObserver(loadBalancer);

		//invoking requestHandler to handle the requests
		requestHandler(ioProcessor, cluster, loadBalancer, executionResult);

	}

	public static void requestHandler(FileProcessor ioProcessor, Cluster clusterIn, LoadBalancer loadBalancerIn, Results resultsIn){
		
		String request, result;
		String[] requestFields;
		ServiceConfig servConfigIn;

		try{
			while((request = ioProcessor.readLine()) != null){
				requestFields = request.split(" ");

				//requestFields[0] is requestType
				if(requestFields[0].equals("REQUEST")){
					String service = requestFields[1];
					result = loadBalancerIn.executeRequest(service);
				}
				else{
					servConfigIn = new ServiceConfig(requestFields);
					result = clusterIn.handleServiceRequest(servConfigIn);
				}
				resultsIn.logResult(result);
			}

			resultsIn.writeToFile(ioProcessor);
			ioProcessor.closeInFile();
			ioProcessor.closeOutFile();
			resultsIn.writeToStdOut();
		}
		catch(Exception ex){
			System.err.println("Exception Occured:");
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
