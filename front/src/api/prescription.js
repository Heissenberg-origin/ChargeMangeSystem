import http from './axios'

// 统一请求配置
const jsonConfig = {
  headers: {
    'Content-Type': 'application/json'
  }
}

/**
 * 获取处方统计数据
 * @param {Object} params 请求参数
 * @param {string} params.startTime 开始时间，格式：YYYY-MM-DD HH:mm:ss
 * @param {string} params.endTime 结束时间，格式：YYYY-MM-DD HH:mm:ss
 * @param {string} [params.timeType='day'] 时间类型，可选值：day/month/year
 * @param {string} [params.groupBy='department'] 分组依据，可选值：department/doctor
 * @returns {Promise} 包含统计数据的Promise
 */
export function getPrescriptionStats(params) {
  return http.post('/api/stats/prescription', {
    startTime: params.startTime,
    endTime: params.endTime,
    timeType: params.timeType || 'day',
    groupBy: params.groupBy || 'department'
  }, {
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

//获取挂号信息汇总
export function getRegistrationList(params) {
  return http.get('/registration/findall', { params })
}

export function getDailyInformation(data) {
  return http.post('/api/report/daily', data, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
}

export function getBusinessInformation(data) {
  return http.post('/api/report/business', data, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
}

//获取总收费项目列表
export function getChargeitemList(params) {
  return http.get('/chargeitem/findall', { params })
}

/**
 * 根据ID删除收费项目
 * @param {number} id 收费项目ID
 * @returns {Promise} 
 */
export function deleteChargeItem(id) {
  return http.delete(`/chargeitem/deleteById/${id}`)
}

/**
 * 根据ID更新收费项目
 * @param {number} id 收费项目ID
 * @param {object} data 更新数据
 * @returns {Promise} 
 */
export function updateChargeItem(id, data) {
  return http.put(`/chargeitem/updateById/${id}`, data,{
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

