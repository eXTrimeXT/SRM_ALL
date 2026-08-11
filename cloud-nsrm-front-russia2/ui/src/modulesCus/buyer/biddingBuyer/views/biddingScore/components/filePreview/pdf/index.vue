<template>
  <div class="pdf-wrap">
    <div class="pdf-handler-bar">
      <el-button icon="el-icon-zoom-in" class="handle-icon" :title="$t('cusEntry.supplement20250205.enlarged')" @click="handleZoom('in')" />
      <el-button icon="el-icon-zoom-out" class="handle-icon" :title="$t('cusEntry.supplement20250205.narrow')" @click="handleZoom('out')" />
      <i class="devider" />
      <el-button icon="el-icon-c-scale-to-original" class="handle-icon" :title="$t('cusEntry.supplement20250205.resetSize')" @click="handleReset" />
      <i class="devider" />
      <el-button icon="el-icon-arrow-up" class="handle-icon arrow" :title="$t('common.prevPage')" @click="handlePage('prev')" />
      <el-button icon="el-icon-arrow-down" class="handle-icon arrow" :title="$t('common.nextPage')" @click="handlePage('next')" />
    </div>
    <div class="pdf-total-wrap" id="pdf-total-wrap" ref="pdfWrapDom">
      <div class="pdf-scroll-box" ref="pdfScrollDom">
        <div class="pdf-page-box"
             :page="`pdf-page-${idx + 1}`"
             v-for="(item, idx) in pdfArr.list" :key="idx">
          <div class="loading-page" v-if="!item['data'] || item['error']"
               :style="`width:${item.width * (contentWidth / item.width)}px; height:${item.height * (contentWidth / item.width)}px; margin: 0 auto; `"
               :page-data="JSON.stringify({ 'page': item.page, 'docId': item.fileId, 'width': item.width, 'height': item.height })">
            <template v-if="!item['error']">
              <!-- 文档加载中，请稍后... -->
              <i class="el-icon-loading" style="margin-right: 10px; fontSize:18px;" />{{ $t('cusEntry.supplement20250205.later') }}
            </template>
            <template v-else>
              <!-- 文档加载失败 -->
              <span style="color: #ff5a01;">{{ item['error'] }}</span>
            </template>
          </div>
          <div class="pdf-page"
               :page-data="JSON.stringify({ 'page': item.page, 'docId': item.fileId, 'width': item.width, 'height': item.height })"
               v-else>
            <pdfvuer :src="item['data']"
                     :annotation="true"
                     :resize="false"
                     :text="true"
                     :style="`width:${item.width * (contentWidth / item.width)}px; height:${item.height * (contentWidth / item.width)}px; margin: 0 auto; position: relative; `"
                     :scale="pdfScale"
                     @error="pdfError($event, idx)"
                     ref="pdfVuer" />
          </div>
        </div>
      </div>
    </div>

  </div>
</template>
<script >
import { defineComponent } from 'vue';
export default defineComponent({
  name: 'ComPdf',
});
</script>
<script setup>
import pdfvuer from './com-pdfvuer/com-pdfvuer'
import { sysPrefix } from '@/config/ipConfig'
import { systemUrl } from '@/config/sysConfig'
import { getToken } from '@/utils/auth'

const props = defineProps({
  fileData: Object
})

let pdfScale = ref('page-width')
let zoomRatio = ref(1)
let pdfArr = reactive({ list: [] })

// 监听 是否出现 
let loadedPage = [] // 已经加载的页码
let observerList = [] // observer实例列表
let currentPage = 0 // 当前浏览到 第几页
let currentPageSize = 0 // 当前文档总页码
const bindObserver = (dom) => {
  // const element = document.getElementById('my-element');
  const observer = new IntersectionObserver(entries => {
    if (entries[0].isIntersecting) {
      // 如果已经加载过则不进行重复请求加载
      let { page, docId } = JSON.parse(dom.getAttribute('page-data'))
      // console.log(currentPage, page)
      currentPage = page
      if (loadedPage.indexOf(page * 1) > -1) {
        return
      }
      // Element is in viewport
      // console.log(`加载 第${page}页文档`)
      loadedPage.push(page * 1)
      // console.log('已加载文档页码集合', loadedPage)
      // 加载某页文档
      loadingPdfByNum(page, docId)
    } else {
      // Element is not in viewport
      // console.log('not in viewport')
    }
  }, {
    threshold: [0.1],
    // rootMargin: 1 * zoomVal.value * 10 + 'px'
  });
  observer.observe(dom);
  observerList.push(observer)
}

