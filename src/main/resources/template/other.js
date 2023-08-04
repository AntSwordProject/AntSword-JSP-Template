//
// 其它模板
// 向插件提供
//

module.exports = (arg1, arg2, arg3, arg4, arg5) => ({
    redisconn: {
        _: '###RedisConn###',
        [arg1]: '#{newbase64::addr}',
        [arg2]: '#{newbase64::context}'
    },
    portscan: {
        _: '###PortScan###',
        [arg1]: '#{newbase64::ip}',
        [arg2]: '#{newbase64::ports}'
    },
})
