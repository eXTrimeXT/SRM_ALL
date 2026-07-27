import { 
  dynamicCutoffTimeDiffToString, 
  parseTimeByMillisecond,
  dynamicCutoffTimeDiff,
  DynamicCutoffTimeClass
} from './src/util'
import DynamicCutoffTime from './src/dynamic-cutoff-time'

DynamicCutoffTime.install = Vue => Vue.component(DynamicCutoffTime.name, DynamicCutoffTime)

// 对外支持两个方法，请不要直接去调src目录下的util
export {
  dynamicCutoffTimeDiffToString,
  parseTimeByMillisecond,
  dynamicCutoffTimeDiff,
  DynamicCutoffTimeClass
}

export default DynamicCutoffTime
