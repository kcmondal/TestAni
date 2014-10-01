/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cep.testfinal;

import at.ac.tuwien.dsg.daas.entities.Column;
import at.ac.tuwien.dsg.daas.entities.CreateRowsStatement;
import at.ac.tuwien.dsg.daas.entities.Keyspace;
import at.ac.tuwien.dsg.daas.entities.RowColumn;
import at.ac.tuwien.dsg.daas.entities.Table;
import at.ac.tuwien.dsg.daas.entities.TableQuery;
import at.ac.tuwien.dsg.daas.entities.TableRow;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/**
 *
 * @author Anindita
 */
public class XMLOutput {
  public void xmlOutput(LinkedList<String> energystatus, String keyspace1, String table1, 
                        LinkedList<String> columns,LinkedList<String> columnsdatatype) throws JAXBException, IOException {
    Keyspace keyspace = new Keyspace(keyspace1);
    Table table = new Table(keyspace, table1, "key", "int");
    
    //create table columns
    for(int i=0;i<columns.size();i++) 
    {
      table.addColumn(new Column(columns.get(i), columnsdatatype.get(i)));
    }
    
    //the add row statement used in adding rows to the DB
    CreateRowsStatement createRowsStatement = new CreateRowsStatement();
    {
      Table t = new Table();
      t.setName(table1);
      t.setKeyspace(keyspace);
      createRowsStatement.setTable(t);
      //create rows to the database table
      for(int i=0;i<energystatus.size();i=i+columns.size())
      {
        TableRow tableRow = new TableRow();
        for(int j=0;j<columns.size();j++)
        {
          tableRow.addRowColumn(new RowColumn(columns.get(j), energystatus.get(i+j)));
        }
        createRowsStatement.addRow(tableRow);
      }
    }
    
    
    //write the TableQuerry object used in retrieving and deleting rows from the table
    TableQuery query = new TableQuery();
    {
      Table t = new Table();
      t.setName(table1);
      t.setKeyspace(keyspace);
      query.setTable(t);
      query.setCondition("key = 1 AND sensorvalue = 555.3");
    }
    
    File theDir = new File("./examples/CEPDATA/" + keyspace1); 
    if (!theDir.exists())
    {
      theDir.mkdirs();
    }
    
    //output keyspace example to have a format and XSD to play with
    {
      JAXBContext jaxbc = JAXBContext.newInstance(Keyspace.class);
      Marshaller marshaller = jaxbc.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(keyspace, new File("./examples/CEPDATA/" + keyspace1 + "/keyspace.xml"));
      jaxbc.generateSchema(new MySchemaOutputResolver("./examples/CEPDATA/" + keyspace1 + "/keyspace.xsd"));
    }
    
    //output Table example to have a format and XSD to play with
    {
      JAXBContext jaxbc = JAXBContext.newInstance(Table.class);
      Marshaller marshaller = jaxbc.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(table, new File("./examples/CEPDATA/" + keyspace1 + "/table.xml"));
      jaxbc.generateSchema(new MySchemaOutputResolver("./examples/CEPDATA/" + keyspace1 + "/table.xsd"));
    }
    
    //output CreateRowsStatement to have a format and XSD to play with
    {
      JAXBContext jaxbc = JAXBContext.newInstance(CreateRowsStatement.class);
      Marshaller marshaller = jaxbc.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(createRowsStatement, new File("./examples/CEPDATA/" + keyspace1 + "/createRowsStatement.xml"));
      jaxbc.generateSchema(new MySchemaOutputResolver("./examples/CEPDATA/" + keyspace1 + "/createRowsStatement.xsd"));
    }
    
    //output TableQuerry example to have a format and XSD to play with
    {
      JAXBContext jaxbc = JAXBContext.newInstance(TableQuery.class);
      Marshaller marshaller = jaxbc.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(query, new File("./examples/CEPDATA/" + keyspace1 + "/tableQuery.xml"));
      jaxbc.generateSchema(new MySchemaOutputResolver("./examples/CEPDATA/" + keyspace1 + "/tableQuery.xsd"));
    }
  }
  
  private static class MySchemaOutputResolver extends SchemaOutputResolver 
  {
    private String usedFileName;
    
    public MySchemaOutputResolver(String usedFileName) 
    {
      this.usedFileName = usedFileName;
    }
    
    public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
      File file = new File(usedFileName);
      StreamResult result = new StreamResult(file);
      result.setSystemId(file.toURI().toURL().toString());
      return result;
    }
  }
}