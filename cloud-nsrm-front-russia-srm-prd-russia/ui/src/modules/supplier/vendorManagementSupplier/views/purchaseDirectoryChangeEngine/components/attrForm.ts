
/* eslint-disable quotes */
export default {
  minOrderNum: {
    type: 'number',
    title: "{{$t('dataConfMod.orderQuantityMinimum')}}",
    'x-decorator': 'FormItem'
  },
  minInventory: {
    type: 'number',
    title: "{{$t('dataConfMod.minimumSafetyInventory')}}",
    'x-decorator': 'FormItem'
  },
  brand: {
    type: 'string',
    title: "{{$t('dataConfMod.band')}}",
    'x-decorator': 'FormItem'
  },
  innerBoxMinPackNum: {
    type: 'number',
    title: "{{$t('dataConfMod.minimumPackingQuantity')}}",
    'x-decorator': 'FormItem'
  },
  outerBoxPageNum: {
    type: 'number',
    title: "{{$t('dataConfMod.maxPackingCarton')}}",
    'x-decorator': 'FormItem'
  },
  placeOrigin: {
    type: 'string',
    title: "{{$t('purchase.PlaceOfOrigin')}}",
    'x-decorator': 'FormItem'
  },
  deliveryTime: {
    type: 'number',
    title: "{{$t('purchase.DeliveryTime')}}",
    'x-decorator': 'FormItem'
  },
  packNum: {
    type: 'number',
    title: "{{$t('purchase.NumberOfPackages')}}",
    'x-decorator': 'FormItem'
  },
  grossWeight: {
    type: 'number',
    title: "{{$t('purchase.fullContainer')}}",
    'x-decorator': 'FormItem'
  },
  outerBoxLong: {
    type: 'number',
    title: "{{$t('purchase.OuterBoxLength')}}",
    'x-decorator': 'FormItem'
  },
  outerBoxWide: {
    type: 'number',
    title: "{{$t('purchase.OuterBoxWidth')}}",
    'x-decorator': 'FormItem'
  },
  outerBoxHide: {
    type: 'number',
    title: "{{$t('purchase.OuterBoxHeight')}}",
    'x-decorator': 'FormItem'
  },
  innerBoxLong: {
    type: 'number',
    title: "{{$t('purchase.InnerBoxLength')}}",
    'x-decorator': 'FormItem'
  },
  innerBoxWide: {
    type: 'number',
    title: "{{$t('purchase.InnerBoxWidth')}}",
    'x-decorator': 'FormItem'
  },
  innerBoxHide: {
    type: 'number',
    title: "{{$t('purchase.InnerBoxHeight')}}",
    'x-decorator': 'FormItem'
  },
  innerBoxWeight: {
    type: 'number',
    title: "{{$t('purchase.InnerBoxWeight')}}",
    'x-decorator': 'FormItem'
  },
  innerBoxPackNum: {
    type: 'number',
    title: "{{$t('purchase.NumberOfInnerCases')}}",
    'x-decorator': 'FormItem'
  }
}
