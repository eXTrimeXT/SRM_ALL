<template>
  <el-container
    class="the_contractTemplatePreview_wrapper"
    direction="vertical"
  >
    <el-main>
      <el-alert
        :title="$t('contractMod.alertEdit')"
        type="warning"
        show-icon
      />
      <div class="button-group">
        <el-button
          v-if="!isEditing"
          type="primary"
          :disabled="!eidtable"
          @click="edit"
        >
          {{ $t("contractMod.enterEditMode") }}
        </el-button>
        <el-button
          v-else
          type="primary"
          @click="preview"
        >
          {{
            $t("contractMod.exitEditMode")
          }}
        </el-button>
        <el-button
          type="primary"
          @click="printPdf"
        >
          {{
            $t("route.pdfPrint")
          }}
        </el-button>
      </div>
      <div
        id="preview_wrapper"
        ref="preview_wrapper"
        class="preview_wrapper"
      >
        <div
          id="print"
          ref="markedContent"
          style="position: relative"
        />
      </div>
    </el-main>
    <iframe
      ref="iframe"
      style="display: none"
      :src="pdfUrl"
    />
  </el-container>
</template>

<script>
import Parser from 'modb@/contractManagement/views/contractManager/parser'
import axios from 'axios'
import { sysPrefix } from '@/config/ipConfig'
import { getToken } from '@/utils/auth'
import { getMenuInfo } from '@/utils/menu-auth'

export default {
  name: 'Preview',
  components: {},
  data () {
    return {
      eidtable: false,
      pdfUrl: '',
      materialListData: [
        // {
        //   sourceNumber: 123,
        //   sourceLineNumber: 1,
        //   materialCode: "CODE123",
        //   materialName: "test",
        //   categoryName: "name",
        //   taxedPrice: 12,
        //   contractQuantity: 100,
        //   amount: 1234,
        //   unitName: "个"
        // }
      ],
      mergeForm: {
        contractClass: 'NPM',
        contractCode: 'HT2021010100001',
        certificateNo: 'ZS2021010100001',
        partyA: '广东美云智数科技有限公司'
      },
      partnerData: [

      ],
      editorInstance: null,
      childContext: null,
      flag: 'preview',
      html: null
    }
  },
  computed: {
    isEditing () {
      return !!(this.childContext || {}).editable
    }
  },
  mounted () {
    this.compile()
  },
  methods: {
    edit () {
      const { content } = this.$attrs.params.row
      this.childContext.editable = true
    },
    preview () {
      this.childContext.editable = false
    },
    addWatermark (pdf, name, options) {
      const {
        watermark_height,
        watermark_width,
        watermark_x = 0.2,
        watermark_y = 0.2,
        watermark_y_space = 1.5,
        watermark_x_space = 1.5
      } = options
      const angle = 45
      for (let i = 0; i < 10; i++) {
        const y = watermark_y + (watermark_y_space + watermark_height) * i
        for (let j = 0; j < 10; j++) {
          const x = watermark_x + (watermark_width + watermark_x_space) * j
          console.log('[x, y]', x, y)
          pdf.text(name, x, y, { angle })
        }
      }
    },
    getParagraphs (element) {
      const childNodes = element.childNodes
      const count = Math.floor(childNodes.length / 130)
      const html = Array.from(childNodes).map(node => node.outerHTML)
      return { count, html }
    },
    group (array, subGroupLength) {
      let index = 0
      let newArray = []
      while (index < array.length) {
        newArray.push(array.slice(index, (index += subGroupLength)))
      }
      return newArray
    },
    printPdf () {
      this.getPdfFile(true)
    },
    async getPdfFile (flag = false) {
      let htmlBody = this.$refs.preview_wrapper.innerHTML
      // 替换分页符
      const breakPageMatcher = /_ueditor_page_break_tag_/g // 匹配分页符号
      htmlBody = htmlBody.replace(breakPageMatcher, ($0, $1) => {
        return '<div class="breakPage" style="break-after: page;"></div>'
      })
      const res = await axios({
        url: '/egg/upload',
        method: 'POST',
        loading: true,
        data: {
          options: {
            format: 'a4',
            margin: {
              left: '1cm',
              top: '1cm',
              right: '1cm',
              bottom: '1cm'
            }
          },
          htmlString: '<div style="page-break-inside: avoid;overflow: hidden;font-family: simsun;">' + htmlBody + '</div>'
        },
        responseType: 'arraybuffer'
      })
      console.log('[ArrayBuffer]', res)
      const blob = new Blob([res.data], { type: 'application/pdf' })

        const formData = new FormData()
        // formData.append("file", file);
        formData.append('file', blob, 'myfile.pdf')
        let menuInfo = getMenuInfo()
        const pdf = await axios({
          url: `${sysPrefix()}/api-base/pdf/pdfAddWatermark`,
          method: 'POST',
          data: formData,
          headers: {
            Authorization: 'Bearer ' + getToken(),
            contentType: 'form-data',
            'X-Fun-Info': menuInfo.secretKey
          },
          responseType: 'arraybuffer',
          loading: true
        })
        console.log('[pdf]', pdf.data instanceof ArrayBuffer)
        let blobs = new Blob([pdf.data], { type: 'application/pdf' })
        if (flag) {
          this.pdfUrl = URL.createObjectURL(blobs)
          setTimeout(() => {
            this.$refs.iframe.contentWindow.print()
          }, 1000)
        }
        return blobs
    },
    compile () {
      console.log('----compile----')
      const { content } = this.$attrs.params.row
      if (!content) {
        return
      }

      let contentBody = content
      // 替换分页符
      const breakPageMatcher = /_ueditor_page_break_tag_/g // 匹配分页符号
      contentBody = contentBody.replace(breakPageMatcher, ($0, $1) => {
        return '<div class="breakPage" style="break-after: page;"></div>'
      })
      // { generateComponent, replacer }
      const { vueTemplate, elementCodes } = Parser.replacer(contentBody)
      this.eidtable = !!Object.keys(elementCodes || {}).length
      const wrapper = this.$refs.preview_wrapper
      const $el = Parser.generateComponent({
        html: vueTemplate,
        elemKeys: elementCodes,
        context: this,
        wrapper
      })
      this.$refs['markedContent'].appendChild($el)
    }
  }
}
</script>
<style scoped lang="scss">
.the_contractTemplatePreview_wrapper {
  .button-group {
    padding: 10px;
  }
  .preview_wrapper {
    padding: 10px;
    width: 800px;
    margin: 0 auto;
  }
}
</style>
