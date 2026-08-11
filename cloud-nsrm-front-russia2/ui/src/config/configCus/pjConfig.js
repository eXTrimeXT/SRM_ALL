// 项目二开其他配置
/**
 * 二开图片上传类型拓展
 * size 为null则默认已产品设置的值为准
 * accept 可以拓展项目上增加的类型
 */
export const uploadTypePj = {
  picture: {
    accept: [] // 图片类型拓展
  },
  import: {
    size: null,
    accept: []
  }, // 导入类型拓展
  default: { // 默认类型拓展
    size: null,
    accept: []
  }
}
