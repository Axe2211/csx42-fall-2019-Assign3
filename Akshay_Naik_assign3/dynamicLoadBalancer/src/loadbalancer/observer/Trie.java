package loadbalancer.observer;

import loadbalancer.observer.TrieNode;
import loadbalancer.observer.ObserverI;

public class Trie{
    public TrieNode trieRoot;

    public Trie(){
        trieRoot = new TrieNode();
    }

    public TrieNode getTrieRoot(){
        return trieRoot;
    }

    //Adds the manager name as a sequence of Nodes which form the key
    //Instance of the manager is stored in the final node 
    //returns manager if a manager is already present else null pointer
    public ObserverI addManager(String managerNameIn, ObserverI managerIn){
        ObserverI detectedManager = null;
        TrieNode currentNode = getTrieRoot();
        int nodeCounter;
        try{
            for(nodeCounter = 0; nodeCounter < managerNameIn.length(); nodeCounter++){
                Character trieNodeContent = managerNameIn.charAt(nodeCounter);

                if(currentNode.getChildNodes().containsKey(trieNodeContent)){
                    currentNode = (TrieNode)currentNode.getChildNodes().get(trieNodeContent);
                }
                else{
                    TrieNode newNode;
                    newNode = new TrieNode(trieNodeContent);
                    currentNode.addChild(newNode);
                    currentNode = newNode;
                }
                
            }
            if(currentNode.getIsWord() == false || (detectedManager = currentNode.getManager()) == null){
                currentNode.setIsWord(true, nodeCounter);
                currentNode.setManager(managerIn);
            }
        }
        catch(NullPointerException | ClassCastException ex){
            System.err.println("Encountered Error: ");
            ex.printStackTrace();
            System.exit(1);
        }

        return detectedManager;
    }  

    //Given managerName (key) returns reference to an instance of the manager
    // if the key is absent or mapped to null then null pointer is returned 
    public ObserverI getManager(String managerNameIn){
        ObserverI manager = null;
        TrieNode currentNode = getTrieRoot();
        try{
            int nodeCounter;
            for(nodeCounter = 0; nodeCounter < managerNameIn.length(); nodeCounter++){
                Character trieNodeContent = managerNameIn.charAt(nodeCounter);
                if(currentNode.getChildNodes().containsKey(trieNodeContent)){
                    currentNode = currentNode.getChildNodes().get(trieNodeContent);
                    continue;
                }
                else{
                    break;
                }
            }
            if(currentNode.verifyManager(managerNameIn)){
                manager = currentNode.getManager();
            }
        }
        catch(NullPointerException | ClassCastException ex){
            System.err.println("Encountered Error: ");
            ex.printStackTrace();
            System.exit(1);
        }

        return manager;
    }
    // Given manager name calls delete method on the Manager in the Trie
    public Boolean removeManager(String managerNameIn){
        Boolean result;

        result = deleteManager(getTrieRoot(), managerNameIn, 0);
        return result;
    }

    // Given the manager name clears the reference for the manager instance
    // characters pertaining to the manager name are only removed from the child nodes map
    // if the node corresponding to the character itself has no child nodes
    private boolean deleteManager(TrieNode currentNode, String managerNameIn, int index){
        try{
            if(index == managerNameIn.length()){
                if(currentNode.getIsWord() == false){
                    return false;
                }
                currentNode.setIsWord(false, 0);
                return currentNode.getChildNodes().isEmpty();
            }
            Character nextChar = managerNameIn.charAt(index);
            TrieNode nextNode = currentNode.getChildNodes().get(nextChar);
            if(nextNode == null){
                return false;
            }
            int newIndex = index + 1;
            boolean deleteCurrentDecision = (deleteManager(nextNode, managerNameIn, newIndex) && !nextNode.getIsWord());

            if(deleteCurrentDecision){
                currentNode.getChildNodes().remove(nextChar);
                return currentNode.getChildNodes().isEmpty();
            }
        }
        catch(Exception ex){
            System.err.println("Encountered Error: ");
            ex.printStackTrace();
            System.exit(1);
        }
        return false;
    }
    
}