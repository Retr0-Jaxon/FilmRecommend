import axios from 'axios';

export const movieService = {
  /**
   * 从后端获取所有电影列表，默认按热度排序
   * @returns {Promise<Array>} 返回电影数据数组
   */
  async getList() {
    try {
      const response = await axios.get('http://localhost:8080/api/movie/all'); // 直接访问本地接口
      return response.data;
    } catch (error) {
      console.error("Error fetching movie list:", error);
      throw error;
    }
  },
};
