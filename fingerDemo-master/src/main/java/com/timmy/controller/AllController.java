package com.timmy.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.ls.LSInput;

import com.fasterxml.jackson.annotation.JacksonInject.Value;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.fabric.xmlrpc.base.Data;
import com.timmy.entity.AccessDay;
import com.timmy.entity.AccessWeek;
import com.timmy.entity.Device;
import com.timmy.entity.DeviceStatus;
import com.timmy.entity.EnrollInfo;
import com.timmy.entity.LockGroup;
import com.timmy.entity.Msg;
import com.timmy.entity.Person;
import com.timmy.entity.Records;
import com.timmy.entity.SetUserReturnInfo;
import com.timmy.entity.UserInfo;
import com.timmy.entity.UserLock;
import com.timmy.mapper.DeviceMapper;
import com.timmy.mapper.EnrollInfoMapper;
import com.timmy.service.AccessDayService;
import com.timmy.service.AccessWeekService;
import com.timmy.service.DeviceService;
import com.timmy.service.EnrollInfoService;
import com.timmy.service.LockGroupService;
import com.timmy.service.PersonService;
import com.timmy.service.RecordsService;
import com.timmy.service.UserLockService;
import com.timmy.websocket.WSServer;
import com.timmy.websocket.WebSocketPool;




@Controller

public class AllController {
	
	/*@Autowired
	EnrollInfoService enrollInfoService;
	*/
	@Autowired
	AccessDayService accessaDayService;
	
    @Autowired
    AccessWeekService accessWeekService;
    
    @Autowired
    LockGroupService lockGroupService;
    
    @Autowired
    UserLockService userLockService;
    
    @Autowired
    EnrollInfoService enrollInfoService;
    
    @Autowired
    PersonService personService;
    
    @Autowired
    RecordsService recordService;
    
    @Autowired
    DeviceService deviceService;
	
	@RequestMapping("/hello1")
	public String hello() {
		return "hello";
	}
    
	
	
	/*获取所有考勤机*/
	@ResponseBody
	@RequestMapping(value="/device",method=RequestMethod.GET)
	public Msg getAllDevice() {				
		List<Device>deviceList=deviceService.findAllDevice();		
		return Msg.success().add("device", deviceList);
	}
	
	/*获取所有考勤机*/
	@ResponseBody
	@RequestMapping(value="/enrollInfo",method=RequestMethod.GET)
	public Msg getAllEnrollInfo() {				
		List<Person>enrollList=personService.selectAll();		
		return Msg.success().add("enrollInfo", enrollList);
	}
	
	
	/*采集所有的用户*/
	@ResponseBody
    @RequestMapping(value="/sendWs",method = RequestMethod.GET)
    public Msg sendWs(@RequestParam("deviceSn")String deviceSn) {
		String  message="{\"cmd\":\"getuserlist\",\"stn\":true}";
       
		System.out.println("sss"+deviceSn);
		DeviceStatus deviceSatus=new DeviceStatus();
		
		WebSocketPool.sendMessageToDeviceStatus(deviceSn, message);

        return  Msg.success();
    }
	
