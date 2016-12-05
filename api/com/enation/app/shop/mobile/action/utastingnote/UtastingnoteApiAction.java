package com.enation.app.shop.mobile.action.utastingnote;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Maimo;
import com.enation.app.shop.core.model.UtastingNote;
import com.enation.app.shop.mobile.service.impl.ApiUtastingnoteManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.DateUtil;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

import java.io.File;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/api/mobile")
@Action("utastingnote")
@Results({
	@Result(name="uploadfile", type="freemarker",location="/shop/admin/yuemo/upload.html")
})

@SuppressWarnings({ "rawtypes", "unchecked", "serial","static-access" })

public class UtastingnoteApiAction extends WWAction {

	private ApiUtastingnoteManager apiUtastingnoteManager;
    private File appearanceVoice;
    private String appearanceVoiceFileName;
    private File imagea;
    private String imageaFileName;
    private File qualityVoice;
    private String qualityVoiceFileName;
    private File imageb;
    private String imagebFileName; 
    private File brandVoice;
    private String brandVoiceFileName;
    private File imagec;
    private String imagecFileName;  
    private File priceVoice;
    private String priceVoiceFileName;
    private File imaged;
    private String imagedFileName; 
    private File appraiseVoice;
    private String appraiseVoiceFileName;
    private File imagee;
    private String imageeFileName; 
    private File scoreVoice;
    private String scoreVoiceFileName;
    private File imagef;
    private String imagefFileName; 
    private File imageg;
    private String imagegFileName; 
    private File imageh;
    private String imagehFileName; 
    private File imagei;
    private String imageiFileName;  
    private final int PAGE_SIZE = 20;
    /*
     * 品酒笔记列表
     */
	public String list() {
	    HttpServletRequest request = ThreadContextHolder.getHttpRequest();
	    Member member = UserConext.getCurrentMember();
        if (member == null) {
            this.json = JsonMessageUtil.expireSession();
            return WWAction.JSON_MESSAGE;
        }
	    String memberId = member.getMember_id().toString();
	    int page =NumberUtils.toInt(request.getParameter("page"),1) ;
	    try{
	         Page utastingNotePage = apiUtastingnoteManager.listPage(page, PAGE_SIZE,memberId);
	         List<UtastingNote> utastlist= (List<UtastingNote>) utastingNotePage.getResult();
	         
	         for(UtastingNote tast:utastlist)
	         {
	             tast.setFnimagea(UploadUtil.replacePath(tast.getFnimagea()));
	             tast.setFnimageb(UploadUtil.replacePath(tast.getFnimageb()));
	             tast.setFnimagec(UploadUtil.replacePath(tast.getFnimagec()));
	             tast.setFnimaged(UploadUtil.replacePath(tast.getFnimaged()));
	             tast.setFnimagee(UploadUtil.replacePath(tast.getFnimagee()));
	             tast.setFnimagef(UploadUtil.replacePath(tast.getFnimagef()));
	             tast.setFnimageg(UploadUtil.replacePath(tast.getFnimageg()));
	             tast.setFnimageh(UploadUtil.replacePath(tast.getFnimageh()));
	             tast.setFnimagei(UploadUtil.replacePath(tast.getFnimagei()));
	             tast.setFnappearanceVoice(UploadUtil.replacePath(tast.getFnappearanceVoice()));
	             tast.setFnappraiseVoice(UploadUtil.replacePath(tast.getFnappraiseVoice()));
	             tast.setFnbrandVoice(UploadUtil.replacePath(tast.getFnbrandVoice()));
	             tast.setFnpriceVoice(UploadUtil.replacePath(tast.getFnpriceVoice()));
	             tast.setFnqualityVoice(UploadUtil.replacePath(tast.getFnqualityVoice()));
	             tast.setFnscoreVoice(UploadUtil.replacePath(tast.getFnscoreVoice()));
	         }  
	         
             //List<UtastingNote> list = apiUtastingnoteManager.list(memberId);
            this.json = JsonMessageUtil.getMobileObjectJson(utastingNotePage);

	    }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
	    return WWAction.JSON_MESSAGE;
	} 
 
