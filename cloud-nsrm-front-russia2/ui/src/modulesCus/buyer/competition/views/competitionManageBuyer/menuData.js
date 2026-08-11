import i18n from '@/lang'
// 菜单
export const menuData = [
// 询价立项
  {
    id: 'projectInitiation',
    label: '竞价项目立项',
    isSubmenu: true,
    children: [
    // 项目信息
      {
        id: 'projectInfo',
        label: i18n.t('cusEntry.competition.menu.projectInfo'),
        key: 'projectInfo'
      },
      // 项目需求
      {
        id: 'requireInfo',
        label: i18n.t('cusEntry.competition.menu.requireInfo'),
        key: 'requireInfo'
      },
      // 邀请供应商
      {
        id: 'inviteVendor',
        label: i18n.t('bidMod.inviteSupplier'),
        key: 'inviteVendor'
      }
    ]
  },
  // 报名管理
  {
    id: 'signUpManagementParent',
    label: i18n.t('bidMod.entryManagement'),
    isSubmenu: true,
    children: [
      // 报名详情
      {
        id: 'signUpManagement',
        label: i18n.t('cusEntry.competition.menu.signUpManagement'),
        key: 'signUpManagement'
      }
    ]
  },
  // 竞/评标
  {
    id: 'competitiveTender',
    label: i18n.t('cusEntry.competition.menu.competitiveTender'),
    isSubmenu: true,
    children: [
      // 评选
      {
        id: 'evaluation',
        label: i18n.t('cusEntry.competition.menu.evaluation'),
        key: 'evaluation'
      }
    ]
  },
  // 定标
  {
    id: 'calibrate',
    label: i18n.t('cusEntry.competition.menu.calibrate'),
    isSubmenu: true,
    children: [
      // 编制定标结果
      {
        id: 'editCalibrateResult',
        label: i18n.t('cusEntry.competition.menu.editCalibrateResult'),
        key: 'editCalibrateResult'
      },
      // 中标通知
      {
        id: 'bidNotice',
        label: i18n.t('cusEntry.competition.menu.noticeOfAward'),
        key: 'bidNotice'
      },
      // 归档
      {
        id: 'archives',
        label: i18n.t('cusEntry.competition.menu.file'),
        key: 'archives'
      }
    ]
  }
]
