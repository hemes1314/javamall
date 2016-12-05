package com.enation.app.base.core.action;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import com.enation.eop.SystemSetting;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;

public class DownloadAction extends WWAction {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 文件路径
	 */
	private String filePath;
	
	public void download() throws Exception {
		// 找到文件
		String static_server_path= SystemSetting.getStatic_server_path();

		String filePath = static_server_path + "/";
		String fileName = this.filePath.substring(this.filePath.indexOf("attachment"), this.filePath.length());
		filePath += fileName;
		
		// 读到流中
		InputStream inStream = new FileInputStream(filePath); // 文件的存放路径
		// 设置输出的格式
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		response.reset();
		response.setContentType("bin");
		response.addHeader("Content-Disposition", "attachment; filename=\""
				+ fileName + "\"");
		// 循环取出流中的数据
		byte[] b = new byte[100];
		int len;
		try {
			while ((len = inStream.read(b)) > 0)
				response.getOutputStream().write(b, 0, len);
			inStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
