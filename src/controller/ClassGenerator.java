package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import model.Column;
import model.JavaField;

/**
 *
 * @author Tarek Gamal
 */
public class ClassGenerator {

    static List<JavaField> fields;
    public static File generateTableOutput(String pathName, List<Column> columns, String tableName, String javaTableName) throws IOException {

        
        File outputFile = new File(pathName+"/"+javaTableName+".java");
        outputFile.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

        //ClassGenerator.field = columns;
        fields = new ArrayList<JavaField>();
        JavaField field;
        for (Column c : columns) {
            field = new JavaField(c, false);
            fields.add(field);
            // 1. set javaName
            field.setJavaName(toCamelCase(c.getName()));
            // 2. set javaDataType
            if (c.getDataType().equals("VARCHAR2")) {
                field.setJavaDataType("String");
            } else if (c.getDataType().equals("DATE")) {
                field.setJavaDataType("Date");
                // add another field
            } else if (field.getJavaName().startsWith("id") || field.getJavaName().endsWith("Id")) {
                field.setJavaDataType("Long");
            }else if (c.getDataType().equals("BLOB")) {
                field.setJavaDataType("byte[]");
            } 
            else {
                field.setJavaDataType("Integer");
            }
        }
        // print imports and class name

        out.append("import java.io.Serializable;\n");
        out.append("import java.util.Date;\n");
        out.append("\n");
        out.append("import javax.persistence.Basic;\n");
        out.append("import javax.persistence.Column;\n");
        out.append("import javax.persistence.Entity;\n");
        out.append("import javax.persistence.Id;\n");
        out.append("import javax.persistence.Table;\n");
        out.append("import javax.persistence.Temporal;\n");
        out.append("import javax.persistence.TemporalType;\n");

        out.append("import com.code.dal.orm.BaseEntity;\n");

        out.append("@SuppressWarnings(\"serial\")\n");
        out.append("@Entity\n");
        
        out.append("@Table(name = \""+tableName+"\")\n");
        out.append("public class "+javaTableName+" extends BaseEntity implements Serializable {\n");

        //3. printing variables
        printJavaVariables(out);
        // printing setters and getters
        printJavaSettersAndGetters(out);

        //close class
        out.append("}\n");
        out.close();
        return outputFile;
    }

    public static File generateViewOutput(String pathName, List<Column> columns, String viewName, String javaTableName, String javaViewName,
            boolean createStringforDateFieldsCheckBox, boolean createBooleanforFlagFieldsCheckBox) throws IOException {

        File outputFile = new File(pathName+"/"+javaViewName+".java");
        outputFile.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

        //ClassGenerator.field = columns;
        fields = new ArrayList<JavaField>();
        JavaField field;
        for (Column c : columns) {
            field = new JavaField(c, false);
            fields.add(field);
            // 1. set javaName
            field.setJavaName(toCamelCase(c.getName()));
            // 2. set javaDataType
            if (c.getDataType().equals("VARCHAR2")) {
                field.setJavaDataType("String");
            } else if (c.getDataType().equals("DATE")) {
                field.setJavaDataType("Date");
                // add another transient column
                if (createStringforDateFieldsCheckBox) {
                    JavaField transientField = new JavaField(new Column(c.getName() + "_STRING", "TRANSIENT"), true);
                    transientField.setJavaDataType("String");
                    transientField.setJavaName(toCamelCase(transientField.getName()));
                    fields.add(transientField);
                }
            } else if (field.getJavaName().startsWith("id") || field.getJavaName().endsWith("Id")) {
                field.setJavaDataType("Long");
            }else if (c.getDataType().equals("BLOB")) {
                field.setJavaDataType("byte[]");
            } 
            else {
                field.setJavaDataType("Integer");
                if (field.getJavaName().endsWith("Flag") && createBooleanforFlagFieldsCheckBox) {
                    JavaField transientField = new JavaField(new Column(c.getName() + "_BOOLEAN", "TRANSIENT"), true);
                    transientField.setJavaDataType("Boolean");
                    transientField.setJavaName(toCamelCase(transientField.getName()));
                    fields.add(transientField);
                }
            }
        }

        String tableFieldName = null;
        if (javaTableName != null) {
            // Add table object
            tableFieldName = javaTableName.substring(0, 1).toLowerCase() + javaTableName.substring(1);
            JavaField transientField = new JavaField(new Column(tableFieldName, "TRANSIENT"), true);
            transientField.setJavaDataType(javaTableName);
            transientField.setJavaName(tableFieldName);
            fields.add(transientField);
        }

        //2. print imports and class name
        // print imports and class name
        out.append("import java.io.Serializable;\n");
        out.append("import java.util.Date;\n");
        out.append("\n");
        out.append("import javax.persistence.Basic;\n");
        out.append("import javax.persistence.Column;\n");
        out.append("import javax.persistence.Entity;\n");
        out.append("import javax.persistence.Id;\n");
        out.append("import javax.persistence.Table;\n");
        out.append("import javax.persistence.Temporal;\n");
        out.append("import javax.persistence.TemporalType;\n");

        out.append("\n");
        out.append("import javax.persistence.Transient;\n");
        out.append("import com.code.dal.orm.BaseEntity;\n");
        out.append("import com.code.enums.FlagsEnum;\n");
        out.append("import com.code.services.util.HijriDateService;\n");

        out.append("@SuppressWarnings(\"serial\")\n");
        out.append("@Entity\n");
        out.append("@Table(name = \""+viewName+"\")\n");
        out.append("public class "+javaViewName+" extends BaseEntity implements Serializable {\n");

        //3. printing variables
        printJavaVariables(out);

        // print constructor
        if (javaTableName != null) {
            out.append("public "+javaViewName+"()\n"
                    + "    {\n"
                    + tableFieldName+" = new "+javaTableName+"();\n"
                    + "    }");
        }

        // printing setters and getters
        printJavaDataSettersAndGetters(tableFieldName, createStringforDateFieldsCheckBox, createBooleanforFlagFieldsCheckBox, out);

        //close class
        out.append("}\n");
        out.close();
        return outputFile;
    }

