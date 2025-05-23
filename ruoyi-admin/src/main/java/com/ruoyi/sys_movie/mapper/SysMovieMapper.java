package com.ruoyi.sys_movie.mapper;

import java.util.List;
import com.ruoyi.sys_movie.domain.SysMovie;

/**
 * 电影信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-05-23
 */
public interface SysMovieMapper 
{
    /**
     * 查询电影信息
     * 
     * @param movieId 电影信息主键
     * @return 电影信息
     */
    public SysMovie selectSysMovieByMovieId(Long movieId);

    /**
     * 查询电影信息列表
     * 
     * @param sysMovie 电影信息
     * @return 电影信息集合
     */
    public List<SysMovie> selectSysMovieList(SysMovie sysMovie);

    /**
     * 新增电影信息
     * 
     * @param sysMovie 电影信息
     * @return 结果
     */
    public int insertSysMovie(SysMovie sysMovie);

    /**
     * 修改电影信息
     * 
     * @param sysMovie 电影信息
     * @return 结果
     */
    public int updateSysMovie(SysMovie sysMovie);

    /**
     * 删除电影信息
     * 
     * @param movieId 电影信息主键
     * @return 结果
     */
    public int deleteSysMovieByMovieId(Long movieId);

    /**
     * 批量删除电影信息
     * 
     * @param movieIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysMovieByMovieIds(Long[] movieIds);
}
