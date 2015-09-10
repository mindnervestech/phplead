package com.mnt.businessApp.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllotmentEngineCache {

    public Map<String,Map<String, List<Long>>> zipCache = new HashMap<String,Map<String, List<Long>>>();
    public Map<String,Map<String, List<Long>>> productCache = new HashMap<String,Map<String, List<Long>>>();
    public Map<String,Map<String, List<Long>>> brandCache = new HashMap<String,Map<String, List<Long>>>();
	
	private AllotmentEngineCache(Map<String, Map<String, List<Long>>> zipCache,
			Map<String, Map<String, List<Long>>> productCache, Map<String, Map<String, List<Long>>> brandCache) {
		super();
		this.zipCache = zipCache;
		this.productCache = productCache;
		this.brandCache = brandCache;
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
			Map<String, Map<String, List<Long>>> productCache, Map<String, Map<String, List<Long>>> brandCache) {
		if(allotmentEngineCache != null) {
			throw new RuntimeException("Cache is already initialized, have you forgot to invalidate");
		}
		if(allotmentEngineCache == null) {
			allotmentEngineCache = new AllotmentEngineCache(zipCache, productCache, brandCache);
		}
		
	}
	
	public static void invalidate() {
		allotmentEngineCache = null;
	}
	
	
}
