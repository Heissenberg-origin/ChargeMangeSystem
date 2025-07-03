import http from './axios'

const jsonConfig = {
  headers: {
    'Content-Type': 'application/json'
  }
}

// 挂号相关API
export function getRegistrationById(regId) {
  return http.get(`/registration/querybyId/${regId}`, jsonConfig)
}

// 收费项目相关API

export function createChargeItem(itemData) {
  return http.post('/chargeitem/create', itemData, jsonConfig)
}

export function getAllChargeItems() {
  return http.get('/chargeitem/findall', jsonConfig)
}



export function updateChargeItem(id, itemData) {
  return http.put(`/chargeitem/updateById/${id}`, itemData, jsonConfig)
}

export function deleteChargeItem(id) {
  return http.delete(`/chargeitem/deleteById/${id}`, jsonConfig)
}

// 收费相关API
export function createCharge(chargeData) {
  return http.post('/charge/create', chargeData, jsonConfig)
}
export function getAllChargeItemTypes() {
  return http.get('/chargeitem/getalltypes', jsonConfig)
}

// 根据类型查询收费项目
export function getChargeItemsByType(type) {
  return http.get(`/chargeitem/queryBytype/${type}`, jsonConfig)
}
export function createPrescription(prescriptionInfos) {
  return http.post('/prescription/create', prescriptionInfos,jsonConfig)
}
// 根据门诊ID获取处方列表
export function getPrescriptionsByRegId(registrationId) {
  return http.get(`/prescription/queryByRegistrationId/${registrationId}`)
}

// 处方缴费
export function payPrescription(sequence, dealerId, paymentType) {
  return http.put(`/prescription/pay/${sequence}/${dealerId}/${paymentType}`, jsonConfig)
}