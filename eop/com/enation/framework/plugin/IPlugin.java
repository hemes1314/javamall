package com.enation.framework.plugin;

/**
 * 插件接口<br/> 实现此接口的插件可被注册至通过IPluginBundle桩化的业务类中。<br/> 推荐插件注册的实现方法：<br/>
 * 插件实现体构造函数为：<br/> public YourConstructor(IPluginBundle pluginBundle){<br />
 * pluginAble.registerPlugin(this); <br /> }
 * 
 * 
 * 利用如此的构造函数可实现插件本身的注册, 如果通过Spring IoC 方式注入则配置如下：<br/>
 * 
 * &lt;bean id=&quot;yourPlugin&quot; class=&quot;package.PluginClass&quot;
 * lazy-init=&quot;false&quot;&gt;<br />
 * &lt;constructor-arg&gt;<br />
 * &lt;ref bean=&quot;yourPluginBundle&quot;/&gt;<br />
 * &lt;/constructor-arg&gt; <br />
 * &lt;/bean&gt;
 * 
 * @author apexking
 * 
 */
public interface IPlugin {

 
	

}
