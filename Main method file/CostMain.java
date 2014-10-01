/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cep.testfinal;


import java.io.*;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 *
 * @author anindita
 */
public class CostMain {
  public static void main(String []p)
  {
    String keyspace=new String("KeyspaceTestCost1");
    String table=new String("TableTestCost1");
    
    LinkedList<String> testcost=new LinkedList<String>();
    testcost=new CostMain().costCalculation(
                                            "./examples/CEPDATA/sample_current.csv", 
                                            "./examples/CEPDATA/sampletest.csv", 
                                            "./examples/CEPDATA/Testcost.csv");
    
    LinkedList<String> columns=new LinkedList<String>();
    columns.add("Time");
    columns.add("Energy");
    columns.add("Cost");
    
    LinkedList<String> columnsdatatype=new LinkedList<String>();
    columnsdatatype.add("double");
    columnsdatatype.add("double");
    columnsdatatype.add("double");
    
    try
    {
      //need for generating output
      new XMLOutput().xmlOutput(testcost,keyspace,table,columns,columnsdatatype);
      System.out.println("XML file generated!!!!");
      new CassandraOutput().storeCassandra(testcost,keyspace,table,columns,columnsdatatype);
      System.out.println("Stored in cassandra!!!!");
    }
    catch(Exception e)
    {
      System.out.println("Exception Occured : "+e);
    }
  }

  
  
  
  
  
  
  
  public LinkedList<String> costCalculation(String costfilename,String energyfilename,String testcostfile)
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
    
    LinkedList<String> testcost=new LinkedList<String>(); 
    
    File file=new File(testcostfile);
    
    try{
      
      BufferedReader brtest=new BufferedReader(new FileReader(energyfilename));
      
      //file written
      
      if(!file.exists())
      {
        file.createNewFile();
      }
      BufferedWriter bw=new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
      System.out.println("Content Written in="+file.getAbsolutePath());
      bw.write("Time,Energy,Cost");
      
      
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
                System.out.println("Energy="+ sampletest2+" cost="+valuecost3);
                ////for file
                bw.newLine();
                bw.write(valuetest1);
                testcost.add(valuetest1);                 
                bw.write(",");
                bw.write(Double.toString(sampletest2));
                testcost.add(Double.toString(sampletest2));       
                bw.write(",");
                bw.write(valuecost3);
                testcost.add(valuecost3);
              }
            }
            cell=0;
          }
          row=0;
          //System.out.println("energy consumption");
        }
        cellindex=0;
        rownumber++;   
      }
      bw.close();
    }
    catch(Exception e)
    {
      System.out.println("Exception throws:" + e);
    }
    return testcost;                                               
  }
}