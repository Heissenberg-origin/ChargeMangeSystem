import http from './axios'

const jsonConfig = {
  headers: {
    'Content-Type': 'application/json'
  }
}

export function getAllRegisters() {
  return http.get('/registration/findall', jsonConfig)
    .then(response => {
      // 修改响应处理逻辑
      if (response.data && response.data.code === '200') {
        return response.data // 直接返回整个响应数据
      }
      throw new Error(response.data?.message || '获取挂号信息列表失败')
    })
    .catch(error => {
      console.error('获取挂号信息列表错误:', error)
      throw error
    })
}
export function cancelRegistration(regId) {
  return http.put(`/registration/handlecancel/${regId}`)
    .then(() => {
      // 由于后端没有返回值，直接返回成功状态
      return { code: '200', message: '操作成功' }
    })
    .catch(error => {
      console.error('取消挂号错误:', error)
      throw error
    })
}

export function getRegisterById(regId) {
  return http.get(`/registration/querybyId/${regId}`, jsonConfig)
    .then(response => {
      if (response.data && response.data.code === '200') {
        return response.data
      }
      throw new Error(response.data?.message || '获取挂号详情失败')
    })
    .catch(error => {
      console.error('获取挂号详情错误:', error)
      throw error
    })
}

export function processRegistrationPayment(regId, dealerId, paymentType) {
  return http.put(`/registration/handlepayment/${regId}/${dealerId}/${paymentType}`)
   
}
export function createRegistration(registrationInfo) {
  return http.post('/registration', registrationInfo, jsonConfig)
}
export function getRegistrationsByHealthCardId(regHcardId) {
  return http.get(`/registration/querybyhcard/${regHcardId}`, jsonConfig)
    .then(response => {
      // 确保返回的数据结构正确
      if (response.data && response.data.data) {
        return response.data.data // 直接返回data数组
      }
      throw new Error(response.data?.message || '未获取到有效数据')
    })
    .catch(error => {
      console.error('获取挂号信息失败:', error)
      throw error // 继续抛出错误以便组件处理
    })
}
