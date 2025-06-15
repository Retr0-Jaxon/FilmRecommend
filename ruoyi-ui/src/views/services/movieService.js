import axios from 'axios';

export const movieService = {
  /**
   * 从后端获取所有电影列表，默认按热度排序
   * @returns {Promise<Array>} 返回电影数据数组
   */
  async getList() {
    try {
      // 直接请求返回热度排序后所有电影的接口
      const response = await axios.get('http://localhost:8080/api/movies/all');
      return response.data;
    } catch (error) {
      console.error("Error fetching movie list:", error);
      throw error;
    }
  },
  // 【新增】根据ID获取单个电影
  getMovieById(id) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        // 路由参数是字符串，而 movie_id 是数字，所以用 == 进行比较
        const movie = mockMovieDatabase.find(m => m.movie_id == id);
        if (movie) {
          resolve(movie);
        } else {
          reject(new Error(`Movie with id ${id} not found.`));
        }
      }, 300); // 模拟网络延迟
    });
  }
};
