package model;

/**
 *
 * @author Tarek
 */
public class JavaField {
    private Column column;
    private String javaName;
    private String javaDataType;

    private Boolean isTransient;
    
    public JavaField(Column column, Boolean isTransient) {
        this.column = column;
        this.isTransient = isTransient;
    }

    public String getName() {
        return column.getName();
    }

    public void setName(String name) {
        this.column.setName(name);
    }

    public String getDataType() {
        return this.column.getDataType();
    }

    public void setDataType(String dataType) {
        this.column.setDataType(dataType);
    }
    
    public Boolean getIsRelatedToTable() {
        return this.column.getIsRelatedToTable();
    }

    public void setIsRelatedToTable(Boolean isRelatedToTable) {
        this.column.setIsRelatedToTable(isRelatedToTable);
    }
    
    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public String getJavaDataType() {
        return javaDataType;
    }

    public void setJavaDataType(String javaDataType) {
        this.javaDataType = javaDataType;
    }

    public Boolean getIsTransient() {
        return isTransient;
    }
}
