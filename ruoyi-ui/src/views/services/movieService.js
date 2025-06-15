import axios from 'axios';

// 【推荐】将API基础路径定义为常量，方便管理
const API_BASE_URL = 'http://localhost:8080/api/movies';

export const movieService = {
  /**
   * 从后端获取所有电影列表
   * @returns {Promise<Array>} 返回电影数据数组
   */
  async getList() {
    try {
      const response = await axios.get(`${API_BASE_URL}/all`);
      return response.data;
    } catch (error) {
      console.error("Error fetching movie list:", error);
      // 将错误继续向上抛出，让调用它的组件可以处理
      throw error;
    }
  },

  /**
   * 【修正】根据ID从后端获取单个电影的详细信息
   * @param {string|number} id - 电影的ID
   * @returns {Promise<object>} 返回单个电影的数据对象
   */
  async getMovieById(id) {
    // 不再使用 mockMovieDatabase
    try {
      // 构造请求单个电影的URL，例如: http://localhost:8080/api/movies/11
      const response = await axios.get(`${API_BASE_URL}/${id}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching movie with id ${id}:`, error);
      // 将错误继续向上抛出
      throw error;
    }
  }
};