	/*
	 * 增加品酒笔记
	 */
	public String addUtastingnote() {
	    HttpServletRequest request = getRequest();
	    Member member = UserConext.getCurrentMember();
        if (member == null) {
            this.json = JsonMessageUtil.expireSession();
            return WWAction.JSON_MESSAGE;
        }
	    try{
    	    //文字内容
    	    String member_id =  member.getMember_id().toString();
    	    String wine_name = request.getParameter("wine_name");
    	    
    	    String appearance1 = request.getParameter("appearance1");
    	    String appearance2 = request.getParameter("appearance2");
    	    String appearance3 = request.getParameter("appearance3");
    	    
    	    String quality1 = request.getParameter("quality1");
    	    String quality2 = request.getParameter("quality2");
    	    String quality3 = request.getParameter("quality3");
    	    
            String brand1 = request.getParameter("brand1");
            String brand2 = request.getParameter("brand2");
            String brand3 = request.getParameter("brand3");	
            
            String price1 = request.getParameter("price1");
            String price2 = request.getParameter("price2");
            String price3 = request.getParameter("price3");  
            
            String appraise = request.getParameter("appraise");
            String score = request.getParameter("score");
//            String release_time = request.getParameter("release_time");
            
            String fnappearanceVoice = null;
            String fnqualityVoice =  null;
            String fnbrandVoice = null;
            String fnpriceVoice = null;
            String fnappraiseVoice = null;
            String fnscoreVoice = null;
            if((appearanceVoice!=null)&&(appearanceVoiceFileName!=null))
            {
    	      fnappearanceVoice = UploadUtil.upload(appearanceVoice, appearanceVoiceFileName, "utastingnote");
            }
            if((qualityVoice!=null)&&(qualityVoiceFileName!=null))
            {   
                fnqualityVoice = UploadUtil.upload(qualityVoice, qualityVoiceFileName, "utastingnote");
            }
            if((brandVoice!=null)&&(brandVoiceFileName!=null))
            {
    	        fnbrandVoice = UploadUtil.upload(brandVoice, brandVoiceFileName, "utastingnote");
            }
            if((priceVoice!=null)&&(priceVoiceFileName!=null))
            {
    	      fnpriceVoice = UploadUtil.upload(priceVoice, priceVoiceFileName, "utastingnote");
            }
            if((appraiseVoice!=null)&&(appraiseVoiceFileName!=null))
            {
    	      fnappraiseVoice = UploadUtil.upload(appraiseVoice, appraiseVoiceFileName, "utastingnote");
            }
            if((scoreVoice!=null)&&(scoreVoiceFileName!=null))
            {
    	       fnscoreVoice = UploadUtil.upload(scoreVoice, scoreVoiceFileName, "utastingnote");
            }
            
    	    String fnimagea = null;
    	    String fnimageb =  null;
    	    String fnimagec = null;
    	    String fnimaged = null;
    	    String fnimagee = null;
    	    String fnimagef = null;
    	    String fnimageg = null;
    	    String fnimageh = null;
    	    String fnimagei = null;
    	    if(imagea != null)
    	    {
    	       fnimagea = UploadUtil.upload(imagea, imageaFileName, "utastingnote");
    	    } 
    	    if(imageb != null)
            {
    	      fnimageb = UploadUtil.upload(imageb, imagebFileName, "utastingnote");
            }
    	    if(imagec != null)
            {
    	      fnimagec = UploadUtil.upload(imagec, imagecFileName, "utastingnote");
            }
    	    if(imaged != null)
    	    {
    	      fnimaged = UploadUtil.upload(imaged, imagedFileName, "utastingnote");
    	    }
    	    if(imagee != null)
            {
    	      fnimagee = UploadUtil.upload(imagee, imageeFileName, "utastingnote");
            }
    	    if(imagef != null)
    	    {
    	      fnimagef = UploadUtil.upload(imagef, imagefFileName, "utastingnote");
    	    }
    	    if(imageg != null)
    	    {
    	      fnimageg = UploadUtil.upload(imageg, imagegFileName, "utastingnote");
    	    }
    	    if(imageh != null)
    	    {
    	      fnimageh = UploadUtil.upload(imageh, imagehFileName, "utastingnote");
    	    }
    	    if(imagei != null)
    	    {
    	      fnimagei = UploadUtil.upload(imagei, imageiFileName, "utastingnote"); 
    	    }
    	   
    	    UtastingNote utastingNote = new UtastingNote();
    	    
    	    utastingNote.setMember_id(member_id);
    	    utastingNote.setWine_name(wine_name);
    	    
    	    utastingNote.setAppearance1(appearance1);
    	    utastingNote.setAppearance2(appearance2);
    	    utastingNote.setAppearance3(appearance3);
    	    
    	    utastingNote.setBrand1(brand1);
    	    utastingNote.setBrand2(brand2);
    	    utastingNote.setBrand3(brand3);
    	    
    	    utastingNote.setQuality1(price1);
    	    utastingNote.setQuality2(price2);
    	    utastingNote.setQuality3(price3);
    	    
            utastingNote.setPrice1(quality1);
            utastingNote.setPrice2(quality2);
            utastingNote.setPrice3(quality3);
            long time =System.currentTimeMillis() / 1000 ;
            utastingNote.setRelease_time(Long.toString(time));
    	    
    	    utastingNote.setFnimagea(fnimagea);
            utastingNote.setFnimageb(fnimageb);
            utastingNote.setFnimagec(fnimagec);
            utastingNote.setFnimaged(fnimaged);
            utastingNote.setFnimagee(fnimagee);
            utastingNote.setFnimagef(fnimagef);
            utastingNote.setFnimageg(fnimageg);
            utastingNote.setFnimageh(fnimageh);
            utastingNote.setFnimagei(fnimagei);
            
            utastingNote.setAppraise(appraise);
            utastingNote.setScore(score);
            
    	    utastingNote.setFnappearanceVoice(fnappearanceVoice);
    	    utastingNote.setFnappraiseVoice(fnappraiseVoice);
    	    utastingNote.setFnbrandVoice(fnbrandVoice);
    	    utastingNote.setFnpriceVoice(fnpriceVoice);
    	    utastingNote.setFnqualityVoice(fnqualityVoice);
    	    utastingNote.setFnscoreVoice(fnscoreVoice);
    	      
    	    apiUtastingnoteManager.joinUtastingnote(utastingNote);
    	    this.showPlainSuccessJson("成功");
	    }catch (RuntimeException e) {
            this.logger.error("数据库运行异常", e);
            this.showPlainErrorJson(e.getMessage());
        }
	    
	    return WWAction.JSON_MESSAGE;
	}
	 
