
# 自定义解析器
## 简述
在`parsers`文件夹下，放置的后缀为`.java`的文件，其功能和位于源码包`nicelee.bilibili.parsers.impl`等同。  

ClassLoader 会先扫描、编译`parsers`文件夹的java文件并尝试加载, 然后才轮到jar包里的内容。  

需要注意的是, 如果`parsers`文件夹的java文件有使用到了源码parser的情况, 加载顺序会有所改变。  


## parsers.ini
如果`parsers`文件夹不包含`parsers.ini`，那么ClassLoader会扫描加载所有.java文件，但加载顺序不确定。  
如果`parsers`文件夹包含`parsers.ini`，那么ClassLoader会逐行读取其内容，按顺序尝试加载对应的类。  


如果自定实现的类与已有的是相同名称，请确保它在已有的类之前加载。  
而为了实现这个，需要指定加载顺序，需要在`parsers`文件夹下创建配置`parsers.ini`。举例：   
```
假设你自定义实现了BVParser 和 B23Parser，其中B23Parser继承BVParser。  
如果先尝试加载B23Parser，那么它在加载时会搜寻其父类，于是会找到现有的BVParser。  
其结果是你自定义的BVParser没用了，自定义的B23Parser也可能因此而存在逻辑问题。  
解决方案是，在parsers文件夹下创建parsers.ini，将顺序指定，其内容为：
BVParser
B23Parser
```

## 一个最简单的示例  

假设B站又出幺蛾子，在av、BV之后又推出了CV号，我们从神秘路径得知了CV转BV的对应关系，可以在已有的基础上快速地搞定CV解析

```java
package nicelee.bilibili.parsers.impl;

import nicelee.bilibili.annotations.Bilibili;

// 需要Bilibili注释，否则程序不会使用这个解析器
// 普通的解析器权重是66, 因为其它类型输入很容易就包含关键词CV，我们降低权重，等其它解析器失败再来收尾
@Bilibili(name = "CVParser", weight= 55, note = "CV解析")
public class CVParser extends BVParser { // 继承BVParser，接下来工作量减少很多

	private static final Pattern pattern = Pattern.compile("CV[0-9A-Za-z]+");

	public CVParser(Object... obj) {
		super(obj);
	}

    private String cv2Bv(){
        // ...
        return "CVxxx";
    }

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			String CV = matcher.group();
			String BV = cv2Bv(input);
			System.out.println("匹配CVParser: " + CV);
			return super.matches(BV);
		}
		return false;
	}
}
```