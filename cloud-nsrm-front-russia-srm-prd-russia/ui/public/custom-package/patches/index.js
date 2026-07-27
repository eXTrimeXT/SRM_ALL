export default (schemaKey) => {
  console.log(schemaKey, 'schemaKey')

  const schmeaMap = {
    // list: () => import('./list.js'),
    ContractHead: () => import('./contractHead.js'),
    ContractHeadForm: () => import('./contractHeadForm.js')
  }

  return schmeaMap[schemaKey]
}
