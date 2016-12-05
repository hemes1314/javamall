/**
 * 
 */
package com.enation.app.shop.component.goodsindex.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PackedTokenAttributeImpl;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.DrillDownQuery;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.TaxonomyWriter;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.NumericUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.chenlb.mmseg4j.analysis.SimpleAnalyzer;
import com.chenlb.mmseg4j.analysis.TokenUtils;
import com.enation.app.base.core.model.TaskProgress;
import com.enation.app.base.core.service.ProgressContainer;
import com.enation.app.shop.component.goodsindex.GoodsIndexPluginBundle;
import com.enation.app.shop.component.goodsindex.LuceneSetting;
import com.enation.app.shop.component.goodsindex.model.GoodsWords;
import com.enation.app.shop.component.goodsindex.service.IGoodsIndexManager;
import com.enation.app.shop.component.goodsindex.service.ISearchSelectorCreator;
import com.enation.app.shop.component.goodsindex.service.PinYinUtil;
import com.enation.app.shop.core.model.Attribute;
import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.model.Goodslist;
import com.enation.app.shop.core.model.support.GoodsTypeDTO;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.IGoodsTypeManager;
import com.enation.app.shop.core.service.Separator;
import com.enation.app.shop.core.service.impl.GoodsManager;
import com.enation.app.shop.core.utils.ParamsUtils;
import com.enation.app.shop.core.utils.SortContainer;
import com.enation.app.shop.mobile.service.impl.ApiCommentManager;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.jms.EopProducer;
import com.enation.framework.jms.IJmsProcessor;
import com.enation.framework.util.StringUtil;

/**
 * 商品索引管理实现类
 * @author kingapex
 *2015-4-16
 */
@Component
public class GoodsIndexManager implements IGoodsIndexManager,IJmsProcessor {
	
	private IDaoSupport daoSupport;
	private EopProducer eopProducer;
	public final  static String  PRGRESSID="lucene_create"; //进度id
	private IGoodsTypeManager goodsTypeManager;
	private IGoodsCatManager goodsCatManager;
	private String[] selectorCreators ={"catSelectorCreator","propSelectorCreator","brandSelectorCreator","sortSelectorCreator","priceSelectorCreator"};
	protected final Logger logger = Logger.getLogger(getClass());
	private GoodsIndexPluginBundle goodsIndexPluginBundle;
	private GoodsManager goodsManager;
	private ApiCommentManager apiCommentManager;
	
