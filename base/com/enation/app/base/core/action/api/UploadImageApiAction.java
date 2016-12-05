package com.enation.app.base.core.action.api;

import java.io.File;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
@SuppressWarnings("serial")
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/base")
@Action("upload-image")
@Results({
})
public class UploadImageApiAction extends WWAction {

		private File image ;
	 	private String imageFileName;
	 	private String subFolder;
	 	
	 	public String execute(){
	 		try{
	 			String fsImgPath =UploadUtil.upload(image, imageFileName,  subFolder);
	 			System.out.println("===========fsImgPath======"+fsImgPath);
	 			String path="{\"img\":\""+UploadUtil.replacePath(fsImgPath)+"\",\"fsimg\":\""+fsImgPath+"\"}";
	 			System.out.println("****************path**********"+path);
	 			this.json=path;
	 		}catch(Throwable e){
	 			this.showErrorJson("上传出错"+e.getLocalizedMessage());
	 		}
	 		 System.out.println("****************this.JSON_MESSAGE**********"+JSON_MESSAGE);
	 		return JSON_MESSAGE;
	 		
	 	}
		public File getImage() {
			return image;
		}
		public void setImage(File image) {
			this.image = image;
		}
		public String getImageFileName() {
			return imageFileName;
		}
		public void setImageFileName(String imageFileName) {
			this.imageFileName = imageFileName;
		}
		public String getSubFolder() {
			return subFolder;
		}
		public void setSubFolder(String subFolder) {
			this.subFolder = subFolder;
		}
}
