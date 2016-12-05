package com.enation.app.shop.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.model.mapper.CatMapper;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.util.StringUtil;

public class GoodsCatManager extends BaseSupport<Cat> implements IGoodsCatManager {



 
    public boolean checkname(String name,Integer catid){
        if(name!=null)name=name.trim();
        String sql ="select count(0) from goods_cat where name=? and cat_id!=?";
        if(catid==null){
            catid=0;
        }
        
        int count  = this.baseDaoSupport.queryForInt(sql, name,catid);
        if(count>0) return true;
        else        return false;
    }
    
    public int delete(int catId) {
        String sql =  "select count(0) from goods_cat where parent_id = ?";
        int count = this.baseDaoSupport.queryForInt(sql,  catId );
        if (count > 0) {
            return 1; // 有子类别
        }

        sql =  "select count(0) from goods where cat_id = ?";
        count = this.baseDaoSupport.queryForInt(sql,  catId );
        if (count > 0) {
            return 2; // 有子类别
        }
        sql =  "delete from  goods_cat   where cat_id=?";
        this.baseDaoSupport.execute(sql,  catId );

        return 0;
    }

    /**
     * 获取类别详细，将图片加上静态资源服务器地址
     */
    public Cat getById(int catId) {
        String sql = "select * from goods_cat  where cat_id=?";
        Cat cat =baseDaoSupport.queryForObject(sql, Cat.class, catId);
        if(cat!=null){
            String image = cat.getImage();
            if(image!=null){
                image  =UploadUtil.replacePath(image); 
                cat.setImage(image);
            }
        }
        return cat;
    }
    
    public List<Cat> listAllChildrenByType(Integer typeId) {
    	String sql = "select * from es_goods_cat where parent_id>1 and type_id=? and (LENGTH(REGEXP_REPLACE(cat_path,'\\d',''))-1)>1";
    	return this.baseDaoSupport.queryForList(sql, Cat.class, typeId);
    }

    
    public List<Cat> listChildren(Integer catId) {
         String sql  ="select c.*,'' type_name from goods_cat c where parent_id=?";
        return this.baseDaoSupport.queryForList(sql,new CatMapper(), catId);
    }
    
    
    
    public List<Cat> listAllChildren(Integer catId) {

        String sql = "select c.*,t.name as type_name  from  es_goods_cat c "
                + " left join es_goods_type t on c.type_id = t.type_id where list_show=1 "
                + " order by parent_id,cat_order";
        
        // this.findSql("all_cat_list");
        List<Cat> allCatList = daoSupport.queryForList(sql, new CatMapper());
        List<Cat> topCatList  = new ArrayList<Cat>();
        
        if(catId.intValue()!=0){
            Cat cat = this.getById(catId);
            topCatList.add(cat);
        }
        for(Cat cat :allCatList){
            if(cat.getParent_id().compareTo(catId)==0){
                if(this.logger.isDebugEnabled()){
                    this.logger.debug("发现子["+cat.getName()+"-"+cat.getCat_id() +"]"+cat.getImage());
                }
                List<Cat> children = this.getChildren(allCatList, cat.getCat_id());
                
                int i = this.baseDaoSupport.queryForInt("select count(0) from es_goods_cat where parent_id="+cat.getCat_id());
                if(i!=0){
                    cat.setState("closed");
                }
                
                cat.setChildren(children);
                topCatList.add(cat);
            }
        }
        return topCatList;
    }
    
    @Override
    public List<Map> getListChildren(Integer cat_id) {
        String sql = "select c.*,tt.totleNum as totle_num,t.name as type_name from es_goods_cat c left join "
                + " (select gcc.parent_id  gccat, count(gcc.cat_id) as totleNum from es_goods_cat gcc  "
                + " where gcc.parent_id in (select cat_id from es_goods_cat gc where gc.parent_id=? ) GROUP BY gcc.parent_id )  tt on c.cat_id=tt.gccat "
                + " left join es_goods_type t on c.type_id = t.type_id "
                + " where c.parent_id=? and (c.list_show=1 or c.list_show=0)order by c.cat_order asc";
        
         List<Map> catlist = this.daoSupport.queryForList(sql, cat_id,cat_id);

         for(Map map :catlist){
            if(map.get("totle_num")==null){     //判断某一个分类下的子分类数量 null赋值为0
                map.put("totle_num", 0);
            }
             int totle_num = NumberUtils.toInt(map.get("totle_num").toString());
             if(totle_num!=0){      //判断某一个分类下的子分类数量 不为0 则是(easyui)文件夹并且是闭合状态。
                 map.put("state", "closed");
             }
         }
         
        return catlist;
    }
    
