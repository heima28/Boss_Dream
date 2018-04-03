package com.itheima.bos.web.action.take_delivery;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import com.itheima.bos.domain.base.Area;
import com.itheima.bos.domain.base.TakeTime;
import com.itheima.bos.domain.take_delivery.WayBill;
import com.itheima.bos.service.take_delivery.WayBillService;
import com.itheima.bos.web.action.CommonAction;

import net.sf.json.JsonConfig;

/**
 * ClassName:WaybillAction <br/>
 * Function: <br/>
 * Date: 2018年3月25日 上午11:15:01 <br/>
 */
@Namespace("/") // 等价于struts.xml文件中package节点namespace属性
@ParentPackage("struts-default") // 等价于struts.xml文件中package节点extends属性
@Controller // spring 的注解,控制层代码
@Scope("prototype")
public class WaybillAction extends CommonAction<WayBill> {

    public WaybillAction() {
        super(WayBill.class);
    }

    @Autowired
    private WayBillService wayBillService;

    @Action("waybillAction_save")
    public String save() throws IOException {

        String msg = "0";

        try {
            
            int i=1/0;
            
            wayBillService.save(getModel());
        } catch (Exception e) {
            e.printStackTrace();
            msg = "1";
        }

        HttpServletResponse response = ServletActionContext.getResponse();
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(msg);
        return NONE;
    }
    
    //使用属性驱动获取用户上传的文件
    private File file;
    public void setFile(File file) {
		this.file = file;
	}
    

	@SuppressWarnings("resource")
	@Action(value="waybillAction_batchImport")
    public String batchImport() throws Exception{
		List<WayBill> list = new ArrayList<>();
		
		Workbook workbook = WorkbookFactory.create(file);
    	Sheet sheet = workbook.getSheetAt(0);
    	
    	for (Row row : sheet) {
			if (row.getRowNum() == 0 ) {
				continue;
			}
		    String goodsType = row.getCell(1).getStringCellValue();
		    String sendProNum = row.getCell(2).getStringCellValue();
		    String sendName = row.getCell(3).getStringCellValue();
		    //处理发件人号码格式
		    row.getCell(4).setCellType(Cell.CELL_TYPE_STRING);
		    String sendMobile =row.getCell(4).getStringCellValue();
		    String sendAddress = row.getCell(5).getStringCellValue();
		    String recName = row.getCell(6).getStringCellValue();
		    //处理收件人号码格式
		    row.getCell(7).setCellType(Cell.CELL_TYPE_STRING);
		    String recMobile = row.getCell(7).getStringCellValue();
		    String recCompany = row.getCell(8).getStringCellValue();
		    String recAddress = row.getCell(9).getStringCellValue();
		    WayBill wayBill = new WayBill();
		    wayBill.setGoodsType(goodsType);
		    wayBill.setSendProNum(sendProNum);
		    wayBill.setSendName(sendName);
		    wayBill.setSendMobile(sendMobile);
		    wayBill.setSendAddress(sendAddress);
		    wayBill.setRecName(recName);
		    wayBill.setRecMobile(recMobile);
		    wayBill.setRecCompany(recCompany);
		    wayBill.setRecAddress(recAddress);
		    
		    list.add(wayBill);
		}
    	
    	wayBillService.save(list);
    	workbook.close();
    	System.out.println("保存运单成功!");
    	
    	 HttpServletResponse response = ServletActionContext.getResponse();
         response.setContentType("application/json;charset=UTF-8");
         response.getWriter().write("success");
    	
    	return NONE;
    }
	
	@Action(value = "waybillAction_pageQuery")
	public String pageQuery() throws IOException {

		Pageable pageable = new PageRequest(page - 1, rows);
		// 调用业务层进行查询
		Page<WayBill> page = wayBillService.findAll(pageable);
		page2json(page, null);

		return NONE;
	}


}
