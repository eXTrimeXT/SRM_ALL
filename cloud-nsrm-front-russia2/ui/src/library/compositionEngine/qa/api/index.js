/**
 * @description 寻源质疑澄清
 * @author
 */
import http from '@/utils/axios/http'

//  质疑
export const souQuestionApi = {
  //  查询当前供应商下面的项目
  getSouProjectList: data =>
    http({
      url: '/api-sou/vendor/qa/souOrder/page',
      method: 'POST',
      data,
      loading: false
    })
}

// 澄清
export const souAnswerApi = {

}

