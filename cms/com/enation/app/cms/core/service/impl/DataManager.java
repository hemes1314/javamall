package com.enation.app.cms.core.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.MultiSite;
import com.enation.app.cms.core.model.DataCat;
import com.enation.app.cms.core.model.DataField;
import com.enation.app.cms.core.model.DataModel;
import com.enation.app.cms.core.plugin.ArticlePluginBundle;
import com.enation.app.cms.core.plugin.IDataSaveEvent;
import com.enation.app.cms.core.plugin.IFieldValueShowEvent;
import com.enation.app.cms.core.service.IDataCatManager;
import com.enation.app.cms.core.service.IDataFieldManager;
import com.enation.app.cms.core.service.IDataManager;
import com.enation.app.cms.core.service.IDataModelManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IntegerMapper;
import com.enation.framework.database.Page;
import com.enation.framework.plugin.IPlugin;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 数据管理
 * 
 * @author kingapex 2010-7-5下午03:55:14
 */
public class DataManager extends BaseSupport implements IDataManager {

	private IDataModelManager dataModelManager;
	private IDataFieldManager dataFieldManager;
	private ArticlePluginBundle articlePluginBundle;
	private IDataCatManager dataCatManager;

	@Transactional(propagation = Propagation.REQUIRED)
	public void add(Integer modelid, Integer catid) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		DataModel dataModel = this.dataModelManager.get(modelid);
		List<DataField> fieldList = dataFieldManager.list(modelid);
		Map<String, Object> article = new HashMap<String, Object>();

		// 激发字段保存事件
		for (DataField field : fieldList) {
			articlePluginBundle.onSave(article, field);
		}

		String sort = request.getParameter("sort");
		String page_title = request.getParameter("page_title");
		String page_keywords = request.getParameter("page_keywords");
		String page_description = request.getParameter("page_description");
		sort = StringUtil.isEmpty(sort) ? "0" : sort;
		String source = request.getParameter("source");
		// Date now = new Date()
		// 上面的写法有时与服务器的日期时间格式设置不匹配，会造成数据无法存入数据库
		// 改为如下写法
		// By lzf 2012-02-13
		Long now = DateUtil.getDateline();
		article.put("cat_id", String.valueOf(catid.intValue()));
		article.put("sort", sort);
		article.put("hit", 0);        // 浏览数
		article.put("page_keywords", page_keywords);
		article.put("page_title", page_title);
		article.put("page_description", page_description);
		article.put("source", source);

		// if("2".equals(EopSetting.DBTYPE)) {
		article.put("add_time", now);
		article.put("lastmodified", now);
		// } else {
		// article.put("add_time",
		// DateUtil.toString(now,"yyyy-MM-dd HH:mm:ss"));
		// article.put("lastmodified",
		// DateUtil.toString(now,"yyyy-MM-dd HH:mm:ss"));
		// }

 
		this.baseDaoSupport.insert(dataModel.getEnglish_name(), article);

