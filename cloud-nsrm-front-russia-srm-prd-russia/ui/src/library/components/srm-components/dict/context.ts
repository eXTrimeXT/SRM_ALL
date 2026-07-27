import { observable, untracked } from '@meicloud/render-engine'
import { computed, inject, provide } from 'vue-demi'
// @ts-ignore
import { STORE_COMMON_CACHE } from '@/config/store-config'
// @ts-ignore
import { loadCustomSelect } from '@/library/utils/dict/dict-utils'
import { useSetupContext } from '../../composables/useSetupContext'

export const useDictProviderSymbol = Symbol('useDictProviderSymbol')

type Queue = {
  code: string
  resolve: (value: unknown) => void
  reject: (reason?: any) => void
}

const JOIN_KEY = '##___##'

export const useDictProvider = () => {
  // TODO useVuex
  let queues: Queue[] = []
  // 收集统一处理
  const dictCodes = new Set<string>()

  const dictMapStore = observable(new Map())

  const { app } = useSetupContext()

  const dictListFromVuex = computed(() => (app as any).$store.state.common_cache.dictMap ?? {})

  const consumeQueues = (queueKeys: string[], dataMap: Record<string, any[]>, success = true) => {
    for (let i = 0; i < queues.length; i += 1) {
      const item = queues[i]

      if (queueKeys.includes(item.code)) {
        item[success ? 'resolve' : 'reject'](dataMap[item.code] ?? [])
      }
    }

    queues = queues.filter(item => !queueKeys.includes(item.code))
  }

  const requestDictData = useDebounceFn(async () => {
    const { dictCodeList, customSelectCodes } = [...dictCodes.values()].reduce(
      (acc, currentCode) => {
        if (currentCode.indexOf(JOIN_KEY) !== -1) {
          acc.customSelectCodes.push(currentCode)
        } else {
          acc.dictCodeList.push(currentCode)
        }

        return acc
      },
      {
        dictCodeList: [] as string[],
        customSelectCodes: [] as string[],
      },
    )

    if (dictCodeList.length) {
      ;(app as any).$store
        .dispatch(STORE_COMMON_CACHE.LIST_DICT_BATCH, {
          dictCodeList,
        })
        .then((dictBatchMap: any) => {
          consumeQueues(dictCodeList, dictBatchMap, true)

          dictCodeList.forEach(code => {
            dictMapStore.set(code, dictBatchMap[code])
          })
        })
        .catch(() => consumeQueues(dictCodeList, {}, false))
    }

    if (customSelectCodes.length) {
      // 乱序发起即可
      customSelectCodes.forEach(codeStr => {
        const [code, customSelectType] = codeStr.split(JOIN_KEY)

        loadCustomSelect(customSelectType, code, (list: any[]) => {
          if (
            ['PROVINCE', 'CITY', 'ORG_TYPE_ALL', 'payExplain', 'ELEMNAME'].includes(
              customSelectType,
            )
          ) {
            ;(app as any).$store.commit(STORE_COMMON_CACHE.SET_CUSTOM_DICT_MAP, {
              type: customSelectType,
              code,
              list,
            })
          }

          consumeQueues([codeStr], { [codeStr]: list }, true)

          dictMapStore.set(codeStr, list)
        })
      })
    }
  }, 66)

  const getDictByCode = (code: string, customSelectType?: string) =>
    new Promise((resolve, reject) => {
      const currentDict = customSelectType
        ? dictListFromVuex.value[customSelectType]?.[code]
        : dictListFromVuex.value[code]

      if (currentDict) {
        resolve(currentDict)
        return
      }

      const uniqueKey = [code, customSelectType].filter(Boolean).join(JOIN_KEY)

      // 约定的方式
      dictCodes.add(uniqueKey)
      queues.push({
        code: uniqueKey,
        resolve,
        reject,
      })

      requestDictData()
    })

  // TODO1 提供值格式
  // TODO2 提供连接符传入
  // TODO3 优化遍历
  const previewText = (value: number | string | boolean | any[] | undefined, data: any[]) => {
    if (value === undefined) {
      return ''
    }

    if (Array.isArray(value)) {
      return (
        Array.isArray(data)
          ? data.filter(item => (value as string[]).includes(item.value)).map(item => item.label)
          : Object.keys(data).reduce(
              (acc, key) =>
                (value as string[]).includes(data[key])
                  ? [...acc, (data[key] as Record<string, any>).label]
                  : acc,
              [] as string[],
            )
      ).join('、')
    }

    return (
      (Array.isArray(data)
        ? // TODO 转换成 map
          // 用 == 来做自动类型转换比较
          data.find(item => item.value == value)?.label
        : data[value as string]) ?? ''
    )
  }

  // TODO 后续可以针对 table 的场景做进一步优化
  const findPreviewText = (
    value: number | string | boolean | any[],
    requestParams: { code: string; customSelectType?: string },
  ) => {
    const uniqueKey = [requestParams.code, requestParams.customSelectType]
      .filter(Boolean)
      .join(JOIN_KEY)

    const dictData = dictMapStore.get(uniqueKey)

    if (!dictData) {
      untracked(() => {
        dictCodes.add(uniqueKey)
        requestDictData()
      })
    }

    return !dictData ? value : previewText(value, dictData)
  }

  const provideValues = {
    getDictByCode,
    previewText,
    findPreviewText,
  }

  provide(useDictProviderSymbol, provideValues)

  return provideValues
}

export const useDict = () => {
  const storeContext = inject<ReturnType<typeof useDictProvider>>(useDictProviderSymbol)

  if (!storeContext) {
    throw new Error('请先挂载 useDictProvider')
  }

  return storeContext
}
