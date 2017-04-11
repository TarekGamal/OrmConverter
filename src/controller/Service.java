/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;
import model.DataBaseHandler;
import model.Column;

/**
 *
 * @author Tarek
 */
public class Service {

    private static File parentDirectory;

    public static File getParentDirectory() {
        return parentDirectory;
    }

    public static void setParentDirectory(File parentDirectory) {
        Service.parentDirectory = parentDirectory;
    }

    public static boolean connect() {
        return DataBaseHandler.connect();
    }

    public static String[] getTablesNames() {
        return DataBaseHandler.getAllTables();
    }

    public static String[] getViewsNames() {
        return DataBaseHandler.getAllViews();
    }

    public static String toCamelCase(String upperCaseName) {
        // split columnName by '_'
        StringTokenizer st = new StringTokenizer(upperCaseName, "_");
        String result = "", token;
        int tokensCount = st.countTokens();
        for (int i = 0; i < tokensCount; i++) {
            token = st.nextToken();
            result += token.substring(0, 1).toUpperCase() + token.substring(1).toLowerCase();
        }
        return result;
    }

    public static File[] generate(String pathName, String tableName, String viewName, String javaTableName, String javaViewName,
            boolean isViewRelatedToTableCheckBox, boolean createStringforDateFieldsCheckBox, boolean createBooleanforFlagFieldsCheckBox) throws IOException {

        File tableFile = null, viewFile = null;

        ArrayList<Column> tableColumns = DataBaseHandler.getAllFields(tableName);
        ArrayList<Column> viewColumns = DataBaseHandler.getAllFields(viewName);

        HashSet<String> hashSet = new HashSet<>();
        for (Column column : tableColumns) {
            hashSet.add(column.getName());
        }

        boolean isViewRelatedToTable = false;

        if (isViewRelatedToTableCheckBox) {
            for (Column column : viewColumns) {
                if (hashSet.contains(column.getName())) {
                    column.setIsRelatedToTable(true);
                    isViewRelatedToTable = true;
                }
            }
        }

        if (javaTableName != null) {
            tableFile = ClassGenerator.generateTableOutput(pathName, tableColumns, tableName, javaTableName);
        }

        viewFile = null;
        if (javaViewName != null) {
            if (isViewRelatedToTable) {
                viewFile = ClassGenerator.generateViewOutput(pathName, viewColumns, viewName, isViewRelatedToTable ? javaTableName : null, javaViewName, createStringforDateFieldsCheckBox, createBooleanforFlagFieldsCheckBox);
            } else {
                viewFile = ClassGenerator.generateViewOutput(pathName, viewColumns, viewName, null, javaViewName, false, false);
            }
        }

        return new File[]{tableFile, viewFile};
    }

}
