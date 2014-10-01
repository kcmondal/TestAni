/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.tuwien.dsg.cep.testfinal;

import at.ac.tuwien.dsg.smartcom.AdapterTest.DropboxAdapter;
import java.util.LinkedList;
/**
 *
 * @author anindita
 */
public class CriticalOutput {
  public void criticalSectionMonitor(String code, LinkedList<String> energystatus, LinkedList<String> columns)throws Exception
  {
    int columnssize=columns.size();
    for(int i=0;i<energystatus.size();i=i+columns.size())
    {
      if(energystatus.get(i+(columnssize-1)).equalsIgnoreCase("Waste"))
      {
        String testcontent="The resource is facing problem at = " + energystatus.get(i) +
                           "\n The amount of resource consumed = " + energystatus.get(i+1);
        
        new DropboxAdapter().testAdapter(code, "testId", testcontent, "signal","technical problem", 
                                         "M2M DaaS", "asset manager", 3, "English", "password"); 
      }
      else
      {
        System.out.println("The instrument is ok at= " + energystatus.get(i) + " amount of resource taken = " + energystatus.get(i+1));
      } 
    } 
  }  
}