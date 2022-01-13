import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dto.AddWord;
import dto.DeleteWord;
import dto.IsAnagram;

public class AnagramServer {

    private static ServerSocket server;
    private static int port = 8989;
    private static String SERVER_PREFIX = "SERVER";

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        List<String> dictionary = loadDictionary();
        server = new ServerSocket(port);
        
        while (true) {
            System.out.println("Waiting for client request");
            Socket socket = server.accept();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            Object request = ois.readObject();
            StringBuffer response = new StringBuffer();
            if (request instanceof AddWord) {
                AddWord addWord = (AddWord) request;
                dictionary.add(addWord.getWord());
                response.append(SERVER_PREFIX + " Successfully added word '" + addWord.getWord() + "' to dictionary.");
            } else if (request instanceof DeleteWord) { 
                DeleteWord deleteWord = (DeleteWord) request;
                dictionary.remove(deleteWord.getWord());
                response.append(SERVER_PREFIX + " Successfully deleted word '" + deleteWord.getWord() + "' to dictionary.");
            } else if (request instanceof IsAnagram) {
                String input = ((IsAnagram) request).getWord();
                response.append(SERVER_PREFIX + " Anagrams for word: '" + input + "' are ".concat("\n"));
                for (String dictionaryItem: dictionary) {
                    if (isAnagram(input, dictionaryItem)) {
                        response.append(dictionaryItem.concat("\n"));
                    }
                }
            } else if(((String) request).equalsIgnoreCase("exit")) {
                 break;
            }

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(response.toString());
            ois.close();
            oos.close();
            socket.close();
        }
        System.out.println("Shutting down Socket server!!");
        server.close();
    }

    private static List<String> loadDictionary() {
        List<String> dictionary = new ArrayList<>();
        try {
            File file = new File("dictionary.txt");
            try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
                String line;
                while ((line = br.readLine()) != null) {
                    dictionary.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load dictionary " + e.getMessage());
        }
        return dictionary;
    }

    private static boolean isAnagram(String input, String dictionaryItem) {
        String s1 = input.replaceAll("\\s", "");
        String s2 = dictionaryItem.replaceAll("\\s", "");
        boolean status = true;
        if (s1.length() != s2.length()) {
            status = false;
        } else {
            char[] ArrayS1 = s1.toLowerCase().toCharArray();
            char[] ArrayS2 = s2.toLowerCase().toCharArray();
            Arrays.sort(ArrayS1);
            Arrays.sort(ArrayS2);
            status = Arrays.equals(ArrayS1, ArrayS2);
        }
        return status;
    }
}
