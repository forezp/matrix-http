package io.github.forezp;


import static io.github.forezp.HttpConstants.HTTP_ASYNC_ENABLE;
import static io.github.forezp.HttpConstants.TRUE;

/**
 * Created by forezp on 2019/5/29.
 */
public class HttpAsyncEnable extends PropertyEqualsCondition{
    public HttpAsyncEnable() {
        super(HTTP_ASYNC_ENABLE, TRUE);
    }
}
