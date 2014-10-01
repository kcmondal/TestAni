/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package testRESTClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author dsg
 */
public class TestRESTPost12 {
   public static void main(String []p)throws Exception
   {
      String strurl="http://localhost:8080/testnewmaven8/webresources/service/post";
      
      //StringEntity str=new StringEntity("<a>hello post</a>",ContentType.create("application/xml" , Consts.UTF_8));
      
//
     StringEntity str=new StringEntity("hello post");
      str.setContentType("APPLICATION/xml");
       
      CloseableHttpClient httpclient=HttpClients.createDefault();
      
      HttpPost httppost=new HttpPost(strurl);
      httppost.addHeader("Accept", "application/xml charset=UTF-8");
      //httppost.addHeader("content_type", "application/xml, multipart/related");
      httppost.setEntity(str);
      
      CloseableHttpResponse response=httpclient.execute(httppost);
     // try
      //{
      int statuscode=response.getStatusLine().getStatusCode();
      if(statuscode!=200)
      {
         System.out.println("http error occured="+statuscode);
      }
      
          BufferedReader br=new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
      
      while(br.readLine()!=null)
      {
          System.out.println(br.readLine());
      }
     // }
      /*catch(Exception e)
      {
          System.out.println("exception :"+e);
      }*/
       
      
      
      //httpclient.close();
      
      
      
   }
}