// 分页请求 文档

// `${systemUrl}${sysPrefix()}/api-sou/fileCheck/api/findPageFile`,
const Authorization = getToken() ? 'Bearer ' + getToken() : '' // token
const loadingPdfByNum = (nowPage, docId) => {
  return new Promise((resolve, reject) => {
    fetch(
      `${sysPrefix()}/api-sou/fileCheck/api/findPageFile`,
      {
        method: 'post',
        headers: {
          responseType: 'blob',
          'Content-Type': 'application/json',
          Authorization: Authorization
        },
        body: JSON.stringify({
          pageNum: nowPage,
          projectId: currentProjectId,
          orderDocIds: [
            docId
          ]
        })
      }
    ).then(async res => {
      console.log(res)
      if (res.status === 200) {
        const resBlob = await res.blob()
        let blob = window.URL.createObjectURL(resBlob)
        console.log(blob)
        loadedPage.push(nowPage * 1)
        // 只画一个框
        pdfArr.list[nowPage - 1]['data'] = blob // 显示 pdf的这一页
        let timer = setTimeout(() => {
          resolve(blob)
          clearTimeout(timer)
        }, 1000)
      } else {
        // console.log('文档加载失败')
      }
    })
  })
}

const renderLoadingPage = (pageSize, fileId, height, width) => {
  return new Promise((resolve, reject) => {
    // 销毁实例及dom
    removeInstance()
    // const { ele, area, total } = fileItem
    // currentPage = area[4]
    // 根据页码加载 n个空白页面带loading动画
    // let height = area[5] || 841.92, width = area[6] || 595.32, page = area[4] // 给宽高 默认值 容错 有时候 不给返回 宽高 导致loading都挤在一起的问题
    for (let idx = 1; idx <= pageSize; idx++) {
      pdfArr.list.push(
        {
          height,
          width,
          page: idx,
          data: '',
          error: '',
          fileId,
          projectId: currentProjectId,
        }
      )
      if (idx === pageSize) { // 循环结束
        observerList = []
        resolve()
      }
    }
  })
}

// 滚动到 标注处
const handlePdfLink = (params) => {
  // console.log('定位到指定 标注')
  const pageDiv = document.querySelector(`div[class="annotion-box"]`)
  if (!pageDiv) return
  pageDiv.scrollIntoView({
    behavior: "smooth",
    block: "center",
    // inline: "start"
  });
}

let currentProjectId = null
// let currentFileId = ref(null)
const chooseAnnotion = async (obj) => {
  const { fileId, projectId, coordinate, total_pages, page_width, page_height, page } = obj
  console.log('fileId:', fileId, 'coordinate', coordinate, 'projectId', projectId)
  currentProjectId = projectId
  const pageSize = total_pages
  currentPageSize = total_pages
  // const page = page
  const height = page_height
  const width = page_width
  await renderLoadingPage(pageSize, fileId, height, width)
  await bindListener()
  pdfArr.list[page - 1]['height'] = page_height
  pdfArr.list[page - 1]['width'] = page_width
  jumpPdfPage(page)

  // 删除 历史 标注框
  let dom = document.querySelectorAll('div[class="annotion-box"]')
  if (dom.length) {
    Array.from(dom).map(item => {
      item.remove()
    })
  }
  if (!coordinate.length) return
  let timer = setTimeout(() => {
    clearTimeout(timer)
    coordinate.map(item => {
      drawRect(item, page)
    })
  }, 1800)
}

const jumpPdfPage = (page = currentPage, isSmooth = false) => {
  // console.log('定位到指定 页码', page)
  const pageDom = document.querySelector(`div[page="pdf-page-${page}"]`)
  if (pageDom) {
    pageDom.scrollIntoView({
      behavior: isSmooth ? "smooth" : "instant", // smooth 会引发 current Page错乱
      block: "center",
      inline: "center"
    });
  }
}

