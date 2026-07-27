import { h } from '@meicloud/render-engine'
import { defineComponent, ref } from 'vue-demi'
import FilePreview from './index.vue'

/**
 * 包装一下预览组件，给渲染引擎使用
 */
export const FilePreviewWrapper = defineComponent({
  name: 'FilePreviewWrapper',
  setup (_, { expose }) {
    const propsData = {
      visible: false,
      fileuploadId: '' as string | number,
      fileName: ''
    }

    const customProps = ref(propsData)

    const open = (payload: typeof propsData) => {
      if (!payload.fileuploadId) {
        return
      }

      customProps.value.visible = true
      customProps.value.fileuploadId = payload.fileuploadId
      customProps.value.fileName = payload.fileName
    }

    const cancel = () => {
      customProps.value.visible = false
    }

    expose({
      open,
      cancel
    })

    return () => {
      return h(
        FilePreview,
        {
          props: customProps.value,
          on: {
            cancel
          }
        },
        {}
      )
    }
  }
})
