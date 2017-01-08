package org.seckill.service.impl;

import java.util.Date;
import java.util.List;

import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

//@Component(包括了所有组件，即不知道应该使用哪个组件时，用这个注解)容器组件的实例
//@Service @Dao @Web @Controller

@Service
public class SeckillServiceImpl implements SeckillService{
	//日志对象
	private Logger logger = 	LoggerFactory.getLogger(this.getClass());
	
	//mybatis和spring整合后，mybatis下的所有的dao都会使用mapper的方式初始化好，放入到容器
	//从spring容器中获取这个实例，注入Service依赖//@Resource @Inject
	@Autowired	
	private SeckillDao seckillDao;
	
	@Autowired
	private SuccessKilledDao successKilledDao;
	
	 //md5盐值字符串，加入一个混淆字符串(秒杀接口)的salt，为了我避免用户猜出我们的md5值，值任意给，越复杂越好
	private final String slat = "asdasdasqeyiuy213147432#@$@#%%ewq";
	
	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}

	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	public Exposer exportSeckillUrl(long seckillId) {
		Seckill seckill = seckillDao.queryById(seckillId);
		if (seckill ==null){//说明查不到这个秒杀产品的记录
			return new Exposer(false, seckillId);
		}
		 //若是秒杀未开启
        Date startTime=seckill.getStartTime();
        Date endTime=seckill.getEndTime();
        //系统当前时间
        Date nowTime=new Date();
        if(nowTime.getTime() < startTime.getTime()
        		||	nowTime.getTime() > endTime.getTime())
        {
        	return new Exposer(false, seckillId,nowTime.getTime(),
        			startTime.getTime(),endTime.getTime());
        }
        //转化特定字符串的过程，不可逆
        //秒杀开启，返回秒杀商品的id、用给接口加密的md5
        String md5 = getMD5(seckillId);	
		return new Exposer(true, md5,seckillId);
	}
	
	//通给盐值和规则，生成MD5
	private String getMD5(long seckillId)
    {
        String base=seckillId+"/"+slat;
        //spring提供的工具类，专门生成md5
        String md5= DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
	 @Transactional
	 /*
	  * 使用注解控制事务方法的优点：
	  * 1：开发团队达成一致约定，明确标注事务方法的编程风格
	  * 2：保证事务方法的执行时间尽可能短，不要穿插其他的网络操作，RPC/HTTP请求，应用时间太长，或者剥离到事务外部
	  * 3：不是所有的方法都需要事务，如只有一条修改操作，只读操作，不需要事务控制。
	  */
	public SeckillExecution executeSeckill(long seckillId, long userPhone,
			String md5) throws SeckillException, RepeatKillException,
			SeckillCloseException {
		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			throw new SeckillException("seckill data rewrite");
		}
		// 执行秒杀逻辑：减库存，记录购买行为
		Date nowTime = new Date();

		try {
			// 减库存
			int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
			if (updateCount <= 0) {
				// 没有更新库存记录，说明秒杀结束,不管是库存没有还是没在时间范围内
				throw new SeckillCloseException("seckill is closed");
			} else {
				// 否则更新了库存，秒杀成功,增加明细,记录购买行为
				int insertCount = successKilledDao.insertSuccessKilled(
						seckillId, userPhone);
				// 唯一：seckillId,userPhone
				if (insertCount <= 0) {
					// 该明细被重复插入，即用户重复秒杀
					throw new RepeatKillException("seckill repeated");
				} else {
					// 秒杀成功,得到成功插入的明细记录,并返回成功秒杀的信息
					SuccessKilled successKilled = successKilledDao
							.queryByIdWithSeckill(seckillId, userPhone);
					//数据字典放到枚举当中，养成习惯
					return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
				}
			}
		} catch (SeckillCloseException e1)
        {
            throw e1;
        }catch (RepeatKillException e2)
        {
            throw e2;
        }catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(),e);
			//所以编译期异常转化为运行期异常
			throw new SeckillException("seckill inner error"+e.getMessage());
		}
	}
}
