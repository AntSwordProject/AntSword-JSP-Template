//
// 文件管理模板
//

module.exports = (arg1, arg2, arg3) => ({
  dir: {
    _: '###Dir###',
    [arg1]: '#{newbase64::path}'
  },

  delete: {
    _: '###Delete###',
    [arg1]: '#{newbase64::path}'
  },

  create_file: {
    _: '###Create_file###',
    [arg1]: '#{newbase64::path}',
    [arg2]: '#{newbase64::content}'
  },

  read_file: {
    _: '###Read_file###',
    [arg1]: '#{newbase64::path}'
  },

  copy: {
    _: '###Copy###',
    [arg1]: '#{newbase64::path}',
    [arg2]: '#{newbase64::target}'
  },

  download_file: {
    _: '###Download_file###',
    [arg1]: '#{newbase64::path}'
  },

  upload_file: {
    _: '###Upload_file###',
    [arg1]: '#{newbase64::path}',
    [arg2]: '#{newb64buffer::content}'
  },

  rename: {
    _: '###Rename###',
    [arg1]: '#{newbase64::path}',
    [arg2]: '#{newbase64::name}'
  },

  retime: {
    _: '###Retime###',
    [arg1]: '#{newbase64::path}',
    [arg2]: '#{newbase64::time}'
  },

  chmod: {
    _: '###Chmod###',
    [arg1]: '#{newbase64::path}',
    [arg2]: '#{newbase64::mode}',
  },

  mkdir: {
    _: '###Mkdir###',
    [arg1]: '#{newbase64::path}'
  },

  wget: {
    _: '###Wget###',
    [arg1]: '#{newbase64::url}',
    [arg2]: '#{newbase64::path}'
  },

  filehash: {
    _: '###Filehash###',
    [arg1]: '#{newbase64::path}',
  },
})