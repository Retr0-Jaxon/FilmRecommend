import apiClient from './api';

export const movieService = {
  /**
   * 从后端获取所有电影列表，默认按热度排序
   * @returns {Promise<Array>} 返回电影数据数组
   */
  async getList() {
    try {
      // 直接请求返回热度排序后所有电影的接口
      const response = await axios.get('/api/movie/all');
      return response.data;
    } catch (error) {
      console.error("Error fetching movie list:", error);
      throw error;
    }
  },
};