	@ResponseBody
	@RequestMapping(value="getUserInfo",method=RequestMethod.GET)
	public Msg getUserInfo(@RequestParam("deviceSn")String deviceSn) {
		System.out.println("进入controller");
		List<Person>person=personService.selectAll();
		List<EnrollInfo>enrollsPrepared=new ArrayList<EnrollInfo>();
        for (int i = 0; i < person.size(); i++) {
			int enrollId2=person.get(i).getId();
			List<EnrollInfo>enrollInfos=enrollInfoService.selectByEnrollId(enrollId2);		
			for (int j = 0; j < enrollInfos.size(); j++) {
				if(enrollInfos.get(j).getEnrollId()!=null&&enrollInfos.get(j).getBackupnum()!=null){
				enrollsPrepared.add(enrollInfos.get(j));
			}
			}
		}
        personService.getSignature2(enrollsPrepared, deviceSn);
        
		return  Msg.success();
	}
	
	
	/*获取单个用户*/
	@ResponseBody
    @RequestMapping("sendGetUserInfo")
    public Msg sendGetUserInfo(@RequestParam("enrollId")int enrollId,@RequestParam("backupNum")int backupNum) {
		//String  message="{\"cmd\":\"getuserlist\",\"stn\":true}";
		
	//	String message="{\"cmd\":\"getuserinfo\",\"enrollid\":"+enrollId+",\"backupnum\":0}";
		 String message1="{\"cmd\":\"getuserinfo\",\"enrollid\":"+enrollId+",\"backupnum\":"+backupNum+"}";	
		System.out.println("message"+message1);
        WebSocketPool.sendMessageToAllDeviceFree(message1); 
        try {
   			Thread.sleep(100);
   		} catch (InterruptedException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
        
        if(enrollInfoService.selectByBackupnum(enrollId, backupNum)!=null){
        	return Msg.success();
        }else{
        	return Msg.fail();
        }
      //  return "hello";
    }
	
/*	下发所有用户，面向选中考勤机*/
	@ResponseBody
	@RequestMapping(value="/setPersonToDevice",method = RequestMethod.GET)
	public Msg sendSetUserInfo(@RequestParam("deviceSn")String deviceSn){
	    
		personService.setUserToDevice2(deviceSn);
		return Msg.success();
		
	}
	
	
	@ResponseBody
	@RequestMapping(value="setUsernameToDevice",method=RequestMethod.GET)
	public Msg setUsernameToDevice(@RequestParam("deviceSn")String deviceSn) {
		personService.setUsernameToDevice(deviceSn);
		return Msg.success();
	}
	
	
	@ResponseBody
	@RequestMapping(value="/getDeviceInfo",method=RequestMethod.GET)
	public Msg getDeviceInfo(@RequestParam("deviceSn") String deviceSn){		
		 String message="{\"cmd\":\"getdevinfo\"}";	
		 System.out.println("打卡时间"+new Date());
		 WebSocketPool.sendMessageToDeviceStatus(deviceSn, message);
	     return Msg.success();	
	}
	
	
	
	
	/*下发单个用户到机器，对选中考勤机*/
	@ResponseBody
	@RequestMapping(value="/setOneUser",method = RequestMethod.GET)
	public Msg setOneUserTo(@RequestParam("enrollId")int enrollId,@RequestParam("backupNum")int backupNum,@RequestParam("deviceSn")String deviceSn) {
		Person person=new Person();
		person=personService.selectByPrimaryKey(enrollId);
		EnrollInfo enrollInfo=new EnrollInfo();
		System.out.println("ba"+backupNum);
		enrollInfo=enrollInfoService.selectByBackupnum(enrollId, backupNum);
		if(enrollInfo!=null){
			personService.setUserToDevice(enrollId, person.getName(), backupNum, person.getRollId(), enrollInfo.getSignatures(),deviceSn);
			return Msg.success();
		}else{
			return Msg.fail();
		}
		
	}
	
	/*从考勤机删除用户*/
	@ResponseBody
	@RequestMapping(value="/deletePersonFromDEvice",method = RequestMethod.GET)
	public Msg deleteDeviceUserInfo(@RequestParam("enrollId")int enrollId,@RequestParam("deviceSn")String deviceSn){	
		/*String message="{\"cmd\":\"deleteuser\",\"enrollid\":"+enrollId+",\"backupnum\":"+backupnum+"}";	
		WebSocketPool.sendMessageToDeviceStatus(deviceSn, message);*/
		List<EnrollInfo>enrollInfos=enrollInfoService.selectByEnrollId(enrollId);
		personService.deleteUserInfoFromDevice(enrollInfos, deviceSn);
		personService.deleteByPrimaryKey(enrollId);
		return Msg.success();
	}
	
	
	/*初始化考勤机*/
	@ResponseBody
	@RequestMapping(value="/initSystem",method = RequestMethod.GET)
	public Msg initSystem(@RequestParam("deviceSn")String deviceSn) {
		System.out.println("初始化请求");
		String  message="{\"cmd\":\"initsys\"}";
		System.out.println(message);
		WebSocketPool.sendMessageToDeviceStatus(deviceSn, message);
		return Msg.success();
	}
	
	
	/*采集所有的考勤记录，面向所有机器*/
	@ResponseBody
	@RequestMapping(value="/getAllLog",method = RequestMethod.GET)
	public Msg getAllLog(@RequestParam("deviceSn")String deviceSn) {	
		String  message="{\"cmd\":\"getalllog\",\"stn\":true}";
		System.out.println(message);
        WebSocketPool.sendMessageToDeviceStatus(deviceSn, message);
		return Msg.success();
		
		
	}
	
	/*添加天时段,面向全部考勤机*/
	@ResponseBody
	@RequestMapping(value="/setAccessDay",method = RequestMethod.POST)
	public Msg setAccessDay(@ModelAttribute AccessDay accessDay) {
		if(accessaDayService.selectByPrimaryKey(accessDay.getId())!=null){
			return Msg.fail();
		}
		accessaDayService.insert(accessDay);
		
		accessaDayService.setAccessDay();
		return Msg.success();
	}
	
	
	/*添加周时段，面向全部考勤机*/
	@ResponseBody
	@RequestMapping(value="/setAccessWeek",method = RequestMethod.POST)
	public Msg setAccessWeek(@ModelAttribute AccessWeek accessWeek) {	
	//	accessWeek.set
		if(accessWeekService.selectByPrimaryKey(accessWeek.getId())!=null){
			return Msg.fail();
		}
		accessWeekService.insert(accessWeek);
		accessWeekService.setAccessWeek();	
		return Msg.success();
		
	}
	
	
	/*设置锁组合*/
	@ResponseBody
	@RequestMapping(value="/setLocckGroup",method = RequestMethod.POST)
	public Msg setLockGroup(@ModelAttribute LockGroup lockGroup) {		
		lockGroupService.setLockGroup(lockGroup);	
		return Msg.success();
	}
	
	/*设置用户锁权限*/
	@ResponseBody
	@RequestMapping(value="/setUserLock",method=RequestMethod.POST)
	public Msg setUserLock(@ModelAttribute UserLock userLock) {
		
		userLockService.setUserLock(userLock, "2019-06-06 00:00:00", "2099-03-25 00:00:00");
		return Msg.success();
	}
	
	
	/*显示员工列表*/
	@RequestMapping(value="/emps")
	@ResponseBody
	public Msg getAllPersonFromDB(@RequestParam(value="pn",defaultValue="1") Integer pn) {
		// 引入 PageHelper 分页插件
				/**
				 * 在查询之前只需要调用，传入要显示的页码，以及每页显示的数量 startPage 后紧跟的查询就是分页查询
				 */
		PageHelper.startPage(pn, 8);
		List<Person>emps=personService.selectAll();
		
		PageInfo page= new PageInfo(emps,5);
		
		return Msg.success().add("pageInfo", page);
		
	}
	
	
	

	
	
	/*显示所有的打卡记录*/
	@RequestMapping(value="/records")
	@ResponseBody
	public Msg getAllLogFromDB(@RequestParam(value="pn",defaultValue="1") Integer pn){
		PageHelper.startPage(pn, 8);
		
		List<Records>records=recordService.selectAllRecords();
		
		PageInfo page=new PageInfo(records, 5);

		return Msg.success().add("pageInfo", page);
		
	}
	
	
	
	/*设置周时间段*/
	@RequestMapping(value="/accessDays",method = RequestMethod.GET)
	@ResponseBody
	public Msg getAccessDayFromDB() {
		List<AccessDay>accessDays=accessaDayService.selectAll();
		return Msg.success().add("accessdays", accessDays);
		
	}
	
	
	
	public Msg uploadUserToDevice(@RequestParam("enrollId")int enrollId) {
		
		Person person=personService.selectByPrimaryKey(enrollId);
		
		return Msg.success();
	}
	
	
	
	
}