		// 激发数据保存事件
		
		
	/** 由于商城中没有eop_data_log表，导致保存数据日志的时候，eclipse报错，暂时取消保存日志。  2015-05-25   whj
	 * 	String article_id = String.valueOf(this.baseDaoSupport.getLastId(dataModel.getEnglish_name()));
		article.put("id", article_id);
		this.articlePluginBundle.onSave(article, dataModel, IDataSaveEvent.DATASAVE_ADD);
	*/	
	}

	public void delete(Integer catid, Integer articleid) {
		DataModel dataModel = this.getModelByCatid(catid);
		// 激发数据删除事件 lzf add 2010-12-01
		this.articlePluginBundle.onDelete(catid, articleid);
		String sql = "delete from " + dataModel.getEnglish_name() + " where id=?";
		this.baseDaoSupport.execute(sql, articleid);
	}
    @Transactional(propagation = Propagation.REQUIRED)
	public void edit(Integer modelid, Integer catid, Integer articleid) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		DataModel dataModel = this.dataModelManager.get(modelid);
		 
		List<DataField> fieldList = dataFieldManager.list(modelid);
		Map article = new HashMap();
		for (DataField field : fieldList) {
			articlePluginBundle.onSave(article, field);
		}

		String page_title = request.getParameter("page_title");
		String page_keywords = request.getParameter("page_keywords");
		String page_description = request.getParameter("page_description");
		String source = request.getParameter("source");
		article.put("page_keywords", page_keywords);
		article.put("page_title", page_title);
		article.put("page_description", page_description);
		article.put("source", source);

		String sort = request.getParameter("sort");
		sort = StringUtil.isEmpty(sort) ? "0" : sort;
		article.put("cat_id", catid);
		article.put("sort", sort);

		// if("2".equals(EopSetting.DBTYPE)){
		article.put("lastmodified", DateUtil.getDateline());
		// }else{
		// article.put("lastmodified", DateUtil.toString( new Date() ,
		// "yyyy-MM-dd HH:mm:ss "));
		// }
		this.baseDaoSupport.update(dataModel.getEnglish_name(), article, "id=" + articleid);
		this.articlePluginBundle.onSave(article, dataModel, IDataSaveEvent.DATASAVE_EDIT);
	}

	public Page list(Integer catid, int page, int pageSize) {
		DataModel model = this.getModelByCatid(catid);
		String sql = "select " + buildFieldStr(model.getModel_id())
				+ ",sort from " + model.getEnglish_name()
				+ " where cat_id=? order by sort desc, add_time desc";
		Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize, catid);
		return webpage;
	}
	public Page pageForWeb(Integer catid, Integer page, int pageSize) {
		DataModel model = this.getModelByCatid(catid);
//		String sql = "select id,cat_id,hit as view_count,click_count,add_time,page_keywords,page_title ,image  " 
//		+ " from " + model.getEnglish_name()
//		+ " where cat_id=? order by sort desc, add_time desc";
//      String sql = "select info.id,info.cat_id,info.hit as view_count,info.click_count,info.add_time,"
//              + " info.page_keywords,info.page_title ,info.image,dc.name ,info.score "
//              + " from " + model.getEnglish_name()+"  info  left join es_data_cat dc on info.cat_id = dc.cat_id  "
//              + " where info.cat_id=? order by sort desc, add_time desc";
      String sql = "select info.id,info.cat_id,info.hit as view_count,info.add_time,"
              + " info.page_keywords,info.page_title ,info.image,dc.name "
              + " from " + model.getEnglish_name()+"  info  left join es_data_cat dc on info.cat_id = dc.cat_id  "
              + " where info.cat_id=? order by sort desc, add_time desc";
          
		Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize, catid);
		return webpage;
	}
	// 2015/9/23 lxl add start 
