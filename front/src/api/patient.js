import http from './axios'

// 统一请求配置
const jsonConfig = {
  headers: {
    'Content-Type': 'application/json'
  }
}

export function registerPatient(patientData) {
  return http.post('/patient/register', patientData, jsonConfig)
}


export function queryPatients(params) {
  // GET请求通常不需要Content-Type头，参数通过URL查询字符串传递
  return http.get('/patient/query', { params })
}

export function updatePatient(healthcardId, patientData) {
  return http.put(`/patient/updateByHealthcard/${healthcardId}`, patientData, jsonConfig)
}

export function deletePatient(healthcardId) {
  return http.delete(
  `/patient/delete/${healthcardId}`,jsonConfig

  )
} 
export function recharge(data) {
  return http.post('/patient/recharge', {
    healthcardId: String(data.healthcardId), // 关键修改：参数名与后端一致
    amount: parseFloat(data.amount)
  }, jsonConfig)
}