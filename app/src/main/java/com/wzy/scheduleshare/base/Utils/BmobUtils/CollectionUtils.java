package com.wzy.scheduleshare.base.Utils.BmobUtils;

import java.util.Collection;

/**
 * @ClassName CollectionUtils
 * @Author Wei Zhouye
 * @Date 2020/3/1
 * @Version 1.0
 */
public class CollectionUtils {

	public static boolean isNotNull(Collection<?> collection) {
		if (collection != null && collection.size() > 0) {
			return true;
		}
		return false;
	}

/*	public static Map<String,BmobChatUser> list2map(List<BmobChatUser> users){
		Map<String,BmobChatUser> friends = new HashMap<String, BmobChatUser>();
		for(BmobChatUser user : users){
			friends.put(user.getUsername(), user);
		}
		return friends;
	}
	

	public static List<BmobChatUser> map2list(Map<String,BmobChatUser> maps){
		List<BmobChatUser> users = new ArrayList<BmobChatUser>();
		Iterator<Entry<String, BmobChatUser>> iterator = maps.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, BmobChatUser> entry = iterator.next();
			users.add(entry.getValue());
		}
		return users;
	}*/
}
