/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cep.testfinal;

import at.ac.tuwien.dsg.cep.testfinal.CassandraOutput;
import at.ac.tuwien.dsg.cep.testfinal.XMLOutput;
import at.ac.tuwien.dsg.smartcom.AdapterTest.DropboxAdapter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 *
 * @author Anindita
 */
public class SummationMain {
  public static void main(String []p)
  {
    String summationcost=new String();
    summationcost=new SummationMain().summationcost(
                                                    "./examples/CEPDATA/sample_current.csv", 
                                                    "./examples/CEPDATA/sampletest.csv", 
                                                    "./examples/CEPDATA/TestSummation.csv");
    
    String keyspace = new String("KeyspaceTestSummation");
    String table=new String("TableTestSummation");
    
    LinkedList<String> columns=new LinkedList<String>();
    columns.add("Cost");
    
    LinkedList<String> columnsdatatype=new LinkedList<String>();
    columnsdatatype.add("double");
    
    LinkedList<String> rows=new LinkedList<String>();
    rows.add(summationcost);
    
    new CassandraOutput().storeCassandra(rows,keyspace,table,columns,columnsdatatype);
    
    try
    {
      //need for generating output
      new XMLOutput().xmlOutput(rows,keyspace,table,columns,columnsdatatype);
      System.out.println("XML file generated!!!!");
      
      System.out.println("Enter the insert token of dropbox: ");
      String code=new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
      
      String testcontent="Summation of cost= " + summationcost;
      new DropboxAdapter().testAdapter(code, "testId", testcontent, "signal","technical problem", 
                                       "M2M DaaS", "asset manager", 3, "English", "password"); 
    }
    catch(Exception e)
    {
      System.out.println("Exception occured: " + e);
    }
  }
  
  
  
  
  
  
  
  public String summationcost(String costfilename,String energyfilename,String testsummationfile)
  {
    String recordcost=null;
    String recordtest=null;
    int rownumber=0;
    int row=0;
    int cellindex=0;
    int cell=0;
    StringTokenizer stcost=null;
    StringTokenizer sttest=null;
    String valuetest1=null;
    String valuetest2=null;
    String valuecost1=null;
    String valuecost2=null;
    String valuecost3=null;
    double sampletest2=0.0;
    double samplecost1=0.0;
    double samplecost2=0.0;
    double samplecost3=0.0;
    double maintenancecost=0.0;
    String summationcost=null;     //for output
    
    File file=new File(testsummationfile);     //for file write
    
    try
    {
      BufferedReader brtest=new BufferedReader(new FileReader(energyfilename));    
      while((recordtest=brtest.readLine())!=null) 
      {
        sttest=new StringTokenizer(recordtest,",");
        while(sttest.hasMoreTokens())
        {
          cellindex++;
          valuetest1=new String(sttest.nextToken());
          valuetest2=new String(sttest.nextToken());
          sampletest2=Double.parseDouble(valuetest2); 
          BufferedReader brcost=new BufferedReader(new FileReader(costfilename));
          while((recordcost=brcost.readLine())!=null)
          {
            row++;
            stcost=new StringTokenizer(recordcost,","); 
            while(stcost.hasMoreTokens())
            {
              cell++;
              valuecost1=new String(stcost.nextToken());
              valuecost2=new String(stcost.nextToken());
              valuecost3=new String(stcost.nextToken());
              
              samplecost1=Double.parseDouble(valuecost1);
              samplecost2=Double.parseDouble(valuecost2);
              samplecost3=Double.parseDouble(valuecost3);
              
              if((samplecost1<(sampletest2*1000)) && ((sampletest2*1000)<=samplecost2))
              {
                System.out.println("Energy= "+ sampletest2 + " cost= " + valuecost3);
                maintenancecost=maintenancecost+samplecost3;
              }
            }
            cell=0;
          }
          row=0;
        }
        cellindex=0;
        rownumber++;
      }
      System.out.println("Totalcost= " + maintenancecost);
      
      if(!file.exists())
      {
        file.createNewFile();
      }
      BufferedWriter bw=new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
      bw.write(Double.toString(maintenancecost));
      summationcost=Double.toString(maintenancecost);
      System.out.println("Content Written in= " + file.getAbsolutePath());
      bw.close();
    }
    catch(Exception e)
    {
      System.out.println("Exception occured: " + e);
    }
    return summationcost;   
  }
}