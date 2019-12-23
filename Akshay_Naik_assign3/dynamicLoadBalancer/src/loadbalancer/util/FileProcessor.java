package loadbalancer.util;
import java.io.File;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.IOException;


public class FileProcessor {
    private String inputFileName;
    private String outputFileName;
    private File inFile, outFile;
    private Scanner inputFileHandler;
    private PrintWriter outputFileHandler;
    
    //constructor
    public FileProcessor(String inputFileNameIn, String outputFileNameIn){

        if(inputFileNameIn == null || outputFileNameIn == null){
            System.err.println("Error: File Name is Null..");
            System.exit(1);
        }
        else{
            this.inputFileName = inputFileNameIn;
            this.outputFileName = outputFileNameIn;
        }
        //input mode
        
        try{
            this.inFile = new File(this.inputFileName);
            if(this.inFile.length() == 0){
                throw new IOException("Input file is empty or File does not exist");
            }
            this.outFile = new File(this.outputFileName);
            //outputFileHandler = new PrintWriter(File);
            this.inputFileHandler = new Scanner(this.inFile);
            this.outFile.createNewFile();
            this.outputFileHandler = new PrintWriter(this.outFile);
        }
        catch(IOException ex){
            System.err.println("Exception Occurred: ");
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public String readLine(){
        String currentLine = null;

        try{
            if(inputFileHandler.hasNextLine()){
                currentLine = inputFileHandler.nextLine();
                return currentLine;
            }
        }
        catch(Exception e){
            System.err.println("Error: Unable to read next line.. ");
            System.exit(0);
        }
        currentLine = null;
        return currentLine;
    }

    public void writeFile(String output){
        outputFileHandler.println(output);
    }

    public void closeOutFile(){
        outputFileHandler.close();
    }

    public void closeInFile(){
        inputFileHandler.close();
    }
}
