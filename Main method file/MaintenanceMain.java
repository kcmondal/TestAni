/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cep.testfinal;

import at.ac.tuwien.dsg.cep.Maintenance.TestMaintenance;
import at.ac.tuwien.dsg.cep.Maintenance.TestMaintenanceMonitor;
import at.ac.tuwien.dsg.cep.Maintenance.TestMaintenanceXMLOutput;
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
public class MaintenanceMain {
  public static void main(String []p)
  {
    //need for machine learning
    //String csvFilename = "./examples/CEPDATA/sample.csv";
    //new InputDB().insertData(csvFilename);
    
    LinkedList<String> coststatus=new LinkedList<String>();
    coststatus=new MaintenanceMain().maintenancecost(
                                                     "./examples/CEPDATA/sample_current.csv", 
                                                     "./examples/CEPDATA/sampletest.csv", 
                                                     "./examples/CEPDATA/sampletemp.csv",
                                                     "./examples/CEPDATA/maintenancecost.csv");
    
    try
    {
      //need for store all the result in cassandra
      String keyspace=new String("KeyspaceTotalCost15");
      String table=new String("TableTotalCost15");
      
      LinkedList<String> columns=new LinkedList<String>();
      columns.add("cost");
      columns.add("status");
      
      LinkedList<String> columnsdatatype=new LinkedList<String>();
      columnsdatatype.add("double");
      columnsdatatype.add("text");
      
      //need for generating output
      new XMLOutput().xmlOutput(coststatus,keyspace,table,columns,columnsdatatype);
      System.out.println("XML file generated!!!!");
      
      System.out.println("All the results are stored in Cassandra!!!!!");
      new CassandraOutput().storeCassandra(coststatus,keyspace,table,columns,columnsdatatype);
      
      System.out.println("Enter the insert token of dropbox:");
      String code=new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
      new CriticalOutput().criticalSectionMonitor(code,coststatus,columns);
      System.out.println("Critical Results are send to the asset manager!!!!!");
    }
    catch(Exception e)
    {
      System.out.println("Exception occured: " + e);
    }
  }
  
  
  
  
  public LinkedList<String> maintenancecost(String costfilename,String energyfilename,String tempfilename,String testmaintenancefile)
  {
    System.out.println("Testmaintenance");
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
    Connection conn=null;
    Statement st=null;
    String recordtemp=null;
    StringTokenizer sttemp=null;
    String valuetemp1=null;
    String valuetemp2=null;
    String sql=null;
    double maximumcost=0.0;
    double cost=0.0;
    
    String status=null;
    String costoutput=null;
    LinkedList<String> coststatus=new LinkedList<String>();   //for output
    
    File file=new File(testmaintenancefile);
    
    try{
      BufferedReader brtest=new BufferedReader(new FileReader(energyfilename));
      BufferedReader brtemp=new BufferedReader(new FileReader(tempfilename));
      Class.forName("org.apache.derby.jdbc.ClientDriver");
      conn=DriverManager.getConnection("jdbc:derby://localhost:1527/store","store","store");
      st=conn.createStatement();
      
      while(((recordtest=brtest.readLine())!=null) && ((recordtemp=brtemp.readLine())!=null))
      {
        sttest=new StringTokenizer(recordtest,",");
        sttemp=new StringTokenizer(recordtemp,",");
        while(sttest.hasMoreTokens() && sttemp.hasMoreTokens())
        {
          cellindex++;
          
          valuetest1=new String(sttest.nextToken());
          valuetest2=new String(sttest.nextToken());
          
          valuetemp1=new String(sttemp.nextToken());
          valuetemp2=new String(sttemp.nextToken());
          
          sampletest2=Double.parseDouble(valuetest2); 
          BufferedReader brcost=new BufferedReader(new FileReader(costfilename));
          
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
                  maintenancecost=maintenancecost+samplecost3; 
                }
                if((samplecost1<(max*1000)) && ((max*1000)<=samplecost2))
                {
                  maximumcost=maximumcost+samplecost3;
                }
              }
              cell=0;
            }
            row=0;
          }
        }
        cellindex=0;
        rownumber++;
      }
      System.out.println("Totalcost= " + maintenancecost);
      System.out.println("Maximum Cost= " + maximumcost);
      
      //write in file about the extra or needed cost for running the chiller. 
      if(!file.exists())
      {
        file.createNewFile();
      }
      BufferedWriter bw=new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
      System.out.println("Content Written in= " + file.getAbsolutePath());
      bw.write("Cost, Status");
      bw.newLine();
      if(maintenancecost<=maximumcost)
      {
        System.out.println("Save the Cost amount= " + (maximumcost-maintenancecost));
        bw.write(Double.toString(maximumcost-maintenancecost));
        bw.write("Save,");
        coststatus.add(Double.toString(maximumcost-maintenancecost));
        coststatus.add("Save");
      }
      else
      {
        System.out.println("Lost the Cost Amount= " + (maintenancecost-maximumcost));
        bw.write(Double.toString(maintenancecost-maximumcost));
        bw.write("Waste,");
        coststatus.add(Double.toString(maintenancecost-maximumcost));
        coststatus.add("Waste");
      }
      bw.close();
    }
    catch(Exception e)
    {
      System.out.println("Exception occured: " + e);
    }
    return coststatus;  
  }
}
