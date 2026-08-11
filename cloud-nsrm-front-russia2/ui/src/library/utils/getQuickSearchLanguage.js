import http from '@/utils/axios/http'

const toCamel = str =>
  str.replace(/\s+/g, '').toLocaleLowerCase().replace(/([^_])(?:_+([^_]))/g, function ($0, $1, $2) {
    return $1 + $2.toUpperCase()
  })

const transform = async (queryTable, name, nameKey) => {
  const { data } = await http({
    url: '/api-base/quicksearch/quicksearchConfig/getDetail',
    data: { name: nameKey },
    method: 'POST'
  })
  // const { data } = await http({
  //   url: '/api-base/quicksearch/quicksearchConfig/listTables',
  //   data: { queryTable },
  //   method: 'POST'
  // });
  const json = data.attrConfigs.reduce((last, item) => {
    if (!last[`${nameKey}_title`]) {
      last[`${nameKey}_title`] = name
    }
    const key = toCamel(item.attr)
    if (!last[key]) {
      last[key] = item.title
    }
    return last
  }, {})
  const res = { [nameKey]: json }
  console.log(res)
}

const getAllQuickSearchZhLanguage = async (type = 'zh') => {
  const language = {}
  const {
    data: { list }
  } = await http({
    url: '/api-base/quicksearch/quicksearchConfig/listPage',
    method: 'POST',
    data: {
      pageSize: 999,
      pageNum: 1
    }
  })
  for (const { name, description } of list) {
    const {
      data: { attrConfigs }
    } = await http({
      url: '/api-base/quicksearch/quicksearchConfig/getDetail',
      data: { name },
      method: 'POST'
    })
    const languageItem = attrConfigs.reduce((last, item) => {
      if (!last[`${name}_title`]) {
        last[`${name}_title`] = type === 'zh' ? description : toCamel(`${name}_title`)
      }
      const key = toCamel(item.attr)
      if (!last[key]) {
        last[key] = type === 'zh' ? item.title : key
      }
      return last
    }, {})
    language[name] = languageItem
  }

  console.log(language)
}

export { transform, getAllQuickSearchZhLanguage }
