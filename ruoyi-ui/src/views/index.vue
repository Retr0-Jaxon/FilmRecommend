<template>
  <div class="app-container">
    <!-- 顶部统计行 -->
    <!-- <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="card-panel">
            <div class="card-title">电影总数</div>
            <div class="card-value">{{ totalCount }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row> -->

    <!-- ECharts图表行 -->
    <el-row :gutter="20" style="margin-top:20px;">
      <!-- 评分分析图表 -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>电影评分分析</span>
            <el-button 
              style="float: right; padding: 3px 0" 
              type="text"
              @click="refreshVoteChart">
              刷新
            </el-button>
          </template>
          <div v-loading="voteLoading" ref="voteChart" style="height:400px"></div>
        </el-card>
      </el-col>

      <!-- 类型词云图表 -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>电影类型分布</span>
          </template>
          <div v-loading="genreLoading" ref="genreChart" style="height:400px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import 'echarts-wordcloud'
import axios from 'axios'

export default {
  data() {
    return {
      totalCount: 0,
      voteChart: null,
      genreChart: null,
      voteLoading: false,
      genreLoading: false
    }
  },
  mounted() {
    this.initCharts()
    this.loadData()
  },
  methods: {
    initCharts() {
      this.voteChart = echarts.init(this.$refs.voteChart)
      this.genreChart = echarts.init(this.$refs.genreChart)
      window.addEventListener('resize', this.resizeCharts)
    },

    async loadData() {
      this.voteLoading = true
      this.genreLoading = true
      
      try {       
        // 并行获取图表数据
        await Promise.all([
          this.loadVoteData(),
          this.loadGenreData()
        ])
      } catch (error) {
        this.$message.error('数据加载失败')
      } finally {
        this.voteLoading = false
        this.genreLoading = false
      }
    },

    async loadVoteData() {
      try {
        const res = await axios.get('http://localhost:8080/api/charts/movieVoteAnalysis')
        console.log(res.data.data);
        
        this.renderVoteChart(res.data.data)
      } catch (error) {
        console.error('评分数据加载失败:', error)
      }
    },

    async loadGenreData() {
      try {
        const res = await axios.get('http://localhost:8080/api/charts/movieGenreCloud')
        this.renderGenreChart(res.data.data)
      } catch (error) {
        console.error('类型数据加载失败:', error)
      }
    },

    renderVoteChart(data) {
      const option = {
        title: { text: '评分与投票量关系', left: 'center' },
        tooltip: {
          trigger: 'item',
          formatter: params => {
            return `电影: ${params.data[2]}<br/>
                    评分: ${params.data[1]}<br/>
                    投票: ${params.data[0]}`
          }
        },
        xAxis: { 
          name: '投票量',
          nameLocation: 'middle',
          nameGap: 30,
          type: 'value' 
        },
        yAxis: { 
          name: '平均评分',
          nameLocation: 'middle',
          nameGap: 30,
          type: 'value',
          min: 0,
          max: 10
        },
        visualMap: {
          min: 0,
          max: Math.max(...data.map(item => item.vote_count)),
          dimension: 0,
          inRange: { 
            color: ['#65B581', '#FFCE34', '#FD665F'] 
          }
        },
        series: [{
          type: 'scatter',
          symbolSize: function(data) {
            return Math.sqrt(data[0]) / 5
          },
          data: data.map(item => [item.vote_count, item.vote_average, item.name]),
          itemStyle: { 
            opacity: 0.8,
            borderWidth: 1,
            borderColor: '#fff'
          }
        }]
      }
      this.voteChart.setOption(option)
    },

    renderGenreChart(data) {
      const option = {
        title: { text: '电影类型分布', left: 'center' },
        tooltip: { 
          show: true,
          formatter: params => {
            return `${params.data.name}: ${params.data.value}部电影`
          }
        },
        series: [{
          type: 'wordCloud',
          shape: 'circle',
          left: 'center',
          sizeRange: [12, 60],
          rotationRange: [-45, 45],
          textStyle: {
            fontFamily: 'Microsoft YaHei',
            fontWeight: 'bold',
            color: () => {
              return `rgb(${
                Math.round(Math.random() * 155 + 100)
              },${
                Math.round(Math.random() * 155 + 100)
              },${
                Math.round(Math.random() * 155 + 100)
              })`
            }
          },
          emphasis: {
            focus: 'self',
            textStyle: {
              shadowBlur: 10,
              shadowColor: '#333'
            }
          },
          data: data
        }]
      }
      this.genreChart.setOption(option)
    },

    resizeCharts() {
      this.voteChart && this.voteChart.resize()
      this.genreChart && this.genreChart.resize()
    },

    refreshVoteChart() {
      this.voteLoading = true
      this.loadVoteData().finally(() => {
        this.voteLoading = false
      })
    }
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts)
    this.voteChart && this.voteChart.dispose()
    this.genreChart && this.genreChart.dispose()
  }
}
</script>

<style scoped>
.card-panel {
  padding: 10px;
  text-align: center;
}
.card-title {
  font-size: 16px;
  color: #666;
  margin-bottom: 10px;
}
.card-value {
  font-size: 24px;
  font-weight: bold;
}
</style>