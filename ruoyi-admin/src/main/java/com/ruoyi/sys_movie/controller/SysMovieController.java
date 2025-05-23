package com.ruoyi.sys_movie.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.sys_movie.domain.SysMovie;
import com.ruoyi.sys_movie.service.ISysMovieService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 电影信息Controller
 * 
 * @author ruoyi
 * @date 2025-05-23
 */
@RestController
@RequestMapping("/sys_movie/sys_movie")
public class SysMovieController extends BaseController
{
    @Autowired
    private ISysMovieService sysMovieService;

    /**
     * 查询电影信息列表
     */
    @PreAuthorize("@ss.hasPermi('sys_movie:sys_movie:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysMovie sysMovie)
    {
        startPage();
        List<SysMovie> list = sysMovieService.selectSysMovieList(sysMovie);
        return getDataTable(list);
    }

    /**
     * 导出电影信息列表
     */
    @PreAuthorize("@ss.hasPermi('sys_movie:sys_movie:export')")
    @Log(title = "电影信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysMovie sysMovie)
    {
        List<SysMovie> list = sysMovieService.selectSysMovieList(sysMovie);
        ExcelUtil<SysMovie> util = new ExcelUtil<SysMovie>(SysMovie.class);
        util.exportExcel(response, list, "电影信息数据");
    }

    /**
     * 获取电影信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('sys_movie:sys_movie:query')")
    @GetMapping(value = "/{movieId}")
    public AjaxResult getInfo(@PathVariable("movieId") Long movieId)
    {
        return success(sysMovieService.selectSysMovieByMovieId(movieId));
    }

    /**
     * 新增电影信息
     */
    @PreAuthorize("@ss.hasPermi('sys_movie:sys_movie:add')")
    @Log(title = "电影信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysMovie sysMovie)
    {
        return toAjax(sysMovieService.insertSysMovie(sysMovie));
    }

    /**
     * 修改电影信息
     */
    @PreAuthorize("@ss.hasPermi('sys_movie:sys_movie:edit')")
    @Log(title = "电影信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysMovie sysMovie)
    {
        return toAjax(sysMovieService.updateSysMovie(sysMovie));
    }

    /**
     * 删除电影信息
     */
    @PreAuthorize("@ss.hasPermi('sys_movie:sys_movie:remove')")
    @Log(title = "电影信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{movieIds}")
    public AjaxResult remove(@PathVariable Long[] movieIds)
    {
        return toAjax(sysMovieService.deleteSysMovieByMovieIds(movieIds));
    }
}