//	public List list(Integer catid, int page, int pageSize) {
//		DataModel model = this.getModelByCatid(catid);
//		String sql = "select * " 
//		+ "from " + model.getEnglish_name()
//		+ " where cat_id=? order by sort desc, add_time desc";
//		Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize, catid);
//		return webpage;
//	}
	// 2015/9/23 lxl add edn
	public Page list(Integer catid, int page, int pageSize, Integer site_code) {
		DataModel model = this.getModelByCatid(catid);
		String sql = "select "
				+ buildFieldStr(model.getModel_id())
				+ ",sort from "
				+ model.getEnglish_name()
				+ " where cat_id=? and site_code between "
				+ site_code
				+ " and "
				+ StringUtil.getMaxLevelCode(site_code)
				+ " and (not siteidlist like '%,"
				+ EopContext.getContext().getCurrentChildSite().getSiteid()
				+ ",%' or siteidlist is null) order by sort desc, add_time desc";

		Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize, catid);
		return webpage;
	}

	public void importdata(Integer catid, Integer[] ids) {
		DataModel model = this.getModelByCatid(catid);
		String ids_str = StringUtil.arrayToString(ids, ",");
		int site_id = EopContext.getContext().getCurrentChildSite().getSiteid();
		String sql = "update " + model.getEnglish_name()
				+ " set siteidlist = CASE WHEN siteidlist is null THEN ',"
				+ site_id + ",' ELSE CONCAT(siteidlist,'" + site_id
				+ ",') END where id in (" + ids_str + ")";
		this.baseDaoSupport.execute(sql);
	}

	public List list(Integer catid) {
		DataModel model = this.getModelByCatid(catid);
		String sql = "select " + buildFieldStr(model.getModel_id())
				+ ",sort from " + model.getEnglish_name()
				+ " where cat_id=? order by sort desc, add_time desc";
		List webpage = this.baseDaoSupport.queryForList(sql, catid);
		return webpage;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateSort(Integer[] ids, Integer[] sorts, Integer catid) {
		if (ids == null || sorts == null || sorts.length != ids.length)
			throw new IllegalArgumentException("ids or sorts params error");
		DataModel model = this.getModelByCatid(catid);
		String sql = "update " + model.getEnglish_name() + " set sort=? where id=?";

		for (int i = 0; i < ids.length; i++) {
			this.baseDaoSupport.execute(sql, sorts[i], ids[i]);
		}
	}

	public Page listAll(Integer catid, String term, int page, int pageSize) {
		DataModel model = this.getModelByCatid(catid);
		DataCat cat = this.dataCatManager.get(catid);
		StringBuffer sql = new StringBuffer("select * from ");
		sql.append(this.getTableName(model.getEnglish_name()));
		sql.append(" where cat_id in (select cat_id from ");
		sql.append(this.getTableName("data_cat"));
		sql.append(" where cat_path like '");
		sql.append(cat.getCat_path());
		sql.append("%'");
		sql.append(") ");

		if (!StringUtil.isEmpty(term)) {
			sql.append(term);
		}

		sql.append(" order by sort desc, add_time desc");
		final List<DataField> fieldList = this.dataFieldManager.list(model.getModel_id());
		return this.daoSupport.queryForPage(sql.toString(), page, pageSize, new RowMapper() {

			public Object mapRow(ResultSet rs, int c) throws SQLException {
				Map<String, Object> data = new HashMap<String, Object>();
				for (DataField field : fieldList) {
					Object value = null;
					String name = field.getEnglish_name();
					value = rs.getString(name);

					IPlugin plugin = articlePluginBundle.findPlugin(field.getShow_form());

					if (plugin != null) {
						if (plugin instanceof IFieldValueShowEvent) {
							value = ((IFieldValueShowEvent) plugin).onShow(field, value);
						}
					}
					data.put(name, value);
				}
				data.put("id", rs.getInt("id"));
				data.put("cat_id", rs.getInt("cat_id"));
				data.put("sort", rs.getInt("sort"));
				data.put("add_time", rs.getLong("add_time"));
				data.put("lastmodified", rs.getLong("lastmodified"));
				data.put("hit", rs.getLong("hit"));
				data.put("sys_lock", rs.getInt("sys_lock"));

				// 王峰注释下面语句，此种写法性能过于低下
				DataCat cat = dataCatManager.get(rs.getInt("cat_id"));
				data.put("cat_name", cat.getName());
				return data;
			}
		});
	}

	public List listRelated(Integer catid, Integer relcatid, Integer id, String fieldname) {

		Map article = this.get(id, catid, false);
		String ids = (String) article.get(fieldname);
		if (StringUtil.isEmpty(ids)) {
			return new ArrayList();
		}

		DataModel model = this.getModelByCatid(relcatid);

		StringBuffer sql = new StringBuffer("select * from ");
		sql.append(this.getTableName(model.getEnglish_name()));
		sql.append(" where id in (" + ids + ")");
		sql.append(" order by sort desc, add_time desc");

		final List<DataField> fieldList = this.dataFieldManager.list(model.getModel_id());
		return this.daoSupport.queryForList(sql.toString(), new RowMapper() {

			public Object mapRow(ResultSet rs, int c) throws SQLException {
				Map<String, Object> data = new HashMap<String, Object>();
				for (DataField field : fieldList) {
					Object value = null;
					String name = field.getEnglish_name();
					value = rs.getString(name);

					IPlugin plugin = articlePluginBundle.findPlugin(field.getShow_form());
					if (plugin != null) {
						if (plugin instanceof IFieldValueShowEvent) {
							value = ((IFieldValueShowEvent) plugin).onShow(field, value);
						}
					}
					data.put(name, value);
				}
				data.put("id", rs.getInt("id"));
				data.put("cat_id", rs.getInt("cat_id"));
				data.put("sort", rs.getInt("sort"));
				data.put("add_time", rs.getLong("add_time"));
				data.put("lastmodified", rs.getLong("lastmodified"));
				data.put("hit", rs.getLong("hit"));
				data.put("sys_lock", rs.getInt("sys_lock"));

				// 王峰注释下面语句，此种写法性能过于低下
				// DataCat cat = dataCatManager.get(rs.getInt("cat_id"));
				data.put("cat_name", "");
				return data;
			}
		});
	}

	public Map get(Integer articleid, Integer catid, boolean filter) {
		DataModel model = this.getModelByCatid(catid);
		String sql = "select * from " + model.getEnglish_name() + " a left join es_data_cat b on a.cat_id = b.cat_id where id=?";
		Map data = this.baseDaoSupport.queryForMap(sql, articleid);

		if (filter) {
			List<DataField> fieldList = this.dataFieldManager.list(model.getModel_id());

			for (DataField field : fieldList) {
				String name = field.getEnglish_name();
				Object value = data.get(name);
				IPlugin plugin = articlePluginBundle.findPlugin(field.getShow_form());
				if (plugin != null) {
					if (plugin instanceof IFieldValueShowEvent) {
						value = ((IFieldValueShowEvent) plugin).onShow(field, value);
						data.put(name, value);
					}
				}
			}
		}
		return data;
	}
	
	
	public Map getForMobile(Integer articleid, Integer catid, boolean filter) {
        DataModel model = this.getModelByCatid(catid);
        String sql = "select a.*,b.name from " + model.getEnglish_name() + " a left join es_data_cat b on a.cat_id = b.cat_id where id=?";
        Map data = this.baseDaoSupport.queryForMap(sql, articleid);

        if (filter) {
            List<DataField> fieldList = this.dataFieldManager.list(model.getModel_id());

            for (DataField field : fieldList) {
                String name = field.getEnglish_name();
                Object value = data.get(name);
                IPlugin plugin = articlePluginBundle.findPlugin(field.getShow_form());
                if (plugin != null) {
                    if (plugin instanceof IFieldValueShowEvent) {
                        value = ((IFieldValueShowEvent) plugin).onShow(field, value);
                        data.put(name, value);
                    }
                }
            }
        }
        
        String pageDescriptiono = (String) data.get("page_description");
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<meta charset='utf-8' />");
        sb.append("<title></title>");
        sb.append("<style type='text/css'>");
        sb.append("body {margin:0px; text-align:center;}");
        sb.append("img {width:100%; max-width:100%; height:auto; vertical-align:bottom;}");
        sb.append("p {width:90%; max-width:100%; height:auto; line-height:25px; float:left; padding :0 5%; ");
        sb.append("float:left; text-align:left; font-size:14px; font-family:'Microsoft YaHei';");
        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append(pageDescriptiono);
        sb.append("</body>");
        sb.append("</html>");
                
        data.put("page_description", sb.toString());
        this.updateHit(articleid, catid);
        return data;
    }
	

	/**
	 * 更新浏览量
	 */
	public void updateHit(Integer id, Integer catid) {
		DataModel model = this.getModelByCatid(catid);
		this.baseDaoSupport.execute("update " + model.getEnglish_name()	+ " set hit=hit+1 where id=?", id);
	}

	private DataModel getModelByCatid(Integer catid) {
		String sql = "select dm.* from " + this.getTableName("data_model")
				+ " dm ," + this.getTableName("data_cat")
				+ " c where dm.model_id=c.model_id and c.cat_id=?";
		List modelList = this.daoSupport.queryForList(sql, DataModel.class,	catid);
		if (modelList == null || modelList.isEmpty()) {
			throw new RuntimeException("此类别[" + catid + "]不存在模型");
		}
		DataModel model = (DataModel) modelList.get(0);

		return model;
	}

	private String buildFieldStr(Integer modelid) {
		StringBuffer sql = new StringBuffer("id");
		List<DataField> fieldList = this.dataFieldManager.listIsShow(modelid);
		for (DataField field : fieldList) {
			if (field.getIs_show() == 1) {
				sql.append(",");
				sql.append(field.getEnglish_name());
			}
		}
		return sql.toString();
	}

	public List search(int modelid, String connector) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		final List<DataField> fieldList = this.dataFieldManager.list(modelid);
		DataModel model = this.dataModelManager.get(modelid);
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append(model.getEnglish_name());

		int i = 0;
		StringBuffer term = new StringBuffer();
		for (DataField field : fieldList) {

			String showform = field.getShow_form();
			if ("image".equals(showform))
				continue;

			String value = request.getParameter(field.getEnglish_name());
			if (!"utf-8".toLowerCase().equals(request.getCharacterEncoding())) {
				if (value != null)
					value = StringUtil.toUTF8(value);
			}

			String name = field.getEnglish_name();

			FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getCurrInstance();
			freeMarkerPaser.putData(name, value);

			if ("radio".equals(showform) || "select".equals(showform)) {
				if (StringUtil.isEmpty(value))
					continue;
				if (i != 0)
					term.append(connector);
				term.append(name);
				term.append(" ='");
				term.append(value);
				term.append("'");
			} else if ("dateinput".equals(showform)) {

				// 对于日期数据要进行区间查询
				String paramname = field.getEnglish_name();
				String start = request.getParameter(paramname + "_start");
				String end = request.getParameter(paramname + "_end");
				if (!StringUtil.isEmpty(start) || !StringUtil.isEmpty(end)) {
					term.append("(");
					if (!StringUtil.isEmpty(start)) {
						term.append(name);
						term.append(">='");
						term.append(start);
						term.append("'");
					}

					if (!StringUtil.isEmpty(end)) {
						if (!StringUtil.isEmpty(start)) {
							term.append(connector);
						}
						term.append(name);
						term.append("<='");
						term.append(end);
						term.append("'");
					}
					term.append(")");
				}
			} else {
				if (StringUtil.isEmpty(value))
					continue;
				if (i != 0)
					term.append(connector);
				term.append(name);
				term.append(" like '%");
				term.append(value);
				term.append("%'");
			}
			i++;
		}

		if (term.length() > 0) {
			sql.append(" where ");
			sql.append(term);
		}

		return this.baseDaoSupport.queryForList(sql.toString(),	new RowMapper() {
			public Object mapRow(ResultSet rs, int c) throws SQLException {
				Map data = new HashMap();
				for (DataField field : fieldList) {
					Object value = null;
					String name = field.getEnglish_name();
					value = rs.getString(name);

					IPlugin plugin = articlePluginBundle.findPlugin(field.getShow_form());
					if (plugin != null) {
						if (plugin instanceof IFieldValueShowEvent) {
							value = ((IFieldValueShowEvent) plugin).onShow(field, value);
						}
					}
					data.put(name, value);
				}
				data.put("id", rs.getInt("id"));
				data.put("cat_id", rs.getInt("cat_id"));
				data.put("add_time", rs.getLong("add_time"));
				data.put("hit", rs.getLong("hit"));
				return data;
			}
		});
	}

	public List search(int modelid, String connector, boolean showchild) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		final List<DataField> fieldList = this.dataFieldManager.list(modelid);
		DataModel model = this.dataModelManager.get(modelid);
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append(model.getEnglish_name());

		int i = 0;
		StringBuffer term = new StringBuffer();
		for (DataField field : fieldList) {

			String showform = field.getShow_form();
			if ("image".equals(showform))
				continue;

			String value = request.getParameter(field.getEnglish_name());
			if (!"utf-8".toLowerCase().equals(request.getCharacterEncoding())) {
				if (value != null)
					value = StringUtil.toUTF8(value);
			}

			String name = field.getEnglish_name();

			FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getCurrInstance();
			freeMarkerPaser.putData(name, value);

			if ("radio".equals(showform) || "select".equals(showform)) {
				if (StringUtil.isEmpty(value))
					continue;
				if (i != 0)
					term.append(connector);
				term.append(name);
				term.append(" ='");
				term.append(value);
				term.append("'");
			} else if ("dateinput".equals(showform)) {
				// 对于日期数据要进行区间查询
				String paramname = field.getEnglish_name();
				String start = request.getParameter(paramname + "_start");
				String end = request.getParameter(paramname + "_end");
				if (!StringUtil.isEmpty(start) || !StringUtil.isEmpty(end)) {
					term.append("(");
					if (!StringUtil.isEmpty(start)) {
						term.append(name);
						term.append(">='");
						term.append(start);
						term.append("'");
					}

					if (!StringUtil.isEmpty(end)) {
						if (!StringUtil.isEmpty(start)) {
							term.append(connector);
						}
						term.append(name);
						term.append("<='");
						term.append(end);
						term.append("'");
					}
					term.append(")");
				}

			} else {
				if (StringUtil.isEmpty(value))
					continue;
				if (i != 0)
					term.append(connector);
				term.append(name);
				term.append(" like '%");
				term.append(value);
				term.append("%'");
			}
			i++;
		}

	 
		if (term.length() > 0) {
			sql.append(" where ");
			sql.append(term);
		}

		return this.baseDaoSupport.queryForList(sql.toString(),	new RowMapper() {
			public Object mapRow(ResultSet rs, int c) throws SQLException {
				Map data = new HashMap();
				for (DataField field : fieldList) {
					Object value = null;
					String name = field.getEnglish_name();

					value = rs.getString(name);

					IPlugin plugin = articlePluginBundle.findPlugin(field.getShow_form());
					if (plugin != null) {
						if (plugin instanceof IFieldValueShowEvent) {
							value = ((IFieldValueShowEvent) plugin).onShow(field, value);
						}
					}
					data.put(name, value);
				}
				data.put("id", rs.getInt("id"));
				data.put("cat_id", rs.getInt("cat_id"));
				data.put("add_time", rs.getLong("add_time"));
				data.put("hit", rs.getLong("hit"));
				return data;
			}
		});
	}

	public Page search(int pageNo, int pageSize, int modelid, String connector,String catid) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		final List<DataField> fieldList = this.dataFieldManager.list(modelid);
		DataModel model = this.dataModelManager.get(modelid);
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append(model.getEnglish_name());

		StringBuffer term = new StringBuffer();
		for (DataField field : fieldList) {
			String showform = field.getShow_form();
			if ("image".equals(showform))
				continue;

			String value = request.getParameter(field.getEnglish_name());
			if (!request.getCharacterEncoding().toLowerCase().equals("utf-8")) {
				if (value != null)
					value = StringUtil.toUTF8(value);
			}
			String name = field.getEnglish_name();

			FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getCurrInstance();
			freeMarkerPaser.putData(name, value);

			if ("radio".equals(showform) || "select".equals(showform)) {
				if (StringUtil.isEmpty(value))
					continue;
				if (term.length() > 0)
					term.append(connector);
				term.append(name);
				term.append(" ='");
				term.append(value);
				term.append("'");
			} else if ("dateinput".equals(showform)) {
				// 对于日期数据要进行区间查询
				String paramname = field.getEnglish_name();
				String start = request.getParameter(paramname + "_start");
				String end = request.getParameter(paramname + "_end");
				if (!StringUtil.isEmpty(start) || !StringUtil.isEmpty(end)) {
					if (term.length() > 0)
						term.append(connector);

					term.append("(");
					if (!StringUtil.isEmpty(start)) {
						term.append(name);
						term.append(">='");
						term.append(start);
						term.append("'");
					}
					if (!StringUtil.isEmpty(end)) {
						if (!StringUtil.isEmpty(start)) {
							term.append(connector);
						}
						term.append(name);
						term.append("<='");
						term.append(end);
						term.append("'");
					}
					term.append(")");
				}
			} else {
				if (StringUtil.isEmpty(value))
					continue;
				if (term.length() > 0)
					term.append(connector);
				term.append(name);
				term.append(" like '%");
				term.append(value);
				term.append("%'");
			}
		}

		/*
		 * Liuzy add 增加分类查询
		 */
		
		if (catid != null) {
			if (catid.startsWith("lt"))
				catid = catid.replaceAll("lt", "<");
			else if (catid.startsWith("gt"))
				catid = catid.replaceAll("gt", ">");
			else
				catid = "=" + catid;
			if (term.length() > 0)
				term.append(connector);
			term.append(" cat_id" + catid);
		}

		if (term.length() > 0) {
			sql.append(" where ");
			sql.append(term);
		}
		sql.append(" order by sort desc, add_time desc");
		return this.baseDaoSupport.queryForPage(sql.toString(), pageNo,	pageSize, new RowMapper() {

			@SuppressWarnings("unchecked")
			public Object mapRow(ResultSet rs, int c) throws SQLException {
				Map data = new HashMap();
				for (DataField field : fieldList) {
					Object value = null;
					String name = field.getEnglish_name();
					value = rs.getString(name);

					IPlugin plugin = articlePluginBundle.findPlugin(field.getShow_form());
					if (plugin != null) {
						if (plugin instanceof IFieldValueShowEvent) {
							value = ((IFieldValueShowEvent) plugin).onShow(field, value);
						}
					}
					data.put(name, value);
				}
				data.put("id", rs.getInt("id"));
				data.put("cat_id", rs.getInt("cat_id"));
				data.put("add_time", rs.getLong("add_time"));
				data.put("lastmodified", rs.getLong("lastmodified"));
				data.put("hit", rs.getLong("hit"));
				return data;
			}
		});
	}

	public Page search(int pageNo, int pageSize, int modelid, String connector, boolean showchild) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		final List<DataField> fieldList = this.dataFieldManager.list(modelid);
		DataModel model = this.dataModelManager.get(modelid);
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append(model.getEnglish_name());

		int i = 0;
		StringBuffer term = new StringBuffer();
		for (DataField field : fieldList) {
			String showform = field.getShow_form();
			if ("image".equals(showform))
				continue;

			String value = request.getParameter(field.getEnglish_name());
			if (value != null)
				value = StringUtil.toUTF8(value);

			String name = field.getEnglish_name();

			if ("radio".equals(showform) || "select".equals(showform)) {
				if (StringUtil.isEmpty(value))
					continue;
				if (i != 0)
					sql.append(connector);
				term.append(name);
				term.append(" ='");
				term.append(value);
				term.append("'");
			} else if ("dateinput".equals(showform)) {
				// 对于日期数据要进行区间查询
				String paramname = field.getEnglish_name();
				String start = request.getParameter(paramname + "_start");
				String end = request.getParameter(paramname + "_end");
				if (!StringUtil.isEmpty(start) || !StringUtil.isEmpty(end)) {
					term.append("(");
					if (!StringUtil.isEmpty(start)) {
						term.append(name);
						term.append(">='");
						term.append(start);
						term.append("'");
					}

					if (!StringUtil.isEmpty(end)) {
						if (!StringUtil.isEmpty(start)) {
							term.append(connector);
						}
						term.append(name);
						term.append("<='");
						term.append(end);
						term.append("'");
					}
					term.append(")");
				}
			} else {
				if (StringUtil.isEmpty(value))
					continue;
				if (i != 0)
					sql.append(connector);
				term.append(name);
				term.append(" like '%");
				term.append(value);
				term.append("%'");
			}
			i++;
		}

		 

		if (term.length() > 0) {
			sql.append(" where ");
			sql.append(term);
		}

		return this.baseDaoSupport.queryForPage(sql.toString(), pageNo,	pageSize, new RowMapper() {

			public Object mapRow(ResultSet rs, int c) throws SQLException {
				Map data = new HashMap();
				for (DataField field : fieldList) {
					Object value = null;
					String name = field.getEnglish_name();

					value = rs.getString(name);

					IPlugin plugin = articlePluginBundle.findPlugin(field.getShow_form());
					if (plugin != null) {
						if (plugin instanceof IFieldValueShowEvent) {
							value = ((IFieldValueShowEvent) plugin).onShow(field, value);
						}
					}
					data.put(name, value);
				}
				data.put("id", rs.getInt("id"));
				data.put("cat_id", rs.getInt("cat_id"));
				data.put("add_time", rs.getLong("add_time"));
				data.put("hit", rs.getLong("hit"));
				return data;
			}
		});
	}

	/**
	 * 
	 */
	public Map census() {
		List<DataModel> modelList = this.dataModelManager.list();
		String sql;
		int count = 0;

		// 读取模型列表，并且读取模型相应的信息数，累加之后为当前站点的信息数量和。
		for (DataModel model : modelList) {
			String tbname = model.getEnglish_name();
			sql = "select count(0)  from " + tbname;
			count += this.baseDaoSupport.queryForInt(sql);

		}
		// 读取栏目数
		sql = "select count(0) from data_cat";
		int catcount = this.baseDaoSupport.queryForInt(sql);

		// 读取留言数
		sql = "select count(0)  from " + this.getTableName("guestbook")	+ " g where parentid=0"; // and g.id not in(select parentid from
											// "+ this.getTableName("guestbook")+"
											// )";
		int msgcount = this.daoSupport.queryForInt(sql);

		Map<String, Integer> map = new HashMap<String, Integer>(3);
		map.put("count", count);
		map.put("catcount", catcount);
		map.put("msgcount", msgcount);
		return map;
	}

	public Page listAll(Integer catid, String term, String orders, boolean showchild, int page, int pageSize) {
		DataModel model = this.getModelByCatid(catid);
		DataCat cat = this.dataCatManager.get(catid);
		StringBuffer sql = new StringBuffer("select * from ");
		sql.append(this.getTableName(model.getEnglish_name()));
		sql.append(" where cat_id in (select cat_id from ");
		sql.append(this.getTableName("data_cat"));
		sql.append(" where cat_path like '");
		sql.append(cat.getCat_path());
		sql.append("%'");
		sql.append(") ");

		if (!StringUtil.isEmpty(term)) {
			sql.append(term);
		}

		 

		if (!StringUtil.isEmpty(orders)) {
			sql.append(" order by " + orders);
		} else {
			sql.append(" order by sort desc, add_time desc");
		}
		final List<DataField> fieldList = this.dataFieldManager.list(model.getModel_id());
		Page datalist = this.daoSupport.queryForPage(sql.toString(), page, pageSize, new RowMapper() {

			public Object mapRow(ResultSet rs, int c) throws SQLException {
				Map data = new HashMap();
				for (DataField field : fieldList) {
					Object value = null;
					String name = field.getEnglish_name();
					value = rs.getString(name);

					IPlugin plugin = articlePluginBundle.findPlugin(field.getShow_form());
					if (plugin != null) {
						if (plugin instanceof IFieldValueShowEvent) {
							value = ((IFieldValueShowEvent) plugin).onShow(field, value);
						}
					}
					data.put(name, value);
				}
				data.put("id", rs.getInt("id"));
				data.put("cat_id", rs.getInt("cat_id"));
				data.put("sort", rs.getInt("sort"));
				data.put("add_time", rs.getLong("add_time"));
				data.put("lastmodified", rs.getLong("lastmodified"));
				data.put("hit", rs.getLong("hit"));
				data.put("sys_lock", rs.getInt("sys_lock"));
				data.put("site_code", rs.getInt("site_code"));
				if (rs.getInt("cat_id") == 1 || rs.getInt("cat_id") == 2) {
				    data.put("page_title", rs.getString("page_title"));
				} else {
                    data.put("page_title", "<a href='/document/list_inner.html?cat_id="+ rs.getInt("cat_id") +"&id="+ rs.getInt("id") +
                            "' target='_blank'>" + 
                            rs.getString("page_title") + "</a>");
                }

				// 经测试，有严重性能问题，暂时注释
				// DataCat cat =
				// dataCatManager.get(rs.getInt("cat_id"));
				data.put("cat_name", "");
				return data;
			}
		});
		this.logger.debug("查询sql[" + sql + "]完成");
		return datalist;
	}

	/**
	 * 获取某篇文章的当前类别下下一个文章id，如果是最后一篇则返回0
	 * 
	 * @param currentId
	 * @return
	 */
	public int getNextId(Integer currentId, Integer catid) {
		DataModel model = this.getModelByCatid(catid);
		String sql = "select min(id) from " + model.getEnglish_name() + " where cat_id=? and id>?";
		List<Integer> list = this.baseDaoSupport.queryForList(sql, new IntegerMapper(), catid, currentId);
		if (list == null || list.isEmpty())
			return 0;
		return list.get(0);
	}

	/**
	 * 获取某篇文章的当前类别下的上一篇文章id,如果是第一篇则为0
	 * 
	 * @param currentId
	 * @param catid
	 * @return
	 */
	public int getPrevId(Integer currentId, Integer catid) {
		DataModel model = this.getModelByCatid(catid);
		String sql = "select max(id) from " + model.getEnglish_name() + " where cat_id=? and id<?";
		List<Integer> list = this.baseDaoSupport.queryForList(sql, new IntegerMapper(), catid, currentId);
		if (list == null || list.isEmpty())
			return 0;
		return list.get(0);
	}

	public IDataModelManager getDataModelManager() {
		return dataModelManager;
	}

	public void setDataModelManager(IDataModelManager dataModelManager) {
		this.dataModelManager = dataModelManager;
	}

	public IDataFieldManager getDataFieldManager() {
		return dataFieldManager;
	}

	public void setDataFieldManager(IDataFieldManager dataFieldManager) {
		this.dataFieldManager = dataFieldManager;
	}

	public ArticlePluginBundle getArticlePluginBundle() {
		return articlePluginBundle;
	}

	public void setArticlePluginBundle(ArticlePluginBundle articlePluginBundle) {
		this.articlePluginBundle = articlePluginBundle;
	}

	public IDataCatManager getDataCatManager() {
		return dataCatManager;
	}

	public void setDataCatManager(IDataCatManager dataCatManager) {
		this.dataCatManager = dataCatManager;
	}

    @Override
    public Page searchForApp(int page, int PAGE_SIZE, String keyWords,Integer catid) {
        DataModel model = this.getModelByCatid(catid);
        String sql = "select id,cat_id,add_time,page_keywords,page_title ,image ,score " 
        + " from " + model.getEnglish_name()
        + " where cat_id=? and page_keywords like '%"+keyWords+"%' order by sort desc, add_time desc";
       
        return this.daoSupport.queryForPage(sql, page, PAGE_SIZE, catid);
    }

    @Override
    public void toClick(Integer id, Integer catid) {
       
        DataModel model = this.getModelByCatid(catid);
        String sql1 = "select * from " + model.getEnglish_name() + " where id=?";
        Integer num= (Integer) this.baseDaoSupport.queryForMap(sql1, id).get("click_count");
        String sql3 = "update " + model.getEnglish_name() + " set click_count =? +1  where id=?";
        this.baseDaoSupport.execute(sql3, num,id);     
        
    }

    @Override
    public Page searchWordsForApp(int page, int pageSize, String keyword) {
        
        String sql = "select * " 
        + " from es_wine_key " 
        + " where lower(title) like '%'||lower('"+keyword+"')||'%' order by sort desc, add_time desc";
       
        return this.daoSupport.queryForPage(sql, page, pageSize);
    }

    @Override
    public List getYears(String keyword) {
        String sql = " select year from es_winescore where title like '%"+keyword+"%'  GROUP BY year order by year desc";
       
        return this.daoSupport.queryForList(sql);
    }

    @Override
    public Page getWineScore(int page, int pageSize, String keyword, String year) {
        String sql = "";
        
        if (year != null && !year.isEmpty()) {
            sql = " select * from es_winescore where title like '%"+keyword+"%' and year=? order by sort desc";
            return this.daoSupport.queryForPage(sql, page, pageSize, year);
        } else {
            sql = " select * from es_winescore where title like '%"+keyword+"%' order by sort desc";
            return this.daoSupport.queryForPage(sql, page, pageSize);
        }
    }

    @Override
    public Page searchArticle(int page, int pageSize, String keyWords) {
        String sql = "select id,cat_id,add_time,page_keywords,page_title ,image ,score " 
            + " from es_information "
            + " where  page_keywords like '%"+keyWords+"%' order by sort desc, add_time desc";
        return this.daoSupport.queryForPage(sql, page, pageSize);
    }
    


}
