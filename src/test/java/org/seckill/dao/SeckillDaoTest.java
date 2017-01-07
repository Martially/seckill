package org.seckill.dao;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.annotations.Param;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
/*
 * 配置spring和junit整合，junit启动时加载SpringIOS容器
 * spring-test，junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SeckillDaoTest {
	
	//注入Dao实现类依赖
	@Resource
	private SeckillDao seckillDao;
	
	@Test
	public void testQueryById() {
		long id =1000;
		Seckill seckill = seckillDao.queryById(id);
		System.out.println(seckill.getName());
		System.out.println(seckill);
	}

	@Test
	public void testReduceNumber() {
		Date killTime = new Date();
		int updateCount = seckillDao.reduceNumber(1000L, killTime);
		System.out.println("updateCount="+updateCount);
	}

	
	@Test
	public void testQueryAll() {
		//  List<Seckill> queryAll( int offset, int limit);	
		//java没有保存行参的记录
		//List<Seckill> queryAll(int offset,int limit);中的参数变成这样:queryAll(int arg0,int arg1)
		//用@Param("xxxx")
		List<Seckill> seckills = seckillDao.queryAll(0, 100);
			for (Seckill seckill : seckills) {
				System.out.println(seckill);
			}
	}

}
