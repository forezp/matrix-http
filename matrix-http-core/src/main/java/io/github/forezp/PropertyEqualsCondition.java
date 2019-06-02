package io.github.forezp;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;


/**
 * @author fangzhipeng
 * create 2018-05-27
 **/
public class PropertyEqualsCondition implements Condition {
    private String key;
    private String value;
    private String defaultValue;

    public PropertyEqualsCondition(String key, String value,String defaultValue) {
        this.key = key;
        this.value = value;
        this.defaultValue=defaultValue;
    }

    public PropertyEqualsCondition(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String value;
        if(StringUtils.isEmpty(defaultValue)) {
           value  = context.getEnvironment().getProperty(key);
        }else {
            value=context.getEnvironment().getProperty(key,defaultValue);
        }
        if (StringUtils.isEmpty( value )) {
            return false;
        }
        if (value.equals( this.value )) {
            return true;
        }
        return false;
    }
}