const drawRect = (list, page) => {
  // const _dom = document.getElementById(`pdfDom-${props.dataDir}`)
  // if (!_dom) return
  const pageDiv = document.querySelector(`div[page="pdf-page-${page}"]`)
  const isError = pdfArr.list[page - 1]['error']
  if (!pageDiv || isError.length) return
  // const posArr = props.drawData.coordinate
  // if (!posArr) return
  // x0 y0 左上
  // x1 y1 右下

  // console.log('坐标', list)
  let x0 = list[0]
  let y0 = list[1]
  let x1 = list[2]
  let y1 = list[3]
  let _w = (x1 - x0)
  let _h = (y1 - y0)
  let _x = list[0]
  let _y = list[1]
  let pageHeight = pdfArr.list[0]['height']
  let pageWidth = pdfArr.list[0]['width']
  // console.log('起始坐标：', `(${_x},${_y})`, '长度：', _w, '高度:', _h)
  // originPdfData.width = _width
  // originPdfData.height = _height
  const coverDiv = document.createElement('div');
  coverDiv.setAttribute('style', `width:${_w * (contentWidth.value / pageWidth)}px;height:${_h * (contentWidth.value / pageWidth)}px;background:#ff5a0152;position:absolute;top:${_y * (contentWidth.value / pageWidth)}px;left:${_x * (contentWidth.value / pageWidth)}px;z-index:999;`)
  coverDiv.setAttribute("annotion-data", JSON.stringify({ width: _w, height: _h, top: _y, left: _x, pageWidth }))
  coverDiv.setAttribute("class", `annotion-box`)

  nextTick(() => {
    const posDiv = pageDiv.querySelector('#viewerContainer')
    posDiv.append(coverDiv)
  })
}

let pdfVuer = ref(null)
// 删除各种实例
const removeInstance = () => {
  if (pdfVuer.value) {
    console.log(pdfVuer.value)
    // pdfVuer.value.destroy()
    pdfVuer.value.map(item => {
      // console.log(item)
      item.pdf._transport.destroy() // 销毁历史 pdfvuer实例
    })
  }
  // 清空observer 对象list
  for (let idx = 0; idx < observerList.length; idx++) {
    observerList[idx].disconnect();
  }
  loadedPage = []
  pdfArr.list = []
  // currentPdfObj = {}
}

const bindListener = () => {
  return new Promise((resolve, reject) => {
    let loadingDom = document.querySelectorAll(`div[class="loading-page"]`)
    if (loadingDom.length) { // 已绑定过则不需要二次绑定
      Array.from(loadingDom).map((ele, idx) => {
        // ele.remove()
        bindObserver(ele)
        if (loadingDom.length >= idx - 1) {
          resolve()
        }
      })
    }
  })
}

let resizeTimeout = null;
let endFlag = true
let contentWidth = ref(595)
const resizeObserver = new ResizeObserver(entries => {
  if (!endFlag) return
  endFlag = false
  resizeTimeout = setTimeout(() => {
    // 代码待优化
    clearTimeout(resizeTimeout);
    // 如果处于关闭状态 则不做操作
    // 解决 初次加载回重复画坐标的问题
    // if (!props.pdfOpen || !pdfDomLoaded.value) return
    for (let entry of entries) {
      // console.log('doc width changed:', entry.contentRect.width);
      const width = entry.contentRect.width
      // console.log('检测到宽度改变：', width)
      contentWidth.value = width - 40
      if (!width) return
      // if() {
      //   pdfScrollDom.value.style.width = '100%'
      // }
      // 在这里执行宽度变化结束后的操作
      nextTick(() => {
        if (pdfVuer.value) {
          pdfVuer.value.map((item, idx) => {
            item.drawScaled('page-width')
            if (pdfVuer.value.length >= idx - 1) {
              reRenderRect()
            }
          })
        }
      })
    }
    endFlag = true
  }, 0) // 设置延迟时间，单位为毫秒 500毫秒触发一次 用于节流
})

// 重新画框
const reRenderRect = () => {
  // 获取当前 rect 值
  // drawRect(annotation)
  return new Promise((resolve, reject) => {
    let dom = document.getElementById(`pdf-total-wrap`)
    if (!dom) {
      resolve()
      return
    }
    let domList = dom.querySelectorAll('div[class="annotion-box"]')
    if (!domList.length) return resolve()
    // 获取 dom 属性 重新计算赋予新 值
    Array.from(domList).map((item, idx) => {
      // console.log(item)
      // console.log(item.getAttribute('annotion-data'))
      // console.log(JSON.parse(item.getAttribute('annotion-data')))
      const { width, height, left, top, pageWidth } = JSON.parse(item.getAttribute('annotion-data'))
      const ratio = contentWidth.value / pageWidth
      item.style.width = width * ratio + 'px'
      item.style.height = height * ratio + 'px'
      item.style.top = top * ratio + 'px'
      item.style.left = left * ratio + 'px'
      //获取当前 坐标及大小
      if (domList.length >= idx - 1) {
        resolve()
      }
    })
  })
}

