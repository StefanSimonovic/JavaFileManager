/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Scanner;

/**
 *
 * @author Stefan ILL
 */
public class FileManager {
    
    private static String getStringFromFileTime(FileTime time) {
		
		long milliseconds = time.toMillis();
		Date date = new Date();		
		date.setTime(milliseconds);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");

		String dateTime = dateFormat.format(date);
		return dateTime;

	}
    
    private static boolean isValidPath(String path) {

		// Regular expression for validating file name.
		String pattern = "([a-zA-Z]:)?(\\\\[a-z A-Z0-9_.-]+)+\\\\?";
		boolean result = false;
		if (path.matches(pattern)) {
			result = true;
		}
		return result;
	}
    
     private static boolean isValidName(String path) {		
		String pattern = "([A-Za-z0-9])\\w+\\.*";  
                
		boolean result = false;
		if (path.matches(pattern)) {
			result = true;
		}
		return result;
	}
     
     public static void copyDir(File src, File dest) throws IOException{
         if(src.isDirectory()){
             
             //if destination does not exists, then we create it
             if(!dest.exists()){
                 dest.mkdir();                 
             }
             
             //list all directory content to be able to use them for copyng
             String files[] = src.list();
             
             for(String filename: files){
                 //put data in the src and dest
                 File srcFile = new File(src, filename);
                 File destFile = new File(dest, filename);
                 
                 //recursive copy
                 copyDir(srcFile, destFile);
             }
         }else{
             //if file, then copy it
             fileCopy(src, dest);
         }
     
     }
    
     private static void fileCopy(File src, File dest) throws IOException{         
         try(FileInputStream inStream = new FileInputStream(src);
             FileOutputStream outStream = new FileOutputStream(dest)){
             
                byte[] buffer = new byte[1024];
                int length;
                while((length = inStream.read(buffer))>0){
                    outStream.write(buffer, 0, length);
                }
         }
     }
     
     public static void deleteDir(File file) throws IOException{
	
	if(file.isDirectory()){
		
		//if directory is empty, delete it
		if(file.list().length == 0){
			file.delete();
		}
		else{
			//list all content
			File files[] = file.listFiles();

			for(File fileDelete : files){
				//recursive delete
				deleteDir(fileDelete);

			}

			//check again if directory is emppty and delete it
			if(file.list().length == 0){
			file.delete();
			}
		}
	}
	else{
		//if file, delete it
			file.delete();

	}


}
     
     public static void moveDir(File sorc, File destin) throws IOException{
         
         copyDir(sorc, destin);
         
         deleteDir(sorc);
     
     }
    
    public static void main(String[] args) throws IOException {
        
        try(Scanner scan = new Scanner(System.in);){                           
            System.out.println("Enter a proper path( use single \\ ): ");
            String path = scan.nextLine();  
            if(isValidPath(path)){
                System.out.println("You can manipulate using next commands: LIST, INFO, CREATE_DIR, RENAME, COPY, MOVE, DELETE");            
                File file = new File(path);              
                
                if(file.exists()){
                    String whatNext = scan.nextLine();
                    
                    //----------------INFO ABOUT FILE/DIRECTORY-----------------
                    if("INFO".equals(whatNext)){                                                    
                        System.out.println("Name :             " + file.getName());
                        System.out.println("Path :             " + file.getPath());
                        
                        // length don t return specified value when it comes to directory
                        if(!file.isDirectory()){
                        System.out.println("Size :             " + file.length());
                        }
                        // getting the creation date and time
                        Path pth = file.toPath();
                        BasicFileAttributes attributes = Files.readAttributes(pth,BasicFileAttributes.class);					
                        FileTime time = attributes.creationTime();
                        String dateTime = getStringFromFileTime(time);                        
                        System.out.println("Created on :       " + dateTime);
                        //getting the last modified date and time
                        Instant instant = Instant.ofEpochMilli(file.lastModified());                        
                        LocalDateTime timeModif = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                        DateTimeFormatter dTF = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");               
                        System.out.println("Last modified on : " + timeModif.format(dTF));
                    }
                    //--------------------- LISTING THE DIRECTORY --------------
                    else if("LIST".equals(whatNext)){   
                        if(file.isDirectory()){
                            File[] filesInDir = file.listFiles();
                                for (File filesInDir1 : filesInDir) {
                                System.out.println(filesInDir1.getName());
                                }
                        }else{
                           System.out.println("Can only list a directory!"); 
                         }
                    }    
                    // --------------------CREATING NEW DIRECTORY---------------
                    else if("CREATE_DIR".equals(whatNext)){             
                        if(file.isDirectory()){
                            System.out.println("Enter a name(only letters and numbers)for directory:");
                            String dirName = scan.nextLine();
                                if(isValidName(dirName)){                             
                                File newDir = new File(file.getAbsolutePath()+"\\" + dirName);
                                newDir.mkdir();
                                }else{
                                   System.out.println("Not valid name!");
                                }                                                            
                        }else{                 
                            System.out.println("Enter a name(only letters and numbers)for file:");
                            String dirName = scan.nextLine();
                            if(isValidName(dirName)){
                            File newDir = new File(file.getParent()+"\\" + dirName);
                            newDir.mkdir();
                            }else{
                                  System.out.println("Not valid name!");
                             }  
                        }
                    }
                    //------------------------ RENAMING ------------------------
                    else if("RENAME".equals(whatNext)){                                    
                            System.out.println("Enter a new name ( only letters and numbers! ) for file/directory:");
                            String dirName = scan.nextLine();
                                if(isValidName(dirName)){                             
                                File newDir = new File(file.getParent()+"\\" + dirName);
                                file.renameTo(newDir);
                                }else{
                                   System.out.println("Not valid name!");
                                }                                                                                   
                    } 
                    //------------------------ COPY ----------------------------
                    else if("COPY".equals(whatNext)){                                    
                            System.out.println("Enter a destination to copy: ");
                            String dirName = scan.nextLine();
                                if(isValidPath(dirName)){                             
                                File newDir = new File(dirName);
                                copyDir(file, newDir);
                                   System.out.println("Successfully copied");
                                }else{
                                   System.out.println("Not valid path!");
                                }                                                                                   
                    } 
                    //------------------------ DELETE --------------------------
                    else if("DELETE".equals(whatNext)){
                            System.out.println("Are You sure You want to delete this (YES/NO):");
                            String whatToDo = scan.nextLine();
                                if("YES".equals(whatToDo)){                             
                                   deleteDir(file);                             
                                   System.out.println("Successfully deleted");
                                }else if("NO".equals(whatToDo)){
                                   System.out.println("Relax, everything is in its place.");
                                }else{
                                   System.out.println("That was not the option!");
                                }                                                                                                      
                    }
                    //------------------------ MOVE / CUT ----------------------
                    else if("MOVE".equals(whatNext)){                                    
                            System.out.println("Enter a destination: ");
                            String dirName = scan.nextLine();
                                if(isValidPath(dirName)){                             
                                File newDir = new File(dirName);
                                   moveDir(file, newDir);
                                   System.out.println("Successfully moved");
                                }else{
                                   System.out.println("Not valid path!");
                                }                                                                                   
                    } 
                    else{
                        System.out.println("Only commands listed can be executed!");
                    }                    
                }else{
                    System.out.println("The file You are trying to reach doesn't exist.");                      
                 }
            }else{
                System.out.println("The path You entered is invalid!");                      
             }          
        }
    }           
}
