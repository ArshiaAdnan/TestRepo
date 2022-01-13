import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import dto.AddWord;
import dto.DeleteWord;
import dto.Input;
import dto.IsAnagram;

public class AnagramClient {

    public static void main(String args[]) throws UnknownHostException, IOException, ClassNotFoundException {
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                inputOptions();
                String input = sc.nextLine();
                Input requestObj = null;
                String exitRequestObj = null;
                if (input.equals("A")) {
                    requestObj = new AddWord();
                    System.out.println("Enter word to Add");
                    requestObj.setWord(sc.nextLine());
                } else if (input.equals("D")) {
                    requestObj = new DeleteWord();
                    System.out.println("Enter word to Delete");
                    requestObj.setWord(sc.nextLine());
                } else if (input.equals("P")) {
                    requestObj = new IsAnagram();
                    System.out.println("Enter word to Print Anagrams");
                    requestObj.setWord(sc.nextLine());
                } else if (input.equals("E")) {
                    exitRequestObj = "exit";
                } else {
                    System.out.println("Invalid Input");
                }

                if (null != requestObj || null != exitRequestObj) {
                    socket = new Socket(host.getHostName(), 8989);
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(null == requestObj ? exitRequestObj : requestObj);
                    if (null != exitRequestObj) {
                        break;
                    }
                    ois = new ObjectInputStream(socket.getInputStream());
                    String message = (String) ois.readObject();
                    System.out.println(message);
                } 
            }
        }        
        oos.close();
        if (null != ois)
            ois.close();
        socket.close();
    }

    private static void inputOptions() {
        System.out.println("[A] Add a word");
        System.out.println("[D] Delete a word");
        System.out.println("[P] Print anagrams");
        System.out.println("[E] Exit");
    }
}