package com.timmy.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.timmy.entity.DeviceStatus;
import com.timmy.entity.EnrollInfo;
import com.timmy.entity.Person;
import com.timmy.entity.UserInfo;
import com.timmy.mapper.PersonMapper;
import com.timmy.service.EnrollInfoService;
import com.timmy.service.PersonService;
import com.timmy.websocket.WebSocketPool;

@Service
public class PersonServiceImpl implements PersonService {
	
	@Autowired 
	PersonMapper personMapper;

	@Autowired
	EnrollInfoService enrollInfoService;
	
	@Override
	public int updateByPrimaryKeySelective(Person record) {
		// TODO Auto-generated method stub
		return personMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(Person record) {
		// TODO Auto-generated method stub
		return personMapper.updateByPrimaryKey(record);
	}



	


	@Override
	public int insertSelective(Person person) {
		// TODO Auto-generated method stub
		return personMapper.insertSelective(person);
	}



	@Override
	public int insert(Person person) {
		// TODO Auto-generated method stub
		return personMapper.insert(person);
	}

	@Override
	public int deleteByPrimaryKey(int id) {
		// TODO Auto-generated method stub
		return personMapper.deleteByPrimaryKey(id);
	}

	@Override
	public Person selectByPrimaryKey(int id) {
		// TODO Auto-generated method stub
		return personMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<Person> selectAll() {
		// TODO Auto-generated method stub
		return personMapper.selectAll();
	}

      public void setUserToDevice(int enrollId,String name,int backupnum,int admin,String records,String deviceSn) {
    	  try {
   			Thread.sleep(400);
   		} catch (InterruptedException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
    	 //  System.out.println("所有在线机器"+WebSocketPool.get);
    		String message="{\"cmd\":\"setuserinfo\",\"enrollid\":"+enrollId+ ",\"name\":\"" + name +"\",\"backupnum\":" + backupnum
					+ ",\"admin\":" + admin + ",\"record\":\"" + records + "\"}"; 
    		 DeviceStatus deviceStatus1=WebSocketPool.getDeviceStatus(deviceSn);
    	//	deviceStatus1.setStatus(deviceStatus1.getStatus());
    	 		if(deviceStatus1.getStatus()==1){
    	 			deviceStatus1.setStatus(0);
        	 		updateDevice(deviceSn, deviceStatus1);
        	 		WebSocketPool.sendMessageToDeviceStatus(deviceSn, message);
    	 		}else{
    	 			try {
    		   			Thread.sleep(600);
    		   		} catch (InterruptedException e) {
    		   			// TODO Auto-generated catch block
    		   			e.printStackTrace();
    		   		}
    	 			deviceStatus1.setStatus(0);
        	 		updateDevice(deviceSn, deviceStatus1);
    	 			//WebSocketPool.sendMessageToAll(message);
        	 		WebSocketPool.sendMessageToDeviceStatus(deviceSn, message);
    	 		}
    	 		
    	 		
	  }
      
      public void setUserToDevice2(String deviceSn) {
    	  List<UserInfo>userInfos=enrollInfoService.usersToSendDevice();
		    
	    	System.out.println(userInfos.size());
	    	int i=0;
		   while(i<userInfos.size()){
				int enrollId=userInfos.get(i).getEnrollId();
				String name=userInfos.get(i).getName();
				int backupnum=userInfos.get(i).getBackupnum();
				int admin=userInfos.get(i).getAdmin();
				String record=userInfos.get(i).getRecord();
			//	personService.setUserToDevice(enrollId, name, backupnum, admin, record);
				try {
		   			Thread.sleep(100);
		   		} catch (InterruptedException e) {
		   			// TODO Auto-generated catch block
		   			e.printStackTrace();
		   		}
		    //	   System.out.println("所有在线机器"+WebSocketPool.getOnlineDevice());
		    		String message="{\"cmd\":\"setuserinfo\",\"enrollid\":"+enrollId+ ",\"name\":\"" + name +"\",\"backupnum\":" + backupnum
							+ ",\"admin\":" + admin + ",\"record\":\"" + record + "\"}";
		    		/*String message1="{\"cmd\":\"setuserinfo\",\"enrollid\":"+enrollId+ ",\"name\":\"" + name +"\",\"backupnum\":" + backupnum
							+ ",\"admin\":" + admin + ",\"record\":\"" + record + "\"}"; */
		    		 DeviceStatus deviceStatus1=WebSocketPool.getDeviceStatus(deviceSn);
		    	//	deviceStatus1.setStatus(deviceStatus1.getStatus());
		    	 		if(deviceStatus1.getStatus()==1){
		    	 			deviceStatus1.setStatus(0);
		        	 		updateDevice(deviceSn, deviceStatus1);
		    	 			WebSocketPool.sendMessageToDeviceStatus(deviceSn, message);
		    	 			i++;
		    	 		}
		    	 	/*	else{
		    	 			try {
		    		   			Thread.sleep(1100);
		    		   		} catch (InterruptedException e) {
		    		   			// TODO Auto-generated catch block
		    		   			e.printStackTrace();
		    		   		}
		    	 			deviceStatus1.setStatus(0);
		        	 		updateDevice(deviceSn, deviceStatus1);
		    	 			//WebSocketPool.sendMessageToAll(message);
		        	 		WebSocketPool.sendMessageToDeviceStatus(deviceSn, message);
		    	 		}*/
			}
	}
      
      
       
      public void getSignature(int enrollId,String deviceSn,int backupNum) {
    	  try {
	   			Thread.sleep(400);
	   		} catch (InterruptedException e) {
	   			// TODO Auto-generated catch block
	   			e.printStackTrace();
	   		}
    	// List<Person>persons=personMapper.selectAll();
    	 String message="{\"cmd\":\"getuserinfo\",\"enrollid\":"+enrollId+",\"backupnum\":0}";
    	 String message1="{\"cmd\":\"getuserinfo\",\"enrollid\":"+enrollId+",\"backupnum\":"+backupNum+"}";	
    	 DeviceStatus deviceStatus=WebSocketPool.getDeviceStatus(deviceSn);
    	 System.out.println("socket连接"+WebSocketPool.getDeviceSocketBySn(deviceSn));
    //	 WebSocketPool.sendMessageToAll(message);
 		if(deviceStatus.getStatus()==1){
 			//WebSocketPool.sendMessageToAll(message);
 			deviceStatus.setStatus(0);
	 		updateDevice(deviceSn, deviceStatus);          
	 		if (null!=deviceStatus.getWebSocket()) {
				deviceStatus.getWebSocket().send(message1);
	 			
			}
 		}else{
 			try {
	   			Thread.sleep(400);
	   		} catch (InterruptedException e) {
	   			// TODO Auto-generated catch block
	   			e.printStackTrace();
	   		}
 			deviceStatus.setStatus(0);
	 		updateDevice(deviceSn, deviceStatus);
	 		if (null!=deviceStatus.getWebSocket()) {
	 			WebSocketPool.sendMessageToDeviceStatus(deviceSn, message);
				//WebSocketPool.sendMessageToAll(message);
			}
 		}
	}
    
    public void getSignature2(List<EnrollInfo>enrolls,String deviceSn) {
		int i=0;
		
		while(i<enrolls.size()){		
			
			try {
	   			Thread.sleep(100);
	   		} catch (InterruptedException e) {
	   			// TODO Auto-generated catch block
	   			e.printStackTrace();
	   		}
			
			 String message1="{\"cmd\":\"getuserinfo\",\"enrollid\":"+enrolls.get(i).getEnrollId()+",\"backupnum\":"+enrolls.get(i).getBackupnum()+"}";	
	    	 DeviceStatus deviceStatus=WebSocketPool.getDeviceStatus(deviceSn);
	    	 if(deviceStatus.getStatus()==1){
	  			//WebSocketPool.sendMessageToAll(message);
	  			deviceStatus.setStatus(0);
	 	 		updateDevice(deviceSn, deviceStatus);          
	 	 		if (null!=deviceStatus.getWebSocket()) {
	 	 			System.out.println("调用了getSignature");
	 	 			WebSocketPool.sendMessageToDeviceStatus(deviceSn, message1);
	 	 			i++;
	 			}
	  		}
		}
	}  
      
      
      
      public void updateDevice(String sn,DeviceStatus deviceStatus) {
    	  if(WebSocketPool.getDeviceStatus(sn)!=null){
  			WebSocketPool.removeDeviceStatus(sn);
  			WebSocketPool.addDeviceAndStatus(sn, deviceStatus);
  		}else{
  			WebSocketPool.addDeviceAndStatus(sn, deviceStatus);
  		}
	}

	@Override
	public void getSignatureByAll(int enrollId) {
		// TODO Auto-generated method stub
		//List<Person>persons=personMapper.selectAll();
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void deleteUserInfoFromDevice(List<EnrollInfo> enrolls,
			String deviceSn) {
		
		int i=0;
        
		while (i<enrolls.size()) {
			try {
	   			Thread.sleep(10);
	   		} catch (InterruptedException e) {
	   			// TODO Auto-generated catch block
	   			e.printStackTrace();
	   		}
			String message="{\"cmd\":\"deleteuser\",\"enrollid\":"+enrolls.get(i).getEnrollId()+",\"backupnum\":"+enrolls.get(i).getBackupnum()+"}";	
			 DeviceStatus deviceStatus=WebSocketPool.getDeviceStatus(deviceSn);
	    	 if(deviceStatus.getStatus()==1){
	  			//WebSocketPool.sendMessageToAll(message);
	  			deviceStatus.setStatus(0);
	 	 		updateDevice(deviceSn, deviceStatus);          
	 	 		if (null!=deviceStatus.getWebSocket()) {
	 	 		//	System.out.println("调用了getSignature");
	 	 			WebSocketPool.sendMessageToDeviceStatus(deviceSn, message);
	 	 			i++;
	 			}
	  		}
		}
		 
	}

	@Override
	public void setUsernameToDevice(String deviceSn) {
		// TODO Auto-generated method stub
		
		 List<Person>persons=personMapper.selectAll();
		    
	    	System.out.println(persons.size());
	    	int i=0;		    
	    	StringBuilder sb=new StringBuilder();
	    	sb.append("{\"cmd\":\"setusername\",\"count\":"+persons.size()+",\"record\":[");
	    	for (int j = 0; j < persons.size(); j++) {
	    		if(j==persons.size()-1||persons.size()==1){
				sb.append("{\"enrollid\":"+persons.get(j).getId()+",\"name\":\"" + persons.get(j).getName()+"\"}");	
	    		}else{
	    		sb.append("{\"enrollid\":"+persons.get(j).getId()+",\"name\":\"" + persons.get(j).getName()+"\"},");
	    		}
			}
	    	sb.append("]}");
	    	System.out.println("下发用户姓名"+sb);
	    	
	    	while (i<1) {
	    		DeviceStatus deviceStatus=WebSocketPool.getDeviceStatus(deviceSn);
		    	 if(deviceStatus.getStatus()==1){
		  			//WebSocketPool.sendMessageToAll(message);
		  			deviceStatus.setStatus(0);
		 	 		updateDevice(deviceSn, deviceStatus);          
		 	 		if (null!=deviceStatus.getWebSocket()) {
		 	 		//	System.out.println("调用了getSignature");
		 	 			WebSocketPool.sendMessageToDeviceStatus(deviceSn, sb.toString());
		 	 			i++;
		 			}
		  		}
				
			}
	}
}
