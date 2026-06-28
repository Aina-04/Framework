package main.java.map;
import java.lang.reflect.*;

public class Mapping {
   String tableName;
   String[] colonnes;
   Field[] fields;

   public String[] getColonnes() {
       return colonnes;
   }
   public String getTableName() {
       return tableName;
   }
   public void setColonnes(String[] colonnes) {
       this.colonnes = colonnes;
    }
   public void setTableName(String tableName) {
       this.tableName = tableName;
    }
    public Field[] getFields() {
        return fields;
    }
    public void setFields(Field[] fields) {
        this.fields = fields;
    }
}
