import http from './axios'

const jsonConfig = {
  headers: {
    'Content-Type': 'application/json'
  }
}

export function getDoctorByDeptId(depId) {
  return http.get(`doctor/getBydepid/${depId}`, jsonConfig)

}

export function getDoctorById(docId) {
  return http.get(`/doctor/getById/${docId}`, jsonConfig)
    .then(response => {
      if (response.data && response.data.code === '200') {
        return response.data.data
      }
      throw new Error(response.data?.message || '获取医生信息失败')
    })
    .catch(error => {
      console.error('获取医生信息错误:', error)
      throw error
    })
}

export function updateDoctor(docId, doctorData) {
  return http.put(`/doctor/update/${docId}`, doctorData, jsonConfig)
    .then(response => {
      if (response.data && response.data.code === '200') {
        return response.data.data
      }
      throw new Error(response.data?.message || '更新医生信息失败')
    })
    .catch(error => {
      console.error('更新医生信息错误:', error)
      throw error
    })
}

export function deleteDoctor(docId) {
  return http.delete(`/doctor/delete/${docId}`, jsonConfig)
    .then(response => {
      if (response.data && response.data.code === '200') {
        return response.data.data
      }
      throw new Error(response.data?.message || '删除医生失败')
    })
    .catch(error => {
      console.error('删除医生错误:', error)
      throw error
    })
}