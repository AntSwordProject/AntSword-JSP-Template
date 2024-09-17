//
// 命令执行模板
//

module.exports = (arg1, arg2, arg3) => ({
  exec: {
    _: '###Exec###',
    [arg1]: "#{base64::bin}",
    [arg2]: "#{base64::cmd}",
    [arg3]: "#{base64::env}"
  },
  listcmd: {
    _: '###Listcmd###',
    [arg1]: '#{base64::binarr}'
  }
})