const pdfWrapDom = ref()
let zoomVal = ref(1)
const handleZoom = (type, zoomStep = 0.2) => {
  if (type === 'in') {
    zoomVal.value += zoomVal.value * zoomStep
  }
  if (type === 'out') {
    if (zoomVal.value <= 0.5) return
    zoomVal.value -= zoomVal.value * zoomStep
  }
  if (type === 'reset') {
    zoomVal.value = 1
    pdfScrollDom.value.style.width = '100%'
    return
  }
  // console.log('缩放倍数：', zoomVal.value)
  if (pdfVuer.value) {
    pdfScrollDom.value.style.width = originDomWidth * zoomVal.value + 'px'
    // 让 横向滚动条滚动到中部位置（暂时解决 无法选中横向滚动条的问题）
    const scrollDom = pdfWrapDom.value
    scrollDom.scrollLeft = (scrollDom.scrollWidth - scrollDom.clientWidth) / 2
    jumpPdfPage(currentPage, true)
  }
}

const handlePage = (type) => {
  currentPage = type === 'next' ? (currentPage + 1) : (currentPage - 1)
  if (currentPage < 1) currentPage = 1
  if (currentPage > 48) currentPage = 48
  jumpPdfPage(currentPage, true)
}

const handleReset = () => {
  handleZoom('reset')
}

const pdfError = (err, idx) => {
  // console.log('文档出错：', err, idx)
  pdfArr.list[idx]['error'] = this.$t('cusEntry.supplement20250205.loadingFailed') // 文档加载失败
}

watch(
  () => props.fileData,
  (obj) => {
    // console.log('propsPdfInfo', obj)
    if (obj.fileId) chooseAnnotion(obj)
  },
  { deep: true, immediate: true }
)


const pdfScrollDom = ref()
let originDomWidth = 0
onMounted(async () => {
  resizeObserver.observe(pdfScrollDom.value)
  originDomWidth = pdfScrollDom.value.offsetWidth
})

onUnmounted(() => {
  loadedPage = []
  // currentPdfObj = {}  
  currentPage = 0
  resizeObserver.disconnect()
})
</script>

<style  scoped lang="less">
.pdf-wrap {
  height: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;

  .pdf-handler-bar {
    width: 100%;
    height: 45px;
    background: #EFF0F4;
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);
    display: flex;
    flex-direction: row;
    flex-wrap: nowrap;
    align-items: center;
    padding: 8px 0;
    justify-content: center;

    .devider {
      // position: absolute;
      margin: 0 10px;
      height: 100%;
      width: 1px;
      background: rgba(0, 0, 0, 0.05);

    }

    .anticon {
      border-radius: 4px;
      font-size: 20px;
      padding: 6px;
      cursor: pointer;

      &:hover {
        background: rgba(0, 0, 0, .04);
      }
    }

    /deep/ .handle-icon {
      padding: 5px 7px;

      i {
        font-size: 18px;
      }

      &.arrow {
        padding: 6px 7px;
      }

      &.arrow i {
        font-size: 16px;
      }
    }
  }
}

.pdf-total-wrap {
  // height: 100%;
  flex: 1;
  // display: flex;
  // flex-direction: column;
  background: #edeff2;
  overflow: scroll;
  // overflow-y: scroll;
  // overflow-x: hidden;

  .pdf-page-box {
    width: 100%;
    // height: 100%;
    margin-top: 10px;

    .pdf-page {
      width: 100%;
      // height: 100%;
    }

    .loading-page {
      height: 1000px;
      width: 100%;
      display: flex;
      flex-direction: row;
      flex-wrap: nowrap;
      justify-content: center;
      align-items: center;
      background-color: #fff;
    }
  }

  .pdf-scroll-box {
    margin: 0 auto;
  }
}
</style>