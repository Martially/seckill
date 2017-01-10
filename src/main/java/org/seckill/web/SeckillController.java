package org.seckill.web;

import java.util.Date;
import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller	//@Service	@Component
@RequestMapping//url:模块/资源/{id}/细分	/seckill/list
public class SeckillController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SeckillService	seckillService;
	
	//二级url
	@RequestMapping(value = "/list",	method = RequestMethod.GET)
	public String list(Model model){
		//获取列表页
		List<Seckill> list = seckillService.getSeckillList();
		model.addAttribute("list",list);
		//list.jsp(页面模版)	+	model(数据)	=	ModelAndView
		//也可以直接返回ModelAndView，但是建议返回String，把Model当成参数放里边
		return "list";	//	/WEB-INF/jsp/"list".jsp
	}
	
	@RequestMapping(value = "/{seckillId}/detail",method= RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId ,Model model)
	{
		if(seckillId==null){
			return "redirect:/seckill/list";//请求重定向
		}
		Seckill seckill = seckillService.getById(seckillId);
		if(seckill == null){
			return "forward:/seckill/list";//请求转发
		}
		model.addAttribute("seckill",seckill);
		return "detail";
	}
	//ajax json
	@RequestMapping(value = "/{seckillId}/exposer",
										method= RequestMethod.POST,
										produces = {"application/json;charset=UTF-8"})
										//produces这个参数是一个好习惯，好书浏览器我们的type。同时解决乱码
	//POST形式，也就是说如果直接敲入这个url链接是无效的
	@ResponseBody	//SpringMVC看到这个注解会试图把返回类型包装成json
	public SeckillResult<Exposer>/*TODO*/ expser(@PathVariable Long seckillId){
		SeckillResult<Exposer> result;
		try{
            Exposer exposer=seckillService.exportSeckillUrl(seckillId);
            result=new SeckillResult<Exposer>(true,exposer);
        }catch (Exception e)
        {
            logger.error(e.getMessage(),e);
            result=new SeckillResult<Exposer>(false,e.getMessage());
        }
		return result;
	}
	
	@RequestMapping(value = "/{seckillId}/{md5}/execution",
										method= RequestMethod.POST,
										produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<SeckillExecution>  execute(@PathVariable("seckillId")Long seckillId,
																						@PathVariable("md5")String md5,
																						//phone从浏览器的request的cookie中获取，这里注意，如果cookie中没有killPhone会报错，所以要将required置为false
																						@CookieValue(value = "killPhone",required = false)Long phone){
		//springmvc vaild
		if(phone==null){
			return new SeckillResult<SeckillExecution>(false,"未注册") ;
		}
		SeckillResult<SeckillExecution> result;
		try {
			SeckillExecution execution = seckillService.executeSeckill(seckillId, phone, md5);
			return new SeckillResult<SeckillExecution>(true,execution);
		}catch (RepeatKillException e1)
        {
            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true,execution);
        }catch (SeckillCloseException e2)
        {
            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStatEnum.END);
            return new SeckillResult<SeckillExecution>(true,execution);
        } catch (Exception e) {
        	 SeckillExecution execution=new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
             return new SeckillResult<SeckillExecution>(true,execution);
		}		
	}
	
	 //获取系统时间
    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time()
    {
        Date now=new Date();
        return new SeckillResult<Long>(true,now.getTime());
    }

}
