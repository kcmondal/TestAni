/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cep.testfinal;

import at.ac.tuwien.dsg.cep.KW.TestKWXMLOutput;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 *
 * @author Anindita
 */
public class KWMain {
  public static void main(String []p)
  {
    //need for machine learning
    //String csvFilename = "./examples/CEPDATA/sample.csv";
    //new InputDB().insertData(csvFilename);
    try
    {
      LinkedList<String> energystatus=new LinkedList<String>();
      energystatus=new KWMain().energyStatusCalculation(
                                                        "./examples/CEPDATA/sampletest.csv", 
                                                        "./examples/CEPDATA/sampletemp.csv",                 
                                                        "./examples/CEPDATA/TestKW.csv");
      
      String keyspace = new String("KeyspaceTestKW1");
      String table=new String("TableTestKW1");
      
      LinkedList<String> columns=new LinkedList<String>();
      columns.add("Time");
      columns.add("Energy");
      columns.add("Status");
      
      LinkedList<String> columnsdatatype=new LinkedList<String>();
      columnsdatatype.add("double");
      columnsdatatype.add("double");
      columnsdatatype.add("text");
      
      
      new XMLOutput().xmlOutput(energystatus,keyspace,table,columns,columnsdatatype );
      System.out.println("XML file generated!!!!");
      
      new CassandraOutput().storeCassandra(energystatus,keyspace,table,columns,columnsdatatype);
      System.out.println("All the results are stored in Cassandra!!!!!");
      
      //need for sending critical result to asset manager
      System.out.println("Enter the insert token of dropbox: ");
      String code=new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
      new CriticalOutput().criticalSectionMonitor(code, energystatus,columns);
      System.out.println("Critical Results are send to the asset manager!!!!!");
    }
    catch(Exception e)
    {
      System.out.println("Exception occured: " + e);
    }
  }
  
  
  
  
  
  
  
  public LinkedList<String> energyStatusCalculation(String fileenergyname,String filetempname, String testkwfile)
  {
    String recordtest=null;
    String recordtemp=null;
    int rownumber=0;
    StringTokenizer sttest=null;
    StringTokenizer sttemp=null;
    String valuetest1=null;
    String valuetest2=null;
    String valuetemp1=null;
    String valuetemp2=null;
    Connection conn=null;
    Statement st=null;
    String sql=null;
    int cellindex=0;
    LinkedList<String> energystatus=new LinkedList<String>(); //For sending output
    
    File file=new File(testkwfile);
    try
    {
      BufferedReader brtest=new BufferedReader(new FileReader(fileenergyname));
      BufferedReader brtemp=new BufferedReader(new FileReader(filetempname));
      Class.forName("org.apache.derby.jdbc.ClientDriver");
      conn=DriverManager.getConnection("jdbc:derby://localhost:1527/store","store","store");
      st=conn.createStatement();
      
      ///////////////////////////////////File write
      if(!file.exists())
      {
        file.createNewFile();
      }
      BufferedWriter bw=new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
      bw.write("Time,Energy,Status");
      /////////////////////
        
      while(((recordtest=brtest.readLine())!=null) && ((recordtemp=brtemp.readLine())!=null) )
      {
        rownumber++;
        sttest=new StringTokenizer(recordtest,",");
        sttemp=new StringTokenizer(recordtemp,",");
        while((sttest.hasMoreTokens()) && (sttemp.hasMoreTokens()))
        {
          cellindex++;
          valuetest1=new String(sttest.nextToken());
          valuetest2=new String(sttest.nextToken());
          valuetemp1=new String(sttemp.nextToken());
          valuetemp2=new String(sttemp.nextToken());
          System.out.println("Cell column index: " + cellindex);
          System.out.println("Temp time= " + valuetemp1 + " temp= " + valuetemp2);
          System.out.println("Test time= " + valuetest1 + " energy= " + valuetest2);
          
          bw.newLine();  //for file
          
          if(Double.parseDouble(valuetest1)==Double.parseDouble(valuetemp1))
          {
            System.out.println("Cell Value: " + valuetemp2);
            sql="select * from STORE.STORE where STORE.TEMP=" + valuetemp2;
            ResultSet rs=st.executeQuery(sql);
            double min=0.0;
            double max=0.0;
            while(rs.next())
            {
              double temp=rs.getDouble(1);
              min=rs.getDouble(2);
              max=rs.getDouble(3);
              System.out.println("Temp from table= " + temp);
            }
            if(min <= Double.parseDouble(valuetest2))
            {
              if(Double.parseDouble(valuetest2)<=max)
              {
                System.out.println("OK when energy consumed= " + valuetest2);
                bw.write(valuetest1);
                energystatus.add(valuetest1);           //for XML output
                bw.write(",");
                bw.write(valuetest2);
                energystatus.add(valuetest2);         //for XML output
                bw.write(",");
                bw.write("OK");
                energystatus.add("OK");               //for XML output
              }
              else
              {
                System.out.println("Waste of energy when energy consumed= " + valuetest2);
                bw.write(valuetest1);
                energystatus.add(valuetest1);         //for XML output     
                bw.write(",");
                bw.write(valuetest2);
                energystatus.add(valuetest2);         //for XML output
                bw.write(",");
                bw.write("Waste");
                energystatus.add("Waste");             //for XML output
              }
            }
            else
            {
              System.out.println("Some energy required when energy consumed= " + valuetest2);
              bw.write(valuetest1);
              energystatus.add(valuetest1);             //for XML output
              bw.write(",");
              bw.write(valuetest2);
              energystatus.add(valuetest2);             //for XML output
              bw.write(",");
              bw.write("Need");
              energystatus.add("Need");                 //for XML output
            }
            System.out.println("---"); 
          }
        }
        cellindex=0;
      }
      bw.close();
    }
    catch(Exception e)
    {
      System.out.println("Exception = " + e);
    }
    return energystatus;
  }
}