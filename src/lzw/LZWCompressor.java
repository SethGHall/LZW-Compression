/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lzw;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Seth
 */
public class LZWCompressor {
    
    public static void LZW_compress_to_File(File input, File output) throws FileNotFoundException, IOException
    {
        FileReader fis = new FileReader(input);
        BufferedReader dis = new BufferedReader(fis);
        
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(output, false));
        
        //init lookup table:
        short table_size = 256;
        Map<String,Short> lookuptable = new HashMap<>();
        
        
        
        for(short i=0;i<table_size;i++)
        {
            lookuptable.put(""+(char)i, i);
        }
        
      
        boolean endOfFile = false;
        String inputLine = "";
        String w = "";
        do
        {
            inputLine =  dis.readLine();
            
            if(inputLine != null){
                inputLine = inputLine+"\n";
                
                for(int i =0;i<inputLine.length();i++)
                {   

                    char c = inputLine.charAt(i);
                    String wc = w+c;

                    if(lookuptable.containsKey(wc))
                        w = wc;
                    else
                    {
                        //output code for P
                        System.out.println("ENCODING "+w+" TO "+lookuptable.get(w));
                        dos.writeShort(lookuptable.get(w));
                        lookuptable.put(wc, table_size);

                        table_size++;
                        w = ""+c;
                    }    
                }
                
           }
        }while(inputLine != null);
        if(!w.equals(""))
        {   dos.writeChar(lookuptable.get(w));
            System.out.println("FINAL PROCESSING "+w);

        }
        System.out.println("COMPRESSION COMPLETE....");
        
            //output code for P
        dos.flush();
        dos.close();
        fis.close();
        dis.close();
    }
    
/*
    PSEUDOCODE
1    Initialize table with single character strings
2    OLD = first input code
3    output translation of OLD
4    WHILE not end of input stream
5        NEW = next input code
6        IF NEW is not in the string table
7               S = translation of OLD
8               S = S + C
9       ELSE
10              S = translation of NEW
11       output S
12       C = first character of S
13       OLD + C to the string table
14       OLD = NEW
15   END WHILE
    */
    
    public static void LZW_decompress_from_File(File input, File output) throws FileNotFoundException, IOException
    {
        FileInputStream fis = new FileInputStream(input);
        DataInputStream dis = new DataInputStream(fis);
        
        PrintWriter dos = new PrintWriter(new FileWriter(output));
        
        //init lookup table:
        short table_size = 256;
        Map<Short,String> lookuptable = new HashMap<>();
        for(short i=0;i<table_size;i++)
        {
            lookuptable.put(i, ""+(char)i);
        }
        
        
        boolean endOfFile = false;
       
        short old = dis.readShort();
        System.out.println("READING SHORT "+old);
        String old_string = lookuptable.get(old); 
        System.out.println(old_string);
        char firstChar = old_string.charAt(0);
        String append = "";
        while(fis.available() > 0 && !endOfFile)
        {   
            try
            {
                String entry = "";
                short nextCode = (short)dis.readShort();
                
                if(!lookuptable.containsKey(nextCode))
                {
                    entry = lookuptable.get(old);
                    entry = entry + firstChar;
                }
                else
                {
                    entry = lookuptable.get(nextCode);
                }
            
                System.out.println("processing "+entry);
                append+=old_string;
                dos.print(old_string);
                
                firstChar = entry.charAt(0);
                lookuptable.put(table_size, old_string+firstChar);
                table_size++;
                old = nextCode;
                old_string = entry;
                
            }catch(EOFException e)
            {
                endOfFile = true;
                append+=old_string;
                dos.print(old_string);
            }
            
        }
        System.out.println("DECODDED:\n"+append);
        dos.flush();
        dos.close();
        fis.close();
        dis.close();
        
    }
    
    
    public static void main(String[] args)
    {
        System.out.println("================LZW COMPRESSION==============\n");
        
        File input = new File("testInput.txt");
        File output = new File("testOutput.lzw");
        
        
        try{
            LZW_compress_to_File(input,output);
        }catch(FileNotFoundException e)
        {
            System.out.println("Exception "+e);
        } catch (IOException ex) {
            Logger.getLogger(LZWCompressor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("NOW DECOMPRESSING");
        
         try{
            LZW_decompress_from_File(output,new File("decompress.txt"));
        }catch(FileNotFoundException e)
        {
            System.out.println("Exception "+e);
        } catch (IOException ex) {
            Logger.getLogger(LZWCompressor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
