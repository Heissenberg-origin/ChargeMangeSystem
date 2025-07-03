import http from './axios'

const jsonConfig = {
  headers: {
    'Content-Type': 'application/json'
  }
}

export function getAllDepartments() {
  return http.get('departments/getall', jsonConfig)
    .then(response => {
      // 修改响应处理逻辑
      if (response.data && response.data.code === '200') {
        return response.data // 直接返回整个响应数据
      }
      throw new Error(response.data?.message || '获取科室列表失败')
    })
    .catch(error => {
      console.error('获取科室列表错误:', error)
      throw error
    })
}

export function getDepartmentsByName(departmentName) {
  return http.get('/departments/querybyname', {
    params: { departmentName },
    ...jsonConfig
  })
    .then(response => {
      if (response.data && response.data.code === '200') {
        return response.data // 直接返回整个响应数据
      }
      throw new Error(response.data?.message || '搜索科室失败')
    })
    .catch(error => {
      console.error('搜索科室错误:', error)
      throw error
    })
}

export function getDepartmentById(departmentId) {
  return http.get(`/departments/getById/${departmentId}`, jsonConfig)
    .then(response => {
      if (response.data && response.data.code === '200') {
        return response.data.data
      }
      throw new Error(response.data?.message || '获取科室信息失败')
    })
    .catch(error => {
      console.error('获取科室信息错误:', error)
      throw error
    })
}

export function addDepartment(departmentData) {
  return http.post('/departments/add', departmentData, jsonConfig)
    .then(response => {
      if (response.data && response.data.code === '200') {
        return response.data.data
      }
      throw new Error(response.data?.message || '添加科室失败')
    })
    .catch(error => {
      console.error('添加科室错误:', error)
      throw error
    })
}

export function updateDepartment(departmentId, departmentData) {
  return http.put(`/departments/update/${departmentId}`, departmentData, jsonConfig)
    .then(response => {
      if (response.data && response.data.code === '200') {
        return response.data.data
      }
      throw new Error(response.data?.message || '更新科室信息失败')
    })
    .catch(error => {
      console.error('更新科室信息错误:', error)
      throw error
    })
}

export function deleteDepartment(departmentId) {
  return http.delete(`/departments/delete/${departmentId}`, jsonConfig)
    .then(response => {
      if (response.data && response.data.code === '200') {
        return response.data.data
      }
      throw new Error(response.data?.message || '删除科室失败')
    })
    .catch(error => {
      console.error('删除科室错误:', error)
      throw error
    })
}