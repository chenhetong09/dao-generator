
/**
*
* ${beanComment}
*
* @author chenhetong
* @version 1.0
* @created ${time}
**/
public class ${beanName} extends BaseDao {

private static final String TABLE_NAME = "${tableName}";

private static final String ALL_FIELDS = "${allFields}";

private static final String SQL_INSERT = String.format("INSERT INTO %s(%s) " +
"VALUES(${allValues})", TABLE_NAME, ALL_FIELDS);


}