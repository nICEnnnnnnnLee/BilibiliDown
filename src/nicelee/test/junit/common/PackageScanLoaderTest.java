package nicelee.test.junit.common;


import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nicelee.bilibili.PackageScanLoader;
import nicelee.bilibili.annotations.Bilibili;

public class PackageScanLoaderTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	//@Test
	public void testAll() {
		PackageScanLoader pLoader = new PackageScanLoader() {
			@Override
			public boolean isValid(Class<?> klass) {
				return true;
			}
		};
		List<Class<?>> clazzes = pLoader.scanRoot("nicelee.bilibili");
		System.out.println("所有类名");
		for(Class<?> clazz: clazzes) {
			System.out.println(clazz.getName());
		}
	}
	
	@Test
	public void testLoadAnnotation() {
		PackageScanLoader pLoader = new PackageScanLoader() {
			@Override
			public boolean isValid(Class<?> klass) {
				Bilibili bili = klass.getAnnotation(Bilibili.class);
				if(null != bili) {
					//System.out.println(bili.name());
					return true;
				}
				return false;
			}
		};
		List<Class<?>> clazzes = pLoader.scanRoot("nicelee.bilibili");
		System.out.println("有bilibili注解的类名");
		for(Class<?> clazz: clazzes) {
			System.out.println(clazz.getName());
		}
	}

}
