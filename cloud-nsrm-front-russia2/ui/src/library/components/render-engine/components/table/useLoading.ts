import { Loading } from '@meicloud/element-ui'

import { useSetupContext } from '../../../composables/useSetupContext'
import { useAutoRun } from '@/library/components/composables/useAutoRun'
import { Ref } from 'vue-demi'

export const useLoading = (loading: Ref<boolean>) => {
  const { currentInstance } = useSetupContext()
  const loadingInstanceRef = shallowRef()

  const tryFindElNumber = ref(0)
  const tryFindInstanceNumber = ref(0)

  const openLoading = () => {
    const target = currentInstance?.$refs?.vxeTable?.$el

    const _open = () => {
      // TODO 重试
      if (!target || tryFindElNumber.value >= 5) {
        tryFindElNumber.value += 1
        setTimeout(() => {
          openLoading()
        })
        return
      }

      loadingInstanceRef.value = Loading.service({
        target,
        text: currentInstance.$t('hierarchical.Loading'),
      })
    }

    if (!target) {
      nextTick(() => {
        _open()
      })
    } else {
      _open()
    }
  }

  const closeLoading = () => {
    if (loadingInstanceRef.value) {
      loadingInstanceRef.value.close()
      loadingInstanceRef.value = null
    } else {
      if (tryFindInstanceNumber.value < 5) {
        setTimeout(() => {
          closeLoading()
        })
      }
    }
  }

  useAutoRun(() => {
    const bool = loading.value

    if (bool) {
      openLoading()
    } else {
      closeLoading()
    }
  })
}
