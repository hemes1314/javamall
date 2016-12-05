package com.enation.app.shop.core.utils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/*
 * xml解析类 用于读取配置文件信息
 */
public class ParseXml {
    
    private String filePath;
 
    private Document document; 
     
    public ParseXml(String filePath) {     
        this.filePath = filePath;
        this.load(this.filePath);
    }  
     
    private void load(String filePath){
        File file = new File(filePath);
        if (file.exists()) {
            SAXReader saxReader = new SAXReader();
            try {
                document = saxReader.read(file);
            } catch (DocumentException e) {    
                System.out.println("文件加载异常：" + filePath);              
            }
        } else{
            System.out.println("文件不存在 : " + filePath);
        }          
    }  
     
    public Element getElementObject(String elementPath) {
        return (Element) document.selectSingleNode(elementPath);
    }  
     
    @SuppressWarnings("unchecked")
    public List<Element> getElementObjects(String elementPath) {
        return document.selectNodes(elementPath);
    }
     

     
    public boolean isExist(String elementPath){
        boolean flag = false;
        Element element = this.getElementObject(elementPath);
        if(element != null) flag = true;
        return flag;
    }
 
    public String getElementText(String elementPath) {
        Element element = this.getElementObject(elementPath);
        if(element != null){
            return element.getText().trim();
        }else{
            return null;
        }      
    }
}