	/* (non-Javadoc)
	 * @see com.enation.framework.jms.IJmsProcessor#process(java.lang.Object)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void process(Object data) {
	    this.addAallIndex();
		
	}
 
	
	
	/**
	 * 将商品名称按中文名、英文全拼、首字母进行索引<br>
	 * 同时将名称的分词写入数据库，以便形成搜索建议
	 */
	@Override
	public void addIndex(Map goods) {
		
		try {
			
			IndexWriter indexWriter =this.getIndexWriter();
			//维度
		    TaxonomyWriter taxoWriter = getTaxoWirter();
	
			String goods_name = goods.get("name").toString();
			List<String> wordsList = toWordsList(goods_name);
			this.fillGoodsPinyin(goods);
			
			Document doc  = createDocument(goods);
			indexWriter.addDocument(config.build(taxoWriter, doc));
			
			
			this.wordsToDb(wordsList);// 分词入库
	
			indexWriter.close();

			taxoWriter.close();
		} catch (Exception e) {
			this.logger.error("商品索引错误",e);
			throw new RuntimeException("商品索引错误");
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.enation.app.shop.component.goodsindex.service.IGoodsIndexManager#updateIndex(java.util.Map)
	 */
	@Override
	public void updateIndex(Map goods) {
		
		try {
			
			IndexWriter indexWriter =this.getIndexWriter();
			//维度
		   TaxonomyWriter taxoWriter = getTaxoWirter();
		    
			this.fillGoodsPinyin(goods);
			
			Document doc = this.createDocument(goods);
			Term term = new Term("goods_id", goods.get("goods_id").toString());
			 
			indexWriter.updateDocument(term,config.build(taxoWriter, doc));
			
			indexWriter.close();
			taxoWriter.close();
		} catch (Exception e) {
			this.logger.error("商品索引错误",e);
			throw new RuntimeException("商品索引错误");
		}
		
		
	}

	
	/* (non-Javadoc)
	 * @see com.enation.app.shop.component.goodsindex.service.IGoodsIndexManager#deleteIndex(java.util.Map)
	 */
	@Override
	public void deleteIndex(Map goods) {
		try {
			
			String goods_name = goods.get("name").toString();
			List<String> wordsList = toWordsList(goods_name);
			
			IndexWriter indexWriter =  this.getIndexWriter();
			Term term = new Term("goods_id", goods.get("goods_id").toString());
			indexWriter.deleteDocuments(term);
			indexWriter.close();
			this.deleteWords(wordsList);
		} catch (Exception e) {
			this.logger.error("商品索引错误",e);
			throw new RuntimeException("商品索引错误");
		}
	}

	
	/**
	 * 将list中的分词减一
	 * @param wordsList
	 */
	private void deleteWords(List<String> wordsList){
		for (String words : wordsList) {
			this.daoSupport.execute("update es_goods_words set goods_num=goods_num-1 where words=?", words);
		}
	}
	private final  FacetsConfig config = new FacetsConfig();
	
	/* (non-Javadoc)
	 * @see com.enation.app.shop.component.goodsindex.service.IGoodsIndexManager#addAallIndex()
	 */
	@Override
	public void addAallIndex() {
		try {
			
			String index_path = LuceneSetting.INDEX_PATH+"/content";
			String facet_path = LuceneSetting.INDEX_PATH+"/facet";
			//先全部删除，再重建
			 File file= new File(index_path);
			 file.deleteOnExit();
			 file.mkdirs();
			 
			 file= new File(facet_path);
			 file.deleteOnExit();
			 file.mkdirs();
			 
			
			IndexWriter indexWriter =this.getIndexWriter();
			
			//维度
		    TaxonomyWriter taxoWriter = getTaxoWirter();
		     
		     
			indexWriter.deleteAll();
			this.daoSupport.execute("delete from es_goods_words");
			
			//商品总数
			int goods_count = this.getGoodsCount();
			int page_size=100; //100条查一次
			int page_count = 0;
			page_count = goods_count / page_size;
			page_count = goods_count % page_size > 0 ? page_count + 1 : page_count;
			
			//生成任务进度
			TaskProgress taskProgress = new TaskProgress(goods_count);
			ProgressContainer.putProgress(PRGRESSID, taskProgress);
			System.out.println("put "+PRGRESSID);
			for(int i=1;i<=page_count;i++){
				List<Map> goodsList  = this.daoSupport.queryForListPage("select * from es_goods",i, page_size);
				for (Map goods : goodsList) {

					
					String goods_name = goods.get("name").toString();
					List<String> wordsList = toWordsList(goods_name);
					
					this.fillGoodsPinyin(goods);
					
					Document doc  = createDocument(goods);
					indexWriter.addDocument(config.build(taxoWriter, doc));
					
					this.wordsToDb(wordsList);// 分词入库
			
					ProgressContainer.getProgress(PRGRESSID).step("正在为["+goods.get("name")+"]生成索引");
					
				}
				
			}
			indexWriter.close();
			taxoWriter.close();
			ProgressContainer.getProgress(PRGRESSID).success();
			
		} catch (Exception e) {
			
			
			
			this.logger.error("商品索引错误",e);
			ProgressContainer.getProgress(PRGRESSID).fail("生成索引出错:"+e.getMessage());
			throw new RuntimeException("商品索引错误");
		
		}
		
	}
	
	
	
	
	/**
	 * 获取分词结果
	 * @param txt
	 * @return 分词list
	 */
	public static List<String> toWordsList(String txt) {
		
		List<String> list = new ArrayList<String>();
		TokenStream ts = null;
		try {
			ts = getAnalyzer().tokenStream("text", new StringReader(txt));
			ts.reset();
			for(PackedTokenAttributeImpl t= new PackedTokenAttributeImpl(); (t=TokenUtils.nextToken(ts, t)) !=null;) {
//				System.out.println(t.toString());
				list.add(t.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ts != null) {
				try {
					ts.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return list;
	}
	
	public static void main(String[] args) {
		String str ="中国长城桑干酒庄赤霞珠干红葡萄酒";
//	    String str ="约翰保罗爱慕法国红酒";
		List<String> wordList = toWordsList(str);
		for(String word : wordList){
		    System.out.println(word);
		}
	}
	

	/* (non-Javadoc)
	 * @see com.enation.app.shop.component.goodsindex.service.IGoodsIndexManager#getGoodsWords(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsWords> getGoodsWords(String keyword) {
		String sql ="select words,goods_num from es_goods_words where words like  '"+keyword+"%' or quanpin like '"+keyword+"%' or szm like '"+keyword+"%' order by goods_num desc";
		return (List)this.daoSupport.queryForPage(sql,1,15,GoodsWords.class).getResult();
	}
	
	
	/* (non-Javadoc)
	 * @see com.enation.app.shop.component.goodsindex.service.IGoodsIndexManager#search(int, int)
	 */
	@Override
	public Page search(int pageNo, int pageSize) {
		 try {
			 

			 IndexSearcher searcher = this.getIndexSearcher();
			  
		   
			 int maxResultNum= pageNo*pageSize;

			 List list  = new ArrayList();
			 Query query  = this.createQuery();
			  
			 
			  Sort sort = this.getSort();
			  
			  TopDocs results = searcher.search(query, maxResultNum, sort) ;
			 
			  ScoreDoc[] hits = results.scoreDocs;
			  int totalCount = results.totalHits;
			  int start = pageSize*(pageNo-1);
//			  System.out.println("共检索出 "+totalCount+" 条记录");
			  int end =Math.min(pageNo * pageSize, totalCount);
			  for (int i = start ; i < end; i++)
			  {
				  Document  document = searcher.doc(hits[i].doc);
			      list.add(document);
			  }
			  
			  Page webPage = new Page(0, totalCount, pageSize, list);
			  return webPage;
		} catch (Exception e) {
			e.printStackTrace();
			 throw new RuntimeException(e);
		}
		
	 
	}
	
	
	public Page searchForApp(int pageNo, int pageSize) {
        try {
            IndexSearcher searcher = this.getIndexSearcher();
            int maxResultNum= pageNo*pageSize;
            List<Goodslist> list  = new ArrayList<Goodslist>();
            Query query  = this.createQuery();
            Sort sort = this.getSort(); 
            TopDocs results = searcher.search(query, maxResultNum,sort) ;
            ScoreDoc[] hits = results.scoreDocs;
            int totalCount = results.totalHits;
            int start = pageSize*(pageNo-1);
//          System.out.println("共检索出 "+totalCount+" 条记录");
            int end =Math.min(pageNo * pageSize, totalCount);
             
            for (int i = start ; i < end; i++) {
                Document  document = searcher.doc(hits[i].doc);
                Map map = goodsManager.get(Integer.valueOf(document.get("goods_id")));
                
                Goodslist goodsList = new Goodslist();
                goodsList.setName(document.get("name"));
                goodsList.setGoods_id(Integer.valueOf(document.get("goods_id")));
                goodsList.setThumbnail(document.get("thumbnail"));
                goodsList.setPrice(Double.valueOf(document.get("price")));
                goodsList.setBig(map.get("big") == null ? null : map.get("big").toString());
                goodsList.setBrief(map.get("brief") == null ? null : map.get("brief").toString());
                goodsList.setBuy_count(map.get("buy_count") == null ? null : (int)map.get("buy_count"));
                goodsList.setComment_num(map.get("comment_num") == null ? null : (int)map.get("comment_num"));
                goodsList.setGrade(map.get("grade") == null ? null : (int)map.get("grade"));
                goodsList.setGoods_transfee_charge(map.get("goods_transfee_charge")==null ? null : map.get("goods_transfee_charge").toString());
                list.add(goodsList);
            }
             
            Page webPage = new Page(0, totalCount, pageSize, list);
            return webPage;
       } catch (Exception e) {
           e.printStackTrace();
            throw new RuntimeException(e);
       }
        
	}
	
	
	/**
	 * 由reuquest的sort参数 生成sort对象 
	 * @return
	 */
	private Sort getSort(){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();

		String sortfield = request.getParameter("sort");
		
		//for mobile api search
		if (sortfield == null) {
		    sortfield = "def_asc";
		}
		 
		Map<String,String> sortMap  = SortContainer.getSort(sortfield);
		 
		 boolean desc ="desc".equals(sortMap.get("def_sort") );
		 
		//如果是默认排序，则按店铺ID升序进行排序
		Sort sort = null;
		if(sortMap.get("id").equals("def")){
//		    sort = new Sort(new SortField("store_sort", SortField.Type.LONG,false));
		    sort = new Sort(SortField.FIELD_SCORE, new SortField("store_sort", SortField.Type.LONG,desc));  
		}else{
		    if (sortMap.get("id").equals("grade")) {
		        sort = new Sort(new SortField("commentnum_sort", SortField.Type.LONG,desc));
		    } else {
		        sort = new Sort(new SortField(sortMap.get("id")+"_sort", SortField.Type.LONG,desc));
		    }
//		    sort = new Sort(SortField.FIELD_SCORE,new SortField(sortMap.get("id")+"_sort", SortField.Type.LONG, desc)); 
		}

		
		return sort;
	}
	
	/* (non-Javadoc)
	 * @see com.enation.app.shop.component.goodsindex.service.IGoodsIndexManager#createSelector()
	 */
	@Override
	public Map<String, Object> createSelector() {
		long start = System.currentTimeMillis();
		
		try {
			HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
			String servlet_path = request.getServletPath();
			
			Query query = this.createQuery();
			DrillDownQuery drillDownQuery = new DrillDownQuery(config, query);  
			
			
			String[] prop_ar = ParamsUtils.getProps();
			for (String p : prop_ar) {
				 String[] onprop_ar=p.split(Separator.separator_prop_vlaue);
				 drillDownQuery.add(onprop_ar[0], onprop_ar[1]);  
			}
		
			
			Map<String, Object> map = new HashMap<String, Object>(); //要返回的结果
			
			TaxonomyReader taxoReader  = this.getTaxoReader();
			FacetsCollector fc = new FacetsCollector();  
			
			IndexSearcher searcher=this.getIndexSearcher();
			FacetsCollector.search(searcher, drillDownQuery, 10, fc);  
			Facets facets = new FastTaxonomyFacetCounts(taxoReader, config, fc);  
			
		    List<FacetResult> results = facets.getAllDims(10);  
		    //处理选择器
		    for (String creatorid : selectorCreators) {
				ISearchSelectorCreator creator =SpringContextHolder.getBean(creatorid);
				creator.createAndPut(map, results);
				
			}
		    long end = System.currentTimeMillis();
//		    System.out.println(end-start);
		    return map;
		    
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		  
	}
	

 
	/**
     * 根据搜索条件创建query
     * @return
     * @throws ParseException
     */
    private Query createQuery() throws ParseException {
        
        HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
        String keyword =request.getParameter("keyword");
        String cat = request.getParameter("cat");
        String brand = request.getParameter("brand_id");
        String price = request.getParameter("price");
        String selfStore = request.getParameter("self_store");
        
      //for mobile api search       add by Tension
        if (cat == null) {
            if (request.getAttribute("cat") != null && !request.getAttribute("cat").equals("0")) {
                cat = request.getAttribute("cat") == null ? null : request.getAttribute("cat").toString();
            }
        }
        
        Analyzer analyzer = this.getAnalyzer();
        
        BooleanQuery query = new BooleanQuery();
        
        //关键字检索
        if(!StringUtil.isEmpty(keyword)){
            QueryParser parser = new QueryParser("name", analyzer);
            Query queryname;
            // 2015/10/24 humaodong catch ParseException
            try {
                queryname = parser.parse( keyword );
            } catch(ParseException e) {
                queryname = parser.parse("...");
            }
        
            query.add(queryname,BooleanClause.Occur.MUST);
        }
        
        //品牌搜素
        if(!StringUtil.isEmpty(brand)){
            Query brandquery  = new TermQuery(new Term("brand_id",brand));
            query.add(brandquery,BooleanClause.Occur.MUST);
        }
        
        //自营店       add by Tension
        if (!StringUtil.isEmpty(selfStore)) {
            Query storeQuery  = new TermQuery(new Term("store_id",selfStore));
            query.add(storeQuery, BooleanClause.Occur.MUST);
        }
        
        //分类检索
        if(!StringUtil.isEmpty(cat)){
            
             String[] catar = cat.split(Separator.separator_prop_vlaue);
             String catid =  catar[catar.length-1];

             Cat goodscat  =this.goodsCatManager.getById(StringUtil.toInt(catid,0));
             
             if(goodscat==null) throw new UrlNotFoundException();
             
             PrefixQuery catquery = new PrefixQuery(new Term("catpath",goodscat.getCat_path())); //只查出最后的分类（最小的子类）
             query.add(catquery,BooleanClause.Occur.MUST);
        }
     
        
        
        //属性检索
        String[] prop_ar = ParamsUtils.getProps();
        for (String p : prop_ar) {
            String[] onprop_ar=p.split(Separator.separator_prop_vlaue);
            //QueryParser parser1 = new QueryParser(onprop_ar[0], analyzer);
            //Query query1 =  parser1.parse( onprop_ar[1] );
            //query.add(query1,BooleanClause.Occur.MUST);
            Query brandquery  = new TermQuery(new Term(onprop_ar[0], onprop_ar[1]));
            query.add(brandquery,BooleanClause.Occur.MUST);
        }
        
        
        if(!StringUtil.isEmpty(price)){
            
             String[] pricear = price.split(Separator.separator_prop_vlaue);
             int min = StringUtil.toInt( pricear[0],0 );
             int max=Integer.MAX_VALUE;
             
             if( pricear.length==2){
                 max=  StringUtil.toInt( pricear[1] ,Integer.MAX_VALUE);
             }
            
             
        
             Query priceQuery  = NumericRangeQuery.newDoubleRange("price", Double.valueOf(min),Double.valueOf( max), true, true);
             
             query.add(priceQuery,BooleanClause.Occur.MUST);
        }
        //删除的商品不显示
        Query disableQuery = new TermQuery(new Term("disabled","0"));  
        query.add(disableQuery, BooleanClause.Occur.MUST);
        // 删除的商品不显示
        Query marketEnableQuery = new TermQuery(new Term("market_enable","1"));
        query.add(marketEnableQuery, BooleanClause.Occur.MUST); 
        return query;
    }
    
    
	
	private static Analyzer getAnalyzer(){
//		return new MMSegAnalyzer();
	    SimpleAnalyzer analyzer = new SimpleAnalyzer();
	    return analyzer;
	}



	
	/**
	 * 填充商品
	 * @param goods
	 */
	private void fillGoodsPinyin(Map goods){
		String goods_name = goods.get("name").toString();
		List<String> wordsList = toWordsList(goods_name);
		String seg_name = StringUtil.listToString(wordsList, " "); // 将分词结果转为空格分格的一串字符

		String name_quanpin = PinYinUtil.getPingYin(seg_name); // 全拼
		String name_header_py = PinYinUtil.getPinYinHeadChar(seg_name);// 首字母

		goods.put("goods_name", goods_name);
		goods.put("name_quanpin", name_quanpin);
		goods.put("name_header_py", name_header_py);
	}
	 
	
	/**
	 * 获取lucence 的indexwriter
	 * @return
	 * @throws IOException
	 */
	private IndexWriter getIndexWriter() throws IOException{
		String index_path = LuceneSetting.INDEX_PATH+"/content";
		File indexDir = new File(index_path);
		Path path = indexDir.toPath();
		Directory directory = FSDirectory.open(path);
		Analyzer luceneAnalyzer = getAnalyzer();

		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
				luceneAnalyzer);
		IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
		return indexWriter;
	}
	
	
	/**
	 * 获取lucene的 indexsearcher
	 * @return
	 * @throws IOException
	 */
	private IndexSearcher getIndexSearcher() throws IOException{
		String index_path = LuceneSetting.INDEX_PATH+"/content";

		File indexDir = new File(index_path);
		FSDirectory directory = FSDirectory.open(indexDir.toPath());
		 IndexReader reader = DirectoryReader.open(directory);
		 IndexSearcher searcher = new IndexSearcher(reader);
		 return searcher;
	}
	
	/**
	 * 获取维度的writer
	 * @return
	 * @throws IOException
	 */
	private TaxonomyWriter getTaxoWirter() throws IOException{
		 String facet_path = LuceneSetting.INDEX_PATH+"/facet";

		 File   taoDir = new File(facet_path); 
	     Path taopath =taoDir.toPath();
	     Directory taoDirectory= FSDirectory.open(taopath);
	     TaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taoDirectory);
	     return taxoWriter;
	     
	}
	
	/**
	 * 获取 维度的reader
	 * @return
	 * @throws IOException
	 */
	private TaxonomyReader getTaxoReader() throws IOException{
		 String facet_path = LuceneSetting.INDEX_PATH+"/facet";

		 File   taoDir = new File(facet_path); 
	     Path taopath =taoDir.toPath();
	     Directory taoDirectory= FSDirectory.open(taopath);
	     TaxonomyReader taxoReader = new DirectoryTaxonomyReader(taoDirectory);  
	     return taxoReader;
	}
	
	
	/**
	 * 获取商品数量 
	 * @return
	 */
	private int getGoodsCount(){
		return this.daoSupport.queryForInt("select count(0) from es_goods") ;
	}
	
	
	/**
     * 将一个商品创建为Document
     * @param goods
     * @return
     * @throws IOException
     */
    private Document createDocument( Map goods) throws IOException{
        
        Document document = new Document(); 
        
        try {
            
        
        String goods_name  = goods.get("goods_name").toString();
        String name_quanpin  = goods.get("name_quanpin").toString();
        String name_header_py  = goods.get("name_header_py").toString();
         
         
        Field name = new TextField("name",goods_name,Field.Store.YES) ;
        Field f_name_quanpin = new TextField("name_quanpin",name_quanpin,Field.Store.YES) ;
        Field f_name_header_py = new TextField("name_header_py",name_header_py,Field.Store.YES) ;
        String thumb = goods.get("thumbnail")==null?"":goods.get("thumbnail").toString() ;
        
        Field thumbnail = new StringField("thumbnail", thumb,Field.Store.YES) ;
 
        
        //价格要排序
        Double p =  StringUtil.toDouble( goods.get("price").toString() ,0d);
        Field price_sort =new NumericDocValuesField("price_sort", NumericUtils.doubleToSortableLong(p)); 
        Field price= new DoubleField("price", p, Field.Store.YES);
//      document.add (new NumericDocValuesField("price",p.longValue() ));
        
        
        //销量要排序
        Integer buy_count = (Integer)goods.get("buy_count");
        if( buy_count==null) buy_count=0;
        document.add( new NumericDocValuesField("buynum_sort", buy_count));
        document.add(new IntField( "buy_count",buy_count,Field.Store.YES));
       
        // lxl  品论数
        Integer comment_num = apiCommentManager.getCommentsCount((Integer)goods.get("goods_id"));
        document.add(new NumericDocValuesField("commentnum_sort", comment_num));
        document.add(new IntField( "comment_num",comment_num,Field.Store.YES));
        //评价要排序
        Integer grade = (Integer)goods.get("grade");
        if(grade==null) grade =0;
        document.add( new NumericDocValuesField("grade_sort", grade));
        document.add(new IntField( "grade",grade,Field.Store.YES));
        
        //商户ID要排序
        Integer store = new Integer(NumberUtils.stringToInt(goods.get("store_id").toString(), Integer.MAX_VALUE));
        if(store == null)
            store = 0;
        document.add( new NumericDocValuesField("store_sort", store));
        document.add(new IntField( "store",store,Field.Store.YES));       
        
        
        Field goodsid_field  = new StringField("goods_id",  goods.get("goods_id").toString(),Field.Store.YES);
        
        
        //品牌维度
        document.add (new FacetField("brand_id",  goods.get("brand_id").toString()));
        document.add (new StringField("brand_id", goods.get("brand_id").toString(),Field.Store.YES));
        
        //分类维度
        Integer catid = StringUtil.toInt(  goods.get("cat_id").toString() ,0);
        document.add (new FacetField("cat_id", ""+catid));
        Cat cat  = this.goodsCatManager.getById(catid);
        if(cat!=null){
//          System.out.println(goods_name +"->"+cat.getCat_path());
            document.add (new StringField("catpath",  cat.getCat_path(),Field.Store.YES) );
        }
        
        /*---------------------------------*/
        /*           创建属性维度
        /* ------------------------------- */
        
        Integer typeid  = StringUtil.toInt(goods.get("type_id").toString(),0);
        GoodsTypeDTO goodsType = goodsTypeManager.get( typeid );
        
        List<Attribute> attrList  =   goodsType.getPropList();
        int i=1;
        for (Attribute attribute : attrList) {
            String attrName  = attribute.getName();
        
            
            String attrValue= "";
            
            if(attribute.getType()==3){
                String[] s = attribute.getOptionAr();
                String pvalue = (String) goods.get("p"+i);
                Integer num=0;
                if(!StringUtil.isEmpty(pvalue)){
					num = NumberUtils.toInt(pvalue);
				}
				attrValue = s[num];
            }else{
                attrValue = (String)goods.get("p"+i);
            }
            if(StringUtil.isEmpty( attrName ) || StringUtil.isEmpty( attrValue )  ) continue;
             document.add (new FacetField(attrName, attrValue));
             document.add (new StringField(attrName, attrValue,Field.Store.YES));
            i++;
        }
        
        document.add(goodsid_field);
        document.add(name);
        document.add(f_name_quanpin);
        document.add(f_name_header_py);
        
        document.add(thumbnail);
 
        document.add(price);
        document.add(price_sort);
        //disabled 是否删除
        Field disable= new StringField("disabled", goods.get("disabled").toString(), Field.Store.YES);
        document.add(disable);
        //是否上架
        Field market_enable= new StringField("market_enable", goods.get("market_enable").toString(), Field.Store.YES);
        document.add(market_enable);
       
        /**
         * 激发商品索引事件
         */
        this.goodsIndexPluginBundle.onIndex(goods, document);
        } catch (Exception e) {
         e.printStackTrace();
        }
        
        
        return document;
         
         
    }
	

	/**
	 * 将分词结果写入数据库
	 * @param wordsList
	 */
	private void wordsToDb(List<String> wordsList){
		for (String words : wordsList) {
			
			int count= this.daoSupport.queryForInt("select count(0)  from es_goods_words where words=?", words);
			if(count>0){//已经存在此分词 +1
				this.daoSupport.execute("update es_goods_words g set g.goods_num=g.goods_num+1 where g.words=? ", words);
			
			}else{
				
				Map data  = new HashMap();
				data.put("words", words);
				String name_quanpin= PinYinUtil.getPingYin(words); //全拼
				String name_header_py=PinYinUtil.getPinYinHeadChar(words);//首字母
				data.put("quanpin", name_quanpin);
				data.put("szm", name_header_py);
				data.put("goods_num", 0);
				
				this.daoSupport.insert("es_goods_words", data);
			}
			
		}
	}
	
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

	public IGoodsTypeManager getGoodsTypeManager() {
		return goodsTypeManager;
	}

	public void setGoodsTypeManager(IGoodsTypeManager goodsTypeManager) {
		this.goodsTypeManager = goodsTypeManager;
	}

	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}

	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}

	public EopProducer getEopProducer() {
		return eopProducer;
	}

	public void setEopProducer(EopProducer eopProducer) {
		this.eopProducer = eopProducer;
	}

	public GoodsIndexPluginBundle getGoodsIndexPluginBundle() {
		return goodsIndexPluginBundle;
	}

	public void setGoodsIndexPluginBundle(
			GoodsIndexPluginBundle goodsIndexPluginBundle) {
		this.goodsIndexPluginBundle = goodsIndexPluginBundle;
	}
	
    public GoodsManager getGoodsManager() {
        return goodsManager;
    }
    
    public void setGoodsManager(GoodsManager goodsManager) {
        this.goodsManager = goodsManager;
    }



    
    public ApiCommentManager getApiCommentManager() {
        return apiCommentManager;
    }



    
    public void setApiCommentManager(ApiCommentManager apiCommentManager) {
        this.apiCommentManager = apiCommentManager;
    }

}
