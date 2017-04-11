package model;

/**
 *
 * @author Tarek
 */
public class Column {
    private String name;
    private String dataType;
    
    private Boolean isRelatedToTable;
   
    public Column(String name, String dataType){
        this.name = name;
        this.dataType = dataType;
        isRelatedToTable = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Boolean getIsRelatedToTable() {
        return isRelatedToTable;
    }

    public void setIsRelatedToTable(Boolean isRelatedToTable) {
        this.isRelatedToTable = isRelatedToTable;
    }
    
    
}
