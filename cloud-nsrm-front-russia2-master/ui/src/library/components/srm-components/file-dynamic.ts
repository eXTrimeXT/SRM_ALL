import { defineComponent, h, onMounted } from 'vue-demi'
// @ts-ignore
import OriginalFileDynamic from '@/library/components/c-file-management/file-dynamic'
import { useSetupContext } from '@/library/components/composables/useSetupContext'
import { useAutoMountInstanceToField, useCascadeDeletionForArray } from '@meicloud/render-engine'

export const FileDynamic = defineComponent({
  name: 'RenderFileDynamic',
  props: {
    initLoad: {
      type: Boolean,
      default: true
    }
  },
  setup(props, { listeners, attrs, expose }) {
    const { currentInstance } = useSetupContext()

    useAutoMountInstanceToField()

    // @ts-ignore
    const { storeCascadeDeleteData } = useCascadeDeletionForArray(attrs)

    const reLoadFileInfo = () => {
      // @ts-ignore
      // eslint-disable-next-line no-unused-expressions
      currentInstance.$refs?.sceneAttachment?.loadFileInfo?.()
    }

    onMounted(() => {
      if (props.initLoad) {
        reLoadFileInfo()
      }
    })

    expose({
      reLoadFileInfo
    })

    const handleRemoveFile = (_: any, item: Record<string, any>) => {
      storeCascadeDeleteData(item)
    }

    watch(() => attrs.editable, () => {
      reLoadFileInfo()
    })

    return () => {
      return h(OriginalFileDynamic, {
        attrs,
        on: { input: listeners.change, removeFile: handleRemoveFile },
        ref: 'sceneAttachment'
      })
    }
  }
})
