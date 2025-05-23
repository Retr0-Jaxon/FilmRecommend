<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="电影标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入电影标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="电影类型(多个类型用|分隔)" prop="genres">
        <el-input
          v-model="queryParams.genres"
          placeholder="请输入电影类型(多个类型用|分隔)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="IMDb ID" prop="imdbId">
        <el-input
          v-model="queryParams.imdbId"
          placeholder="请输入IMDb ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="TMDB ID" prop="tmdbId">
        <el-input
          v-model="queryParams.tmdbId"
          placeholder="请输入TMDB ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['sys_movie:sys_movie:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['sys_movie:sys_movie:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['sys_movie:sys_movie:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['sys_movie:sys_movie:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="sys_movieList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="电影ID" align="center" prop="movieId" />
      <el-table-column label="电影标题" align="center" prop="title" />
      <el-table-column label="电影类型(多个类型用|分隔)" align="center" prop="genres" />
      <el-table-column label="IMDb ID" align="center" prop="imdbId" />
      <el-table-column label="TMDB ID" align="center" prop="tmdbId" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['sys_movie:sys_movie:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['sys_movie:sys_movie:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改电影信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="电影标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入电影标题" />
        </el-form-item>
        <el-form-item label="电影类型(多个类型用|分隔)" prop="genres">
          <el-input v-model="form.genres" placeholder="请输入电影类型(多个类型用|分隔)" />
        </el-form-item>
        <el-form-item label="IMDb ID" prop="imdbId">
          <el-input v-model="form.imdbId" placeholder="请输入IMDb ID" />
        </el-form-item>
        <el-form-item label="TMDB ID" prop="tmdbId">
          <el-input v-model="form.tmdbId" placeholder="请输入TMDB ID" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listSys_movie, getSys_movie, delSys_movie, addSys_movie, updateSys_movie } from "@/api/sys_movie/sys_movie"

export default {
  name: "Sys_movie",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 电影信息表格数据
      sys_movieList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        genres: null,
        imdbId: null,
        tmdbId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        title: [
          { required: true, message: "电影标题不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询电影信息列表 */
    getList() {
      this.loading = true
      listSys_movie(this.queryParams).then(response => {
        this.sys_movieList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        movieId: null,
        title: null,
        genres: null,
        imdbId: null,
        tmdbId: null,
        createTime: null,
        updateTime: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.movieId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加电影信息"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const movieId = row.movieId || this.ids
      getSys_movie(movieId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改电影信息"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.movieId != null) {
            updateSys_movie(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addSys_movie(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const movieIds = row.movieId || this.ids
      this.$modal.confirm('是否确认删除电影信息编号为"' + movieIds + '"的数据项？').then(function() {
        return delSys_movie(movieIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('sys_movie/sys_movie/export', {
        ...this.queryParams
      }, `sys_movie_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
