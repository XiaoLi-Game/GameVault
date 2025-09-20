import request from './index'

/**
 * 资产管理API
 */
export const assetApi = {
  /**
   * 获取资产列表
   */
  getAssets(params = {}) {
    return request({
      url: '/assets',
      method: 'get',
      params
    })
  },

  /**
   * 获取资产详情
   */
  getAssetDetail(id) {
    return request({
      url: `/assets/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建资产
   */
  createAsset(data) {
    return request({
      url: '/assets',
      method: 'post',
      data
    })
  },

  /**
   * 更新资产
   */
  updateAsset(id, data) {
    return request({
      url: `/assets/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除资产
   */
  deleteAsset(id) {
    return request({
      url: `/assets/${id}`,
      method: 'delete'
    })
  },

  /**
   * 下载资产
   */
  downloadAsset(id) {
    return request({
      url: `/assets/${id}/download`,
      method: 'get',
      responseType: 'blob'
    })
  },

  /**
   * 上传资产文件
   */
  uploadAsset(formData) {
    return request({
      url: '/assets/upload',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  /**
   * 获取资产统计信息
   */
  getAssetStats() {
    return request({
      url: '/assets/stats',
      method: 'get'
    })
  }
}

export default assetApi
