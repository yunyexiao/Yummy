export default {
  nameRules: [
    name => !!name || '请填写餐馆名',
    name => (name && name.length <= 40) || '餐馆名必须在40个字符以内'
  ],
  pwdRules: [
    pwd => !!pwd || '请设置密码',
    pwd => (pwd && pwd.length >= 6 && pwd.length <= 20) || '密码长度为6-20个字符'
  ],
  typeRules: [
    type => !!type || '请填写餐馆类型（例如湘菜、日料等等）',
    type => (type && type.length <= 20) || '类型必须在20个字符以内'
  ],
  descriptionRules: [
    description => !!description || '请填写餐馆介绍',
    description => (description && description.length <= 100) || '餐馆介绍长度为100个字符以内'
  ],
  addressRules: [
    address => !!address || '请填写餐馆地址',
    address => (address && address.length < 50) || '餐馆地址长度为50个字符以内'
  ],
  latitudeRules: [
    latitude => !!latitude || '请填写纬度（可以通过各类地图APP查询）'
  ],
  longitudeRules: [
    longitude => !!longitude || '请填写经度（可以通过各类地图APP查询）'
  ],
  phoneRules: [
    phone => !!phone || '请填写餐厅的联系电话',
    phone => (phone && /[0-9]*/.test(phone) && phone.length === 11) || '联系电话应为11个数字'
  ]
}
