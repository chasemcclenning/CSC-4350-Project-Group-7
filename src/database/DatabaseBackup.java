package database;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class DatabaseBackup {
    private DatabaseBackup() {}

    public static void write(Path destination) throws SQLException, IOException {
        Connection connection=DBConn.getInstance();
        List<String> tables=new ArrayList<>();
        try(ResultSet rs=connection.getMetaData().getTables(connection.getCatalog(),null,"%",new String[]{"TABLE"})){
            while(rs.next())tables.add(rs.getString("TABLE_NAME"));
        }
        try(BufferedWriter out=Files.newBufferedWriter(destination, StandardCharsets.UTF_8)){
            out.write("-- Libris database backup\nSET FOREIGN_KEY_CHECKS=0;\n\n");
            for(String table:tables){
                try(Statement statement=connection.createStatement();ResultSet rs=statement.executeQuery("SHOW CREATE TABLE `"+table+"`")){
                    rs.next();out.write("DROP TABLE IF EXISTS `"+table+"`;\n");out.write(rs.getString(2));out.write(";\n\n");
                }
            }
            for(String table:tables)writeRows(connection,out,table);
            writeTriggers(connection,out);
            out.write("SET FOREIGN_KEY_CHECKS=1;\n");
        }
    }

    private static void writeRows(Connection connection,BufferedWriter out,String table)throws SQLException,IOException{
        try(Statement statement=connection.createStatement();ResultSet rs=statement.executeQuery("SELECT * FROM `"+table+"`")){
            ResultSetMetaData meta=rs.getMetaData();int columns=meta.getColumnCount();
            while(rs.next()){
                out.write("INSERT INTO `"+table+"` VALUES (");
                for(int i=1;i<=columns;i++){if(i>1)out.write(",");out.write(sqlValue(rs.getObject(i)));}
                out.write(");\n");
            }
            out.write("\n");
        }
    }

    private static void writeTriggers(Connection connection,BufferedWriter out)throws SQLException,IOException{
        try(Statement statement=connection.createStatement();ResultSet rs=statement.executeQuery("SHOW TRIGGERS")){
            while(rs.next()){
                String name=rs.getString("Trigger"),timing=rs.getString("Timing"),event=rs.getString("Event"),table=rs.getString("Table"),body=rs.getString("Statement");
                out.write("DROP TRIGGER IF EXISTS `"+name+"`;\nDELIMITER $$\nCREATE TRIGGER `"+name+"` "+timing+" "+event+" ON `"+table+"` FOR EACH ROW "+body+"$$\nDELIMITER ;\n\n");
            }
        }
    }

    private static String sqlValue(Object value){
        if(value==null)return "NULL";
        if(value instanceof Number)return value.toString();
        if(value instanceof byte[] bytes){StringBuilder hex=new StringBuilder("0x");for(byte b:bytes)hex.append(String.format("%02x",b));return hex.toString();}
        return "'"+value.toString().replace("\\","\\\\").replace("'","''").replace("\n","\\n").replace("\r","\\r")+"'";
    }
}
