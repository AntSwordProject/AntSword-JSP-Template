//
// 命令执行模板
//

module.exports = (arg1, arg2, arg3) => ({
  exec: {
    _: '###Exec###',
    [arg1]: "#{newbase64::bin}",
    [arg2]: "#{newbase64::cmd}",
    [arg3]: "#{newbase64::env}"
  },
  listcmd: {
    _: '###Listcmd###',
    [arg1]: '#{newbase64::binarr}'
  }
})
