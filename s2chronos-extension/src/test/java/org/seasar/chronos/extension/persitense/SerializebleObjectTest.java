package org.seasar.chronos.extension.persitense;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.junit.Test;
import org.seasar.framework.exception.IORuntimeException;

public class SerializebleObjectTest implements Serializable {

	private static final long serialVersionUID = 1L;

	@Test
	public void testSerializebleObject() {
		String className = TestTask.class.getName();
		TestTask a = new TestTask();
		a.testTask = new TestTask();
		a.testTask.name = "hoge";
		SerializebleObject so = new SerializebleObject(className, a);

		FileOutputStream fos = null;
		try {
			File targetFile = new File("C:\\temp\\", className);
			fos = new FileOutputStream(targetFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(so);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					throw new IORuntimeException(e);
				}
			}
		}
	}

	@Test
	public void testDeserializebleObject() {
		TestTask target = null;
		String className = TestTask.class.getName();
		FileInputStream fis = null;
		try {
			File targetFile = new File("C:\\temp\\", className);
			if (targetFile.exists()) {
				fis = new FileInputStream(targetFile);
				ObjectInputStream ois = new ObjectInputStream(fis);
				target = (TestTask) ois.readObject();
			}
		} catch (IOException e) {
			throw new IORuntimeException(e);
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					throw new IORuntimeException(e);
				}
			}
		}
		System.out.println(target);
		System.out.println(target.name);
		System.out.println(target.id);
		System.out.println(target.testTask);
		System.out.println(target.testTask.name);
		System.out.println(target.testTask.id);
	}
}
