package model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Tarek
 */
public class DataBaseHandler {

    private final static String connectionURL = "jdbc:oracle:thin:@svn.ejada.com:1522:FGO";
    private final static String userName = "BGETR";
    private final static String password = "bgetr";
    private static Connection connection;

    // to prevent making an object of this class
    private DataBaseHandler() {

    }

    public static boolean connect() {
        try {
            connection = DriverManager.getConnection(connectionURL, userName, password);
            System.out.println("Connection successfully established");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

        public static String[] getAllTables() {
        ArrayList<String> tableNames = new ArrayList<String>();

        if (connection == null) {
            return new String[0];
        }

        try {
            DatabaseMetaData myDatabaseMetaData = connection.getMetaData();
            ResultSet columnsResultSet = myDatabaseMetaData.getTables(null, "BGETR", null, new String[]{"TABLE"});
            
            while (columnsResultSet.next()) {
                String tableName = columnsResultSet.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String[] result = new String[tableNames.size() + 1];
        result[0]= "";
        
        for(int i=1; i< tableNames.size(); i++)
            result[i]= tableNames.get(i);
        
        return result;
    }

    
    public static String[] getAllViews() {
        ArrayList<String> tableNames = new ArrayList<String>();
        
        if (connection == null) {
            return new String[0];
        }

        try {
            DatabaseMetaData myDatabaseMetaData = connection.getMetaData();
            
            ResultSet columnsResultSet = myDatabaseMetaData.getTables(null, "BGETR", null, new String[]{"VIEW"});
            while (columnsResultSet.next()) {
                String tableName = columnsResultSet.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        String[] result = new String[tableNames.size()+1];
        result[0]= "";
        for(int i=1; i< tableNames.size(); i++)
            result[i]= tableNames.get(i);
        
        return result;
    }

    public static ArrayList<Column> getAllFields(String tableName) {
        ArrayList<Column> allFields = new ArrayList<Column>();
       if(tableName == null)
           return allFields;
        try {
            DatabaseMetaData myDatabaseMetaData = connection.getMetaData();
            ResultSet columnsResultSet = myDatabaseMetaData.getColumns(null, null, tableName, null);

            while (columnsResultSet.next()) {
                String columnName = columnsResultSet.getString("COLUMN_NAME");
                String columnDataType = columnsResultSet.getString("TYPE_NAME");
                allFields.add(new Column(columnName, columnDataType));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allFields;
    }

}
