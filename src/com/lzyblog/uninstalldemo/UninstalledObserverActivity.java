package com.lzyblog.uninstalldemo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

/**
 * @author liuzhiyong ת����pengyiming
 * @note ������Ӧ���Ƿ�ж�أ�����ж���򵯳�ж�ط���
 * @note ����API17������û�֧�֣�ԭ��������4.2�����߰汾��ִ��ʱȱ��userSerial�������ش��޸�
 * @note �˴δ����޸����������ᵽ��һЩbug������������ݡ����USB�ߡ����ǰ�װ�Ȳ��������������ж�ء�
 * @note ����κ���������ظ����������⣬������ͨ�����ļ�������ֹ�����ps����ȡ���ؽ�������˽������ķ���Ҫ�úܶࡣ
 * @note ��װ��SD����ж�ؼ�����Ȼû�����⣬��������û�������Internal SD����װ�õ�Ӧ���ƶ���external
 *       SD��������.c��161��δ����files�ļ��к����ļ���Ӧ�û�bug�����붼�У���Ҫ�������޸���bug���ɡ�
 * @note ���͵�ַ:http://lzyblog.com
 */

public class UninstalledObserverActivity extends Activity {
	/* ���ݶ�begin */
	private static final String TAG = "UninstalledObserverActivity";
	
	//������վ
	private static final String WEBSITE = "http://lzyblog.com";

	// ��������pid
	private int mObserverProcessPid = -1;

	/* ���ݶ�end */

	/* static */
	// ��ʼ����������
	private native int init(String userSerial, String webSite);

	static {
		Log.d(TAG, "load lib --> uninstalled_observer");
		System.loadLibrary("uninstalled_observer");
	}

	/* static */

	/* ������begin */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		createFile();
		
		
		// API levelС��17������Ҫ��ȡuserSerialNumber
		if (Build.VERSION.SDK_INT < 17) {
			mObserverProcessPid = init(null, WEBSITE);
		}
		// ������Ҫ��ȡuserSerialNumber
		else {
			mObserverProcessPid = init(getUserSerial(), WEBSITE);
		}
	}

	private void createFile() {
		File file = new File("/data/data/com.lzyblog.uninstalldemo/files/observedFile");
		if (!file.exists()) {
			try {
				File dir = new File("/data/data/com.lzyblog.uninstalldemo/files");
				if (!dir.exists()) {
					if (dir.mkdir()) {
						Log.e(TAG, "����filesĿ¼�ɹ�");
					} else {
						Log.e(TAG, "����filesĿ¼ʧ��");
						return;
					}
				}
				if (file.createNewFile()) {
					Log.e(TAG, "����observedFile�ɹ�");
					return;
				}
				Log.e(TAG, "����observedFileʧ��");
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "����observedFileʧ��");
			}
		} else {
			Log.e(TAG, "observedFile����");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// ʾ�����룬���ڽ�����������
		// if (mObserverProcessPid > 0)
		// {
		// android.os.Process.killProcess(mObserverProcessPid);
		// }
	}

	// ����targetSdkVersion����17��ֻ��ͨ�������ȡ
	private String getUserSerial() {
		Object userManager = getSystemService("user");
		if (userManager == null) {
			Log.e(TAG, "userManager not exsit !!!");
			return null;
		}

		try {
			Method myUserHandleMethod = android.os.Process.class.getMethod(
					"myUserHandle", (Class<?>[]) null);
			Object myUserHandle = myUserHandleMethod.invoke(
					android.os.Process.class, (Object[]) null);

			Method getSerialNumberForUser = userManager.getClass().getMethod(
					"getSerialNumberForUser", myUserHandle.getClass());
			long userSerial = (Long) getSerialNumberForUser.invoke(userManager,
					myUserHandle);
			return String.valueOf(userSerial);
		} catch (NoSuchMethodException e) {
			Log.e(TAG, "", e);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "", e);
		} catch (IllegalAccessException e) {
			Log.e(TAG, "", e);
		} catch (InvocationTargetException e) {
			Log.e(TAG, "", e);
		}

		return null;
	}
	/* ������end */
}
