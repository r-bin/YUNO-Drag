package org.yuno.apps.dragdropmanager;

import java.util.HashMap;
import java.util.Map;

import org.yuno.apps.dragdropmanager.helper.DropZone;
import org.yuno.apps.dragdropmanager.support.DragEvent;
import org.yuno.apps.dragdropmanager.support.DragShadowBuilder;
import org.yuno.apps.dragdropmanager.support.OnDragListener;
import org.yuno.apps.dragdropmanager.support.raw.ClipData;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.View;

public class DragDropManager implements DragDropContainer.Callbacks {
	public static final String TAG = DragDropManager.class.getSimpleName();

	public static final boolean SUPPORT_Z_ORDER_DETECTION = true; // TODO
	public static final boolean SUPPORT_CLIP = false;

	public static final boolean SUPPORT_ACTION_DRAG_LOCATION = false;

	private static DragDropManager m_instance;

	private final Map<View, DropZone> m_dropzone = new HashMap<View, DropZone>();

	@SuppressLint("NewApi")
	private final android.view.View.OnDragListener m_onDragListener;
	private final DragDropContainer m_dragDropContainer;

	// EVENT

	private DragDropManager(Activity activity) {
		m_onDragListener = createSupportDragListener();
		m_dragDropContainer = new DragDropContainer(activity, m_dropzone, this);
	}

	// PUBLIC

	@SuppressLint("NewApi")
	public void setOnDragListener(View view, OnDragListener dropZonelistener) {
		m_dropzone.put(view, new DropZone(view, dropZonelistener));

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			view.setOnDragListener(m_onDragListener);
		}
	}

	@SuppressLint("NewApi")
	public void startDrag(View view, ClipData data,
			DragShadowBuilder shadowBuilder, Object myLocalState, int flags) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			view.startDrag(null,
					DragShadowBuilder.supportConvert(shadowBuilder),
					myLocalState, flags);
		} else {
			m_dragDropContainer.startDrag(view, data, shadowBuilder,
					myLocalState, flags);
		}
	}

	public boolean onDrag(View view, DragEvent event) {
		DropZone dropZone = m_dropzone.get(view);

		if (dropZone == null) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG, "DropZone not found!");
			}

			return false;
		} else {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "event[" + event.getAction() + "] - dropZone["
						+ view + "]");
			}

			return dropZone.listener.onDrag(view, event);
		}
	}

	// PRIVATE

	private void deinit() {
		m_dropzone.clear();
	}

	// FACTORY

	private android.view.View.OnDragListener createSupportDragListener() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			return new android.view.View.OnDragListener() {
				@Override
				public boolean onDrag(View v, android.view.DragEvent event) {
					return DragDropManager.this.onDrag(v, new DragEvent(event));
				}
			};
		} else {
			return null;
		}
	}

	// STATIC

	public static void createInstance(Activity activity) {
		if (m_instance != null) {
			// throw new IllegalStateException(
			// "DragDropManager already is initialized!");
		} else {
			m_instance = new DragDropManager(activity);
		}
	}

	public static void destroyInstance() {
		if (m_instance == null) {
			throw new IllegalStateException(
					"DragDropManager already is deinitialized!");
		} else {
			m_instance.deinit();

			m_instance = null;
		}
	}

	public static DragDropManager getInstance() {
		if (m_instance == null) {
			throw new IllegalStateException(
					"DragDropManager must be initialized!");
		} else {
			return m_instance;
		}
	}
}
