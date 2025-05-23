import request from '@/utils/request'

// 查询电影信息列表
export function listSys_movie(query) {
  return request({
    url: '/sys_movie/sys_movie/list',
    method: 'get',
    params: query
  })
}

// 查询电影信息详细
export function getSys_movie(movieId) {
  return request({
    url: '/sys_movie/sys_movie/' + movieId,
    method: 'get'
  })
}

// 新增电影信息
export function addSys_movie(data) {
  return request({
    url: '/sys_movie/sys_movie',
    method: 'post',
    data: data
  })
}

// 修改电影信息
export function updateSys_movie(data) {
  return request({
    url: '/sys_movie/sys_movie',
    method: 'put',
    data: data
  })
}

// 删除电影信息
export function delSys_movie(movieId) {
  return request({
    url: '/sys_movie/sys_movie/' + movieId,
    method: 'delete'
  })
}
