import http from './axios'

const jsonConfig = {
  headers: {
    'Content-Type': 'application/json'
  }
}

export function registerPatient(patientData) {
  return http.post('/patient/register', patientData, jsonConfig)
}


export function queryPatients(healthcardId) {
  return http.get(`/patient/querybyId/${healthcardId}`, jsonConfig)
}

export function updatePatient(healthcardId, patientData) {
  return http.put(`/patient/updateByHealthcard/${healthcardId}`, patientData, jsonConfig)
    .then(() => {
      // 不处理响应数据，只要请求成功就resolve
      return Promise.resolve()
    })
    .catch(error => {
      console.error('更新患者错误:', error)
      throw error // 仍然抛出错误以便页面处理
    })
}

export function deletePatient(healthcardId) {
  return http.delete(`/patient/delete/${healthcardId}`, jsonConfig)
}

// api/patient.js
export function recharge(healthcardId, amount) {
  return http.post(`/patient/recharge/${healthcardId}/${amount}`, {}, jsonConfig)
}

// 新增获取所有患者的函数
export function listAllPatients() {
  return http.get('/patient/listall')
}
export function settlehcard(healthcardId) {
  return http.put(`/patient/settlement/${healthcardId}`, {}, jsonConfig)
}