    public static String toCamelCase(String upperCaseName) {
        // split columnName by '_'
        StringTokenizer st = new StringTokenizer(upperCaseName, "_");
        String result = "", token;
        int tokensCount = st.countTokens();
        for (int i = 0; i < tokensCount; i++) {
            token = st.nextToken();
            if (i == 0) {
                result += token.toLowerCase();
            } else {
                result += token.substring(0, 1).toUpperCase() + token.substring(1).toLowerCase();
            }
        }
        return result;
    }

    public static void printJavaVariables(BufferedWriter out) throws IOException {
        for (JavaField f : fields) {
            out.append("private " + f.getJavaDataType() + " " + f.getJavaName() + ";//" + f.getDataType()+"\n");
        }
    }

    public static void printJavaSettersAndGetters(BufferedWriter out) throws IOException {

        for (JavaField f : fields) {
            if (f.getJavaName().equals("id")) {
                out.append("@Id\n");
            } else {
                out.append("@Basic\n");
            }

            out.append("@Column(name = \""+f.getName()+"\")");
            out.append("\n");

            if (f.getDataType().equals("DATE")) {
                out.append("@Temporal(TemporalType.TIMESTAMP)\n");
            }

            // print getter methods
            String methodName = f.getJavaName().substring(0, 1).toUpperCase() + f.getJavaName().substring(1);
            out.append("public "+f.getJavaDataType()+" get"+methodName+"() {\n");
            out.append("	return "+f.getJavaName()+";\n");
            out.append("    }\n");

            // 2. print setter method
            out.append("public void set"+methodName+"("+f.getJavaDataType()+" "+f.getJavaName()+") {\n");
            out.append("	this."+f.getJavaName()+" = "+f.getJavaName()+";\n");
            out.append("    }\n");
        }
    }

    public static void printJavaDataSettersAndGetters(String tableName, boolean createStringforDateFieldsCheckBox, boolean createBooleanforFlagFieldsCheckBox, BufferedWriter out) throws IOException {
        for (JavaField f : fields) {
            if (f.getJavaName().equals("id")) {
                out.append("@Id\n");
            } else if (f.getIsTransient()) {
                out.append("@Transient\n");
            } else {
                out.append("@Basic\n");
            }

            if (!f.getIsTransient()) {
                out.append("@Column(name = \""+f.getName()+"\")");
                out.append("\n");
            }
            if (f.getDataType().equals("DATE")) {
                out.append("@Temporal(TemporalType.TIMESTAMP)\n");
            }

            // print getter methods
            String methodName = f.getJavaName().substring(0, 1).toUpperCase() + f.getJavaName().substring(1);
            out.append("public "+f.getJavaDataType()+" get"+methodName+"() {\n");
            out.append("	return "+f.getJavaName()+";\n");
            out.append("    }\n");

            // 2. print setter method
            out.append("public void set"+methodName+"("+f.getJavaDataType()+" "+f.getJavaName()+") {\n");
            out.append("	this."+f.getJavaName()+" = "+f.getJavaName()+";\n" );

            // if date type convert to String
            if (f.getJavaDataType().equals("Date") && createStringforDateFieldsCheckBox) {
                out.append("this."+f.getJavaName() + "String = "
                        + "HijriDateService.getHijriDateString("+f.getJavaName()+");\n");

            } else if (f.getJavaName().endsWith("Flag") && createBooleanforFlagFieldsCheckBox) {

                out.append("if (this."+f.getJavaName()+" == null || this."+f.getJavaName()+" == FlagsEnum.OFF.getCode()) {\n"
                        + "	    this."+f.getJavaName() + "Boolean = false;\n"
                        + "	} else {\n"
                        + "	    this."+f.getJavaName() + "Boolean = true;\n"
                        + "	}\n");
            } //if original type is transient convert to date
            else if (f.getIsTransient()) {
                if (f.getJavaName().contains("Date") && createStringforDateFieldsCheckBox) {
                    out.append("this."+f.getJavaName().substring(0, f.getJavaName().length() - 6)+" = HijriDateService.getHijriDate("+f.getJavaName()+");\n");
                }

                if (f.getJavaDataType().equals("Boolean") && createBooleanforFlagFieldsCheckBox) {
                    out.append("if (this."+f.getJavaName()+" == false) {\n"
                            +"set" + methodName.substring(0, methodName.length() - 7)+ "(FlagsEnum.OFF.getCode());\n"
                            + "	} else {\n"
                            +"set" + methodName.substring(0, methodName.length() - 7)+ "(FlagsEnum.ON.getCode());\n"
                            + "	}\n"
                            );

                }
            }
            // add setting values for Table reference
            if (f.getIsRelatedToTable()) {
                out.append("this."+tableName+".set"+methodName+"("+f.getJavaName()+");\n");
            }

            out.append("    }\n");
        }
    }
}
