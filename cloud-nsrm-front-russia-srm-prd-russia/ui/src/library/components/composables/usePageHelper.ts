import { useSetupContext } from './useSetupContext'
import { useI18n } from './useI18n'

export const usePageHelper = (): any => {
  const { app, currentInstance } = useSetupContext()
  const { t } = useI18n()

  const emitTabAdd = (config: any) => currentInstance.$emit('tab-add', config)

  const emitTabRemove = (config: any) => currentInstance.$emit('tab-remove', config)

  const getCurrentUserRole = () => (app as any).$store.getters.userType

  const getCurrentUserInfo = () => (app as any).$store.getters.userInfo

  const buyer = () => getCurrentUserRole() === 'BUYER'
  const vendor = () => getCurrentUserRole() === 'VENDOR'

  const getGlobalNickname = () => (app as any).$store.getters.userInfo?.username

  const createdUserIsCurrentUserByRow = (row: any, field = 'createdBy') => {
    return getGlobalNickname() === row[field]
  }

  const confirmMessage = (message: string, options = {}) =>
    app.$confirm(message, {
      confirmButtonText: app.$t('common.confirm'),
      cancelButtonText: app.$t('common.cancel'),
      type: 'warning',
      ...options,
    } as any)

  const confirmDeleteMessage = () => {
    return confirmMessage(app.$t('common.confirmDelete') as any)
  }

  const authorityVisible = (code: any) => {
    let { buttonPermission = {} } = getCurrentUserInfo()
    if (buttonPermission[code]) {
      return buttonPermission[code] == 'Y'
    } else {
      return true
    }
  }

  const eqY = (v: any) => v === 'Y'
  const eqN = (v: any) => v === 'N'

  return {
    app,
    // @ts-ignore
    http: app.$http,
    currentInstance,

    eqY,
    eqN,

    t,
    emitTabAdd,
    emitTabRemove,
    getCurrentUserRole,
    getGlobalNickname,
    buyer,
    vendor,
    createdUserIsCurrentUserByRow,
    confirmDeleteMessage,
    confirmMessage,
    getCurrentUserInfo,
    authorityVisible
  }
}
