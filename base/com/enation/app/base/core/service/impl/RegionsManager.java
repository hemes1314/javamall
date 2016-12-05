package com.enation.app.base.core.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Regions;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.IntegerMapper;

/**
 * 地区管理
 * @author kingapex
 * 2010-7-18下午08:12:03
 */
public class RegionsManager extends BaseSupport<Regions> implements IRegionsManager {

	public List listCity(int province_id) {
		List list = this.baseDaoSupport.queryForList("select * from regions where region_grade = 2 and p_region_id = ?", Regions.class, province_id);
		list = list == null ? new ArrayList() : list;
		return list;
	}
	
	public List listProvince() {
		List list = this.baseDaoSupport.queryForList("select * from regions where region_grade = 1", Regions.class);
		list = list == null ? new ArrayList() : list;
		return list;
	}
	
	public List listRegion(int city_id) {
		List list = this.baseDaoSupport.queryForList("select * from regions where region_grade = 3 and p_region_id = ?", Regions.class, city_id);
		list = list == null ? new ArrayList() : list;
		return list;
	}
	
	/**
	 * 根据区域编码查询对应的省市县.
	 * <br/>此接口已不再使用，相应功能已在其他服务中添加支持.
	 * 
	 * @param code 区域代码
	 * @return 对应的省市县信息
	 */
	@Deprecated
	public Map getRegionEntity(int code) {
	    StringBuffer buffer = new StringBuffer();
	    buffer.append("SELECT t1.region_id as province_id, t1.local_name as province_name,");
	    buffer.append("    t2.region_id as city_id, t2.local_name as city_name,");
	    buffer.append("    t3.region_id as county_id, t3.local_name as county_name ");
	    buffer.append("FROM es_regions t1");
	    buffer.append("    INNER JOIN es_regions t2 ON t1.region_id = t2.p_region_id ");
	    buffer.append("    INNER JOIN es_regions t3 ON t2.region_id = t3.p_region_id ");
	    buffer.append("WHERE t3.zipcode = ?");
	    return this.baseDaoSupport.queryForMap(buffer.toString(), code);
	}
	
	
	
	//add by shop
    public List listChildrenAsyn(Integer regionid) {
        StringBuffer sql  = new StringBuffer();
        sql.append("select * from  es_regions ");
        List<Regions> list = this.daoSupport.queryForList(sql.toString(),Regions.class);
        List<Regions> reglist = new ArrayList<Regions>();
        for(Regions map:list){
            if(map.getP_region_id().equals(regionid)){
                List<Regions> children =this.getChildren(list, map.getRegion_id());
//            map.setChildren(children);
                int i = this.baseDaoSupport.queryForInt("select count(0) from es_regions where p_region_id="+map.getRegion_id());
                if(i!=0){
                    map.setState("closed");
                }
                reglist.add(map);
            }
        }       
        return reglist;
    }
	
	public List listChildren(Integer regionid) {
		StringBuffer sql  = new StringBuffer();
		sql.append("select * from  es_regions ");
		List<Regions> list = this.daoSupport.queryForList(sql.toString(),Regions.class);
		List<Regions> reglist = new ArrayList<Regions>();
		for(Regions map:list){
			if(map.getP_region_id().equals(regionid)){
				List<Regions> children =this.getChildren(list, map.getRegion_id());
				map.setChildren(children);
				int i = this.baseDaoSupport.queryForInt("select count(0) from es_regions where p_region_id="+map.getRegion_id());
				if(i!=0){
					map.setState("closed");
				}
				reglist.add(map);
			}
		}		
		return reglist;
	}
	
	private List<Regions> getChildren(List<Regions> list,Integer id){
		List<Regions> children = new ArrayList<Regions>();
		for (Regions map : list) {
			if(map.getP_region_id().equals(id)){				
				map.setChildren(this.getChildren(list, map.getRegion_id()));
				children.add(map);
			}
		}
		return children;
	}
	
	@SuppressWarnings("rawtypes")
	public List listChildren(String regionid) {
		
		if(regionid==null || regionid.equals("")) return new ArrayList();
		StringBuffer sql  = new StringBuffer();
		sql.append("select * from  ");
		sql.append(this.getTableName("regions"));
		sql.append(" c");
		sql.append("  where c. p_region_id in("+regionid+")  order by region_id");
		List list = this.daoSupport.queryForList(sql.toString(),new IntegerMapper());
		return list;
	}
	
	public List listChildrenByid(Integer regionid) {
		StringBuffer sql  = new StringBuffer();
		sql.append("select c.region_id,c.local_name,c.region_grade,c.p_region_id,count(s.region_id) as childnum,c.zipcode,c.cod  from  ");

		sql.append(this.getTableName("regions"));
		sql.append(" c");
		
		sql.append(" left join ");
		sql.append(this.getTableName("regions"));
		sql.append(" s");
		
		sql.append(" on s.p_region_id = c.region_id  where c.p_region_id=? group by c.region_id,c.local_name,c.region_grade,c.p_region_id,c.zipcode,c.cod order by region_id");
		List list = this.daoSupport.queryForList(sql.toString(),regionid);
		return list;
	}
	
	@SuppressWarnings("rawtypes")
	public String getChildrenJson(Integer regionid) {
		List list  = this.listChildren(regionid);
		JSONArray jsonArray = JSONArray.fromObject( list );  
		return jsonArray.toString();
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(Regions regions) {
		this.baseDaoSupport.insert("regions", regions);
		String region_path = "";
		int region_id = this.baseDaoSupport.getLastId("regions");
		if(regions.getP_region_id()!=null&&regions.getP_region_id() != 0){
			Regions p = get(regions.getP_region_id());
			region_path = p.getRegion_path() + region_id + ",";
		}else{
			region_path = "," + region_id + ",";
		}
		regions = get(region_id);
		regions.setRegion_path(region_path);
		update(regions);
	}

	public void delete(int regionId) {
		this.baseDaoSupport.execute("delete from regions where region_path like '%,"+regionId+",%'");
		
	}
	
	public Regions get(int regionId) {
		return this.baseDaoSupport.queryForObject("select * from regions where region_id = ?", Regions.class, regionId);
	}
	
	public Regions getByName(String name) {
		try{
			List<Regions> list = this.baseDaoSupport.queryForList("select * from regions where local_name = ?", Regions.class, name  );
			if(list== null || list.isEmpty()) {
				return null;
			}
			return list.get(0);
		}catch(RuntimeException e){
			return null;
		}
	}

	public void update(Regions regions) {
		this.baseDaoSupport.update("regions", regions, "region_id="+regions.getRegion_id());
		
	}

	@Transactional(propagation = Propagation.REQUIRED) 
	public void reset() {
		Connection conn = DBSolutionFactory.getConnection(null);
		try {
			Statement state = conn.createStatement();
			state.execute("truncate table " + this.getTableName("regions"));	//	先清空表中数据
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String xmlFile = "file:com/enation/app/base/city.xml";
			DBSolutionFactory.dbImport(xmlFile, "es_");
		/*
		IDBSolution dbsolution = DBSolutionFactory.getDBSolution();
		dbsolution.setPrefix("es_");
		dbsolution.setConnection(conn);
		boolean result = dbsolution.dbImport();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		*/
	}

	public Regions[] get(String area) {
		//这里什么也不做
		return null;
	}
	
}
