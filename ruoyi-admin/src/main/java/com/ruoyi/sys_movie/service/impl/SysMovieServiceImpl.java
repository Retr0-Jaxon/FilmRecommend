package com.ruoyi.sys_movie.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.sys_movie.mapper.SysMovieMapper;
import com.ruoyi.sys_movie.domain.SysMovie;
import com.ruoyi.sys_movie.service.ISysMovieService;

/**
 * 电影信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-05-23
 */
@Service
public class SysMovieServiceImpl implements ISysMovieService 
{
    @Autowired
    private SysMovieMapper sysMovieMapper;

    /**
     * 查询电影信息
     * 
     * @param movieId 电影信息主键
     * @return 电影信息
     */
    @Override
    public SysMovie selectSysMovieByMovieId(Long movieId)
    {
        return sysMovieMapper.selectSysMovieByMovieId(movieId);
    }

    /**
     * 查询电影信息列表
     * 
     * @param sysMovie 电影信息
     * @return 电影信息
     */
    @Override
    public List<SysMovie> selectSysMovieList(SysMovie sysMovie)
    {
        return sysMovieMapper.selectSysMovieList(sysMovie);
    }

    /**
     * 新增电影信息
     * 
     * @param sysMovie 电影信息
     * @return 结果
     */
    @Override
    public int insertSysMovie(SysMovie sysMovie)
    {
        sysMovie.setCreateTime(DateUtils.getNowDate());
        return sysMovieMapper.insertSysMovie(sysMovie);
    }

    /**
     * 修改电影信息
     * 
     * @param sysMovie 电影信息
     * @return 结果
     */
    @Override
    public int updateSysMovie(SysMovie sysMovie)
    {
        sysMovie.setUpdateTime(DateUtils.getNowDate());
        return sysMovieMapper.updateSysMovie(sysMovie);
    }

    /**
     * 批量删除电影信息
     * 
     * @param movieIds 需要删除的电影信息主键
     * @return 结果
     */
    @Override
    public int deleteSysMovieByMovieIds(Long[] movieIds)
    {
        return sysMovieMapper.deleteSysMovieByMovieIds(movieIds);
    }

    /**
     * 删除电影信息信息
     * 
     * @param movieId 电影信息主键
     * @return 结果
     */
    @Override
    public int deleteSysMovieByMovieId(Long movieId)
    {
        return sysMovieMapper.deleteSysMovieByMovieId(movieId);
    }
}
