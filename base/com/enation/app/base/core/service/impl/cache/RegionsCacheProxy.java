package com.enation.app.base.core.service.impl.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.enation.app.base.core.model.Regions;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.cache.CacheFactory;
import com.enation.framework.cache.ICache;

public class RegionsCacheProxy extends BaseSupport<Regions> implements IRegionsManager {
	
	//FIXME 检查发现此Cache逻辑混乱，应未实现Cache功能
	
	protected final Logger logger = Logger.getLogger(getClass());
	protected ICache<List<Regions>> cache;
	
	private IRegionsManager regionsManager;
	private static final String REGION_LIST_CACHE_KEY = "regionCache";
	public  RegionsCacheProxy(IRegionsManager regionsManager){
		cache = CacheFactory.getCache(REGION_LIST_CACHE_KEY);
		this.regionsManager =regionsManager;
	}
	
	
	public List listCity(int province_id) {
		 
		return regionsManager.listCity(province_id);
	}

	
	public List listProvince() {
 
		return regionsManager.listProvince();
	}

	
	public List listRegion(int city_id) {
		 
		return this.regionsManager.listRegion(city_id);
	}
	
	
	public List listChildren(Integer regionid) {
	 
		return this.regionsManager.listChildren(regionid);
	}

	private List<Regions> listAll(){
		String sql ="select * from regions order by region_id";
		return this.baseDaoSupport.queryForList(sql, Regions.class);
	 
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List listChildren(String regionid) {
		if(logger.isDebugEnabled()){
			logger.info("find parents is " + regionid);
		}
		if(regionid==null || "".equals(regionid))return new ArrayList();
		
		List<Regions> regionsList = cache.get(REGION_LIST_CACHE_KEY);
		if(regionsList==null){
			if(logger.isDebugEnabled()){
				logger.info("load regions list from db");
			}
			regionsList = listAll() ;
			cache.put(REGION_LIST_CACHE_KEY,regionsList );
		}else{
			if(logger.isDebugEnabled()){
				logger.info("load regions list from cache");
			}
		}
		List list = new ArrayList();
		String[] regionsArray =StringUtils.split(regionid, ",");//regionid.split(",");
		for(String id:regionsArray){

			for(Regions  region:regionsList){
				if(region.getRegion_path().indexOf(","+id+",")>=0){
					list.add(region);
				}
			}
		}
		
		return list;
	}
	

	
	public String getChildrenJson(Integer regionid) {
		 
		return this.regionsManager.getChildrenJson(regionid);
	}

	
	public void add(Regions regions) {
		this.regionsManager.add(regions);
		
	}

	
	public void delete(int regionId) {
		this.regionsManager.delete(regionId);
		
	}

	
	public Regions get(int regionId) {
		return this.regionsManager.get(regionId);
	}

	
	public void update(Regions regions) {
		this.regionsManager.update(regions);
		
	}


	public void reset() {
		this.regionsManager.reset();
	}

	@Deprecated
	public Regions[] get(String area) {
		String[] areas = StringUtils.split(area, "-");//area.split("-");
		List<Regions> list = this.listAll();
		Regions[] result = new Regions[3];
		for(Regions regions:list){
			if(regions.getLocal_name().equals(areas[0])) result[0] = regions;
			if(regions.getLocal_name().equals(areas[1])) result[1] = regions;
			if(regions.getLocal_name().equals(areas[2])) result[2] = regions;
		}
		return result;
	}

	//add by shop
	   @Override
	    public List listChildrenAsyn(Integer regionid) {
	        // TODO Auto-generated method stub
	        return null;
	    }

	public Regions getByName(String name) {
		return this.regionsManager.getByName(name);
	}


	@Override
	public List listChildrenByid(Integer regionsid) {
		return this.regionsManager.listChildrenByid(regionsid);
	}


    @Override
    public Map getRegionEntity(int code) {
        
        return this.regionsManager.getRegionEntity(code);
    }

}
