    /*
     * To change this license header, choose License Headers in Project Properties.
     * To change this template file, choose Tools | Templates
     * and open the template in the editor.
     */

    package at.ac.tuwien.dsg.cep.testfinal;

    import at.ac.tuwien.dsg.cep.CassandraConnector;
    import java.util.LinkedList;

    /**
     *
     * @author anindita
     */
    public class CassandraOutput {
        public void storeCassandra(LinkedList<String> energystatus,String keyspace, 
                String table, LinkedList<String> columns, LinkedList<String> columnsdatatype)
        {

            ///////////////
            String ipAddress="localhost";
            final int port=9042;
            int reflicaFactor=1;
            /////////////////

            final CassandraConnector client = new CassandraConnector();
       
            client.connect(ipAddress, port);
            client.createKeyspace(keyspace, reflicaFactor);
            client.createtable(keyspace,table,columns,columnsdatatype);

            for(int i=0;i<energystatus.size();i=i+columns.size())
            {
                LinkedList<String> rows=new LinkedList<String>();
                for(int j=0;j<columns.size();j++)
                {
                    rows.add(energystatus.get(i+j));
                }
                
                client.insertdata(keyspace, table, columns, columnsdatatype, rows);
            }
            client.readAll(keyspace, table);

            client.close();
             }
    }
