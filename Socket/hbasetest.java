import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.*;
import org.apache.hadoop.conf.Configuration;


public class hbasetest {
    public static Configuration conf;
    static {
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.property.clientPort", "2222");
        conf.set("hbase.zookeeper.quorum", "hbasemaster");
    }
    public static void main() throws IOException {
        /*//setup ADM
        //HBaseAdmin admin = new HBaseAdmin(conf);
        
        // create a hbase table
        String tablename = "myHBaseTable";
        String[] families = {"myFamily"};
        HTableDescriptor htd = new HTableDescriptor(tablename);
        for(String fmly :families)
            {
                HColumnDescriptor col = new HColumnDescriptor(fmly.getBytes());
                htd.addFamily(col);
            }

            if (admin.tableExists(tablename))
            {
                System.out.println("Table: " + tablename + "Existed.");
                System.out.println("dropTable "+ tablename);
                //drop the existing table
                admin.disableTable(tablename);
                admin.deleteTable(tablename);
            }
        admin.createTable(htd);
        System.out.println( tablename + " created.");*/

        HTable table = new HTable(conf, "UserTableTmp");
        HTable gazer = new HTable(conf, "gazer_table_tmp");
        
        //PUT
        Put p = new Put(Bytes.toBytes("MAC1")); 
        p.add(Bytes.toBytes("angle"), Bytes.toBytes(""),
              Bytes.toBytes("180"));
        table.put(p); 
        
        /*//GET
        Get g = new Get(Bytes.toBytes("sec1")); 
        Result r = gazer.get(g); 
        byte[] value = r.getValue(Bytes.toBytes("a1"),
                                  Bytes.toBytes(""));
        String valueStr = Bytes.toString(value); 
        System.out.println("GET: " + valueStr);*/
        
        /*//SCAN
        Scan s = new Scan();
        s.addColumn(Bytes.toBytes("angle"), Bytes.toBytes(""));
        ResultScanner scanner = table.getScanner(s);
        try {
            for (Result rr : scanner) {
                System.out.println("Found row: " + rr);
            }
        
        } finally {
            scanner.close();
        }*/
    }
}