    public ApiUtastingnoteManager getApiUtastingnoteManager() {
        return apiUtastingnoteManager;
    }

    
    public void setApiUtastingnoteManager(ApiUtastingnoteManager apiUtastingnoteManager) {
        this.apiUtastingnoteManager = apiUtastingnoteManager;
    }
    
    public File getImagea() {
        return imagea;
    }

    
    public void setImagea(File imagea) {
        this.imagea = imagea;
    }

    
    public String getImageaFileName() {
        return imageaFileName;
    }

    
    public void setImageaFileName(String imageaFileName) {
        this.imageaFileName = imageaFileName;
    }

    
    public File getImageb() {
        return imageb;
    }

    
    public void setImageb(File imageb) {
        this.imageb = imageb;
    }

    
    public String getImagebFileName() {
        return imagebFileName;
    }

    
    public void setImagebFileName(String imagebFileName) {
        this.imagebFileName = imagebFileName;
    }


    
    public File getImagec() {
        return imagec;
    }

    
    public void setImagec(File imagec) {
        this.imagec = imagec;
    }

    
    public String getImagecFileName() {
        return imagecFileName;
    }

    
    public void setImagecFileName(String imagecFileName) {
        this.imagecFileName = imagecFileName;
    }

    
    public File getImaged() {
        return imaged;
    }

    
    public void setImaged(File imaged) {
        this.imaged = imaged;
    }

    
    public String getImagedFileName() {
        return imagedFileName;
    }

    
    public void setImagedFileName(String imagedFileName) {
        this.imagedFileName = imagedFileName;
    }

    
    public File getImagee() {
        return imagee;
    }

    
    public void setImagee(File imagee) {
        this.imagee = imagee;
    }

    
    public String getImageeFileName() {
        return imageeFileName;
    }

    
    public void setImageeFileName(String imageeFileName) {
        this.imageeFileName = imageeFileName;
    }

    
    public File getAppearanceVoice() {
        return appearanceVoice;
    }

    
    public void setAppearanceVoice(File appearanceVoice) {
        this.appearanceVoice = appearanceVoice;
    }

    
    public String getAppearanceVoiceFileName() {
        return appearanceVoiceFileName;
    }

    
    public void setAppearanceVoiceFileName(String appearanceVoiceFileName) {
        this.appearanceVoiceFileName = appearanceVoiceFileName;
    }

    
    public File getQualityVoice() {
        return qualityVoice;
    }

    
    public void setQualityVoice(File qualityVoice) {
        this.qualityVoice = qualityVoice;
    }

    
    public String getQualityVoiceFileName() {
        return qualityVoiceFileName;
    }

    
    public void setQualityVoiceFileName(String qualityVoiceFileName) {
        this.qualityVoiceFileName = qualityVoiceFileName;
    }

    
    public File getBrandVoice() {
        return brandVoice;
    }

    
    public void setBrandVoice(File brandVoice) {
        this.brandVoice = brandVoice;
    }

    
    public String getBrandVoiceFileName() {
        return brandVoiceFileName;
    }

    
    public void setBrandVoiceFileName(String brandVoiceFileName) {
        this.brandVoiceFileName = brandVoiceFileName;
    }

    
    public File getPriceVoice() {
        return priceVoice;
    }

    
    public void setPriceVoice(File priceVoice) {
        this.priceVoice = priceVoice;
    }

    
    public String getPriceVoiceFileName() {
        return priceVoiceFileName;
    }

    
    public void setPriceVoiceFileName(String priceVoiceFileName) {
        this.priceVoiceFileName = priceVoiceFileName;
    }

    
    public File getAppraiseVoice() {
        return appraiseVoice;
    }

    
    public void setAppraiseVoice(File appraiseVoice) {
        this.appraiseVoice = appraiseVoice;
    }

    
    public String getAppraiseVoiceFileName() {
        return appraiseVoiceFileName;
    }

    
    public void setAppraiseVoiceFileName(String appraiseVoiceFileName) {
        this.appraiseVoiceFileName = appraiseVoiceFileName;
    }

    
    public File getScoreVoice() {
        return scoreVoice;
    }

    
    public void setScoreVoice(File scoreVoice) {
        this.scoreVoice = scoreVoice;
    }

    
    public String getScoreVoiceFileName() {
        return scoreVoiceFileName;
    }

    
    public void setScoreVoiceFileName(String scoreVoiceFileName) {
        this.scoreVoiceFileName = scoreVoiceFileName;
    }

    
    public File getImagef() {
        return imagef;
    }

    
    public void setImagef(File imagef) {
        this.imagef = imagef;
    }

    
    public String getImagefFileName() {
        return imagefFileName;
    }

    
    public void setImagefFileName(String imagefFileName) {
        this.imagefFileName = imagefFileName;
    }

    
    public File getImageg() {
        return imageg;
    }

    
    public void setImageg(File imageg) {
        this.imageg = imageg;
    }

    
    public String getImagegFileName() {
        return imagegFileName;
    }

    
    public void setImagegFileName(String imagegFileName) {
        this.imagegFileName = imagegFileName;
    }

    
    public File getImageh() {
        return imageh;
    }

    
    public void setImageh(File imageh) {
        this.imageh = imageh;
    }

    
    public String getImagehFileName() {
        return imagehFileName;
    }

    
    public void setImagehFileName(String imagehFileName) {
        this.imagehFileName = imagehFileName;
    }

    
    public File getImagei() {
        return imagei;
    }

    
    public void setImagei(File imagei) {
        this.imagei = imagei;
    }

    
    public String getImageiFileName() {
        return imageiFileName;
    }

    
    public void setImageiFileName(String imageiFileName) {
        this.imageiFileName = imageiFileName;
    }
    
}
