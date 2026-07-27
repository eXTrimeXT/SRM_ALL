
export const createTreeClass = (keyName = 'id', parentKeyName = 'parentId', childrenKey = 'children') => {
  const TreeClass = {
    keyName: keyName,
    parentKeyName: parentKeyName,
    childrenKey: childrenKey,
    findRoots: (allNodes) => {
      var results = []
      for (let i = 0; i < allNodes.length; i++) {
        var isRoot = true
        for (let k = 0; k < allNodes.length; k++) {
          if (allNodes[i][TreeClass.parentKeyName] === allNodes[k][TreeClass.keyName]) {
            isRoot = false
            break
          }
        }
        if (isRoot) {
          const nodeInfo = Object.assign({}, allNodes[i])
          results.push(nodeInfo)
        }
      }
      return results
    },
    findChildren: (root, allNodes) => {
      var children = []
      for (let i = 0; i < allNodes.length; i++) {
        if (allNodes[i][TreeClass.parentKeyName] === root[TreeClass.keyName]) {
          const nodeInfo = Object.assign({}, allNodes[i])
          children.push(nodeInfo)
        }
      }
      for (let i = 0; i < children.length; i++) {
        var tmpChildren = TreeClass.findChildren(children[i], allNodes)
        if (tmpChildren.length > 0) { // iview tree组件的bug，不能包含children为空的子节点，否则会导致tree全选并无法勾选
          children[i][TreeClass.childrenKey] = tmpChildren
        }
      }
      if (children.length > 0) { // iview tree组件的bug，不能包含children为空的子节点，否则会导致tree全选并无法勾选
        root[TreeClass.childrenKey] = children
      }
      return children
    },
    resetBottom: (allNodes) => {
      var results = []
      for (let i = 0; i < allNodes.length; i++) {
        var hasChild = false
        for (let k = 0; k < allNodes.length; k++) {
          if (allNodes[i][TreeClass.keyName] === allNodes[k][TreeClass.parentKeyName]) {
            hasChild = true
            break
          }
        }
        if (hasChild) {
          results.push(allNodes[i])
        }
      }
      return results
    },
    buildTree: (dataList) => {
      // console.log('dataList', dataList)
      if (!dataList || dataList.length <= 1) {
        return dataList
      }
      // var roots = TreeClass.resetBottom(dataList)
      var roots = TreeClass.findRoots(dataList)
      for (var i = 0; i < roots.length; i++) {
        TreeClass.findChildren(roots[i], dataList)
      }
      return roots
    },
    getTreeNodeProp: (treeList, prop) => { // 获取所有节点某个属性
      if (treeList.length > 0 && prop !== '') {
        const propArr = []
        treeList.map(item => {
          if (item.children) {
            const childrenProps = TreeClass.getTreeNodeProp(item.children, prop)
            propArr.push(...childrenProps)
          }
          propArr.push(item[prop])
        })
        return propArr
      }
    },
    clearChecked: (treeList) => {
      if (treeList.length <= 0) {
        return
      }
      treeList.map(item => {
        treeList.checked = false
        if (item.children) {
          item.children = TreeClass.clearChecked(item.children)
        }
      })
      return treeList
    }
  }
  return TreeClass
}

export default createTreeClass
