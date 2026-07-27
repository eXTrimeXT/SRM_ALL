-- 使用map存储。<key, {36位线程代号}_{10位有效期截止时间戳}_{持有锁次数.针对重入锁}>

local businessKey = KEYS[1] -- 对应map
local keys = ARGV[1] -- 需要锁定的keys
local threadName = ARGV[2] -- 当前线程唯一代号
local nowTime = os.time()
local expireTime = nowTime + ARGV[3]

-- 1. 获取map
local isExists = redis.call('exists', businessKey)
if isExists == 0 then
    -- 进行初次map构造
    redis.call('hmset', businessKey, 1, 0)
end

-- 2. 遍历keys，确保当前需要锁定的key都可用
local keyLockCount = {} -- 当前key的重入次数
local value
for i, v in pairs(keys) do
    value = redis.call('hget', businessKey, v)
    if type(value) ~= "nil" then
        -- key存在，判断key是否被当前线程持有，或者已过期
        if string.sub(value, 1, 36) ~= threadName then
            -- 不是当前线程持有，判断是否已超时
            if tonumber(string.sub(value, 38, 47)) > nowTime then
                -- 说明还未超时
                return false
            else
                -- 已超时
                keyLockCount[i] = 0
            end
        else
            -- 是当前线程持有
            if tonumber(string.sub(value, 38, 47)) > nowTime then
                -- 说明还未超时
                keyLockCount[i] = tonumber(string.sub(value, 49))
            else
                keyLockCount[i] = 0
            end
        end
    else
        keyLockCount[i] = 0
    end
end

-- 3. 遍历keys，获取锁
for i, v in pairs(keys) do
    redis.call('hmset', businessKey, v, threadName.."_"..expireTime.."_"..(keyLockCount[i] + 1))
end

return true
