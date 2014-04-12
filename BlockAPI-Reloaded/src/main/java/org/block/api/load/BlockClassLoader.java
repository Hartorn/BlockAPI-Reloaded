 package org.block.api.load;
 
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.net.URLConnection;
 
 public class BlockClassLoader extends ClassLoader
 {
   public BlockClassLoader(ClassLoader parent)
   {
     super(parent);
   }
 
   public Class<?> loadClass(String url, String name) throws ClassNotFoundException {
     try {
       URL myUrl = new URL(url);
       URLConnection connection = myUrl.openConnection();
       InputStream input = connection.getInputStream();
       ByteArrayOutputStream buffer = new ByteArrayOutputStream();
       int data = input.read();
 
       while (data != -1) {
         buffer.write(data);
         data = input.read();
       }
 
       input.close();
 
       byte[] classData = buffer.toByteArray();
 
       return defineClass(null, classData, 0, classData.length);
     }
     catch (MalformedURLException e) {
       e.printStackTrace();
     } catch (IOException e) {
       e.printStackTrace();
     }
 
     return null;
   }
 }
