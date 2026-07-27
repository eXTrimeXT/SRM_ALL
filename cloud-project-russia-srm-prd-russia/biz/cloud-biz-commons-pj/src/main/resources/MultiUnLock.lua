-- 使用map存储。<key, {36位线程代号}_{10位有效期截止时间戳}_{持有锁次数.针对重入锁}>

local businessKey = KEYS[1] -- 对应map
local keys = ARGV[1] -- 需要锁定的keys
local threadName = ARGV[2] -- 当前线程唯一代号
local nowTime = os.time()

-- 1. 获取map
local isExists = redis.call('exists', businessKey)
if isExists == 0 then
    return true
end

-- 2. 遍历keys，确保keys均可删除，其他线程持有的未超时key不能被删除
local keyLockCount = {} -- 当前key的重入次数
local keyExpireTime = {} -- 当前key的过期时间
local value
for i, v in pairs(keys) do
    value = redis.call('hget', businessKey, v)
    if type(value) ~= "nil" then
        -- key存在，判断key是否被当前线程持有，或者已过期
        keyExpireTime[i] = tonumber(string.sub(value, 38, 47));
        if string.sub(value, 1, 36) ~= threadName then
            -- 不是当前线程持有，判断是否已超时
            if keyExpireTime[i] > nowTime then
                -- 说明还未超时
                return false
            else
                -- 已超时
                keyLockCount[i] = 0
            end
        else
            -- 是当前线程持有
            if keyExpireTime[i] > nowTime then
                -- 说明还未超时
                keyLockCount[i] = tonumber(string.sub(value, 49))
            else
                -- 已超时
                keyLockCount[i] = 0
            end
        end
    else
        keyLockCount[i] = 0
        keyExpireTime[i] = 0
    end
end

-- 3. 删除keys
for i, v in pairs(keys) do
    if keyLockCount[i] <= 1 then
        -- 不是重入状态，直接删除
        redis.call('hdel', businessKey, v)
    else
        -- 重入状态，计数-1; 原有过期时间保持不变
        redis.call('hmset', businessKey, v, threadName.."_"..keyExpireTime[i].."_"..(keyLockCount[i] - 1))
    end
end

return true
