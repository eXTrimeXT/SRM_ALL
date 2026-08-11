// 主执行函数
function searchTree (treeList, id) {
  var res = null
  if (!treeList.length) return null
  for (var i = 0; i < treeList.length; i++) {
    res = findNode(treeList[i], id)
    if (res) {
      res.path.unshift(treeList[i].organizationName)
      break
    }
  }

  // 边界处理，输入的id不存在相对应的节点时
  if (!res) {
    console.log('在该树的中没有相对应的id的节点')
    return null
  }

  var path = res.path.join('/')
  return path
}

// 深度遍历查找目标节点及缓存相关路径
function findNode (tree, id) {
  if (tree.fullPathId === id) {
    return {
      path: [],
      node: tree
    }
  }

  var res
  for (var i = 0; i < tree.childOrganRelation.length; i++) {
    res = findNode(tree.childOrganRelation[i], id)
    if (res !== undefined) {
      res.path.unshift(tree.childOrganRelation[i].organizationName)
      return res
    }
  }
  return undefined
}

function getFullPathName (id, tree, fullPathNameMap) {
  if (fullPathNameMap.has(id)) {
    console.log('[has cache]')
    return { fullPathName: fullPathNameMap.get(id), fullPathNameMap }
  } else {
    var fullPathName = searchTree(tree, id)
    if (fullPathName) {
      fullPathNameMap.set(id, fullPathName)
      return { fullPathName, fullPathNameMap }
    }
  }
  return null
}

export default function getOrgList (
  organizationUsers,
  tree,
  fullPathNameMap = new Map()
) {
  var _fullPathNameMap = fullPathNameMap
  var orgList = organizationUsers.map(i => {
    var { fullPathId } = i
    var { fullPathName, fullPathNameMap: temp } =
      getFullPathName(fullPathId, tree, _fullPathNameMap) || {}
    _fullPathNameMap = temp || _fullPathNameMap
    return { ...i, fullPathName }
  })
  return { orgList, fullPathNameMap: _fullPathNameMap }
}
