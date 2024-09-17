//
// 文件管理模板
//

module.exports = (arg1, arg2, arg3) => ({
  dir: {
    _: '###Dir###',
    [arg1]: '#{base64::path}'
  },

  delete: {
    _: '###Delete###',
    [arg1]: '#{base64::path}'
  },

  create_file: {
    _: '###Create_file###',
    [arg1]: '#{base64::path}',
    [arg2]: '#{base64::content}'
  },

  read_file: {
    _: '###Read_file###',
    [arg1]: '#{base64::path}'
  },

  copy: {
    _: '###Copy###',
    [arg1]: '#{base64::path}',
    [arg2]: '#{base64::target}'
  },

  download_file: {
    _: '###Download_file###',
    [arg1]: '#{base64::path}'
  },

  upload_file: {
    _: '###Upload_file###',
    [arg1]: '#{base64::path}',
    [arg2]: '#{newb64buffer::content}'
  },

  rename: {
    _: '###Rename###',
    [arg1]: '#{base64::path}',
    [arg2]: '#{base64::name}'
  },

  retime: {
    _: '###Retime###',
    [arg1]: '#{base64::path}',
    [arg2]: '#{base64::time}'
  },

  chmod: {
    _: '###Chmod###',
    [arg1]: '#{base64::path}',
    [arg2]: '#{base64::mode}',
  },

  mkdir: {
    _: '###Mkdir###',
    [arg1]: '#{base64::path}'
  },

  wget: {
    _: '###Wget###',
    [arg1]: '#{base64::url}',
    [arg2]: '#{base64::path}'
  },

  filehash: {
    _: '###Filehash###',
    [arg1]: '#{base64::path}',
  },
})