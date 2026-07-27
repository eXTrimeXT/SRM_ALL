import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

/* eslint-disable quotes */
export default {
  minOrderNum: {
    type: 'string',
    title: "{{$t('dataConfMod.orderQuantityMinimum')}}",
    'x-decorator': 'FormItem'
  },
  minInventory: {
    type: 'string',
    title: "{{$t('dataConfMod.minimumSafetyInventory')}}",
    'x-decorator': 'FormItem'
  },
  brand: {
    type: 'string',
    title: "{{$t('dataConfMod.band')}}",
    'x-decorator': 'FormItem'
  },
  innerBoxMinPackNum: {
    type: 'string',
    title: "{{$t('dataConfMod.minimumPackingQuantity')}}",
    'x-decorator': 'FormItem'
  },
  outerBoxPageNum: {
    type: 'string',
    title: "{{$t('dataConfMod.maxPackingCarton')}}",
    'x-decorator': 'FormItem'
  },
  placeOrigin: {
    type: 'string',
    title: "{{$t('purchase.PlaceOfOrigin')}}",
    'x-decorator': 'FormItem'
  },
  deliveryTime: {
    ...yearMonthDaySelectorSegment,
    title: "{{$t('purchase.DeliveryTime')}}",
    'x-decorator': 'FormItem'
  },
  packNum: {
    type: 'string',
    title: "{{$t('purchase.NumberOfPackages')}}",
    'x-decorator': 'FormItem'
  },
  grossWeight: {
    type: 'string',
    title: "{{$t('purchase.fullContainer')}}",
    'x-decorator': 'FormItem'
  },
  outerBoxLong: {
    type: 'string',
    title: "{{$t('purchase.OuterBoxLength')}}",
    'x-decorator': 'FormItem'
  },
  outerBoxWide: {
    type: 'string',
    title: "{{$t('purchase.OuterBoxWidth')}}",
    'x-decorator': 'FormItem'
  },
  outerBoxHide: {
    type: 'string',
    title: "{{$t('purchase.OuterBoxHeight')}}",
    'x-decorator': 'FormItem'
  },
  innerBoxLong: {
    type: 'string',
    title: "{{$t('purchase.InnerBoxLength')}}",
    'x-decorator': 'FormItem'
  },
  innerBoxWide: {
    type: 'string',
    title: "{{$t('purchase.InnerBoxWidth')}}",
    'x-decorator': 'FormItem'
  },
  innerBoxHide: {
    type: 'string',
    title: "{{$t('purchase.InnerBoxHeight')}}",
    'x-decorator': 'FormItem'
  },
  innerBoxWeight: {
    type: 'string',
    title: "{{$t('purchase.InnerBoxWeight')}}",
    'x-decorator': 'FormItem'
  },
  innerBoxPackNum: {
    type: 'string',
    title: "{{$t('purchase.NumberOfInnerCases')}}",
    'x-decorator': 'FormItem'
  }
}
