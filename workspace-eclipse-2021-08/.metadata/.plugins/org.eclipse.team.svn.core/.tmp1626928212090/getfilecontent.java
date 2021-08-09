package kr.co.hanshinit.NeoCMS.convert;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import kr.co.hanshinit.NeoCMS.convert.service.BeforeBoard;
import kr.co.hanshinit.NeoCMS.convert.service.ConvertService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ConvertController {

    @Resource(name="convertService")
    private ConvertService convertService;
	
	@RequestMapping(value="/neo/convertList.do")
	public String convertList(HttpServletRequest request, ModelMap model) throws Exception {
		
		return "NeoCMS/convert/convlist";
	}
	
	@RequestMapping(value="/neo/convert.do")
	public String convert(@RequestParam("id") String id, @RequestParam("bbsNo") Integer bbsNo, HttpServletRequest request, ModelMap model) throws Exception {
	    
	    String result = "success";
	    
	    try {
	        List<BeforeBoard> bblist = convertService.selectBeforeBoard(id);
	        
	        convertService.convertBoard(bblist, bbsNo, id, request);
	        
        } catch(Exception e) {
            result = "fail";
        }
	    
	    model.addAttribute("rst", result);
	    
	    return "NeoCMS/convert/convResult";
	}
	
	@RequestMapping(value="/neo/convertAcademy.do")
	public String convertAcademy(@RequestParam("bbsNo") Integer bbsNo, HttpServletRequest request, ModelMap model) throws Exception {
	    
	    String result = "success";
	    
	    try {
	    	List<BeforeBoard> bblist = convertService.selectAcademyList();
	        
	        convertService.convertAcademy(bblist, bbsNo, request);
			   
	        
        } catch(Exception e) {
            result = "fail";
        }
	    
	    model.addAttribute("rst", result);
	    
	    return "NeoCMS/convert/convResult";
	}
}
