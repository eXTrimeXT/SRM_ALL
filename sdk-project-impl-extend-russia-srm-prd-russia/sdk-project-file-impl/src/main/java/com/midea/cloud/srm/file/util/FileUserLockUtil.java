package com.midea.cloud.srm.file.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Slf4j
@Component
public class FileUserLockUtil extends AbstractFileLockUtil {
    @Override
    protected String businessType() {
        return FileCommon.USER_SECRET;
    }
    @Override
    protected long keyTimeout() {
        long second = 3600 * 24 * 30L;
        return second;
    }

}
