// 遍历文件对象
export const requireAll = (require) => {
  return (requireContext => {
    return Object.keys(requireContext).map(key => requireContext[key].default)
  })(require)
}
