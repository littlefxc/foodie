---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by fengxuechao.
--- DateTime: 2021/12/15 17:14
---

-- 模拟限流

-- 用作限流的 Key, （一秒一个）
local key = KEYS[1]
redis.log(redis.LOG_DEBUG, 'key is ', key)

-- 限流的最大阈值
local limit = tonumber(ARGV[1])

-- 当前流量大小
local current = tonumber(redis.call("get", key) or "0")

-- 如果超出限流大小
if current + 1 > limit then
    return false
else
    -- 请求数 +1，并设置 2 秒过期
    redis.call("INCRBY", key, "1")
    redis.call("expire", key, "2")
    return true
end