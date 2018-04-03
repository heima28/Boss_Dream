package com.itheima.bos.web.action.base;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import com.itheima.bos.domain.base.Standard;
import com.itheima.bos.domain.base.SubArea;
import com.itheima.bos.service.base.SubAreaService;
import com.itheima.bos.web.action.CommonAction;
import com.itheima.utils.FileDownloadUtils;

import net.sf.json.JsonConfig;

/**
 * ClassName:SubAreaAction <br/>
 * Function: <br/>
 * Date: 2018年3月16日 上午9:41:27 <br/>
 */
@Namespace("/") // 等价于struts.xml文件中package节点namespace属性
@ParentPackage("struts-default") // 等价于struts.xml文件中package节点extends属性
@Controller // spring 的注解,控制层代码
@Scope("prototype") // spring 的注解,多例
public class SubAreaAction extends CommonAction<SubArea> {

    public SubAreaAction() {
        super(SubArea.class);
    }

    @Autowired
    private SubAreaService subAreaService;

    @Action(value = "subareaAction_save", results = {@Result(name = "success",
            location = "/pages/base/sub_area.html", type = "redirect")})
    public String save() {

        subAreaService.save(getModel());
        return SUCCESS;
    }

    @Action(value = "subAction_pageQuery")
    public String pageQuery() throws IOException {

        // EasyUI的页码是从1开始的
        // SPringDataJPA的页码是从0开始的
        // 所以要-1

        Pageable pageable = new PageRequest(page - 1, rows);

        Page<SubArea> page = subAreaService.findAll(pageable);

        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setExcludes(new String[] {"subareas", "couriers"});

        page2json(page, jsonConfig);
        return NONE;
    }

    // 查询未关联的分区
    @Action(value = "subAreaAction_findUnAssociatedSubAreas")
    public String findUnAssociatedSubAreas() throws IOException {

        List<SubArea> list = subAreaService.findUnAssociatedSubAreas();
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setExcludes(new String[] {"subareas"});
        list2json(list, jsonConfig);
        return NONE;
    }

    // 查询已关联的分区
    @Action(value = "subAreaAction_findAssociatedSubAreas")
    public String findAssociatedSubAreas() throws IOException {

        List<SubArea> list =
                subAreaService.findAssociatedSubAreas(getModel().getId());
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setExcludes(new String[] {"subareas","couriers"});
        list2json(list, jsonConfig);
        return NONE;
    }
    
    /**
     * 区域数据柱状图
     */
    @Action(value = "subAreaAction_exportfigure")
    public String exportfigure() throws IOException {
        List<LinkedHashMap<String, Object>> list = subAreaService.exportfigure();
        String json = com.alibaba.fastjson.JSONObject.toJSONString(list);
        System.out.println(json);
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
        return NONE;
    }
    
  //导出excel
	
  		@Action(value="subAreaAction_exportExcel")
  		public String exportExcel() throws IOException{
  			//查出所有区域数据
  			Page<SubArea> page = subAreaService.findAll(null);
  			List<SubArea> list = page.getContent();
  			//在内存中创建一个excel文件
  			HSSFWorkbook workbook = new HSSFWorkbook();
  			//创建一个sheet
  			HSSFSheet sheet = workbook.createSheet();
  			//创建标题行
  			HSSFRow titleRow = sheet.createRow(0);
  			titleRow.createCell(0).setCellValue("分拣编号");
  			titleRow.createCell(1).setCellValue("省");
  			titleRow.createCell(2).setCellValue("市");
  			titleRow.createCell(3).setCellValue("区"); 
  			titleRow.createCell(4).setCellValue("关键字");
  			titleRow.createCell(5).setCellValue("起始号");
  			titleRow.createCell(6).setCellValue("终止号");
  			titleRow.createCell(7).setCellValue("单双号");
  			titleRow.createCell(8).setCellValue("辅助关键字");
  			//遍历数据,创建数据行
  			for (SubArea subArea : list) {
  				//获取最后一行的行号
  				int lastRowNum = sheet.getLastRowNum();
  				HSSFRow dataRow = sheet.createRow(lastRowNum+1);
  				dataRow.createCell(0).setCellValue(subArea.getId());
  				dataRow.createCell(1).setCellValue(subArea.getArea().getProvince());
  				dataRow.createCell(2).setCellValue(subArea.getArea().getCity());
  				dataRow.createCell(3).setCellValue(subArea.getArea().getDistrict());
  				dataRow.createCell(4).setCellValue(subArea.getKeyWords());
  				dataRow.createCell(5).setCellValue(subArea.getStartNum());
  				dataRow.createCell(6).setCellValue(subArea.getEndNum());
  				dataRow.createCell(7).setCellValue(subArea.getSingle()+"");
  				dataRow.createCell(8).setCellValue(subArea.getAssistKeyWords());
  			}
  			// 文件名
  	        String filename = "区域设置统计.xls";

  	        // 两头一流
  	        HttpServletResponse response = ServletActionContext.getResponse();
  	        ServletContext servletContext = ServletActionContext.getServletContext();
  	        ServletOutputStream outputStream = response.getOutputStream();
  	        HttpServletRequest request = ServletActionContext.getRequest();

  	        // 获取mimeType
  	        // 先获取mimeType再重新编码,避免编码后后缀名丢失,导致获取失败
  	        String mimeType = servletContext.getMimeType(filename);
  	        // 获取浏览器的类型
  	        String userAgent = request.getHeader("User-Agent");
  	        // 对文件名重新编码
  	        filename =FileDownloadUtils.encodeDownloadFilename(filename, userAgent);

  	        // 设置信息头
  	        response.setContentType(mimeType);
  	        response.setHeader("Content-Disposition", "attachment; filename=" + filename);

  	        // 写出文件
  	        workbook.write(outputStream);
  	        workbook.close();
  	        return NONE;
  		}
    
}
