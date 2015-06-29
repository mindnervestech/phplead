package com.mnt.businessApp.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllotmentEngineCache {

    Map<String,Map<String, List<Long>>> zipCache = new HashMap<String,Map<String, List<Long>>>();
	Map<String,Map<String, List<Long>>> productCache = new HashMap<String,Map<String, List<Long>>>();
	
	private AllotmentEngineCache(Map<String, Map<String, List<Long>>> zipCache,
			Map<String, Map<String, List<Long>>> productCache) {
		super();
		this.zipCache = zipCache;
		this.productCache = productCache;
	}
	
    void addOrUpdateZipToCache(String zip, String userType, Long id){
	  
    }
  
    void addOrUpdateProductToCache(String zip, String userType, Long id){
	  
    }
	
	
	private static AllotmentEngineCache allotmentEngineCache;
	
	private AllotmentEngineCache(){}
	
	public static AllotmentEngineCache getInstance() {
		if(allotmentEngineCache == null) {
			throw new RuntimeException("Cache is not initialized, have you forgot to build()");
		}
		return allotmentEngineCache;
	}
	
	public static void build(Map<String, Map<String, List<Long>>> zipCache,
			Map<String, Map<String, List<Long>>> productCache) {
		if(allotmentEngineCache != null) {
			throw new RuntimeException("Cache is already initialized, have you forgot to invalidate");
		}
		if(allotmentEngineCache == null) {
			allotmentEngineCache = new AllotmentEngineCache(zipCache, productCache);
		}
		
	}
	
	public void invalidate() {
		allotmentEngineCache = null;
	}
	
	
}
