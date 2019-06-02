package io.github.forezp;

import static io.github.forezp.HttpConstants.HTTP_SYNC_ENABLE;
import static io.github.forezp.HttpConstants.TRUE;

/**
 * Created by forezp on 2019/5/29.
 */


public class HttpSyncEnable extends PropertyEqualsCondition {

    public HttpSyncEnable() {
        super(HTTP_SYNC_ENABLE, TRUE);
    }
}
