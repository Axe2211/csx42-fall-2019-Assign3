package loadbalancer.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class Results implements FileDisplayInterface, StdoutDisplayInterface {

    private List<String> log;

    public Results(){
        log = new ArrayList<String>();
    }

    //get methods
    public List<String> getLog(){
        return log;
    }

    public void logResult(String resultIn){
        getLog().add(resultIn);
    }

    //interface methods
    public void writeToFile(FileProcessor outFile){

        Iterator<String> logIterator = getLog().listIterator();

        while(logIterator.hasNext()){
            outFile.writeFile((String)logIterator.next());
        }
    }

    public void writeToStdOut(){

        Iterator<String> logIterator = getLog().listIterator();
        try{
            while(logIterator.hasNext()){
                System.out.println((String) logIterator.next());
            }
        }
        catch(Exception ex){
            System.out.println("Encountered Error: ");
            ex.printStackTrace();
            System.exit(1);
        }
    }


	
}
