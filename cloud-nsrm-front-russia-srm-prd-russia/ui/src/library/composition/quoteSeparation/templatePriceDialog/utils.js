import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'

// 根据业务类型和报价类型，返回API
export const getApiByBusinessType = type => {
  const map = new Map([
    // 简易询价
    [
      BUSINESS_TYPE_ENUM.INQUIRY_LTS,
      {
        buyer: {
          quoteTemp: '/api-sou/buyer/inq/order/quoteTemp', //采购商查看、价格计算
          importQuoteTemp: '/api-sou/buyer/inq/order/import/quoteTemp', // 报价导入
          downloadQuoteTemp: '/api-sou/buyer/inq/order/download/quoteTemp' // 报价模板下载
        },
        vendor: {
          quoteTemp: '/api-sou/vendor/inq/order/quoteTemp', //采购商查看、价格计算
          importQuoteTemp: '/api-sou/vendor/inq/order/import/quoteTemp', // 报价导入
          downloadQuoteTemp: '/api-sou/vendor/inq/order/download/quoteTemp' // 报价模板下载
        }
      }
    ],
    // 招标
    [
      BUSINESS_TYPE_ENUM.BIDDING_LTS,
      {
        buyer: {
          quoteTemp: '/api-sou/buyer/bid/order/quoteTemp', //采购商查看、价格计算
          importQuoteTemp: '/api-sou/buyer/bid/order/import/quoteTemp', // 报价导入
          downloadQuoteTemp: '/api-sou/buyer/bid/order/download/quoteTemp' // 报价模板下载
        },
        vendor: {
          quoteTemp: '/api-sou/vendor/bid/order/quoteTemp', //采购商查看、价格计算
          importQuoteTemp: '/api-sou/vendor/bid/order/import/quoteTemp', // 报价导入
          downloadQuoteTemp: '/api-sou/vendor/bid/order/download/quoteTemp' // 报价模板下载
        }
      }
    ]
  ])
  return map.get(type)
}
