package com.ruoyi.tmdb_movie.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.tmdb_movie.domain.Movie;
import com.ruoyi.tmdb_movie.service.MovieService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/report")
public class MovieReportController extends BaseController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/exportSimple")
    public void exportSimpleReport(HttpServletResponse response) {
        try {
            List<Movie> movieList = movieService.getMoviesOrderByPopularityDesc();

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("电影热度排行");

            // 设置表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"电影ID", "电影名称", "实时热度"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 填充数据
            int rowNum = 1;
            for (Movie movie : movieList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(movie.getMovie_id());
                row.createCell(1).setCellValue(movie.getTitle());
                row.createCell(2).setCellValue(movie.getPopularity());
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=movies_simple_report.xlsx");

            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("导出Excel失败");
        }
    }
}