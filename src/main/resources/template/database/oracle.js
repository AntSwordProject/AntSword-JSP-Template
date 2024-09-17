//
// oracle 模板
//
// @params
// :encode  SHELL编码
// :conn    数据库连接字符串
// :sql     执行SQL语句
// :db      数据库名
// :table   表名

module.exports = (arg1, arg2, arg3, arg4, arg5, arg6) => ({
    show_databases: {
      _: '###Show_databases###',
      [arg1]: '#{base64::encode}',
      [arg2]: '#{base64::conn}'
    },
    show_tables: {
      _: '###Show_tables###',
      [arg1]: '#{base64::encode}',
      [arg2]: '#{base64::conn}',
      [arg3]: '#{base64::db}'
    },
    show_columns: {
      _: '###Show_columns###',
      [arg1]: '#{base64::encode}',
      [arg2]: '#{base64::conn}',
      [arg3]: '#{base64::db}',
      [arg4]: '#{base64::table}'
    },
    query: {
      _: '###Query###',
      [arg1]: '#{base64::encode}',
      [arg2]: '#{base64::conn}',
      [arg3]: '#{base64::sql}'
    }
  })