    public List<Cat> listAllChildrenAdmin(Integer catId) {

        String sql = "select c.*,t.name as type_name  from  es_goods_cat c "
                + " left join es_goods_type t on c.type_id = t.type_id "
                + " order by parent_id,cat_order";
        
        // this.findSql("all_cat_list");
        List<Cat> allCatList = daoSupport.queryForList(sql, new CatMapper());
        List<Cat> topCatList  = new ArrayList<Cat>();
        
        if(catId.intValue()!=0){
            Cat cat = this.getById(catId);
            topCatList.add(cat);
        }
        for(Cat cat :allCatList){
            if(cat.getParent_id().compareTo(catId)==0){
                if(this.logger.isDebugEnabled()){
                    this.logger.debug("发现子["+cat.getName()+"-"+cat.getCat_id() +"]"+cat.getImage());
                }
                List<Cat> children = this.getChildren(allCatList, cat.getCat_id());
                
                int i = this.baseDaoSupport.queryForInt("select count(0) from es_goods_cat where parent_id="+cat.getCat_id());
                if(i!=0){
                    cat.setState("closed");
                }
                
                cat.setChildren(children);
                topCatList.add(cat);
            }
        }
        return topCatList;
    }
 
    
    private List<Cat> getChildren(List<Cat> catList ,Integer parentid){
        if(this.logger.isDebugEnabled()){
            this.logger.debug("查找["+parentid+"]的子");
        }
        List<Cat> children =new ArrayList<Cat>();
        for(Cat cat :catList){
            if(cat.getParent_id().compareTo(parentid)==0){
                if(this.logger.isDebugEnabled()){
                    this.logger.debug(cat.getName()+"-"+cat.getCat_id()+"是子");
                }
                cat.setChildren(this.getChildren(catList, cat.getCat_id()));
                children.add(cat);
            }
        }
        return children;
    }

    
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveAdd(Cat cat) {
         
        baseDaoSupport.insert("goods_cat", cat);
        int cat_id = baseDaoSupport.getLastId("goods_cat");
        String sql = "";
        //判断是否是顶级类似别，如果parentid为空或为0则为顶级类似别
        //注意末尾都要加|，以防止查询子孙时出错
        if (cat.getParent_id() != null && cat.getParent_id().intValue() != 0) { //不是顶级类别，有父
            sql = "select * from goods_cat  where cat_id=?";
            Cat parent = baseDaoSupport.queryForObject(sql, Cat.class, cat
                    .getParent_id());
            cat.setCat_path(parent.getCat_path()  + cat_id+"|"); 
        } else {//是顶级类别
            cat.setCat_path(cat.getParent_id() + "|" + cat_id+"|");
            //2014-6-19 @author LiFenLong 如果为顶级类别则parent_id为0
            cat.setParent_id(0);
        }

        sql = "update goods_cat set  cat_path=?,parent_id=?  where  cat_id=?";
        baseDaoSupport.execute(sql, new Object[] { cat.getCat_path(),cat.getParent_id(), cat_id });

    }

    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Transactional(propagation = Propagation.REQUIRED)
    public void update(Cat cat) {
        checkIsOwner(cat.getCat_id());
        // 如果有父类别，根据父的path更新这个类别的path信息
        if (cat.getParent_id() != null && cat.getParent_id().intValue() != 0) {
            
            String sql = "select * from goods_cat where cat_id=?";
            Cat parent = baseDaoSupport.queryForObject(sql, Cat.class, cat.getParent_id());
            cat.setCat_path(parent.getCat_path() + cat.getCat_id()+"|");
            
        } else {
            // 顶级类别，直接更新为parentid|catid
            cat.setCat_path(cat.getParent_id() + "|" + cat.getCat_id()+"|");
        }

        HashMap map = new HashMap();
        map.put("name", cat.getName());
        map.put("parent_id", cat.getParent_id());
        map.put("cat_order", cat.getCat_order());
        map.put("type_id", cat.getType_id());
        map.put("cat_path", cat.getCat_path());
        map.put("list_show", cat.getList_show());
        map.put("image", StringUtil.isEmpty(cat.getImage())?null:cat.getImage());
        baseDaoSupport.update("goods_cat", map, "cat_id=" + cat.getCat_id());
        
        //修改子分类的cat_path
        List<Map> childList = this.baseDaoSupport.queryForList("select * from es_goods_cat where parent_id=?", cat.getCat_id());
        if(childList!=null && childList.size()>0){
            for(Map maps : childList){
                Integer cat_id = (Integer) maps.get("cat_id");
                Map childmap = new HashMap();
                childmap.put("cat_path", cat.getCat_path()+cat_id+"|");
                baseDaoSupport.update("goods_cat", childmap, " cat_id="+cat_id);
            }
        }
    }
    
    protected void checkIsOwner(Integer catId) {
//      String sql = "select userid from  goods_cat  where cat_id=?";
//      int userid = saasDaoSupport.queryForInt(sql, catId);
//      super.checkIsOwner(userid);
    }

    

    /**
     * 保存分类排序
     * 
     * @param cat_ids
     * @param cat_sorts
     */
    public void saveSort(int[] cat_ids, int[] cat_sorts) {
        String sql = "";
        if (cat_ids != null) {
            for (int i = 0; i < cat_ids.length; i++) {
                sql= "update  goods_cat  set cat_order=? where cat_id=?" ;
                baseDaoSupport.execute(sql,  cat_sorts[i], cat_ids[i] );
            }
        }
    }

    public List getNavpath(int catId) {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<Cat> getParents(int catid) {
        Cat cat = this.getById(catid);
        String path=cat.getCat_path();
        path = path.substring(0,path.length()-1);
        path = path.replace("|", ",");
        List lists = new ArrayList();
        this.getParent(catid,lists);
        
        List list = new ArrayList();
        for(int i=(lists.size()-1);i>=0;i--){
            Cat c = (Cat) lists.get(i);
            list.add(c);
        }
        return list;
    }
    
    private List  getParent(Integer catid,List ls){
        if(catid!=null){
            String sql ="select cat_id,name,parent_id,type_id from goods_cat where cat_id="+catid;
            List<Cat> list =  this.baseDaoSupport.queryForList(sql, Cat.class);
            if(!list.isEmpty()){
                for(Cat cat :list){
                    ls.add(cat);
                    this.getParent(cat.getParent_id(),ls);
                }
            }
        }
        return ls;
    }
    
    /**
     * 获取根节点数据除了一键选酒
     * @return
     */
    
    public List<Cat> getRoot() {
        String sql = "select * from es_goods_cat where parent_id=0 and cat_id<>1";
        return this.baseDaoSupport.queryForList(sql, Cat.class);
    }

}
