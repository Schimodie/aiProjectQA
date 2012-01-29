/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package QuestionParser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Dinu
 */
public class Server {

    /**
     * @param args
     * @throws Exception 
     */
    public static void Run() throws IOException
    {
//        try{
            String received;
            String response;
            ServerSocket welcomeSocket = new ServerSocket(9001);
            byte option = 0;
            int len = 0;
            byte[] data ;

            System.out.println("Server Started");
            
            while (true) {
                Socket connectionSocket = welcomeSocket.accept();
                System.out.println("Accepted client");
                try
                {
                    DataInputStream inFromClient = new DataInputStream(connectionSocket.getInputStream());
                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    option = inFromClient.readByte();
        //            String option = received.substring(0, received.indexOf(' '));
                    switch(option)
                    {
                        case 1:
                            String question = readString(inFromClient);
                            System.out.println("Received question: " + question);
                            writeString(outToClient, question + " Jora");
                            break;
                        case 2:
                            String path = readString(inFromClient);//calea primita
                            System.out.println("received path: " + path);
                            outToClient.writeByte(0);
    //                        outToClient.writeBytes(response);
                            break;

                    }
                }
                catch(IOException ex)
                {
                    //Client died
                }

            }

//        }
//        catch(Exception ex)
//        {
//            
//        }
    }
    
    private static String readString(DataInputStream inStream) throws IOException
    {
        int len = inStream.readInt();
        byte[] data = new byte[len];
        //        String question = inStream.readUTF();//intrebarea primita
        int read = 0, offset = 0;
        
        while(len > 0)
        {
            read = inStream.read(data, offset, len);
            len -= read;
            offset += read;
        }

        return new String(data);
    }
    
    private static void writeString(DataOutputStream outStream, String text) throws IOException
    {
        byte[] bytes = text.getBytes();
        outStream.writeInt(bytes.length);
        outStream.write(bytes);
    }
}
