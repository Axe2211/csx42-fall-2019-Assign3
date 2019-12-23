package loadbalancer.observer;

import java.util.Map;
import java.util.HashMap;

import loadbalancer.observer.ObserverI;

public class TrieNode{
    private Map<Character, TrieNode> childNodes;
    private Character content;
    private boolean isWord;
    private int wordLength;
    private ObserverI manager;

    //constructor
    public TrieNode(){
        childNodes = new HashMap<Character, TrieNode>();
        content = null;
        setIsWord(false, 0);
        setManager(null);
    }

    public TrieNode(Character charIn){
        childNodes = new HashMap<Character, TrieNode>();
        setContent(charIn);
        setIsWord(false, 0);
        setManager(null);
    }

    //set method
    public void setContent(Character contentIn){
        content = contentIn;
    }

    public void setIsWord(Boolean isWordIn, int wordLengthIn){
        isWord = isWordIn;
        setWordLength(wordLengthIn);
    }

    public void setWordLength(int wordLengthIn){
        wordLength = wordLengthIn;
    }

    public void setManager(ObserverI managerIn){
        manager = managerIn;
    }

    //get method
    public Character getContent(){
        return content;
    }

    public Boolean getIsWord(){
        return isWord;
    }

    public int getWordLength(){
        return wordLength;
    }

    public Map<Character, TrieNode> getChildNodes(){
        return childNodes;
    }

    public ObserverI getManager(){
        return manager;
    }

    public void addChild(TrieNode childNodeIn){
        Character contentRef = childNodeIn.getContent();
        childNodes.putIfAbsent(contentRef, childNodeIn);
    }

    public boolean verifyManager(String managerNameIn){
        if(getIsWord() == true && managerNameIn.length() == getWordLength()){
            return true;
        }
        return false;
    }

}