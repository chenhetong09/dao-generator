/**
*
* ${beanComment}
*
* @author chenhetong
* @version 1.0
* @created ${time}
**/
public class ${beanName} {

<#list beanFields as field >
    /**
    * ${field.comment}
    */
    private ${field.type} ${field.camelName};

</#list>


}